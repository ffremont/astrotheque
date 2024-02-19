package com.github.ffremont.astrotheque.web;

import com.github.ffremont.astrotheque.service.model.Picture;
import com.github.ffremont.astrotheque.web.model.WebTag;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;


public class PictureResource {


    public String hello(HttpExchange ex) {
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

    public List<Picture> all() {
        return null;
    }
}
