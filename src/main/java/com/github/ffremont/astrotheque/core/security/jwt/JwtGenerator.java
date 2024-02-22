package com.github.ffremont.astrotheque.core.security.jwt;

import com.github.ffremont.astrotheque.core.security.MetaToken;

public interface JwtGenerator {
    String generate(MetaToken meta);
}
