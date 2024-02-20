package com.github.ffremont.astrotheque.core.httpserver.route;

import java.io.InputStream;

public record Stream(InputStream inputStream, long size) {
}
