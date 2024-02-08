package com.github.ffremont.astrotheque.web;

import com.sun.net.httpserver.HttpExchange;


public class PictureResource {


    public String hello(HttpExchange ex) {
        return "picutr";
    }
}
