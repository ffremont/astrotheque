package com.github.ffremont.astrotheque.core.httpserver.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import lombok.Builder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Builder
public class WrapperFactory implements Function<HttpExchange, HttpExchangeWrapper> {

    private static final Pattern QUERY_PATTERN = Pattern.compile("\\s*&\\s*");

    private ObjectMapper json;
    private Class bodyClass;
    private Pattern pattern;


    @Override
    public HttpExchangeWrapper apply(HttpExchange httpExchange) {
        try {
            Object body = null;
            if (Objects.nonNull(bodyClass) && Objects.nonNull(json)) {
                body = json.readValue(httpExchange.getRequestBody(), bodyClass);
            }

            Map<String, String> queryParams = QUERY_PATTERN
                    .splitAsStream(Optional.ofNullable(httpExchange.getRequestURI()).map(URI::getQuery).orElse("").trim())
                    .map(s -> s.split("=", 2))
                    .collect(Collectors.toMap(a -> a[0], a -> a.length > 1 ? a[1] : ""));
            HttpExchangeWrapper wrapper = HttpExchangeWrapper.from(httpExchange, queryParams, body);

            var matcher = pattern.matcher(httpExchange.getRequestURI().getPath());
            while (matcher.find()) {
                if (matcher.groupCount() > 0) {
                    wrapper.pathParams().add(matcher.group(1));
                }
            }

            return wrapper;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
