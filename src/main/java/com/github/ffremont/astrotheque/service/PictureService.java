package com.github.ffremont.astrotheque.service;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.dao.PictureDAO;
import com.github.ffremont.astrotheque.service.model.Picture;
import com.github.ffremont.astrotheque.web.model.Observation;

import java.io.InputStream;
import java.util.List;

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

    /**
     * Black list une session d'observation
     *
     * @param obsId
     */
    public void blackListObservationId(String obsId) {
        dao.blackListObservationId(obsId);
    }

    /**
     * Retourne vrai si l'observation Id a été identifié comme "blacklisté"
     *
     * @param obsId
     * @return
     */
    public boolean isBlackListed(String obsId) {
        return dao.isBlackListed(obsId);
    }


    /**
     * Sauvegarde les images d'une observation afin de les mettre à jour ultérieurement
     *
     * @param owner
     * @param obs
     */
    public void allocate(String owner, Observation obs) {
        dao.allocate(owner, obs);
    }

    public boolean has(String owner, String hashOfRaw) {
        return dao.has(owner, hashOfRaw);
    }

    public void save(String owner, Picture picture, InputStream jpg, InputStream thumb, InputStream raw, InputStream astrometryRaw, InputStream annotated) {
        dao.save(owner, picture, jpg, thumb, raw, astrometryRaw, annotated);
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
