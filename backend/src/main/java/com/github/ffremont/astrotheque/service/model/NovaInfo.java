package com.github.ffremont.astrotheque.service.model;

import java.util.List;

public record NovaInfo(List<String> tags, String status, NovaCalibration calibration) {
}
