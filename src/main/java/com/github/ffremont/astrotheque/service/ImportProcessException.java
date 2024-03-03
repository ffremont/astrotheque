package com.github.ffremont.astrotheque.service;

public class ImportProcessException extends RuntimeException {
    public ImportProcessException(String message) {
        super(message);
    }

    public ImportProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
