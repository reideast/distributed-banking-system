package net.teamtrycatch.server;

import net.teamtrycatch.shared.Transaction;

import java.util.Date;

public class DepositTransaction implements Transaction {
    private Date date;
    private int depositAmount;

    public DepositTransaction(Date date, int depositAmount) {
        this.date = date;
        this.depositAmount = depositAmount;
    }

    @Override
    public String getDescription() {
        return "DEPOSIT";
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public int getAmount() {
        return depositAmount;
    }
}
