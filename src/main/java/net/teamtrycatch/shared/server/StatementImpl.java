package net.teamtrycatch.shared.server;

import net.teamtrycatch.shared.interfaces.Statement;
import net.teamtrycatch.shared.interfaces.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatementImpl implements Statement {
    private int accountNum;
    private Date startDate;
    private Date endDate;
    private String accountName;
    private ArrayList<Transaction> transactions;

    public StatementImpl(int accountNum, String accountName, Date startDate, Date endDate) {
        this.accountNum = accountNum;
        this.startDate = startDate;
        this.endDate = endDate;
        this.accountName = accountName;
        this.transactions = new ArrayList<>();
    }

    public StatementImpl(int accountNum, String accountName, Date startDate, Date endDate, List<Transaction> transactions) {
        this(accountNum, accountName, startDate, endDate);
        this.transactions.addAll(transactions);
    }

    @Override
    public int getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(int accountNum) {
        this.accountNum = accountNum;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction t) {
        this.transactions.add(t);
    }
}
