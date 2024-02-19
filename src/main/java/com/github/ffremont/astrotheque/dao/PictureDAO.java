package com.github.ffremont.astrotheque.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.StartupListener;
import com.github.ffremont.astrotheque.service.DynamicProperties;
import com.github.ffremont.astrotheque.service.model.Belong;
import com.github.ffremont.astrotheque.service.model.Picture;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PictureDAO implements StartupListener {

    public final static String THUMB_FILENAME = "thumb.jpg";
    public final static String RAW_FILENAME = "raw.fit";
    public final static String PICTURE_FILENAME = "picture.jpg";
    public final static String DATA_FILENAME = "data.json";
    public  final static String ANNOTATED_FILENAME = "annotated.jpg";

    private final static ConcurrentHashMap<String, Belong<Picture>> DATASTORE = new ConcurrentHashMap<>();

    private final static ObjectMapper JSON = new ObjectMapper();

    @Override
    public void onStartup(IoC ioc) {
        try {
            List<String> accounts = ioc.get(AccountDao.class).getAccounts();
            Path dataDir = ioc.get(DynamicProperties.class).getDataDir();
            if (!dataDir.toFile().exists()) Files.createDirectories(dataDir);

            Files.list(dataDir)
                    .filter(path -> accounts.contains(path.toFile().getName()))
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
                                                    throw new RuntimeException("Filename invalid : "+id,e);
                                                }
                                            });
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
