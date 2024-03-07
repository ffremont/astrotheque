package com.github.ffremont.astrotheque.web;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.httpserver.route.HttpExchangeWrapper;
import com.github.ffremont.astrotheque.service.PictureService;
import com.github.ffremont.astrotheque.service.model.Picture;
import com.github.ffremont.astrotheque.web.model.WebTag;

import java.util.List;


public class PictureResource {

    private final PictureService service;

    public PictureResource(IoC ioC) {
        this.service = ioC.get(PictureService.class);
    }


    /**
     * Tous les tags
     *
     * @return
     */
    public List<WebTag> tags() {
        return null;
    }

    /**
     * @param exchangeWrapper
     * @return
     */
    public List<Picture> all(HttpExchangeWrapper exchangeWrapper) {
        return service.getAll(exchangeWrapper.httpExchange().getPrincipal().getUsername());
    }

    public Picture get(HttpExchangeWrapper exchangeWrapper) {
        String id = exchangeWrapper.pathParams().stream().findFirst().orElseThrow();
        return service.get(exchangeWrapper.httpExchange().getPrincipal().getUsername(), id);
    }

    /**
     * @param exchangeWrapper
     * @return
     */
    public Picture delete(HttpExchangeWrapper exchangeWrapper) {
        String id = exchangeWrapper.pathParams().stream().findFirst().orElseThrow();
        return service.delete(exchangeWrapper.httpExchange().getPrincipal().getUsername(), id);
    }

    /**
     * @param exchangeWrapper
     * @return
     */
    public Picture update(HttpExchangeWrapper exchangeWrapper) {
        Picture body = (Picture) exchangeWrapper.body();
        return service.update(exchangeWrapper.httpExchange().getPrincipal().getUsername(), body);
    }


}
