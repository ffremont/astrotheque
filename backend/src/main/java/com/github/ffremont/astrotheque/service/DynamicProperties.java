package com.github.ffremont.astrotheque.service;

import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public class DynamicProperties {
    private final Path dataDir;

    public DynamicProperties(){
        this.dataDir = Paths.get(System.getenv("DATA_DIR"));
    }
}
