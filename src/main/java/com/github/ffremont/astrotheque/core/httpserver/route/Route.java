package com.github.ffremont.astrotheque.core.httpserver.route;

import com.sun.net.httpserver.HttpExchange;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface Route extends Predicate<HttpExchange> {
    void handle(HttpExchange exchange);

    Method method();

    Pattern pattern();

    default boolean test(HttpExchange exchange) {
        return exchange.getRequestMethod().equalsIgnoreCase(method().name()) &&
                pattern().matcher(exchange.getRequestURI().getPath()).matches();
    }


    enum Method {
        GET, POST, PUT, DELETE;
    }
}
