package com.github.ffremont.astrotheque.core.security;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AstroAuthenticator extends Authenticator {

    public final static String COOKIE_NAME = "astrotheque-auth";
    private final String jwtSecret;

    public AstroAuthenticator() {
        super();
        this.jwtSecret = UUID.randomUUID().toString();
    }


    @Override
    public Result authenticate(HttpExchange exch) {
        return null;
    }

    public SecretJwt getSecretJwt() {
        return new SecretJwt(this.jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
