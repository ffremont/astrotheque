package com.github.ffremont.astrotheque.service
        ;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.dao.AstrometryDAO;
import com.github.ffremont.astrotheque.service.model.*;
import com.github.ffremont.astrotheque.service.utils.PngToJpegUtils;
import com.github.ffremont.astrotheque.web.model.Observation;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

import static com.github.ffremont.astrotheque.service.utils.FileUtils.*;

/**
 * Pour une observation donnée, importe les fits
 */
@Slf4j
public class FitImporter implements Runnable {
    /**
     * Temps entre chaque appel pour vérifier l'avancement du job
     */
    final int TEMPO_MS = 20000;

    private final FitMapper fitMapper = new FitMapper();

    private final AstrometryDAO astrometryDAO;

    private final MoonService moonService;
    private final DeepSkyCatalogService deepSkyCatalogService;

    private final Observation observation;

    private final String astrometryNovaApikey;
    private final String owner;
    private final PictureService pictureService;

    public FitImporter(AstrometryDAO astrometryDAO, PictureService pictureService, MoonService moonService, DeepSkyCatalogService deepSkyCatalogService, Observation observation, String astrometryNovaApikey, String owner) {
        this.astrometryDAO = astrometryDAO;
        this.pictureService = pictureService;
        this.moonService = moonService;
        this.deepSkyCatalogService = deepSkyCatalogService;
        this.observation = observation;
        this.astrometryNovaApikey = astrometryNovaApikey;
        this.owner = owner;
    }

    public FitImporter(IoC ioC, Observation observation, String astrometryNovaApikey, String owner) {
        this(
                ioC.get(AstrometryDAO.class),
                ioC.get(PictureService.class),
                ioC.get(MoonService.class),
                ioC.get(DeepSkyCatalogService.class),
                observation, astrometryNovaApikey, owner
        );
    }


    /**
     * Vérifie que l'observation n'a pas été black listé
     *
     * @param obs
     */
    private void checkObservation(Observation obs) {
        if (pictureService.isBlackListed(observation.id())) {
            throw new RuntimeException("Observation on blacklist : " + observation.id());
        }
    }

    public void run() {
        log.info("{} / ⚙️ Importation {}", owner, observation.id());
        var sessionId = astrometryDAO.createLoginSession(astrometryNovaApikey);

        for (File file : observation.files()) {
            log.info("{} / ->️ Fichier {}", owner, file.filename());
            var counter = 0;
            String pictureId = null;
            Optional<File> fit = Optional.empty();
            Optional<File> image = Optional.empty();
            Optional<File> astrometryFit = Optional.empty();
            try {
                image = Optional.of(file)
                        .map(f -> isImage.test(f.filename()) ? f : f.relatedTo())
                        .filter(f -> isImage.test(f.filename()))
                        .map(f -> {
                            if (!isJpeg.test(f.filename())) {
                                try {
                                    Path jpg = (new PngToJpegUtils()).apply(f.tempFile());

                                    return f.toBuilder()
                                            .tempFile(jpg)
                                            .filename(f.filename().substring(0, f.filename().lastIndexOf(".")) + ".jpg").build();
                                } catch (Exception e) {
                                    log.warn("{} / Convertion JPG de l'image impossible", owner, e);
                                    return null;
                                }
                            } else {
                                return f;
                            }
                        })
                        .filter(Objects::nonNull)
                        .or(() -> Optional.ofNullable(file.relatedTo()));
                fit = Optional.of(file)
                        .filter(f -> isFit.test(f.filename()))
                        .or(() -> Optional.ofNullable(file.relatedTo()));
                astrometryFit = Optional.empty();

                pictureId = file.id();

                checkObservation(observation);

                if (Objects.isNull(sessionId)) {
                    throw new ImportProcessException("Session id nova astrometry null");
                }

                log.info("{} / uploading image...", owner);
                var submissionId = astrometryDAO.upload(sessionId, file.tempFile());

                log.info("{} / ✅ uploaded image with submission {}", owner, submissionId);
                if (Objects.isNull(submissionId)) {
                    throw new ImportProcessException("Submission id nova astrometry null");
                }
                while (counter < 60) {
                    log.info("{}/ waiting for {}", owner, pictureId);
                    Thread.sleep(TEMPO_MS);
                    counter++;
                    checkObservation(observation);

                    var subInfo = astrometryDAO.getSubInfo(submissionId);
                    log.info("{} / ✅ getting subInfo of {}", owner, submissionId);
                    var jobs = Optional.ofNullable(subInfo)
                            .map(si -> Optional.ofNullable(si.jobs()).orElse(Collections.emptyList()))
                            .orElse(Collections.emptyList());
                    Optional<Integer> jobId = !jobs.isEmpty() ? Optional.ofNullable(jobs.getFirst()) : Optional.empty();

                    if (jobId.isEmpty()) {
                        log.info("{} / ❌ job id not found", owner);
                        continue;
                    } else {
                        log.info("{} / ✅ job id found {}", owner, jobId.get());
                    }

                    var info = astrometryDAO.info(jobId.get());
                    if ("solving".equals(info.status())) {
                        continue;
                    } else if ("success".equals(info.status())) {
                        log.info("{} / ✅ getting info of job {}", owner, jobId.get());
                    } else {
                        log.info("{} / ❌ failed to get info of job {}, current status : {}", owner, jobId.get(), info.status());
                        throw new RuntimeException(" ❌ Job invalid status :" + info.status());
                    }

                    if (fit.isPresent()) {
                        log.info("{} / ⬇ download astrometry fit (new-fit)", owner);
                        Path newAstrometryFit = Files.createTempFile("astrotheque_astrometry_new_", ".fit");
                        var newFitTry = 0;
                        while (newFitTry < 10) {
                            newFitTry++;

                            try {
                                FileUtils.copyInputStreamToFile(astrometryDAO.getFit(jobId.get()), newAstrometryFit.toFile());
                                astrometryFit = Optional.of(new File(UUID.randomUUID().toString(), newAstrometryFit, newAstrometryFit.toFile().getName(), fit.get()));
                                break;
                            } catch (IOException io) {
                                log.warn("{} / ❌ téléchargement impossible du new-fit, retentative...", owner);
                                Thread.sleep(Duration.ofSeconds(20));
                            }
                        }

                        astrometryFit.ifPresent((a) -> log.info("{} / ✅ downloaded astrometry fit (new-fit)", owner));
                    }

                    var thumbnail = new ByteArrayOutputStream();

                    Integer astroNovaImage = Optional.ofNullable(subInfo.images()).flatMap(images -> images.stream().findFirst()).orElseThrow();
                    if (fit.isPresent() && image.isPresent()) {
                        log.info("{} / ⚙️building thumb from image {}", owner, image.get().filename());
                        Thumbnails.of(image.get().tempFile().toFile())
                                .size(1024, 1024)
                                .outputFormat("jpg")
                                .toOutputStream(thumbnail);
                    } else if (fit.isPresent()) {
                        log.info("{} / ⬇ download image to define preview...", owner);

                        Path novaImageFile = Files.createTempFile("astrotheque_", ".jpg");
                        FileUtils.copyInputStreamToFile(astrometryDAO.getImage(astroNovaImage), novaImageFile.toFile());

                        log.info("{} / ⚙️ Building thumb from nova image...", owner);
                        Thumbnails.of(Files.newInputStream(novaImageFile))
                                .size(1024, 1024)
                                .outputFormat("jpg")
                                .toOutputStream(thumbnail);
                        image = Optional.of(new File(UUID.randomUUID().toString(), novaImageFile, novaImageFile.toFile().getName(), null));
                        log.info("{} / ✅ image defined", owner);
                    } else if (image.isPresent()) {
                        log.info("{} / ⬇ download fit to define raw...", owner);

                        Path novaRawFile = Files.createTempFile("astrotheque_", ".fit");
                        FileUtils.copyInputStreamToFile(astrometryDAO.getFit(jobId.get()), novaRawFile.toFile());
                        fit = Optional.of(new File(UUID.randomUUID().toString(), novaRawFile, novaRawFile.toFile().getName(), image.get()));
                        log.info("{} / ✅ fit downloaded and defined", owner);

                        log.info("{} / ⚙️ Building thumb from image...", owner);
                        Thumbnails.of(Files.newInputStream(image.get().tempFile()))
                                .size(1024, 1024)
                                .outputFormat("jpg")
                                .toOutputStream(thumbnail);
                    }
                    log.info("{} / ✅ thumb built", owner);

                    FitData fitData = fitMapper.apply(Map.entry(fit.orElseThrow().filename(), fit.orElseThrow().tempFile()));

                    var tags = info.tags().stream().map(tag -> tag.replace(" ", "")).toList();
                    var celest = deepSkyCatalogService.findCelestObject(tags);
                    var picture = pictureService.get(owner, pictureId).toBuilder()
                            .ra(info.calibration().ra())
                            .name(celest.map(CelestObject::name).orElse(fitData.getObject()))
                            .dec(info.calibration().dec())
                            .camera(fitData.getInstrume())
                            .gain(fitData.getGain())
                            .instrument(observation.instrument())
                            .tags(tags)
                            .hash(fitData.getHash())
                            .radius(info.calibration().radius())
                            .pixscale(info.calibration().pixscale())
                            .state(PictureState.DONE)
                            .moonPhase(Optional.ofNullable(fitData.getDateObs()).map(d -> moonService.phaseOf(d.toLocalDate())).orElse(null))
                            .dateObs(fitData.getDateObs())
                            .constellation(deepSkyCatalogService.constellationOf(tags).orElse(null))
                            .exposure(fitData.getExposure())
                            .weather(observation.weather())
                            .location(observation.location())
                            .type(celest.map(CelestObject::type).orElse(null))
                            .stackCnt(fitData.getStackCnt())
                            .novaAstrometry(Picture.NovaAstrometry.builder()
                                    .jobId(jobId.get())
                                    .submission(submissionId)
                                    .image(astroNovaImage)
                                    .build())
                            .build();
                    log.info("{} / ⬇ getting annotated image...", owner);
                    var annotated = astrometryDAO.getAnnotatedImage(jobId.get());
                    log.info("{} / ✅ annotated image got", owner);

                    log.info("{} / now, saving picture {}...", owner, fit.get().filename());
                    pictureService.save(
                            owner,
                            picture,
                            Files.newInputStream(image.get().tempFile()),
                            new ByteArrayInputStream(thumbnail.toByteArray()),
                            Files.newInputStream(fit.get().tempFile()),
                            astrometryFit.map(afit -> {
                                try {
                                    return Files.newInputStream(afit.tempFile());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }).orElse(null),
                            annotated
                    );
                    log.info("{}/ ✅🥳 Picture {} ({}) saved !", owner, pictureId, fit.get().filename());
                    break;
                }
            } catch (Exception e) {
                log.error("{}/ Analyze de l'image impossible : {}", owner, pictureId, e);
                try {
                    pictureService.cancel(owner, pictureId);
                    fit.ifPresent(f -> {
                        f.tempFile().toFile().delete();
                        log.info("{}/ Effacement du fit temp", owner);
                    });
                    image.ifPresent(f -> {
                        f.tempFile().toFile().delete();
                        log.info("{}/ Effacement de l'image temp", owner);
                    });
                    astrometryFit.ifPresent(f -> {
                        f.tempFile().toFile().delete();
                        log.info("{}/ Effacement du astrometry fit temp", owner);
                    });
                } catch (RuntimeException ee) {
                    log.error("{}/ Effacement impossible des fichiers FIT/JPG... {}", owner, pictureId, ee);
                }
            }
        }
    }


}
