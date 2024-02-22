package com.github.ffremont.astrotheque.service;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.StartupListener;
import com.github.ffremont.astrotheque.core.exception.InvalidLoginExeption;
import com.github.ffremont.astrotheque.core.security.AstroAuthenticator;
import com.github.ffremont.astrotheque.core.security.MetaToken;
import com.github.ffremont.astrotheque.dao.AccountDao;
import com.github.ffremont.astrotheque.service.model.Account;
import com.github.ffremont.astrotheque.service.model.Jwt;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gère les comptes utilisateurs
 * v1 : mono-utilisateur
 */
@Slf4j
public class AccountService implements StartupListener {


    private static final int LEVEL_WAIT = 5;
    private static final int WAIT = 5;
    private final Map<String, AtomicInteger> attempts;
    private final AstroAuthenticator astroAuthenticator;


    private AccountDao dao;


    public AccountService(IoC ioC) {
        this.attempts = new ConcurrentHashMap<>();
        this.dao = ioC.get(AccountDao.class);
        this.astroAuthenticator = ioC.get(AstroAuthenticator.class);
    }


    @Override
    public void onStartup(IoC ioC) {
        log.debug("Initialisation des comptes utilisateurs à 0 tentatives de connexion");
        List<Account> accountNames = dao.getAccounts();
        accountNames.forEach(account -> attempts.put(account.name(), new AtomicInteger(0)));
    }


    /**
     * Tentative de connexion
     *
     * @param login
     * @param pwd
     */
    public Jwt tryLogin(String login, String pwd) {
        boolean pwdOkay = dao.findByName(login).pwd().equals(pwd);

        if (!pwdOkay) {
            if (attempts.get(login).incrementAndGet() > LEVEL_WAIT) {
                try {
                    Thread.sleep(WAIT);
                } catch (InterruptedException e) {
                    throw new InvalidLoginExeption("Attente de tentative de connexion en erreur", login);
                }
            }
            throw new InvalidLoginExeption("Tentative de connexion infrutueuse", login);
        }
        attempts.get(login).set(0);

        LocalDateTime expireAt = LocalDateTime.now().plusMinutes(30);
        return
                new Jwt(astroAuthenticator.getGenerator().generate(new MetaToken(AstroAuthenticator.ISSUER, login, expireAt)),
                        expireAt.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond()
                                -
                                LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond()
                );
    }
}
