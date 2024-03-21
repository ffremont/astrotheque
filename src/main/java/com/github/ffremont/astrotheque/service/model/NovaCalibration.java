package com.github.ffremont.astrotheque.service.model;

public record NovaCalibration(
        Float ra,
        Float dec,
        /**
         * deg
         */
        Float radius,
        /**
         * arcsec/pixel
         */
        Float pixscale,
        Float orientation) {
}
