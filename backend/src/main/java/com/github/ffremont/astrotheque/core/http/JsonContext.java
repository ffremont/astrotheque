package com.github.ffremont.astrotheque.core.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Slf4j
public class JsonContext implements HttpHandler {

    List<Route> routes;

    private JsonContext(List<Route> routes) {
        this.routes = routes;
    }

    public static JsonContext with(Route... routes) {
        return new JsonContext(Arrays.asList(routes));
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            routes.stream().filter(jsonRoute -> jsonRoute.test(exchange))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(exchange.getRequestURI(), "NotFound in error"))
                    .handle(exchange);
        } catch (NotFoundException nfe) {
            log.debug("Ignore not found", nfe.getUri());
            exchange.sendResponseHeaders(404, 0);
        } catch (Exception e) {
            log.error("Server error occurs", e);
            exchange.sendResponseHeaders(500, 0);
        }
    }
}
