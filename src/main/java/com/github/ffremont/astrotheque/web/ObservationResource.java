package com.github.ffremont.astrotheque.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.httpserver.multipart.MultipartUtils;
import com.github.ffremont.astrotheque.core.httpserver.multipart.Part;
import com.github.ffremont.astrotheque.service.ObservationService;
import com.github.ffremont.astrotheque.service.model.File;
import com.github.ffremont.astrotheque.service.model.Nature;
import com.github.ffremont.astrotheque.service.model.PlanetSatellite;
import com.github.ffremont.astrotheque.web.model.Observation;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

import static com.github.ffremont.astrotheque.service.utils.FileUtils.*;

@Slf4j
public class ObservationResource implements HttpHandler {

    private final static Long MAX_UPLOAD = (long) (1024 * 1024 * 1000);

    private final static ObjectMapper JSON = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    private final ObservationService observationService;


    public ObservationResource(IoC ioC) {
        this.observationService = ioC.get(ObservationService.class);
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        List<Part> parts = MultipartUtils.from(exchange, MAX_UPLOAD);
        try {
            Nature nature = parts.stream().filter(part ->
                            "nature".equals(part.name())
                                    && Objects.nonNull(part.value())
                    )
                    .map(Part::value)
                    .map(Nature::valueOf)
                    .findFirst().orElseThrow();
            Boolean useNovaAstrometry = parts.stream().filter(part ->
                            "analyze".equals(part.name())
                                    && Objects.nonNull(part.value())
                    )
                    .map(Part::value)
                    .anyMatch("true"::equals);
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

            var allFiles = parts.stream()
                    .filter(part -> "files".equals(part.name()) && Objects.nonNull(part.file()))
                    .map(part -> new File(UUID.randomUUID().toString(), part.file(), part.filename(), null))
                    .toList();

            var pictureFiles = new ArrayList<File>();
            // TODO si 2 fichiers, un fit et une image alors les lier
            for (File file : allFiles) {
                // already added ?
                if (pictureFiles.stream().anyMatch(s -> s.filename().equals(file.filename())
                        ||
                        Optional.ofNullable(s.relatedTo()).map(rf -> rf.filename().equals(file.filename())).orElse(Boolean.FALSE))) {
                    continue;
                }

                var filenameWithoutExt = file.filename().substring(0, file.filename().lastIndexOf("."));
                var twin = allFiles.stream().filter(f -> !f.filename().equals(file.filename()) && f.filename().startsWith(filenameWithoutExt)).findFirst();

                // fichiers jumeaux ont des ext. différentes
                if (twin.isPresent() && !extensionOf.apply(file.filename()).equalsIgnoreCase(twin.get().filename())) {
                    if (isFit.test(file.filename()) && isImage.test(twin.get().filename())) {
                        pictureFiles.add(file.toBuilder().relatedTo(twin.get()).build());
                    } else if (isFit.test(file.filename()) && !isImage.test(twin.get().filename())) {
                        pictureFiles.add(file);
                    } else if (isImage.test(file.filename()) && isFit.test(twin.get().filename())) {
                        pictureFiles.add(twin.get().toBuilder().relatedTo(file).build());
                    }
                } else {
                    pictureFiles.add(file);
                }
            }
            Observation newObs = obs.toBuilder().files(pictureFiles).build();

            if (Nature.DSO.equals(nature)) {
                log.info("DSO > Importation basé sur nova astrometry");
                observationService.newDsoObservation(exchange.getPrincipal().getUsername(), newObs);

                //todo useNovaAstrometry
            } else if (Nature.PLANET_SATELLITE.equals(nature)) {
                PlanetSatellite planetSatellite = parts.stream().filter(part ->
                                "planetSatellite".equals(part.name())
                                        && Objects.nonNull(part.value())
                        )
                        .map(Part::value)
                        .map(PlanetSatellite::valueOf)
                        .findFirst().orElseThrow();
                observationService.newPlanetSatelliteObservation(exchange.getPrincipal().getUsername(),
                        newObs.toBuilder()
                                .planetSatellite(planetSatellite)
                                .build()
                );
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
