package com.github.ffremont.astrotheque.service;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.dao.ConfigurationDao;
import com.github.ffremont.astrotheque.service.model.Configuration;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.ffremont.astrotheque.dao.ConfigurationDao.CONFIG_FILENAME;

public class ConfigService {

    /**
     * verrouille les actions réalisées sur le fichier de configuration
     */
    private final ReentrantLock lock;

    private final AtomicBoolean configReady;

    private final ConfigurationDao dao;

    private final IoC ioc;

    public ConfigService(IoC ioC) {
        DynamicProperties dynamicProperties = ioC.get(DynamicProperties.class);
        this.dao = ioC.get(ConfigurationDao.class);
        this.ioc = ioC;
        this.lock = new ReentrantLock();
        this.configReady = new AtomicBoolean(dynamicProperties.getDataDir().resolve(CONFIG_FILENAME).toFile().exists());
    }

    public boolean installed() {
        return configReady.get();
    }

    public Configuration install(Configuration config) {
        try {
            lock.lock();
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
        } finally {
            lock.unlock();
        }
    }

    public void update(Configuration newConfig) {
        try {
            lock.lock();
            Configuration config = getConfiguration();

            dao.write(config.toBuilder()
                    .baseurl(newConfig.baseurl())
                    .astrometryNovaApikey(newConfig.astrometryNovaApikey())
                    .build());
        } finally {
            lock.unlock();
        }
    }

    public void changePassword(String accountName, String actualPassword, String newPassword) {
        try {
            lock.lock();
            Configuration config = getConfiguration();

            AccountService accountService = ioc.get(AccountService.class);
            if (!accountService.verifiedPasswordOf(accountName, actualPassword)) {
                throw new IllegalArgumentException("Actual password invalid for " + actualPassword);
            }
            var newHashPassword = accountService.hashPwd(newPassword);
            dao.write(config.toBuilder()
                    .admin(config.admin().toBuilder()
                            .pwd(newHashPassword)
                            .build())
                    .build());
            accountService.register(accountName, newHashPassword);
        } finally {
            lock.unlock();
        }
    }


    public Configuration getConfiguration() {
        return dao.get();
    }
}
