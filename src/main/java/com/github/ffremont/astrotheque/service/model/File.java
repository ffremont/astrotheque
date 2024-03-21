package com.github.ffremont.astrotheque.service.model;

import lombok.Builder;

import java.nio.file.Path;

@Builder(toBuilder = true)
public record File(String id, Path tempFile, String filename, File relatedTo) {
}
