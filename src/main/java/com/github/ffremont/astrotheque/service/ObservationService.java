package com.github.ffremont.astrotheque.service;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.service.model.*;
import com.github.ffremont.astrotheque.web.model.Observation;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.ffremont.astrotheque.service.utils.FileUtils.isFit;
import static com.github.ffremont.astrotheque.service.utils.FileUtils.isImage;

@Slf4j
public class ObservationService {


    private final FitMapper fitMapper = new FitMapper();
    private static final ExecutorService FIT_IMPORT_THREAD_POOL = Executors.newFixedThreadPool(1);
    private final IoC ioc;
    private final PictureService pictureService;
    private final MoonService moonService;


    public ObservationService(IoC ioC) {
        this.pictureService = ioC.get(PictureService.class);
        this.moonService = ioC.get(MoonService.class);
        this.ioc = ioC;
    }


    /**
     * Mémorise en live l'image sans la version image annotée
     *
     * @param accountName
     * @param observation
     */
    public void newDirectObservation(String accountName, Observation observation) {
        log.info("{} / 🔭 Nouvelle observation ", accountName);

        for (File file : observation.files()) {
            // il faut une image et un fit
            var image = Optional.of(file)
                    .filter(f -> isImage.test(f.filename()))
                    .or(() -> Optional.ofNullable(file.relatedTo())).orElseThrow();
            var fit = Optional.of(file)
                    .filter(f -> isFit.test(f.filename()))
                    .or(() -> Optional.ofNullable(file.relatedTo())).orElseThrow();

            var thumbnail = new ByteArrayOutputStream();
            try {
                var planetSatellite = Optional.ofNullable(observation.planetSatellite());
                Thumbnails.of(image.tempFile().toFile())
                        .size(512, 512)
                        .outputFormat("jpg")
                        .toOutputStream(thumbnail);
                Picture picture = Picture.builder()
                        .id(UUID.randomUUID().toString())
                        .planetSatellite(observation.planetSatellite())
                        .name(planetSatellite.map(PlanetSatellite::getLabel).orElse("inconnu"))
                        .state(PictureState.DONE)
                        .filename(file.filename())
                        .imported(LocalDateTime.now())
                        .instrument(observation.instrument())
                        .tags(planetSatellite.isPresent() ? List.of(observation.planetSatellite().name()) : Collections.emptyList())
                        .state(PictureState.DONE)
                        .weather(observation.weather())
                        .location(observation.location())
                        .type(planetSatellite.map(PlanetSatellite::getType).orElse(Type.OTHER))
                        .stackCnt(1)
                        .build();

                pictureService.save(accountName, picture, Files.newInputStream(image.tempFile()),
                        new ByteArrayInputStream(thumbnail.toByteArray()),
                        Files.newInputStream(fit.tempFile()),
                        null
                );

                log.info("{} / 🔭✅ importation direct", accountName);
            } catch (IOException e) {
                log.error("{} / 🔭❌ importation direct", accountName, e);
                throw new RuntimeException(accountName + " / thumb génération impossible", e);
            }
        }
    }


    public void newDsoObservationWithAstrometry(String accountName, Observation obs) {
        Observation newObs = obs.toBuilder().id(UUID.randomUUID().toString()).build();

        // persist ids
        pictureService.allocate(accountName, newObs);

        //background
        FIT_IMPORT_THREAD_POOL.submit(new FitImporter(
                ioc,
                newObs,
                ioc.get(ConfigService.class).getConfiguration().astrometryNovaApikey(),
                accountName));

    }
}
