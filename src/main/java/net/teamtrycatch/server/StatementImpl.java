package net.teamtrycatch.server;

import net.teamtrycatch.shared.Statement;
import net.teamtrycatch.shared.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatementImpl implements Statement {
    private int accountNum;
    private Date startDate;
    private Date endDate;
    private String accountName;
    private ArrayList<Transaction> transactions;

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

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }
}
