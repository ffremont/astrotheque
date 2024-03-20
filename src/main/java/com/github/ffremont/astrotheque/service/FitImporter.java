package com.github.ffremont.astrotheque.service
        ;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.dao.AstrometryDAO;
import com.github.ffremont.astrotheque.service.model.CelestObject;
import com.github.ffremont.astrotheque.service.model.FitData;
import com.github.ffremont.astrotheque.service.model.PictureState;
import com.github.ffremont.astrotheque.web.model.Observation;
import com.github.ffremont.astrotheque.web.model.PreviewData;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * Pour une observation donnée, importe les fits
 */
@Slf4j
public class FitImporter implements Runnable {
    /**
     * Temps entre chaque appel pour vérifier l'avancement du job
     */
    final int TEMPO_MS = 20000;


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

        for (FitData fit : observation.fits()) {
            log.info("{} / ->️ Fit {}", owner, fit.getFilename());
            var counter = 0;
            Optional<PreviewData> preview = Optional.empty();

            if (observation.fits().size() == 1 && observation.previews().size() == 1) {
                preview = observation.previews().stream().findFirst();
            } else {
                preview = observation.previews().stream()
                        .filter(p -> !p.filename().endsWith(".fit"))
                        .filter(p -> p.filename().startsWith(fit.getFilename().replace(".fit", "")))
                        .findFirst();
            }

            try {
                var pictureId = fit.getId();
                if (Objects.isNull(sessionId)) {
                    throw new ImportProcessException("Session id nova astrometry null");
                }

                log.info("{} / uploading image...", owner);
                var submissionId = astrometryDAO.upload(sessionId, preview.map(PreviewData::tempFile).orElse(fit.getTempFile()));

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
                    if (preview.isPresent()) {
                        log.info("{} / ⚙️building thumb from preview {}", owner, preview.get().filename());
                        Thumbnails.of(preview.get().tempFile().toFile())
                                .size(512, 512)
                                .outputFormat("jpg")
                                .toOutputStream(thumbnail);
                    } else {
                        log.info("{} / ⬇ download image to define preview...", owner);
                        Integer astroNovaImage = Optional.ofNullable(subInfo.images()).flatMap(images -> images.stream().findFirst())
                                .orElseThrow(() -> new ImportProcessException("ImageId introuvable"));
                        Path previewFile = Files.createTempFile("astrotheque_", ".jpg");
                        FileUtils.copyInputStreamToFile(astrometryDAO.getImage(astroNovaImage), previewFile.toFile());
                        preview = Optional.of(new PreviewData(previewFile, previewFile.toFile().getName()));

                        log.info("{} / ⚙️ Building thumb...", owner);
                        Thumbnails.of(Files.newInputStream(previewFile))
                                .size(512, 512)
                                .outputFormat("jpg")
                                .toOutputStream(thumbnail);
                    }
                    log.info("{} / ✅ thumb built", owner);


                    var tags = info.tags().stream().map(tag -> tag.replace(" ", "")).toList();
                    var celest = deepSkyCatalogService.findCelestObject(tags);
                    var picture = pictureService.get(owner, pictureId).toBuilder()
                            .ra(info.calibration().ra())
                            .name(celest.map(CelestObject::name).orElse(null))
                            .dec(info.calibration().dec())
                            .camera(fit.getInstrume())
                            .gain(fit.getGain())
                            .instrument(observation.instrument())
                            .tags(tags)
                            .hash(fit.getHash())
                            .state(PictureState.DONE)
                            .moonPhase(moonService.phaseOf(fit.getDateObs().toLocalDate()))
                            .dateObs(fit.getDateObs())
                            .constellation(deepSkyCatalogService.constellationOf(tags).orElse(null))
                            .exposure(fit.getExposure())
                            .weather(observation.weather())
                            .location(observation.location())
                            .type(celest.map(CelestObject::type).orElse(null))
                            .stackCnt(fit.getStackCnt())
                            .novaAstrometryReportUrl(astrometryDAO.reportUrlOf(subInfo))
                            .build();
                    log.info("{} / ⬇ getting annotated image...", owner);
                    var annotated = astrometryDAO.getAnnotatedImage(jobId.get());
                    log.info("{} / ✅ annotated image got", owner);

                    log.info("{} / now, saving picture {}...", owner, fit.getFilename());
                    pictureService.save(
                            owner,
                            picture,
                            Files.newInputStream(preview.get().tempFile()),
                            new ByteArrayInputStream(thumbnail.toByteArray()),
                            Files.newInputStream(fit.getTempFile()),
                            annotated
                    );
                    log.info("{}/ ✅🥳 Picture {} ({}) saved !", owner, fit.getId(), fit.getFilename());
                    break;
                }
            } catch (Exception e) {
                log.error("{}/ Analyze de l'image impossible : {}", owner, fit.getTempFile().toString(), e);
                try {
                    pictureService.cancel(owner, fit.getId());
                    if (fit.getTempFile().toFile().delete()) {
                        log.info("{}/ Effacement du fichier temporaire", owner);
                    } else {
                        log.info("{}/ Effacement du fichier temporaire", owner);
                    }
                    preview.ifPresent(path -> path.tempFile().toFile().delete());
                } catch (RuntimeException ee) {
                    log.error("{}/ Effacement impossible de l'image FIT/JPG/ {}", owner, fit.getId(), ee);
                }
            }
        }
    }


}
