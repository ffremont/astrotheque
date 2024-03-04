package com.github.ffremont.astrotheque.web.model;

import java.nio.file.Path;

public record PreviewData(Path tempFile, String filename) {
}
