package net.teamtrycatch.server;

import net.teamtrycatch.shared.AccountNotFoundException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.logging.Logger;

public class AccountDatastoreImpl implements AccountDatastore {
    private List<Account> accounts; // Represents the actual "database"
    private HashMap<String, Account> usernameIndex; // Represents an "index" on the database
    private HashMap<Integer, Account> accountNumberIndex; // Represents an "index" on the database

    private static Logger logger = Logger.getLogger("AccountDatastoreImpl");

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
            throw new AccountNotFoundException("Account number " + accountNum + " was not found");
        }
    }

    @Override
    public Account findByUsername(String username) throws AccountNotFoundException {
        if (usernameIndex.containsKey(username)) {
            return usernameIndex.get(username);
        } else {
            throw new AccountNotFoundException("Username + '" + username + "' was not found");
        }
    }

    // TODO: JavaDoc
    public static void createMockAccounts(AccountDatastore accounts) {
        try {
            DateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm");
            PersonalAccount jack = new PersonalAccount(100, "Jack Doe", "username1", "password1");
            jack.addTransaction(new InitialTransaction(df.parse("22 Feb 2018 16:21"), 1000));
            jack.addTransaction(new WithdrawalTransaction(df.parse("1 Mar 2018 11:30"), 311));
            jack.addTransaction(new DepositTransaction(df.parse("23 Mar 2018 10:00"), 1200));
            accounts.add(jack);
            logger.info("Added account: " + jack + " with password " + "password1");

            PersonalAccount jane = new PersonalAccount(200, "Jane Doe", "username2", "password2");
            jane.addTransaction(new InitialTransaction(df.parse("20 Mar 2016 10:30"), 2000));
            jane.addTransaction(new DepositTransaction(df.parse("1 Apr 2016 14:12"), 1500));
            jane.addTransaction(new WithdrawalTransaction(df.parse("2 Apr 2016 12:55"), 120));
            jane.addTransaction(new WithdrawalTransaction(df.parse("2 Apr 2016 14:21"), 18));
            jane.addTransaction(new WithdrawalTransaction(df.parse("14 Aug 2018 13:51"), 220));
            jane.addTransaction(new DepositTransaction(df.parse("1 Sep 2018 14:05"), 1850));
            accounts.add(jane);
            logger.info("Added account: " + jane + " with password " + "password2");
        } catch (DuplicateAccountInformationException e) {
            logger.severe("Could not set up server! Duplicate account created: " + e.getMessage());
            throw new RuntimeException(e); // This SHOULD crash the server process, so throw a RuntimeException
        } catch (ParseException e) {
            logger.severe("Could not set up server! Date could not be parsed: " + e.getMessage());
            throw new RuntimeException(e); // This SHOULD crash the server process, so throw a RuntimeException
        }
    }

}
