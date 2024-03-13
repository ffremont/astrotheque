package com.github.ffremont.astrotheque.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.StartupListener;
import com.github.ffremont.astrotheque.core.exception.InvalidLoginExeption;
import com.github.ffremont.astrotheque.core.security.AstroAuthenticator;
import com.github.ffremont.astrotheque.core.security.MetaToken;
import com.github.ffremont.astrotheque.dao.AccountDao;
import com.github.ffremont.astrotheque.service.model.Account;
import com.github.ffremont.astrotheque.service.model.Configuration;
import com.github.ffremont.astrotheque.service.model.Jwt;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gère les comptes utilisateurs
 * v1 : mono-utilisateur
 */
@Slf4j
public class AccountService implements StartupListener {

    private static final int LEVEL_WAIT = 5;
    private static final int WAIT = 5000;
    private final Map<String, AtomicInteger> attempts;
    private final AstroAuthenticator astroAuthenticator;

    private final AccountDao dao;

    private final Set<String> blackListJwt;
    private final IoC ioc;


    public AccountService(IoC ioC) {
        this.attempts = new ConcurrentHashMap<>();
        this.dao = ioC.get(AccountDao.class);
        this.ioc = ioC;
        this.astroAuthenticator = ioC.get(AstroAuthenticator.class);
        this.blackListJwt = new HashSet<>();
    }


    @Override
    public void onStartup(IoC ioC) {
        log.debug("Initialisation des comptes utilisateurs à 0 tentatives de connexion");
        Configuration config = ioC.get(ConfigService.class).getConfiguration();
        Optional.ofNullable(config).ifPresent(c -> {
            dao.register(c.admin().login(), c.admin().pwd());
            ioc.get(PictureService.class).load(c.admin().login());
        });

        List<Account> accountNames = dao.getAccounts();
        accountNames.forEach(account -> attempts.put(account.name(), new AtomicInteger(0)));
    }

    public Account register(String login, String pwd) {
        Account acc = dao.register(login, pwd);
        attempts.put(acc.name(), new AtomicInteger(0));

        return acc;
    }

    public void logout(String jwt) {
        blackListJwt.add(jwt);
    }

    public boolean isBlacklistedToken(String jwt) {
        return blackListJwt.contains(jwt);
    }

    public void checkAdmin(String name) {
        //throw new UnauthorizeException(name);
    }


    public String hashPwd(String password) {
        return new String(BCrypt.withDefaults().hashToChar(12, password.toCharArray()));
    }

    public boolean verifiedPasswordOf(String login, String pwd) {
        String bcryptExpectPwd = dao.findByName(login).pwd();
        BCrypt.Result result = BCrypt.verifyer().verify(pwd.toCharArray(), bcryptExpectPwd);

        return result.verified;
    }

    /**
     * Tentative de connexion
     *
     * @param login
     * @param pwd
     */
    public Jwt tryLogin(String login, String pwd) {
        boolean pwdOkay = verifiedPasswordOf(login, pwd);

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
