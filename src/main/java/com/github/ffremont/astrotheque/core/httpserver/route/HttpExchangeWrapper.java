package com.github.ffremont.astrotheque.core.httpserver.route;

import com.sun.net.httpserver.HttpExchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record HttpExchangeWrapper(
        HttpExchange httpExchange,
        List<String> pathParams,
        Map<String, String> queryParams,
        Object body
) {

    static HttpExchangeWrapper from(HttpExchange httpExchange, Map<String, String> queryParams, Object body) {
        return new HttpExchangeWrapper(httpExchange, new ArrayList<>(), queryParams, body);
    }
}
