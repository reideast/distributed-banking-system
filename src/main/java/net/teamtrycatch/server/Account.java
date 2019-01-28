package net.teamtrycatch.server;

import java.util.Date;
import java.util.List;

public interface Account {
    public int getAccountNum();

    public String getAccountName(); // returns name of account holder

    public List getAllTransactions();

    public List getTransactionRange(Date beginDate, Date endDate); // returns only those transactions within a date range (inclusive)
}
