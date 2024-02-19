package com.github.ffremont.astrotheque.core.exception;

import java.net.URI;

public class NotFoundException extends RuntimeException {

    private final URI uri;

    public NotFoundException(URI uri) {
        this.uri = uri;
    }

    public NotFoundException(URI uri, String message) {
        super(message);
        this.uri = uri;
    }

    public NotFoundException(URI uri, String message, Throwable cause) {
        super(message, cause);
        this.uri = uri;
    }

    public URI getUri() {
        return uri;
    }
}
