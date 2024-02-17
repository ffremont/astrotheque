package com.github.ffremont.astrotheque.service;

import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Getter
public class DynamicProperties {
    private final Path dataDir;
    private final Integer port;

    public DynamicProperties(){
        this.dataDir = Paths.get(System.getenv("DATA_DIR"));
        this.port = Optional.ofNullable(System.getenv("PORT")).map(Integer::valueOf).orElse(8080);
    }

}
