package com.github.ffremont.astrotheque.core.httpserver.context;

import com.github.ffremont.astrotheque.core.exception.NotFoundException;
import com.github.ffremont.astrotheque.core.httpserver.route.Route;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Slf4j
public class SimpleContext implements HttpHandler {

    List<Route> routes;

    private SimpleContext(List<Route> routes) {
        this.routes = routes;
    }

    public static SimpleContext with(Route... routes) {
        return new SimpleContext(Arrays.asList(routes));
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            // Pattern.compile("^[A-Za-z0-9/]*/pictures/raw/(\w+)$").matcher(exchange.getRequestURI().getPath()).matches
            routes.stream().filter(jsonRoute -> jsonRoute.test(exchange))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(exchange.getRequestURI(), "NotFound in error"))
                    .handle(exchange);
        } catch (NotFoundException nfe) {
            log.debug("Ignore not found", nfe.getUri());
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
        } catch (Exception e) {
            log.error("Server error occurs", e);
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
        }
    }
}
