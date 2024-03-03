package com.github.ffremont.astrotheque.web.model;


import com.github.ffremont.astrotheque.core.Validator;
import com.github.ffremont.astrotheque.service.model.FitData;
import com.github.ffremont.astrotheque.service.model.Weather;
import lombok.Builder;

import java.nio.file.Path;
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
        String corrred,
        List<FitData> fits,
        List<Path> previews) {

    public Observation {
        check(
                Validator.Condition.of(stringSizeLessThan(256).and(stringSizeGreaterThan(2)), location, "location invalid"),
                Validator.Condition.of(Objects::nonNull, weather, "weather invalid")
        );
    }
}
