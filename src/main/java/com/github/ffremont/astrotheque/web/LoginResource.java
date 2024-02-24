package com.github.ffremont.astrotheque.web;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.httpserver.route.HttpExchangeWrapper;
import com.github.ffremont.astrotheque.core.security.AstroAuthenticator;
import com.github.ffremont.astrotheque.service.AccountService;
import com.github.ffremont.astrotheque.service.model.Jwt;
import com.github.ffremont.astrotheque.web.model.LoginRequest;

import java.net.HttpCookie;

import static com.github.ffremont.astrotheque.core.security.AstroAuthenticator.COOKIE_NAME;

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

        return null;

    }

    public String logout(HttpExchangeWrapper wrapper) {
        HttpCookie cookie = HttpCookie.parse(wrapper.httpExchange().getRequestHeaders().getFirst("Cookie")).stream().filter(httpCookie -> COOKIE_NAME.equals(httpCookie.getName()))
                .findFirst().orElseThrow();

        accountService.logout(cookie.getValue());

        wrapper.httpExchange().getResponseHeaders().add("Set-Cookie",
                "$COOKIE=$VALUE; Max-Age=-1; HttpOnly"
                        .replace("$COOKIE", AstroAuthenticator.COOKIE_NAME)
                        .replace("$VALUE", cookie.getValue()));

        return null;
    }
}
