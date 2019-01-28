package net.teamtrycatch.server;

import net.teamtrycatch.shared.Transaction;

import java.util.Date;

public class WithdrawalTransaction implements Transaction {
    private Date date;
    private int withdrawalAmount;

    public WithdrawalTransaction(Date date, int withdrawalAmount) {
        this.date = date;
        this.withdrawalAmount = withdrawalAmount;
    }

    @Override
    public String getDescription() {
        return "WITHDRAWAL";
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public int getAmount() {
        return withdrawalAmount;
    }
}
