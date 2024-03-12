package com.github.ffremont.astrotheque.web;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.httpserver.route.HttpExchangeWrapper;
import com.github.ffremont.astrotheque.service.AccountService;
import com.github.ffremont.astrotheque.service.ConfigService;
import com.github.ffremont.astrotheque.service.model.Configuration;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class ConfigurationResource {

    private final ConfigService configService;
    private final AccountService accountService;

    public ConfigurationResource(IoC ioC) {
        this.configService = ioC.get(ConfigService.class);
        this.accountService = ioC.get(AccountService.class);
    }

    public String install(HttpExchangeWrapper wrapper) {
        configService.install((Configuration) wrapper.body());

        return "okay";
    }

    public String isInstalled(HttpExchangeWrapper wrapper) {
        return Optional.ofNullable(configService.getConfiguration()).map(c -> "installed").orElse(null);
    }

    public String updateConfig(HttpExchangeWrapper wrapper) {
        accountService.checkAdmin(wrapper.httpExchange().getPrincipal().getName());

        Configuration request = (Configuration) wrapper.body();
        requireNonNull(request);

        configService.update(request);
        return "ok";
    }

    public Configuration getConfig(HttpExchangeWrapper wrapper) {
        accountService.checkAdmin(wrapper.httpExchange().getPrincipal().getName());

        return configService.getConfiguration();
    }
}
