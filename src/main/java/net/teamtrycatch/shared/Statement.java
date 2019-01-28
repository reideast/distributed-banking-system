package net.teamtrycatch.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public interface Statement extends Serializable {
    public int getAccountNum(); // returns account number associated with this statement

    public Date getStartDate(); // returns start Date of Statement

    public Date getEndDate(); // returns end Date of Statement

    public String getAccountName(); // returns name of account holder

    public List<Transaction> getTransactions(); // returns list of Transaction objects that encapsulate details about each transaction
}
