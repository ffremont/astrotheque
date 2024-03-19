package com.github.ffremont.astrotheque.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.httpserver.multipart.MultipartUtils;
import com.github.ffremont.astrotheque.core.httpserver.multipart.Part;
import com.github.ffremont.astrotheque.service.ObservationService;
import com.github.ffremont.astrotheque.service.model.FitData;
import com.github.ffremont.astrotheque.service.model.Nature;
import com.github.ffremont.astrotheque.web.model.Observation;
import com.github.ffremont.astrotheque.web.model.PreviewData;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class ObservationResource implements HttpHandler {

    private final static ObjectMapper JSON = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    private final ObservationService observationService;

    public ObservationResource(IoC ioC) {
        this.observationService = ioC.get(ObservationService.class);
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        List<Part> parts = MultipartUtils.from(exchange, (long) (1024 * 1024 * 1000));
        try {
            Nature nature = parts.stream().filter(part ->
                            "nature".equals(part.name())
                                    && Objects.nonNull(part.value())
                    )
                    .map(Part::value)
                    .map(Nature::valueOf)
                    .findFirst().orElseThrow();
            Observation obs = parts.stream().filter(part ->
                    "data".equals(part.name())
                            && Objects.nonNull(part.value())
            ).findFirst().map(
                    data -> {
                        try {
                            return JSON.readValue(data.value(), Observation.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }).orElseThrow();

            List<FitData> fits = observationService.extractFitData(
                    exchange.getPrincipal().getUsername(),
                    parts.stream()
                            .filter(part -> "fits".equals(part.name()) && Objects.nonNull(part.file()))
                            .map(part -> Map.entry(part.filename(), part.file()))
                            .toList());
            Observation newObs = obs.toBuilder()
                    .fits(fits)
                    .previews(parts.stream()
                            .filter(part -> "previews".equals(part.name()) && Objects.nonNull(part.file()))
                            .map(part -> new PreviewData(part.file(), part.filename()))
                            .toList()
                    ).build();

            if (Nature.DSO.equals(nature)) {
                log.info("DSO > Importation basé sur nova astrometry");
                observationService.importObservation(exchange.getPrincipal().getUsername(), newObs);
            } else if (Nature.PLANET.equals(nature)) {
                
            } else {
                throw new RuntimeException("Pas implémenté");
            }
        } finally {
            //MultipartUtils.clear(parts);
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
        }
    }
}
