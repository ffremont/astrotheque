package com.github.ffremont.astrotheque.core.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.isNull;

public class JsonRoute implements  Route {

    private static final ObjectMapper JSON = new  ObjectMapper();

    private final Method method;
    private final String path;
    private final Function<HttpExchange, Object> jsonHandler;

    public JsonRoute(Method method, String path, Function<HttpExchange, Object> jsonHandler) {
        this.method = method;
        this.path = path;
        this.jsonHandler = jsonHandler;
    }

    @Override
    public boolean test(HttpExchange exchange) {
        return exchange.getRequestMethod().equalsIgnoreCase(method.name()) &&
                exchange.getRequestURI().getPath().endsWith(path);
    }

    public static JsonRoute get(String path, Function<HttpExchange, Object> jsonHandler){
        return new JsonRoute(Method.GET, path, jsonHandler);
    }
    public static JsonRoute post(String path, Function<HttpExchange, Object> jsonHandler){
        return new JsonRoute(Method.POST, path, jsonHandler);
    }
    public static JsonRoute put(String path, Function<HttpExchange, Object> jsonHandler){
        return new JsonRoute(Method.PUT, path, jsonHandler);
    }
    public static JsonRoute delete(String path, Function<HttpExchange, Object> jsonHandler){
        return new JsonRoute(Method.DELETE, path, jsonHandler);
    }

    public enum Method{
        GET, POST, PUT, DELETE;
    }

    public void handle(HttpExchange exchange){
        try {
            var result = jsonHandler.apply(exchange);
            if (isNull(result)) {
                exchange.sendResponseHeaders(204, 0);
            } else {
                String jsonResponse = JSON.writeValueAsString(result);

                OutputStream outputStream = exchange.getResponseBody();
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonResponse.length());
                outputStream.write(jsonResponse.getBytes());
                outputStream.flush();
                outputStream.close();
            }
            exchange.close();
        } catch (IOException e) {
            throw new RuntimeException("Json process error",e);
        }
    }


}
