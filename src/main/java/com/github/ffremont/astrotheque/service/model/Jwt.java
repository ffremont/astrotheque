package com.github.ffremont.astrotheque.service.model;

public record Jwt(String bearer, Long maxAge) {
}
