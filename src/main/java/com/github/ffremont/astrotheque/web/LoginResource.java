package com.github.ffremont.astrotheque.web;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.httpserver.route.HttpExchangeWrapper;
import com.github.ffremont.astrotheque.core.security.AstroAuthenticator;
import com.github.ffremont.astrotheque.service.AccountService;
import com.github.ffremont.astrotheque.service.model.Jwt;
import com.github.ffremont.astrotheque.web.model.LoginRequest;

public class LoginResource {

    private final AccountService accountService;

    public LoginResource(IoC ioC) {
        this.accountService = ioC.get(AccountService.class);
    }


    public String login(HttpExchangeWrapper wrapper) {
        LoginRequest loginRequest = (LoginRequest) wrapper.body();
        Jwt jwt = accountService.tryLogin(loginRequest.login(), loginRequest.pwd());

        wrapper.httpExchange().getResponseHeaders().add("Set-Cookie",
                "$COOKIE=$VALUE; Max-Age=$MA; HttpOnly"
                        .replace("$COOKIE", AstroAuthenticator.COOKIE_NAME)
                        .replace("$VALUE", jwt.bearer())
                        .replace("$MA", jwt.maxAge().toString()));

        return "ok";
    }
}
