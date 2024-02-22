package com.github.ffremont.astrotheque.core.security.jwt;

import com.github.ffremont.astrotheque.core.security.InvalidTokenException;
import com.github.ffremont.astrotheque.core.security.MetaToken;

public interface JwtVerifier {
    MetaToken verify(String token, String issuer) throws InvalidTokenException;
}
