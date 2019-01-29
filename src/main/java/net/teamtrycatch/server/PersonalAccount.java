package net.teamtrycatch.server;

import net.teamtrycatch.shared.Transaction;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PersonalAccount implements Account {
    private int accountNum;
    private String accountHolderName;
    private String username;
    private byte[] passwordHashed;
    private byte[] passwordSalt;
    private ArrayList<Transaction> transactions;

    public PersonalAccount(int accountNum, String accountHolderName, String username, String password) {
        this.accountNum = accountNum;
        this.accountHolderName = accountHolderName;
        this.username = username;
        this.saveHashedPassword(password);
        transactions = new ArrayList<>();
    }

    public PersonalAccount(int accountNum, String accountHolderName, String username, String password, List<Transaction> importTransactions) {
        this(accountNum, accountHolderName, username, password);
        transactions.addAll(importTransactions);
    }

    @Override
    public int getAccountNum() {
        return accountNum;
    }

    @Override
    public String getAccountName() {
        return accountHolderName;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setPassword(String newPassword) {
        this.saveHashedPassword(newPassword);
    }

    @Override
    public void setAccountName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    @Override
    public List getAllTransactions() {
        return transactions;
    }

    @Override
    public List getTransactionRange(Date beginDate, Date endDate) {
        // TODO
        return null;
    }

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }
}
