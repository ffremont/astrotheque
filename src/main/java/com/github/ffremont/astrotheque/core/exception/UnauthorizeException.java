package com.github.ffremont.astrotheque.core.exception;

public class UnauthorizeException extends RuntimeException {
    private final String name;

    public UnauthorizeException(String name) {
        this.name = name;
    }

    public UnauthorizeException(String message, String name) {
        super(message);
        this.name = name;
    }

    public UnauthorizeException(String message, Throwable cause, String name) {
        super(message, cause);
        this.name = name;
    }
}
