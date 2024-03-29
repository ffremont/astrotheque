package com.github.ffremont.astrotheque.service;

import com.github.ffremont.astrotheque.service.model.FitData;
import com.github.ffremont.astrotheque.service.utils.FitUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_224;

public class FitMapper implements Function<Map.Entry<String, Path>, FitData> {
    @Override
    public FitData apply(Map.Entry<String, Path> path) {
        return Optional.ofNullable(path)
                .map(entry -> FitUtils.analyze(entry.getValue()).toBuilder().filename(entry.getKey()).build())
                .map(fit -> {
                    try {
                        return fit.toBuilder()
                                .id(UUID.randomUUID().toString())
                                .hash(new DigestUtils(SHA_224).digestAsHex(fit.getTempFile().toFile())).build();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).orElse(null);
    }
}
