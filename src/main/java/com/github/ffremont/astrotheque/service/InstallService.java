package com.github.ffremont.astrotheque.service;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.dao.ConfigurationDao;
import com.github.ffremont.astrotheque.service.model.Configuration;

import java.util.concurrent.atomic.AtomicBoolean;

public class InstallService {

    public final static String CONFIG_FILENAME = "configuration.enc";


    private final DynamicProperties dynamicProperties;
    private final AtomicBoolean configReady;

    private final ConfigurationDao dao;

    private final AccountService accountService;

    public InstallService(IoC ioC) {
        this.dynamicProperties = ioC.get(DynamicProperties.class);
        this.dao = ioC.get(ConfigurationDao.class);
        this.accountService = ioC.get(AccountService.class);
        this.configReady = new AtomicBoolean(dynamicProperties.getDataDir().resolve(CONFIG_FILENAME).toFile().exists());
    }

    public boolean installed() {
        return configReady.get();
    }

    synchronized public Configuration install(Configuration config) {
        dao.write(dynamicProperties.getDataDir().resolve(CONFIG_FILENAME), config, dynamicProperties.getSecret());
        accountService.register(config.admin().login(), config.admin().pwd());
        this.configReady.set(true);
        return config;
    }

    public Configuration getConfiguration() {
        return dao.get(dynamicProperties.getDataDir().resolve(CONFIG_FILENAME), dynamicProperties.getSecret());
    }
}
