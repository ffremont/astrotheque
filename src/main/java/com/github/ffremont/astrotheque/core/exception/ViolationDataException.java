package com.github.ffremont.astrotheque.core.exception;

public class ViolationDataException extends RuntimeException {
    public ViolationDataException(String message) {
        super(message);
    }

    public ViolationDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
