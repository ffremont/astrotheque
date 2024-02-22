package com.github.ffremont.astrotheque.core.security;

import java.time.LocalDateTime;

public record MetaToken(String issuer, String subject, LocalDateTime expireAt) {
}
