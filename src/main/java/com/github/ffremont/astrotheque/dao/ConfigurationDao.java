package com.github.ffremont.astrotheque.dao;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.security.CryptUtils;
import com.github.ffremont.astrotheque.service.DynamicProperties;
import com.github.ffremont.astrotheque.service.model.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

public class ConfigurationDao {

    public final static String CONFIG_FILENAME = "configuration.enc";

    private final static ObjectMapper JSON = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    private final DynamicProperties dynamicProperties;
    private final Path location;

    public ConfigurationDao(IoC ioc) {
        this.dynamicProperties = ioc.get(DynamicProperties.class);
        this.location = dynamicProperties.getDataDir().resolve(CONFIG_FILENAME);
    }

    synchronized public Configuration write(Configuration config) {
        try {
            byte[] cryptedData = CryptUtils.crypt(JSON.writeValueAsBytes(config), dynamicProperties.getSecret());
            Files.write(location, cryptedData, CREATE_NEW);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return config;
    }

    public Configuration get() {
        if (!location.toFile().exists()) {
            return null;
        }
        try {
            byte[] decryptedData = CryptUtils.decrypt(Files.readAllBytes(location), dynamicProperties.getSecret());
            return JSON.readValue(decryptedData, Configuration.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
