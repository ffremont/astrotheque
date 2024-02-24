package com.github.ffremont.astrotheque.core.security;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.security.jwt.JwtGenerator;
import com.github.ffremont.astrotheque.core.security.jwt.JwtVerifier;
import com.github.ffremont.astrotheque.core.security.jwt.SecretJwt;
import com.github.ffremont.astrotheque.service.AccountService;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class AstroAuthenticator extends Authenticator {


    public final static String ISSUER = "astrotheque";
    public final static String COOKIE_NAME = "astrotheque-auth";
    private final String jwtSecret;
    private final IoC ioc;

    public AstroAuthenticator(IoC ioC) {
        super();
        this.jwtSecret = UUID.randomUUID().toString();
        this.ioc = ioC;
    }


    public JwtGenerator getGenerator() {
        return this.getSecretJwt();
    }

    public JwtVerifier getVerifier() {
        return this.getSecretJwt();
    }


    @Override
    public Result authenticate(HttpExchange exch) {
        Optional<HttpCookie> cookie = HttpCookie.parse(exch.getRequestHeaders().getFirst("Cookie")).stream().filter(httpCookie -> COOKIE_NAME.equals(httpCookie.getName()))
                .findFirst();
        if (cookie.isEmpty() || ioc.get(AccountService.class).isBlacklistedToken(cookie.get().getValue())) {
            return new Authenticator.Failure(401);
        }

        try {
            MetaToken metaToken = this.getVerifier().verify(cookie.get().getValue(), ISSUER);
            return new Authenticator.Success(new HttpPrincipal(metaToken.subject(), "app"));
        } catch (InvalidTokenException e) {
            log.warn("Authentification en échec pour {}", cookie.get().getValue(), e);
            return new Authenticator.Failure(401);
        }
    }


    public SecretJwt getSecretJwt() {
        return new SecretJwt(this.jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
