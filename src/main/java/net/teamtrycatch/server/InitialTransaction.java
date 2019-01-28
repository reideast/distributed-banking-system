package net.teamtrycatch.server;

import net.teamtrycatch.shared.Transaction;

import java.util.Date;

public class InitialTransaction implements Transaction {
    private Date date;
    private int initialBalance;

    public InitialTransaction(Date date, int initialBalance) {
        this.date = date;
        this.initialBalance = initialBalance;
    }

    @Override
    public String getDescription() {
        return "INITIAL BALANCE";
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public int getAmount() {
        return initialBalance;
    }
}
