package net.teamtrycatch.server;

import net.teamtrycatch.shared.Transaction;

import java.util.Date;
import java.util.List;

public interface Account {
    public int getAccountNum();
    // There should be no way to change an account number after account creation

    public String getAccountName(); // returns name of account holder

    public void setAccountName(String accountName);

    public String getUsername(); // returns the login name for this account

    public void setUsername(String username);

    public void setPassword(String newPassword);

    public int getBalance();

    public void addTransaction(Transaction t);

    /**
     * Get all transactions
     * @return All transactions for this account
     */
    public List<Transaction> getAllTransactions();

    /**
     * Get only those transactions within a date range
     * @param beginDate Begin data range, inclusive
     * @param endDate End of date range, inclusive
     * @return All transactions for this account within the date range
     */
    public List<Transaction> getTransactionRange(Date beginDate, Date endDate);

    /**
     * Determine if this account can be authorised with a username/password combo
     * @param username
     * @param password
     * @return true if authorised, false if there is no match
     */
    public boolean isAuth(String username, String password);
}
