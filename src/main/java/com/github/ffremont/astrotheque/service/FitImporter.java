package com.github.ffremont.astrotheque.service
        ;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.dao.AstrometryDAO;
import com.github.ffremont.astrotheque.service.model.CelestObject;
import com.github.ffremont.astrotheque.service.model.File;
import com.github.ffremont.astrotheque.service.model.FitData;
import com.github.ffremont.astrotheque.service.model.PictureState;
import com.github.ffremont.astrotheque.web.model.Observation;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.github.ffremont.astrotheque.service.utils.FileUtils.isFit;
import static com.github.ffremont.astrotheque.service.utils.FileUtils.isImage;

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


    public void run() {
        log.info("{} / ⚙️ Importation {}", owner, observation.id());
        var sessionId = astrometryDAO.createLoginSession(astrometryNovaApikey);

        for (File file : observation.files()) {
            log.info("{} / ->️ Fit {}", owner, file.filename());
            var counter = 0;

            var image = Optional.of(file)
                    .filter(f -> isImage.test(f.filename()))
                    .or(() -> Optional.ofNullable(file.relatedTo()));
            var fit = Optional.of(file)
                    .filter(f -> isFit.test(f.filename()))
                    .or(() -> Optional.ofNullable(file.relatedTo()));

            var pictureId = file.id();
            try {
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
                    if (!"success".equals(info.status())) {
                        log.info("{} / ❌ failed to get info of job {}, current status : {}", owner, jobId.get(), info.status());
                        throw new RuntimeException(" ❌ Job invalid status :" + info.status());
                    } else {
                        log.info("{} / ✅ getting info of job {}", owner, jobId.get());
                    }

                    var thumbnail = new ByteArrayOutputStream();
                    Integer astroNovaImage;
                    if (fit.isPresent() && image.isPresent()) {
                        log.info("{} / ⚙️building thumb from image {}", owner, image.get().filename());
                        Thumbnails.of(image.get().tempFile().toFile())
                                .size(512, 512)
                                .outputFormat("jpg")
                                .toOutputStream(thumbnail);
                    } else if (fit.isPresent()) {
                        log.info("{} / ⬇ download image to define preview...", owner);

                        astroNovaImage = Optional.ofNullable(subInfo.images()).flatMap(images -> images.stream().findFirst()).orElseThrow();
                        Path novaImageFile = Files.createTempFile("astrotheque_", ".jpg");
                        FileUtils.copyInputStreamToFile(astrometryDAO.getImage(astroNovaImage), novaImageFile.toFile());

                        log.info("{} / ⚙️ Building thumb from nova image...", owner);
                        Thumbnails.of(Files.newInputStream(novaImageFile))
                                .size(512, 512)
                                .outputFormat("jpg")
                                .toOutputStream(thumbnail);
                        image = Optional.of(new File(UUID.randomUUID().toString(), novaImageFile, novaImageFile.toFile().getName(), null));
                        log.info("{} / ✅ image defined", owner);
                    } else if (image.isPresent()) {
                        log.info("{} / ⬇ download fit to define raw...", owner);

                        Path novaRawFile = Files.createTempFile("astrotheque_", ".fit");
                        FileUtils.copyInputStreamToFile(astrometryDAO.getFit(jobId.get()), novaRawFile.toFile());
                        fit = Optional.of(new File(UUID.randomUUID().toString(), novaRawFile, novaRawFile.toFile().getName(), image.get()));
                        log.info("{} / ✅ fit defined", owner);

                        log.info("{} / ⚙️ Building thumb from image...", owner);
                        Thumbnails.of(Files.newInputStream(image.get().tempFile()))
                                .size(512, 512)
                                .outputFormat("jpg")
                                .toOutputStream(thumbnail);
                    }
                    log.info("{} / ✅ thumb built", owner);

                    FitData fitData = fitMapper.apply(Map.entry(fit.orElseThrow().filename(), fit.orElseThrow().tempFile()));

                    var tags = info.tags().stream().map(tag -> tag.replace(" ", "")).toList();
                    var celest = deepSkyCatalogService.findCelestObject(tags);
                    var picture = pictureService.get(owner, pictureId).toBuilder()
                            .ra(info.calibration().ra())
                            .name(celest.map(CelestObject::name).orElse(null))
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
                            .novaAstrometryReportUrl(astrometryDAO.reportUrlOf(subInfo))
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
                    });
                    image.ifPresent(f -> {
                        f.tempFile().toFile().delete();
                    });
                } catch (RuntimeException ee) {
                    log.error("{}/ Effacement impossible de l'image FIT/JPG/ {}", owner, pictureId, ee);
                }
            }
        }
    }


}
