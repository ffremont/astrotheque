package com.github.ffremont.astrotheque.service
        ;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.dao.AstrometryDAO;

public class AstrometryService {
    private final AstrometryDAO astrometryDAO;

    public AstrometryService(AstrometryDAO astrometryDAO) {
        this.astrometryDAO = astrometryDAO;
    }

    public AstrometryService(IoC ioc){
        this.astrometryDAO = ioc.get(AstrometryDAO.class);
    }

    public String hello(){
        return "oo";
    }
}
