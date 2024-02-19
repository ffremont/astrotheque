package com.github.ffremont.astrotheque.service.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public enum Type {
    GALAXY(List.of("Gxy", "GxyCld")),
    GLOBULAR_CLUSTER(List.of("GC")),
    HII_IONIZED_REGION(List.of("HIIRgn")),
    OPEN_CLUSTER(List.of("OC")),
    PLANETARY_NEBULAR(List.of("PN")),
    NEBULAR(List.of("Neb", "OC+Neb")),
    NOVA_STAR(List.of("SNR")),
    COMETE(List.of("COMETE")),
    OTHER(List.of("_")),

    NEBULAR_STAR_CLUSTER(Collections.EMPTY_LIST),
    GALAXY_PAIR(Collections.EMPTY_LIST),
    GALAXY_TRIPLET(Collections.EMPTY_LIST),
    DARK_NEBULAR(Collections.EMPTY_LIST),
    EMISSION_NEBULA(Collections.EMPTY_LIST),
    REFLECTION_NEBULA(Collections.EMPTY_LIST),
    SUPERNOVA_REMNANT(Collections.EMPTY_LIST),
    PLANET(Collections.EMPTY_LIST)
    ;
    private List<String> code;
    private Type(List<String> code){
        this.code = code;
    }

    public static Type fromCode(String code){
        return Arrays.asList(values()).stream()
                .filter(type -> type.code.contains(code))
                .findFirst().orElse(null);
    }
}
