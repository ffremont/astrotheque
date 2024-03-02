package com.github.ffremont.astrotheque.dao;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.ffremont.astrotheque.core.security.CryptUtils;
import com.github.ffremont.astrotheque.service.model.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

public class ConfigurationDao {

    private final static ObjectMapper JSON = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    synchronized public Configuration write(Path location, Configuration config, String secret) {
        try {
            byte[] cryptedData = CryptUtils.crypt(JSON.writeValueAsBytes(config), secret);
            Files.write(location, cryptedData, CREATE_NEW);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return config;
    }

    public Configuration get(Path location, String secret) {
        if (!location.toFile().exists()) {
            return null;
        }
        try {
            byte[] decryptedData = CryptUtils.decrypt(Files.readAllBytes(location), secret);
            return JSON.readValue(decryptedData, Configuration.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
