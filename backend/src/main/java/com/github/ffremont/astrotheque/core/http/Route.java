package com.github.ffremont.astrotheque.core.http;

import com.sun.net.httpserver.HttpExchange;

import java.util.function.Predicate;

public interface Route  extends Predicate<HttpExchange> {
     void handle(HttpExchange exchange);
}
