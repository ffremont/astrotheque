package com.github.ffremont.astrotheque.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Picture implements Comparable<Picture> {
    String id;
    String observationId;
    PictureState state;
    PlanetSatellite planetSatellite;
    LocalDateTime imported;
    String filename;
    String name;

    MoonPhase moonPhase;
    LocalDateTime dateObs;
    Weather weather;
    String instrument;
    String location;
    String camera;
    Float exposure;
    Integer gain;

    Integer stackCnt;
    List<String> tags;
    String constellation;
    String hash;
    NovaAstrometry novaAstrometry;
    Float ra;
    Float dec;
    Type type;
    /**
     * deg
     */
    Float radius;
    /**
     * arcsec/pixel
     */
    Float pixscale;

    String note;

    List<String> webTags;

    Long size;

    @Override
    public int compareTo(Picture o) {
        return Objects.nonNull(this.dateObs) && Objects.nonNull(o.dateObs) ? this.dateObs.compareTo(o.dateObs) : -1;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class NovaAstrometry {
        Integer jobId;
        Integer image;
        Integer submission;
    }
}
