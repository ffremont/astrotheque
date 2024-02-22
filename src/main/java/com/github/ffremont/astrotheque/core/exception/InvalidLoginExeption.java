package com.github.ffremont.astrotheque.core.exception;

public class InvalidLoginExeption extends RuntimeException {
    private String login;

    public InvalidLoginExeption(String message, String login) {
        super(message);
        this.login = login;
    }

    public InvalidLoginExeption(String message, Throwable cause, String login) {
        super(message, cause);
        this.login = login;
    }
}
