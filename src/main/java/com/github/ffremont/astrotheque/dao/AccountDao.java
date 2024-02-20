package com.github.ffremont.astrotheque.dao;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.service.DynamicProperties;

import java.util.List;

public class AccountDao {

    private final List<String> accounts;

    public AccountDao(IoC ioC) {
        accounts = List.of(ioC.get(DynamicProperties.class).getAdminLogin());
    }

    public List<String> getAccounts() {
        return accounts;
    }
}
