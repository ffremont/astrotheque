package com.github.ffremont.astrotheque.web;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.httpserver.route.HttpExchangeWrapper;
import com.github.ffremont.astrotheque.service.ConfigService;
import com.github.ffremont.astrotheque.service.model.Me;
import com.github.ffremont.astrotheque.web.model.NewPassword;

public class MeResource {

    private final ConfigService configService;

    public MeResource(IoC ioC) {
        this.configService = ioC.get(ConfigService.class);
    }

    public Me myProfil(HttpExchangeWrapper wrapper) {
        return configService.installed() ? new Me(wrapper.httpExchange().getPrincipal().getUsername()) : null;
    }

    public String updatePassword(HttpExchangeWrapper wrapper) {
        NewPassword newPassword = (NewPassword) wrapper.body();
        configService.changePassword(wrapper.httpExchange().getPrincipal().getUsername(), newPassword.actualPassword(), newPassword.newPassword());

        return "ok";
    }

}
