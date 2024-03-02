package com.github.ffremont.astrotheque.dao;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.StartupListener;
import com.github.ffremont.astrotheque.service.DynamicProperties;
import com.github.ffremont.astrotheque.service.model.Account;
import com.github.ffremont.astrotheque.service.model.Belong;
import com.github.ffremont.astrotheque.service.model.Picture;
import com.github.ffremont.astrotheque.service.model.PictureState;
import com.github.ffremont.astrotheque.web.model.Observation;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Slf4j
public class PictureDAO implements StartupListener {

    public final static String THUMB_FILENAME = "thumb.jpg";
    public final static String RAW_FILENAME = "raw.fit";
    public final static String PICTURE_FILENAME = "picture.jpg";
    public final static String DATA_FILENAME = "data.json";
    public final static String ANNOTATED_FILENAME = "annotated.jpg";

    private final static ConcurrentHashMap<String, Belong<Picture>> DATASTORE = new ConcurrentHashMap<>();

    private final static ObjectMapper JSON = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());


    private final Path dataDir;


    public PictureDAO(IoC ioC) {
        this.dataDir = ioC.get(DynamicProperties.class).getDataDir();
    }

    /**
     * Donne l'emplacement d'un rép. de photo pour un propriétaire
     *
     * @param pictureId
     * @param owner
     * @return
     */
    private Path locationOf(String pictureId, String owner) {
        return dataDir.resolve(owner).resolve(pictureId);
    }

    @Override
    public void onStartup(IoC ioc) {
        try {
            List<Account> accounts = ioc.get(AccountDao.class).getAccounts();
            Path dataDir = ioc.get(DynamicProperties.class).getDataDir();
            if (!dataDir.toFile().exists()) Files.createDirectories(dataDir);

            Files.list(dataDir)
                    .filter(path -> accounts.stream().map(Account::name).toList().contains(path.toFile().getName()))
                    .forEach(accountDataPath -> {
                        String owner = accountDataPath.toFile().getName();
                        try {
                            Files.list(accountDataPath)
                                    .filter(path -> path.toFile().isDirectory())
                                    .filter(dir -> !dir.getFileName().toString().startsWith("."))
                                    .forEach(pictureDir -> {
                                        var id = pictureDir.getFileName().toString();
                                        try {
                                            DATASTORE.put(id, new Belong<>(owner, JSON.readValue(Files.readAllBytes(pictureDir.resolve(DATA_FILENAME)), Picture.class)));
                                        } catch (IOException e) {
                                            throw new RuntimeException("Filename invalid : " + id, e);
                                        }
                                    });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            log.debug("Astrothèque chargée !");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @param hashOfRaw
     * @return
     */
    public boolean has(String owner, String hashOfRaw) {
        return DATASTORE.entrySet().stream().anyMatch(stringPictureEntry ->
                owner.equals(stringPictureEntry.getValue().getOwner()) &&
                        hashOfRaw.equals(stringPictureEntry.getValue().getData().getHash()));
    }

    /**
     * @param ids
     * @param obs
     */
    public void allocate(String owner, List<String> ids, Observation obs) {
        for (String id : ids) {
            DATASTORE.put(id, new Belong<>(owner, Picture.builder().id(id)
                    .weather(obs.weather())
                    .imported(LocalDateTime.now())
                    .instrument(obs.instrument())
                    .corrRed(obs.corrred())
                    .observationId(obs.id())
                    .state(PictureState.PENDING).build()));
        }
    }

    /**
     * @param id
     * @return
     */
    public Picture getById(String owner, String id) {
        Belong<Picture> data = DATASTORE.get(id);
        return owner.equals(data.getOwner()) ? data.getData() : null;
    }

    /**
     * @param pictureId
     * @return
     */
    public Path getBin(String owner, String pictureId, String filename) {
        return locationOf(pictureId, owner).resolve(filename);
    }

    /**
     * @param picture
     * @param thumb
     * @param raw
     * @param annotated
     */
    public void save(String owner, Picture picture, InputStream jpg, InputStream thumb, InputStream raw, InputStream annotated) {
        final var pictureDir = locationOf(picture.getId(), owner);

        try {
            Files.createDirectories(pictureDir);
            Files.copy(jpg, pictureDir.resolve(pictureDir.resolve(PICTURE_FILENAME)), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(thumb, pictureDir.resolve(pictureDir.resolve(THUMB_FILENAME)), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(raw, pictureDir.resolve(pictureDir.resolve(RAW_FILENAME)), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(annotated, pictureDir.resolve(pictureDir.resolve(ANNOTATED_FILENAME)), StandardCopyOption.REPLACE_EXISTING);

            Files.write(pictureDir.resolve(pictureDir.resolve(DATA_FILENAME)), JSON.writer().writeValueAsBytes(picture), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            DATASTORE.put(picture.getId(), new Belong<>(owner, picture));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void refresh(String owner, Picture picture) {
        final var pictureDir = locationOf(picture.getId(), owner);

        try {
            Files.deleteIfExists(pictureDir.resolve(pictureDir.resolve(DATA_FILENAME)));
            Files.write(pictureDir.resolve(pictureDir.resolve(DATA_FILENAME)), JSON.writer().writeValueAsBytes(picture), StandardOpenOption.CREATE);

            DATASTORE.put(picture.getId(), new Belong<>(owner, picture));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param pictureId
     */
    public void remove(String owner, String pictureId) {
        final var pictureDir = locationOf(pictureId, owner);

        try {
            if (DATASTORE.containsKey(pictureId)) {
                DATASTORE.remove(pictureId);
                Files.walk(pictureDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retourne toutes les data d'un utilisateur
     *
     * @return
     */
    public Stream<Picture> getAll(String owner) {
        return DATASTORE.entrySet().stream().filter(entry -> entry.getValue().getOwner().equals(owner)).map(Map.Entry::getValue).map(Belong::getData);
    }
}
