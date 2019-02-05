package net.teamtrycatch.server;

import net.teamtrycatch.shared.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.Assert.*;

public class PersonalAccountTest {
    private PersonalAccount account;
    private static final DateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm");

    private static final int ACCOUNT_NUM = 42;
    private static final String USERNAME = "login123";
    private static final String PASSWORD = "password123";

    @Before
    public void setUp() throws Exception {
        account = new PersonalAccount(ACCOUNT_NUM, "Foo Bar", USERNAME, PASSWORD);
    }

    @Test
    public void isAuth() {
        assertTrue(account.isAuth(USERNAME, PASSWORD));
        assertFalse(account.isAuth("NOT_USERNAME", PASSWORD));
        assertFalse(account.isAuth(USERNAME, "NOT_PASSWORD"));
        assertFalse(account.isAuth("NOT_USERNAME", "NOT_PASSWORD"));
    }

    @Test
    public void setPassword() {
        // Resetting the password should cause auth to fail
        assertTrue(account.isAuth(USERNAME, PASSWORD));
        final String newPassword = "newPassword";
        account.setPassword(newPassword);
        assertFalse(account.isAuth(USERNAME, PASSWORD));
        assertTrue(account.isAuth(USERNAME, newPassword));
    }

    @Test
    public void initialTransactionRequired() throws Exception {
        try {
            account.addTransaction(new DepositTransaction(df.parse("1 Jan 2016 12:00"), 1));
            fail("Account can have not have any transactions added until an InitialTransaction is added");
        } catch (IllegalStateException ignored) {}

        account.addTransaction(new InitialTransaction(df.parse("1 Jan 2016 12:00"), 1));

        try {
            account.addTransaction(new InitialTransaction(df.parse("1 Jan 2016 12:00"), 1));
            fail("Account cannot have another initial transaction");
        } catch (IllegalStateException ignored) {}

        try {
        account.addTransaction(new DepositTransaction(df.parse("1 Jan 2016 12:00"), 1));
        } catch (IllegalStateException e) {
            fail("Account should have allowed transactions to be added after being initialised");
        }

        try {
            account.addTransaction(new InitialTransaction(df.parse("1 Jan 2016 12:00"), 1));
            fail("Account cannot have another initial transaction");
        } catch (IllegalStateException ignored) {}
    }

    @Test
    public void getBalance() throws Exception {
        assertEquals(0, account.getBalance());

        account.addTransaction(new InitialTransaction(df.parse("1 Jan 2016 12:00"), 1000));
        assertEquals(1000, account.getBalance());

        account.addTransaction(new DepositTransaction(df.parse("1 Jan 2016 12:00"), 200));
        assertEquals(1200, account.getBalance());

        account.addTransaction(new WithdrawalTransaction(df.parse("1 Jan 2016 12:00"), 400));
        assertEquals(800, account.getBalance());

        account.addTransaction(new DepositTransaction(df.parse("1 Jan 2016 12:00"), 2000));
        account.addTransaction(new DepositTransaction(df.parse("1 Jan 2016 12:00"), 200));
        account.addTransaction(new WithdrawalTransaction(df.parse("1 Jan 2016 12:00"), 20));
        account.addTransaction(new WithdrawalTransaction(df.parse("1 Jan 2016 12:00"), 8));
        account.addTransaction(new WithdrawalTransaction(df.parse("1 Jan 2016 12:00"), 300));
        account.addTransaction(new DepositTransaction(df.parse("1 Jan 2016 12:00"), 100));
        assertEquals(2772, account.getBalance());
    }

    @Test
    public void getAllTransactions() throws Exception {
        account.addTransaction(new InitialTransaction(df.parse("1 Jan 2016 12:00"), 1));
        account.addTransaction(new DepositTransaction(df.parse("1 Jan 2016 12:00"), 1));
        account.addTransaction(new WithdrawalTransaction(df.parse("1 Jan 2016 12:00"), 1));
        assertEquals(3, account.getAllTransactions().size());

        account.addTransaction(new DepositTransaction(df.parse("1 Jan 2016 12:00"), 1));
        account.addTransaction(new DepositTransaction(df.parse("1 Jan 2016 12:00"), 1));
        account.addTransaction(new WithdrawalTransaction(df.parse("1 Jan 2016 12:00"), 1));
        account.addTransaction(new WithdrawalTransaction(df.parse("1 Jan 2016 12:00"), 1));
        account.addTransaction(new WithdrawalTransaction(df.parse("1 Jan 2016 12:00"), 1));
        account.addTransaction(new DepositTransaction(df.parse("1 Jan 2016 12:00"), 1));
        assertEquals(9, account.getAllTransactions().size());
    }

    @Test
    public void getTransactionRange() throws Exception {
        // 2016
        account.addTransaction(new InitialTransaction(df.parse("1 Jun 2016 12:00"), 1));
        // 2017
        account.addTransaction(new DepositTransaction(df.parse("1 Jun 2017 12:00"), 1));
        account.addTransaction(new DepositTransaction(df.parse("1 Jun 2017 12:00"), 1));
        // 2018
        account.addTransaction(new WithdrawalTransaction(df.parse("1 Jun 2018 12:00"), 1));
        account.addTransaction(new WithdrawalTransaction(df.parse("1 Jun 2018 12:00"), 1));
        account.addTransaction(new WithdrawalTransaction(df.parse("1 Jun 2018 12:00"), 1));

        List<Transaction> transactionRange = account.getTransactionRange(df.parse("1 Jan 2016 12:00"), df.parse("31 Dec 2016 12:00"));
        assertEquals(1, transactionRange.size());
        for (Transaction t : transactionRange) {
            assertTrue(t instanceof InitialTransaction);
        }

        transactionRange = account.getTransactionRange(df.parse("1 Jan 2017 12:00"), df.parse("31 Dec 2017 12:00"));
        assertEquals(2, transactionRange.size());
        for (Transaction t : transactionRange) {
            assertTrue(t instanceof DepositTransaction);
        }

        transactionRange = account.getTransactionRange(df.parse("1 Jan 2018 12:00"), df.parse("31 Dec 2018 12:00"));
        assertEquals(3, transactionRange.size());
        for (Transaction t : transactionRange) {
            assertTrue(t instanceof WithdrawalTransaction);
        }

        assertEquals(6, account.getAllTransactions().size());
    }
}
