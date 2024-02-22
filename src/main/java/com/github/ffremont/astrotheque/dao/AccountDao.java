package com.github.ffremont.astrotheque.dao;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.service.DynamicProperties;
import com.github.ffremont.astrotheque.service.model.Account;

import java.util.List;

public class AccountDao {

    private final List<Account> accounts;

    public AccountDao(IoC ioC) {
        accounts = List.of(new Account(
                ioC.get(DynamicProperties.class).getAdminLogin(),
                ioC.get(DynamicProperties.class).getAdminPwd()
        ));
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     * Retourne un compte utilisateur
     *
     * @param name
     * @return
     */
    public Account findByName(String name) {
        return accounts.stream().filter(account -> account.name().equals(name)).findFirst().orElseThrow(() -> new IllegalArgumentException("Nom de compte invalide"));
    }
}
