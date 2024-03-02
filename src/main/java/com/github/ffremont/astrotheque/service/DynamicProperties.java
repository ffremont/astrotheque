package com.github.ffremont.astrotheque.service;

import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.util.function.Predicate.not;

@Getter
public class DynamicProperties {
    private final Path dataDir;
    private final Integer port;
    private final String astrometryNovaBaseUrl;
    private final String astrometryNovaApikey;

    /**
     * Nécessaire
     */
    private final String secret;

    public DynamicProperties() {
        this.secret = Optional.ofNullable(System.getenv("SECRET")).orElseThrow();
        this.dataDir = Paths.get(Optional.ofNullable(System.getenv("DATA_DIR")).filter(not(String::isEmpty)).orElse("./"));
        this.astrometryNovaBaseUrl = Optional.ofNullable(System.getenv("ASTROMETRY_NOVA_BASEURL")).orElse("https://nova.astrometry.net");
        this.astrometryNovaApikey = Optional.ofNullable(System.getenv("ASTROMETRY_NOVA_APIKEY")).orElse("...");
        this.port = Optional.ofNullable(System.getenv("PORT")).map(Integer::valueOf).orElse(8080);
    }

}
