package com.github.ffremont.astrotheque.service
        ;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.dao.AstrometryDAO;
import com.github.ffremont.astrotheque.dao.PictureDAO;
import com.github.ffremont.astrotheque.service.model.CelestObject;
import com.github.ffremont.astrotheque.service.model.FitData;
import com.github.ffremont.astrotheque.service.model.PictureState;
import com.github.ffremont.astrotheque.web.model.Observation;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

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
    private final PictureDAO pictureDAO;

    private final MoonService moonService;
    private final DeepSkyCatalogService deepSkyCatalogService;

    private final Observation observation;

    private final String astrometryNovaApikey;
    private final String owner;

    public FitImporter(AstrometryDAO astrometryDAO, PictureDAO pictureDAO, MoonService moonService, DeepSkyCatalogService deepSkyCatalogService, Observation observation, String astrometryNovaApikey, String owner) {
        this.astrometryDAO = astrometryDAO;
        this.pictureDAO = pictureDAO;
        this.moonService = moonService;
        this.deepSkyCatalogService = deepSkyCatalogService;
        this.observation = observation;
        this.astrometryNovaApikey = astrometryNovaApikey;
        this.owner = owner;
    }

    public FitImporter(IoC ioC, Observation observation, String astrometryNovaApikey, String owner) {
        this(
                ioC.get(AstrometryDAO.class),
                ioC.get(PictureDAO.class),
                ioC.get(MoonService.class),
                ioC.get(DeepSkyCatalogService.class),
                observation, astrometryNovaApikey, owner
        );
    }


    public void run() {
        var sessionId = astrometryDAO.createLoginSession(astrometryNovaApikey);

        for (FitData fit : observation.fits()) {
            var counter = 0;
            Optional<Path> preview = observation.previews().stream()
                    .filter(p -> !p.getFileName().endsWith(".fit"))
                    .filter(p -> p.getFileName().toString().startsWith(fit.getTempFile().getFileName().toString().replace(".fit", ""))).findFirst();
            try {
                var pictureId = fit.getId();
                if (Objects.isNull(sessionId)) {
                    throw new ImportProcessException("Session id nova astrometry null");
                }

                //var submissionId = astrometryDAO.upload(sessionId, fit.getTempFile());
                var submissionId = astrometryDAO.upload(sessionId, preview.get());

                if (Objects.isNull(submissionId)) {
                    throw new ImportProcessException("Submission id nova astrometry null");
                }
                while (counter < 60) {
                    log.info("{}/ waiting for {}", owner, pictureId);
                    Thread.sleep(TEMPO_MS);
                    counter++;

                    var subInfo = astrometryDAO.getSubInfo(submissionId);
                    var jobs = Optional.ofNullable(subInfo)
                            .map(si -> Optional.ofNullable(si.jobs()).orElse(Collections.emptyList()))
                            .orElse(Collections.emptyList());
                    Optional<Integer> jobId = !jobs.isEmpty() ? Optional.ofNullable(jobs.getFirst()) : Optional.empty();
                    if (jobId.isEmpty()) continue;

                    var info = astrometryDAO.info(jobId.get());
                    if (!"success".equals(info.status())) continue;

                    var thumbnail = new ByteArrayOutputStream();
                    if (preview.isPresent()) {
                        Thumbnails.of(preview.get().toFile())
                                .size(512, 512)
                                .outputFormat("jpg")
                                .toOutputStream(thumbnail);
                    } else {
                        Integer astroNovaImage = Optional.ofNullable(subInfo.images()).flatMap(images -> images.stream().findFirst())
                                .orElseThrow(() -> new ImportProcessException("ImageId introuvable"));
                        Thumbnails.of(astrometryDAO.getImage(astroNovaImage))
                                .size(512, 512)
                                .outputFormat("jpg")
                                .toOutputStream(thumbnail);
                    }

                    var tags = info.tags().stream().map(tag -> tag.replace(" ", "")).toList();
                    var celest = deepSkyCatalogService.findCelestObject(tags);
                    var picture = pictureDAO.getById(owner, pictureId).toBuilder()
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
                            .corrRed(observation.corrred())
                            .constellation(deepSkyCatalogService.constellationOf(tags).orElse(null))
                            .exposure(fit.getExposure())
                            .weather(observation.weather())
                            .location(observation.location())
                            .type(celest.map(CelestObject::type).orElse(null))
                            .stackCnt(fit.getStackCnt())
                            .novaAstrometryReportUrl(astrometryDAO.reportUrlOf(subInfo))
                            .build();
                    var annotated = astrometryDAO.getAnnotatedImage(jobId.get());
                    pictureDAO.save(
                            owner,
                            picture,
                            preview.isPresent() ? Files.newInputStream(preview.get()) : null,
                            new ByteArrayInputStream(thumbnail.toByteArray()),
                            Files.newInputStream(fit.getTempFile()),
                            annotated
                    );
                    log.info("✅ {}/ import of {}", owner, fit.getId());
                    break;
                }
            } catch (Exception e) {
                log.error("{}/ Analyze de l'image impossible : {}", owner, fit.getTempFile().toString(), e);
                try {
                    if (fit.getTempFile().toFile().delete()) {
                        log.info("{}/ Effacement du fichier temporaire", owner);
                    } else {
                        log.info("{}/ Effacement du fichier temporaire", owner);
                    }
                    preview.ifPresent(path -> path.toFile().delete());

                    pictureDAO.remove(owner, fit.getId());
                } catch (RuntimeException ee) {
                    log.error("{}/ Effacement impossible de l'image FIT/JPG/ {}", owner, fit.getId(), ee);
                }
            }
        }
    }


}
