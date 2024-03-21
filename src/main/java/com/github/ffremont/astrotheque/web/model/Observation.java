package com.github.ffremont.astrotheque.web.model;


import com.github.ffremont.astrotheque.core.Validator;
import com.github.ffremont.astrotheque.service.model.File;
import com.github.ffremont.astrotheque.service.model.FitData;
import com.github.ffremont.astrotheque.service.model.PlanetSatellite;
import com.github.ffremont.astrotheque.service.model.Weather;
import lombok.Builder;

import java.util.List;
import java.util.Objects;

import static br.com.fluentvalidator.predicate.StringPredicate.stringSizeGreaterThan;
import static br.com.fluentvalidator.predicate.StringPredicate.stringSizeLessThan;
import static com.github.ffremont.astrotheque.core.Validator.check;

@Builder(toBuilder = true)
public record Observation(
        String id,
        String location,
        Weather weather,
        String instrument,

        PlanetSatellite planetSatellite,

        List<File> files,
        @Deprecated
        List<FitData> fits,
        @Deprecated
        List<File> previews) {

    public Observation {
        check(
                Validator.Condition.of(stringSizeLessThan(256).and(stringSizeGreaterThan(2)), location, "location invalid"),
                Validator.Condition.of(Objects::nonNull, weather, "weather invalid")
        );
    }
}
