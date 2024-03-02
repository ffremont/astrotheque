package com.github.ffremont.astrotheque.web;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.service.ObservationService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class ObservationResource implements HttpHandler {

    private final ObservationService observationService;

    public ObservationResource(IoC ioC) {
        this.observationService = ioC.get(ObservationService.class);
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {

        return observationService.importObservation(exchange.getPrincipal().getUsername(), obs);
    }
}
