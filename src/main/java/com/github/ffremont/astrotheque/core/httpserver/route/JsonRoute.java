package com.github.ffremont.astrotheque.core.httpserver.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

public class JsonRoute implements Route {

    private static final ObjectMapper JSON = new ObjectMapper();


    private final Method method;
    private final Function<HttpExchangeWrapper, Object> jsonHandler;
    private final Pattern pattern;
    private final Class bodyClass;

    public JsonRoute(Method method, String path, Function<HttpExchangeWrapper, Object> jsonHandler) {
        this(method, path, jsonHandler, null);
    }

    public JsonRoute(Method method, String path, Function<HttpExchangeWrapper, Object> jsonHandler, Class bodyClass) {
        this.method = method;
        this.jsonHandler = jsonHandler;
        this.pattern = Pattern.compile("^[A-Za-z0-9/]*" + path);
        this.bodyClass = bodyClass;
    }


    public static JsonRoute get(String path, Function<HttpExchangeWrapper, Object> jsonHandler) {
        return new JsonRoute(Method.GET, path, jsonHandler);
    }

    public static JsonRoute post(String path, Function<HttpExchangeWrapper, Object> jsonHandler, Class bodyClass) {
        return new JsonRoute(Method.POST, path, jsonHandler, bodyClass);
    }

    public static JsonRoute put(String path, Function<HttpExchangeWrapper, Object> jsonHandler, Class bodyClass) {
        return new JsonRoute(Method.PUT, path, jsonHandler);
    }

    public static JsonRoute delete(String path, Function<HttpExchangeWrapper, Object> jsonHandler) {
        return new JsonRoute(Method.DELETE, path, jsonHandler);
    }


    public void handle(HttpExchange exchange) {
        try (exchange) {
            var result = jsonHandler.apply(WrapperFactory.builder().json(JSON).bodyClass(bodyClass).pattern(pattern).build().apply(exchange));
            if (isNull(result)) {
                exchange.sendResponseHeaders(204, 0);
            } else {
                String jsonResponse = JSON.writeValueAsString(result);

                OutputStream outputStream = exchange.getResponseBody();
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonResponse.length());
                outputStream.write(jsonResponse.getBytes());
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("Json process error", e);
        }
    }

    @Override
    public Method method() {
        return method;
    }

    @Override
    public Pattern pattern() {
        return pattern;
    }


}
