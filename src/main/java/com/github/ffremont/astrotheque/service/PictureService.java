package com.github.ffremont.astrotheque.service;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.dao.PictureDAO;
import com.github.ffremont.astrotheque.service.model.Picture;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class PictureService {

    private final PictureDAO dao;

    public PictureService(IoC ioC) {
        this.dao = ioC.get(PictureDAO.class);
    }


    public void load(String accountName) {
        dao.load(accountName);
    }

    public List<Picture> getAll(String accountName) {
        return dao.getAll(accountName).toList();
    }

    public Picture get(String accountName, String id) {
        return dao.getById(accountName, id);
    }

    public void cancel(String accountName, String id) {
        dao.cancel(accountName, id);
    }

    public Picture delete(String accountName, String id) {
        Picture picture = dao.getById(accountName, id);
        dao.remove(accountName, id);

        return picture;
    }

    public Picture add(String accountName, Picture picture) {
        var newPicture = picture.toBuilder()
                .id(UUID.randomUUID().toString())
                .build();
        dao.refresh(accountName, newPicture);
        return newPicture;
    }

    public void save(String owner, Picture picture, InputStream jpg, InputStream thumb, InputStream raw, InputStream annotated) {
        dao.save(owner, picture, jpg, thumb, raw, annotated);
    }

    public Picture update(String accountName, Picture picture) {
        var originalPicture = dao.getById(accountName, picture.getId());

        var newPicture = originalPicture.toBuilder()
                .name(picture.getName())
                .type(picture.getType())
                .dateObs(picture.getDateObs())
                .constellation(picture.getConstellation())
                .camera(picture.getCamera())
                .planetSatellite(picture.getPlanetSatellite())
                .instrument(picture.getInstrument())
                .tags(picture.getTags())
                .note(picture.getNote())
                .location(picture.getLocation())
                .moonPhase(picture.getMoonPhase())
                .gain(picture.getGain())
                .exposure(picture.getExposure())
                .stackCnt(picture.getStackCnt()).build();
        dao.refresh(accountName, newPicture);
        return newPicture;
    }
}
