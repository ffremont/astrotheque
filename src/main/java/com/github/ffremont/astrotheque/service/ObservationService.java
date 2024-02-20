package com.github.ffremont.astrotheque.service;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.security.SecurityContextHolder;
import com.github.ffremont.astrotheque.core.security.User;
import com.github.ffremont.astrotheque.dao.PictureDAO;
import com.github.ffremont.astrotheque.service.model.FitData;
import com.github.ffremont.astrotheque.service.utils.FitUtils;
import com.github.ffremont.astrotheque.web.model.Observation;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_224;

public class ObservationService {

    private final PictureDAO pictureDAO;
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


    public Observation importObservation(Observation observation) {
        final User user = SecurityContextHolder.getUser();
        final var obsId = UUID.randomUUID().toString();

        var fits = Arrays.asList(observation.getTargets().toLowerCase().split(","))
                .stream().map(target -> {
                    try {
                        var newTarget = target.trim();
                        return Files.list(Paths.get(observation.getPath()))
                                .filter(file -> file.getFileName().toString().endsWith(".fit"))
                                .filter(fileName -> fileName.getFileName().toString().toLowerCase().contains(newTarget))
                                .map(FitUtils::analyze)
                                .map(fit -> {
                                    try {
                                        return fit.toBuilder()
                                                .id(UUID.randomUUID().toString())
                                                .hash(new DigestUtils(SHA_224).digestAsHex(fit.getPath().toFile())).build();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                                .filter(fit -> !pictureDAO.has(user.sub(), fit.getHash()))
                                .max(Comparator.comparing(FitData::getStackCnt));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).filter(Optional::isPresent).map(Optional::get).toList();

        var newObs = observation.toBuilder().id(obsId).fits(fits.stream().map(FitData::getId).toList()).build();

        // persist ids
        pictureDAO.allocate(user.sub(), fits.stream().map(f -> f.getId()).toList(), newObs);

        //background
        FIT_IMPORT_THREAD_POOL.submit(new FitImporter(
                ioc,
                observation,
                fits,
                ioc.get(DynamicProperties.class).getAstrometryNovaApikey(),
                user.sub()));

        return newObs;
    }
}
