package com.github.ffremont.astrotheque.web.model;

import com.github.ffremont.astrotheque.core.Validator.Condition;

import static br.com.fluentvalidator.predicate.StringPredicate.stringSizeGreaterThan;
import static br.com.fluentvalidator.predicate.StringPredicate.stringSizeLessThan;
import static com.github.ffremont.astrotheque.core.Validator.check;

public record LoginRequest(String login, String pwd) {
    public LoginRequest {
        check(
                Condition.of(stringSizeLessThan(50).and(stringSizeGreaterThan(2)), login, "login invalid"),
                Condition.of(stringSizeLessThan(256).and(stringSizeGreaterThan(4)), pwd, "pwd invalid")
        );
    }
}
