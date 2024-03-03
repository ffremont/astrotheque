package com.github.ffremont.astrotheque.service;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.dao.PictureDAO;
import com.github.ffremont.astrotheque.service.model.FitData;
import com.github.ffremont.astrotheque.web.model.Observation;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ObservationService {

    private final PictureDAO pictureDAO;

    private final FitMapper fitMapper = new FitMapper();
    private static final ExecutorService FIT_IMPORT_THREAD_POOL = Executors.newFixedThreadPool(1);
    private final IoC ioc;


    public ObservationService(PictureDAO pictureDAO, IoC ioC) {
        this.pictureDAO = pictureDAO;
        this.ioc = ioC;
    }

    public ObservationService(IoC ioC) {
        this.pictureDAO = ioC.get(PictureDAO.class);
        this.ioc = ioC;
    }

    public List<FitData> extractFitData(String accountName, List<Path> paths) {
        return paths.stream()
                .filter(file -> file.getFileName().toString().endsWith(".fit"))
                .map(fitMapper)
                .filter(fit -> !pictureDAO.has(accountName, fit.getHash()))
                .toList();
    }


    public Observation importObservation(String accountName, Observation newObs) {
        final var obsId = UUID.randomUUID().toString();

        // persist ids
        pictureDAO.allocate(accountName, newObs.fits().stream().map(FitData::getId).toList(), newObs);

        //background
        FIT_IMPORT_THREAD_POOL.submit(new FitImporter(
                ioc,
                newObs,
                ioc.get(InstallService.class).getConfiguration().astrometryNovaApikey(),
                accountName));

        return newObs;
    }
}
