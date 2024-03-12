package com.github.ffremont.astrotheque.service.model;

import lombok.Builder;

import static br.com.fluentvalidator.predicate.PredicateBuilder.from;
import static br.com.fluentvalidator.predicate.StringPredicate.stringSizeGreaterThan;
import static br.com.fluentvalidator.predicate.StringPredicate.stringSizeLessThan;
import static com.github.ffremont.astrotheque.core.Validator.Condition.of;
import static com.github.ffremont.astrotheque.core.Validator.check;

@Builder(toBuilder = true)
public record Configuration(
        String baseurl,
        String astrometryNovaApikey,
        Admin admin
) {

    public Configuration {
        check(
                of(
                        from((String str) -> str.startsWith("http://"))
                                .or(
                                        from((String str) -> str.startsWith("https://"))
                                ), baseurl, "baseurl invalid"),
                of(stringSizeGreaterThan(10), astrometryNovaApikey, "astrometry nova apikey invalid")
        );
    }

    @Builder(toBuilder = true)
    public record Admin(String login, String pwd, String totp) {
        public Admin {
            check(
                    of(stringSizeLessThan(50).and(stringSizeGreaterThan(2)), login, "login invalid"),
                    of(stringSizeLessThan(256).and(stringSizeGreaterThan(4)), pwd, "pwd invalid")
            );
        }
    }
}
