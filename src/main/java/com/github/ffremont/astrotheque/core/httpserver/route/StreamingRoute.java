package com.github.ffremont.astrotheque.core.httpserver.route;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.function.Function;
import java.util.regex.Pattern;

public class StreamingRoute implements Route {

    private final Method method;
    private final Function<HttpExchangeWrapper, Stream> streamHandler;
    private final Pattern pattern;

    private String mimeType;

    private StreamingRoute(Method method, Function<HttpExchangeWrapper, Stream> streamHandler, String path, String mimeType) {
        this.method = method;
        this.streamHandler = streamHandler;
        this.pattern = Pattern.compile("^[A-Za-z0-9/]*" + path);
        this.mimeType = mimeType;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            Stream stream = streamHandler.apply(WrapperFactory.builder().pattern(pattern).build().apply(exchange));
            exchange.getResponseHeaders().add("Content-Type", mimeType);
            exchange.sendResponseHeaders(200, stream.size());

            stream.inputStream().transferTo(exchange.getResponseBody());
            exchange.getResponseBody().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
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


    public static StreamingRoute get(String path, Function<HttpExchangeWrapper, Stream> streamHandler, String mimeType) {
        return new StreamingRoute(Method.GET, streamHandler, path, mimeType);
    }

}
