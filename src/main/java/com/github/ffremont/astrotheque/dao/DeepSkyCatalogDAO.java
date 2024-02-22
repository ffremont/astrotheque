package com.github.ffremont.astrotheque.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.StartupListener;
import com.github.ffremont.astrotheque.service.model.ConstellationData;
import com.github.ffremont.astrotheque.service.model.DsoEntry;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.function.Predicate.not;

public class DeepSkyCatalogDAO implements StartupListener {
    final static String DSO_FILENAME = "deep-sky-objects.json";
    final static String CONSTS_FILENAME = "constellations.json";

    private final static ObjectMapper JSON = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    List<DsoEntry> dsoEntries;
    List<ConstellationData> constellations;


    public Optional<ConstellationData> getConstellationByAbr(String abr) {
        return constellations.stream().filter(c -> c.abr().equals(abr)).findFirst();
    }


    public Optional<DsoEntry> getDsoByName(String name) {
        var id = Integer.valueOf(Optional.ofNullable(name.replaceAll("[\\D.]", "")).filter(not(String::isBlank)).orElse("0").trim());
        var category = Optional.ofNullable(name.replaceAll("[\\d.]", "")).filter(not(String::isBlank)).orElse("").trim();

        return dsoEntries.stream().filter(dsoEntry ->
                (id.equals(dsoEntry.id1()) && category.equals(dsoEntry.cat1()))
                        ||
                        (id.equals(dsoEntry.id2()) && category.equals(dsoEntry.cat2()))
        ).findFirst();
    }

    @Override
    public void onStartup(IoC ioC) {
        try {
            this.dsoEntries = JSON.readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream(DSO_FILENAME), new TypeReference<List<DsoEntry>>() {
            });
            this.constellations = JSON.readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream(CONSTS_FILENAME), new TypeReference<List<ConstellationData>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Chargement de deep-sky-objects.json / constellations.json impossible", e);
        }
    }
}
