package com.github.ffremont.astrotheque.service.model;

public enum PlanetSatellite {
    MOON("Lune"),
    SATURN("Saturne"),
    SUN("Soleil"),
    JUPITER("Jupiter"),
    VENUS("Venus"),
    MERCURY("Mercure"),
    NEPTUNE("Neptune"),
    MARS("Mars"),
    OTHER("Autre");

    private String label;

    PlanetSatellite(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public Type getType() {
        Type type = Type.PLANET;
        switch (this) {
            case MOON:
                type = Type.SATELLITE;
                break;
            case OTHER:
                type = Type.SATELLITE;
                break;
        }
        return type;
    }
}
