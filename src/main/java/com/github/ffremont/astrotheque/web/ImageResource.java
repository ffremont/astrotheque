package com.github.ffremont.astrotheque.web;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.httpserver.route.HttpExchangeWrapper;
import com.github.ffremont.astrotheque.core.httpserver.route.Stream;
import com.github.ffremont.astrotheque.dao.PictureDAO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageResource {

    private final PictureDAO dao;

    public ImageResource(IoC ioC) {
        this.dao = ioC.get(PictureDAO.class);
    }

    public Stream raw(HttpExchangeWrapper wrapper) {
        String id = wrapper.pathParams().stream().findFirst().orElseThrow();
        try {
            Path raw = dao.getBin(wrapper.httpExchange().getPrincipal().getUsername(), id, PictureDAO.RAW_FILENAME);
            return new Stream(Files.newInputStream(raw), raw.toFile().length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Stream thumb(HttpExchangeWrapper wrapper) {
        String id = wrapper.pathParams().stream().findFirst().orElseThrow();
        try {
            Path thumb = dao.getBin(wrapper.httpExchange().getPrincipal().getUsername(), id, PictureDAO.THUMB_FILENAME);
            return new Stream(Files.newInputStream(thumb), thumb.toFile().length());
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
            Path img = dao.getBin(wrapper.httpExchange().getPrincipal().getUsername(), id, PictureDAO.PICTURE_FILENAME);
            return new Stream(Files.newInputStream(img), img.toFile().length());
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
            Path annotated = dao.getBin(wrapper.httpExchange().getPrincipal().getUsername(), id, PictureDAO.ANNOTATED_FILENAME);
            return new Stream(Files.newInputStream(annotated), annotated.toFile().length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
