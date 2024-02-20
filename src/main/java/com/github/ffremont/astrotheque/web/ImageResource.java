package com.github.ffremont.astrotheque.web;

import com.github.ffremont.astrotheque.core.httpserver.route.HttpExchangeWrapper;
import com.github.ffremont.astrotheque.core.httpserver.route.Stream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageResource {
    public Stream raw(HttpExchangeWrapper wrapper) {
        String id = wrapper.pathParams().stream().findFirst().orElseThrow();
        try {
            return new Stream(Files.newInputStream(Paths.get("./Dockerfile")), Paths.get("./Dockerfile").toFile().length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Stream thumb(HttpExchangeWrapper wrapper) {
        String id = wrapper.pathParams().stream().findFirst().orElseThrow();
        try {
            return new Stream(Files.newInputStream(Paths.get("./Dockerfile")), Paths.get("./Dockerfile").toFile().length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Image jpg
     *
     * @param wrapper
     * @return
     */
    public Stream image(HttpExchangeWrapper wrapper) {
        String id = wrapper.pathParams().stream().findFirst().orElseThrow();
        try {
            return new Stream(Files.newInputStream(Paths.get("./Dockerfile")), Paths.get("./Dockerfile").toFile().length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Image annotée avec les cibles
     *
     * @param wrapper
     * @return
     */
    public Stream annotated(HttpExchangeWrapper wrapper) {
        String id = wrapper.pathParams().stream().findFirst().orElseThrow();
        try {
            return new Stream(Files.newInputStream(Paths.get("./Dockerfile")), Paths.get("./Dockerfile").toFile().length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
