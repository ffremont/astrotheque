package com.github.ffremont.astrotheque.web;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.httpserver.route.HttpExchangeWrapper;
import com.github.ffremont.astrotheque.service.PictureService;
import com.github.ffremont.astrotheque.service.model.Picture;
import com.github.ffremont.astrotheque.service.model.PictureState;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ObservationResource {

    private final PictureService pictureService;

    public ObservationResource(IoC ioC) {
        this.pictureService = ioC.get(PictureService.class);
    }


    public String cancelAll(HttpExchangeWrapper wrapper) {
        String owner = wrapper.httpExchange().getPrincipal().getUsername();
        List<Picture> pendingPictures = this.pictureService.getAll(owner).stream()
                .filter(p -> PictureState.PENDING.equals(p.getState())).toList();

        pendingPictures.stream()
                .map(Picture::getObservationId)
                .collect(Collectors.toSet())
                .forEach(this.pictureService::blackListObservationId);

        new HashSet<>(pendingPictures)
                .forEach(picture -> this.pictureService.cancel(owner, picture.getId()));
        
        return "okay";
    }
}
