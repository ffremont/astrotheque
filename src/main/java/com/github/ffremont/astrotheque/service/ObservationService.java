package com.github.ffremont.astrotheque.service;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.dao.PictureDAO;
import com.github.ffremont.astrotheque.service.model.FitData;
import com.github.ffremont.astrotheque.service.model.Picture;
import com.github.ffremont.astrotheque.service.model.PictureState;
import com.github.ffremont.astrotheque.web.model.Observation;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ObservationService {

    private final PictureDAO pictureDAO;

    private final FitMapper fitMapper = new FitMapper();
    private static final ExecutorService FIT_IMPORT_THREAD_POOL = Executors.newFixedThreadPool(1);
    private final IoC ioc;
    private final PictureService pictureService;
    private final MoonService moonService;


    public ObservationService(IoC ioC) {
        this.pictureDAO = ioC.get(PictureDAO.class);
        this.pictureService = ioC.get(PictureService.class);
        this.moonService = ioC.get(MoonService.class);
        this.ioc = ioC;
    }

    public List<FitData> extractFitData(String accountName, List<Map.Entry<String, Path>> paths) {
        return paths.stream()
                .filter(file -> file.getValue().getFileName().toString().endsWith(".fit"))
                .map(fitMapper)
                .filter(fit -> !pictureDAO.has(accountName, fit.getHash()))
                .toList();
    }

    /**
     * Mémorise en live l'image sans la version image annotée
     *
     * @param accountName
     * @param observation
     * @return
     */
    public Observation newPlanetSatelliteObservation(String accountName, Observation observation) {
        log.info("{} / 🔭 Nouvelle observation planète ou satellite", accountName);

        var fit = observation.fits().stream().findFirst().orElseThrow();
        var preview = observation.previews().stream().findFirst().orElseThrow();

        var thumbnail = new ByteArrayOutputStream();
        try {
            Thumbnails.of(preview.tempFile().toFile())
                    .size(512, 512)
                    .outputFormat("jpg")
                    .toOutputStream(thumbnail);
            Picture picture = Picture.builder()
                    .id(UUID.randomUUID().toString())
                    .planetSatellite(observation.planetSatellite())
                    .name(observation.planetSatellite().getLabel())
                    .state(PictureState.DONE)
                    .filename(fit.getFilename())
                    .imported(LocalDateTime.now())
                    .camera(fit.getInstrume())
                    .gain(fit.getGain())
                    .instrument(observation.instrument())
                    .tags(List.of(observation.planetSatellite().name()))
                    .hash(fit.getHash())
                    .state(PictureState.DONE)
                    .moonPhase(moonService.phaseOf(fit.getDateObs().toLocalDate()))
                    .dateObs(fit.getDateObs())
                    .exposure(fit.getExposure())
                    .weather(observation.weather())
                    .location(observation.location())
                    .type(observation.planetSatellite().getType())
                    .stackCnt(fit.getStackCnt())
                    .build();

            pictureService.save(accountName, picture, Files.newInputStream(preview.tempFile()),
                    new ByteArrayInputStream(thumbnail.toByteArray()),
                    Files.newInputStream(fit.getTempFile()),
                    null
            );

            log.info("{} / 🔭✅ importation planète ou satellite", accountName);
        } catch (IOException e) {
            log.error("{} / 🔭❌ importation planète ou satellite", accountName, e);
            throw new RuntimeException(accountName + " / thumb génération impossible", e);
        }

        return observation;
    }


    public Observation newDsoObservation(String accountName, Observation obs) {
        Observation newObs = obs.toBuilder().id(UUID.randomUUID().toString()).build();

        // persist ids
        pictureDAO.allocate(accountName, newObs);

        //background
        FIT_IMPORT_THREAD_POOL.submit(new FitImporter(
                ioc,
                newObs,
                ioc.get(ConfigService.class).getConfiguration().astrometryNovaApikey(),
                accountName));

        return newObs;
    }
}
