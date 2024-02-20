package com.github.ffremont.astrotheque.core.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class SecretJwt {
    private final byte[] secret;

    public SecretJwt(byte[] secret) {
        this.secret = secret;
    }

    public record MetaToken(String issuer, String subject, LocalDateTime expireAt) {
    }


    public String generate(MetaToken meta) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTCreator.Builder builder = JWT.create()
                .withIssuer(meta.issuer())
                .withSubject(meta.subject())
                .withExpiresAt(Date.from(meta.expireAt().atZone(ZoneId.systemDefault()).toInstant()));

        return builder.sign(algorithm);
    }

    public MetaToken verify(String token, String issuer) throws InvalidTokenException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer).build();
        try {
            DecodedJWT jwt = verifier.verify(token);

            return new MetaToken(jwt.getIssuer(), jwt.getSubject(), LocalDateTime.ofInstant(jwt.getExpiresAt().toInstant(), ZoneId.systemDefault()));
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException("Jeton invalide", e);
        }
    }
}
