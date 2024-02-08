package com.github.ffremont.astrotheque.core.http;

import com.sun.net.httpserver.HttpExchange;

public interface Route {
     void handle(HttpExchange exchange);
}
