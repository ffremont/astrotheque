package com.github.ffremont.astrotheque.core.httpserver.context;

import com.github.ffremont.astrotheque.core.exception.InvalidLoginExeption;
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
            routes.stream().filter(jsonRoute -> jsonRoute.test(exchange))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(exchange.getRequestURI(), "NotFound in error"))
                    .handle(exchange);
        } catch (NotFoundException nfe) {
            log.debug("Ignore not found {}, ", nfe.getUri(), nfe);
            exchange.sendResponseHeaders(404, 0);
        } catch (InvalidLoginExeption ile) {
            log.warn("Login invalide", ile);
            exchange.sendResponseHeaders(401, 0);
        } catch (Exception e) {
            log.error("Server error occurs", e);
            exchange.sendResponseHeaders(500, 0);
        } finally {
            exchange.close();
        }
    }
}
