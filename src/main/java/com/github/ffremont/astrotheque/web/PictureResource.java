package com.github.ffremont.astrotheque.web;

import com.github.ffremont.astrotheque.core.httpserver.route.HttpExchangeWrapper;
import com.github.ffremont.astrotheque.service.model.Picture;
import com.github.ffremont.astrotheque.web.model.WebTag;

import java.util.List;


public class PictureResource {

    public String get(HttpExchangeWrapper ex) {
        return "picutr";
    }

    /**
     * Tous les tags
     *
     * @return
     */
    public List<WebTag> tags() {
        return null;
    }

    public List<Picture> all(HttpExchangeWrapper exchangeWrapper) {
        return null;
    }

    public Picture delete(HttpExchangeWrapper wrapper) {
        String id = wrapper.pathParams().stream().findFirst().orElseThrow();
        return null;
    }

    public Picture update(HttpExchangeWrapper wrapper) {
        Picture body = (Picture) wrapper.body();
        return null;
    }


}
