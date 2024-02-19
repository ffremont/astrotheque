package com.github.ffremont.astrotheque.core.model;

import lombok.Builder;

import java.nio.file.Path;

@Builder
public record Part(String name, String contentType, String value, String filename, Path file) {
}
