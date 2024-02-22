package com.github.ffremont.astrotheque.service;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.dao.PictureDAO;
import com.github.ffremont.astrotheque.service.model.Picture;

import java.util.List;

public class PictureService {

    private final PictureDAO dao;

    public PictureService(IoC ioC) {
        this.dao = ioC.get(PictureDAO.class);
    }

    public List<Picture> getAll(String accountName) {
        return dao.getAll(accountName).toList();
    }

    public Picture delete(String accountName, String id) {
        Picture picture = dao.getById(accountName, id);
        dao.remove(accountName, id);

        return picture;
    }

    public Picture update(String accountName, Picture newPicture) {
        dao.refresh(accountName, newPicture);
        return newPicture;
    }
}
