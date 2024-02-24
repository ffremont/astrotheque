package com.github.ffremont.astrotheque.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DsoEntry(
        Type type,
        @JsonProperty("const")
        String constellation,
        @JsonProperty("mag")
        Float magnitude,
        Integer id1,
        String cat1,
        Integer id2,
        String cat2
) implements Comparable<DsoEntry> {

    boolean isMessier() {
        return "M".equals(this.cat1) || "M".equals(this.cat2);
    }

    @Override
    public int compareTo(DsoEntry o) {
        return this.magnitude.compareTo(o.magnitude);
    }
}
