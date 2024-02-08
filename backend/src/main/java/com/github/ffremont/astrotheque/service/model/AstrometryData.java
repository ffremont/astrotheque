package com.github.ffremont.astrotheque.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AstrometryData {
    Integer userImages;
    String session;
    Integer job;
    Boolean pending;
    Float ra;
    Float dec;
    List<String> tags;
    FitData fit;
}
