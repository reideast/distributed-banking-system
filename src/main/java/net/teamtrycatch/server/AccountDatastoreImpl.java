package net.teamtrycatch.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountDatastoreImpl implements AccountDatastore {
    private List<Account> accounts; // Represents the actual "database"
    private HashMap<String, Account> usernameIndex; // Represents an "index" on the database
    private HashMap<Integer, Account> accountNumberIndex; // Represents an "index" on the database

    public AccountDatastoreImpl() {
        accounts = new ArrayList<>();
        usernameIndex = new HashMap<>();
        accountNumberIndex = new HashMap<>();
    }

    @Override
    public void add(Account account) throws DuplicateAccountInformationException {
        if (!usernameIndex.containsKey(account.getUsername())) {
            usernameIndex.put(account.getUsername(), account);
        } else {
            throw new DuplicateAccountInformationException("Account with username '" + account.getUsername() + "' already exists");
        }

        if (!accountNumberIndex.containsKey(account.getAccountNum())) {
            accountNumberIndex.put(account.getAccountNum(), account);
        } else {
            throw new DuplicateAccountInformationException("Account with account number '" + account.getAccountNum() + "' already exists");
        }

        accounts.add(account);
    }

    @Override
    public Account findByAccountNum(int accountNum) throws AccountNotFoundException {
        if (accountNumberIndex.containsKey(accountNum)) {
            return accountNumberIndex.get(accountNum);
        } else {
            throw new AccountNotFoundException();
        }
    }

    @Override
    public Account findByUsername(String username) throws AccountNotFoundException {
        if (usernameIndex.containsKey(username)) {
            return usernameIndex.get(username);
        } else {
            throw new AccountNotFoundException();
        }
    }
}
