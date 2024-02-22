package com.github.ffremont.astrotheque.core;

import com.github.ffremont.astrotheque.core.exception.ViolationDataException;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
public class Validator {

    private Validator() {

    }

    public static void check(Condition<?>... conditions) {
        Stream.of(conditions).forEach(Condition::verify);
    }


    public record Condition<T>(Predicate<T> predicate, T value, String description) {
        public void verify() {
            if (!predicate.test(value)) {
                throw new ViolationDataException(description);
            }
        }

        public static <T> Condition<T> of(Predicate<T> predicate, T value, String description) {
            return new Condition<>(predicate, value, description);
        }
    }
}
