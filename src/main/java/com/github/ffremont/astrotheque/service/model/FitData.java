package com.github.ffremont.astrotheque.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FitData {
    String id;
    Path tempFile;
    String filename;
    Integer gain;
    String object;
    Integer stackCnt;
    LocalDateTime dateObs;
    String instrume;
    Float exposure;
    Float temp;
    String hash;
}
