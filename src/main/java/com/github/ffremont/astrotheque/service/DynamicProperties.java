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

    /**
     * Nécessaire
     */
    private final String secret;

    public DynamicProperties() {
        var defaultHome = Paths.get((String) System.getProperties().get("user.home")).resolve("astrotheque");
        this.secret = Optional.ofNullable(System.getenv("SECRET")).orElse("ksJTvw7XJ+y7sZ32NqwWH03TvD4K2ADRm7B8NavN1BA=");
        this.dataDir = Paths.get(Optional.ofNullable(System.getenv("DATA_DIR")).filter(not(String::isEmpty)).orElse(defaultHome.toAbsolutePath().toString()));
        this.astrometryNovaBaseUrl = Optional.ofNullable(System.getenv("ASTROMETRY_NOVA_BASEURL")).orElse("https://nova.astrometry.net");
        this.port = Optional.ofNullable(System.getenv("PORT")).map(Integer::valueOf).orElse(9999);
    }

}
