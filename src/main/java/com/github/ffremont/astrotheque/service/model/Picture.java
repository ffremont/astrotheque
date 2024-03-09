package com.github.ffremont.astrotheque.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Picture implements Comparable<Picture> {
    String id;
    String observationId;
    PictureState state;
    LocalDateTime imported;
    String filename;
    String name;

    MoonPhase moonPhase;
    LocalDateTime dateObs;
    Weather weather;
    String instrument;
    String location;
    String camera;
    String corrRed;
    Float exposure;
    Integer gain;

    Integer stackCnt;
    List<String> tags;
    String constellation;
    String hash;
    String novaAstrometryReportUrl;
    Float ra;
    Float dec;
    Type type;

    List<String> webTags;

    @Override
    public int compareTo(Picture o) {
        return this.dateObs.compareTo(o.dateObs);
    }
}
