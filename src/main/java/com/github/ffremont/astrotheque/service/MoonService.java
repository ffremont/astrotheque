package com.github.ffremont.astrotheque.service;

import com.github.ffremont.astrotheque.service.model.MoonPhase;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class MoonService {

    /**
     * Au 1 janvier
     */
    final static Map<Integer, Double> ageByYear = Map.of(
            2023, 9d,
            2024, 19.45,
            2025, 1.5,
            2026, 12.4
    );

    /**
     * Le premier quartier est donc visible quand la lune a environ 7 jours.
     * La pleine lune quand la lune a environ 15 jours.
     * Le dernier quartier quand la lune a environ 22 jours.
     */

    public MoonPhase phaseOf(LocalDate date) {
        var ageOfMoon = ageByYear.get(LocalDate.now().getYear());
        double days = ChronoUnit.DAYS.between(LocalDate.now().withMonth(1).withDayOfMonth(1), date) + ageOfMoon;

        var age = days % 29.53;
        if (age <= 1) {
            return MoonPhase.NEW_MOON;
        } else if (age > 1 && age <= 6) {
            return MoonPhase.WAXING_CRESCENT;
        } else if (age > 6 && age <= 9) {
            return MoonPhase.FIRST_QUARTER;
        } else if (age > 9 && age <= 13) {
            return MoonPhase.WAXING_GIBBOUS;
        } else if (age > 13 && age <= 16) {
            return MoonPhase.FULL_MOON;
        } else if (age > 16 && age <= 20) {
            return MoonPhase.WANING_GIBBOUS;
        } else if (age > 20 && age <= 22) {
            return MoonPhase.LAST_QUARTER;
        } else
            return MoonPhase.WANING_CRESCENT;
    }
}
