package net.teamtrycatch.server;

import net.teamtrycatch.shared.interfaces.AccountNotFoundException;

public interface AccountDatastore {
    /**
     * Will enforce that accounts must not be created with duplicate account numbers or usernames
     * @param account New account to add to DB
     */
    public void add(Account account) throws DuplicateAccountInformationException;

    public Account findByAccountNum(int accountNum) throws AccountNotFoundException;

    public Account findByUsername(String username) throws AccountNotFoundException;
}
