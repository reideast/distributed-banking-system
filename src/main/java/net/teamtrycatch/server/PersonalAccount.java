package net.teamtrycatch.server;

import net.teamtrycatch.shared.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PersonalAccount implements Account {
    private int accountNum;
    private String accountHolderName;
    private ArrayList<Transaction> transactions;

    public PersonalAccount(int accountNum, String accountHolderName) {
        this.accountNum = accountNum;
        this.accountHolderName = accountHolderName;
        transactions = new ArrayList<>();
    }

    public PersonalAccount(int accountNum, String accountHolderName, List<Transaction> importTransactions) {
        this(accountNum, accountHolderName);
        transactions.addAll(importTransactions);
    }

    @Override
    public int getAccountNum() {
        return accountNum;
    }

    // There should be no way to change an account number after account creation

    @Override
    public String getAccountName() {
        return accountHolderName;
    }

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
