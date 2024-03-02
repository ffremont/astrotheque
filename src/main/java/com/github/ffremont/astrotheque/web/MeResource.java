package com.github.ffremont.astrotheque.web;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.httpserver.route.HttpExchangeWrapper;
import com.github.ffremont.astrotheque.service.InstallService;
import com.github.ffremont.astrotheque.service.model.Me;

public class MeResource {

    private final InstallService installService;

    public MeResource(IoC ioC) {
        this.installService = ioC.get(InstallService.class);
    }

    public Me myProfil(HttpExchangeWrapper wrapper) {
        return installService.installed() ? new Me(wrapper.httpExchange().getPrincipal().getUsername()) : null;
    }
}
