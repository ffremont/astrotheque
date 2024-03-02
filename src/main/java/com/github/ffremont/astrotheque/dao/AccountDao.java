package com.github.ffremont.astrotheque.dao;

import com.github.ffremont.astrotheque.service.model.Account;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountDao {

    private final Map<String, Account> accounts;

    public AccountDao() {
        accounts = new ConcurrentHashMap<>();
    }

    public List<Account> getAccounts() {
        return accounts.values().stream().toList();
    }

    public Account register(String login, String pwd) {
        Account acc = new Account(login, pwd);
        accounts.put(login, acc);

        return acc;
    }

    /**
     * Retourne un compte utilisateur
     *
     * @param name
     * @return
     */
    public Account findByName(String name) {
        return accounts.values().stream().filter(account -> account.name().equals(name)).findFirst().orElseThrow(() -> new IllegalArgumentException("Nom de compte invalide"));
    }
}
