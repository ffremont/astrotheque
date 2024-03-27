package com.github.ffremont.astrotheque.service.model;

public enum MoonPhase {
    NEW_MOON("Nouvelle lune"),
    WAXING_CRESCENT("Premier croissant"), // premier croissant
    FIRST_QUARTER("Premier quartier"),
    WAXING_GIBBOUS("Gibbeuse croissante"), // gibbeuse croissante
    FULL_MOON("Pleine lune"), // pleine lune
    WANING_GIBBOUS("Gibbeuse décroissante"), // gibbeuse décroissante
    LAST_QUARTER("Dernier quartier"), // dernier quartier
    WANING_CRESCENT("Dernier quartier");// dernier croissant

    String label;

    MoonPhase(String label) {
        this.label = label;
    }

    public String label() {
        return this.label;
    }
}
