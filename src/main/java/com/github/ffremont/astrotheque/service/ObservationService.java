package com.github.ffremont.astrotheque.service;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.service.model.File;
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
     * Extrait des fichiers .FIT les méta données
     *
     * @param accountName
     * @param paths
     * @return
     */
    public List<FitData> extractFitData(String accountName, List<Map.Entry<String, Path>> paths) {
        return paths.stream()
                .filter(file -> file.getValue().getFileName().toString().endsWith(".fit"))
                .map(fitMapper)
                .filter(fit -> !pictureService.has(accountName, fit.getHash()))
                .toList();
    }

    /**
     * Mémorise en live l'image sans la version image annotée
     *
     * @param accountName
     * @param observation
     */
    public void newPlanetSatelliteObservation(String accountName, Observation observation) {
        log.info("{} / 🔭 Nouvelle observation planète ou satellite", accountName);

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
                Thumbnails.of(image.tempFile().toFile())
                        .size(512, 512)
                        .outputFormat("jpg")
                        .toOutputStream(thumbnail);
                Picture picture = Picture.builder()
                        .id(UUID.randomUUID().toString())
                        .planetSatellite(observation.planetSatellite())
                        .name(observation.planetSatellite().getLabel())
                        .state(PictureState.DONE)
                        .filename(file.filename())
                        .imported(LocalDateTime.now())
                        .instrument(observation.instrument())
                        .tags(List.of(observation.planetSatellite().name()))
                        .state(PictureState.DONE)
                        .weather(observation.weather())
                        .location(observation.location())
                        .type(observation.planetSatellite().getType())
                        .stackCnt(1)
                        .build();

                pictureService.save(accountName, picture, Files.newInputStream(image.tempFile()),
                        new ByteArrayInputStream(thumbnail.toByteArray()),
                        Files.newInputStream(fit.tempFile()),
                        null
                );

                log.info("{} / 🔭✅ importation planète ou satellite", accountName);
            } catch (IOException e) {
                log.error("{} / 🔭❌ importation planète ou satellite", accountName, e);
                throw new RuntimeException(accountName + " / thumb génération impossible", e);
            }
        }
    }


    public void newDsoObservation(String accountName, Observation obs) {
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
