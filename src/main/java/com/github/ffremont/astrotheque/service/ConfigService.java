package com.github.ffremont.astrotheque.service;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.dao.ConfigurationDao;
import com.github.ffremont.astrotheque.service.model.Configuration;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.ffremont.astrotheque.dao.ConfigurationDao.CONFIG_FILENAME;

public class ConfigService {


    private final AtomicBoolean configReady;

    private final ConfigurationDao dao;

    private final IoC ioc;

    public ConfigService(IoC ioC) {
        DynamicProperties dynamicProperties = ioC.get(DynamicProperties.class);
        this.dao = ioC.get(ConfigurationDao.class);
        this.ioc = ioC;
        this.configReady = new AtomicBoolean(dynamicProperties.getDataDir().resolve(CONFIG_FILENAME).toFile().exists());
    }

    public boolean installed() {
        return configReady.get();
    }

    synchronized public Configuration install(Configuration config) {
        AccountService accService = ioc.get(AccountService.class);
        var realConfig = config.toBuilder()
                .admin(config.admin().toBuilder()
                        .pwd(accService.hashPwd(config.admin().pwd()))
                        .build())
                .build();
        dao.write(realConfig);
        accService.register(config.admin().login(), realConfig.admin().pwd());
        ioc.get(PictureService.class).load(config.admin().login());
        this.configReady.set(true);
        return config;
    }

    public synchronized void update(Configuration newConfig) {
        Configuration config = getConfiguration();

        dao.write(config.toBuilder()
                .baseurl(newConfig.baseurl())
                .astrometryNovaApikey(newConfig.astrometryNovaApikey())
                .build());
    }


    public Configuration getConfiguration() {
        return dao.get();
    }
}
