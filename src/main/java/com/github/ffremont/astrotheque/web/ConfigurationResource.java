package com.github.ffremont.astrotheque.web;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.httpserver.route.HttpExchangeWrapper;
import com.github.ffremont.astrotheque.service.InstallService;
import com.github.ffremont.astrotheque.service.model.Configuration;

import java.util.Optional;

public class ConfigurationResource {

    private final InstallService installService;

    public ConfigurationResource(IoC ioC) {
        this.installService = ioC.get(InstallService.class);
    }

    public String install(HttpExchangeWrapper wrapper) {
        installService.install((Configuration) wrapper.body());

        return "okay";
    }

    public String isInstalled(HttpExchangeWrapper wrapper) {
        return Optional.ofNullable(installService.getConfiguration()).map(c -> "installed").orElse(null);
    }

    public Configuration getConfig(HttpExchangeWrapper wrapper) {
        return installService.getConfiguration();
    }
}
