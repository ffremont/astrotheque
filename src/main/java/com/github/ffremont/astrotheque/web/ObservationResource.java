package com.github.ffremont.astrotheque.web;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.httpserver.route.HttpExchangeWrapper;
import com.github.ffremont.astrotheque.service.PictureService;

public class ObservationResource {

    private final PictureService pictureService;

    public ObservationResource(IoC ioC) {
        this.pictureService = ioC.get(PictureService.class);
    }

    public String blackList(HttpExchangeWrapper wrapper) {
        String id = wrapper.pathParams().stream().findFirst().orElseThrow();

        this.pictureService.blackListObservationId(id);

        return null;
    }
}
