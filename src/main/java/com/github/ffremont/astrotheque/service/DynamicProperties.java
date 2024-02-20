package com.github.ffremont.astrotheque.service;

import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Getter
public class DynamicProperties {
    private final Path dataDir;
    private final Integer port;
    private final String astrometryNovaBaseUrl;
    private final String adminLogin;
    private final String adminPwd;
    private final String astrometryNovaApikey;

    public DynamicProperties() {
        this.dataDir = Paths.get(System.getenv("DATA_DIR"));
        this.adminLogin = Optional.ofNullable(System.getenv("ADMIN_LOGIN")).orElse("admin");
        this.adminPwd = Optional.ofNullable(System.getenv("ADMIN_PWD")).orElse("admin");
        this.astrometryNovaBaseUrl = Optional.ofNullable(System.getenv("ASTROMETRY_NOVA_BASEURL")).orElse("https://nova.astrometry.net");
        this.astrometryNovaApikey = Optional.ofNullable(System.getenv("ASTROMETRY_NOVA_APIKEY")).orElse("...");
        this.port = Optional.ofNullable(System.getenv("PORT")).map(Integer::valueOf).orElse(8080);
    }

}
