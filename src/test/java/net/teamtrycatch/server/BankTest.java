package net.teamtrycatch.server;

import net.teamtrycatch.shared.interfaces.AccountNotFoundException;
import net.teamtrycatch.shared.server.DepositTransaction;
import net.teamtrycatch.shared.server.InitialTransaction;
import net.teamtrycatch.shared.interfaces.InvalidLogin;
import net.teamtrycatch.shared.interfaces.InvalidSession;
import net.teamtrycatch.shared.server.WithdrawalTransaction;
import net.teamtrycatch.shared.interfaces.Statement;
import net.teamtrycatch.shared.interfaces.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.Assert.*;

public class BankTest {
    private static final int ACCOUNT_NUM = 101;
    private static final int ACCOUNT_NUM_B = 201;
    private static final String USERNAME = "login123";
    private static final String USERNAME_B = "loginB";
    private static final String PASSWORD = "password123";
    private static final String PASSWORD_B = "passwordB";
    private Bank bank;
    private PersonalAccount account, accountB;
    private static final DateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm");

    @Before
    public void setUp() throws Exception {
        bank = new Bank();

        account = new PersonalAccount(ACCOUNT_NUM, "Account Holder", USERNAME, PASSWORD);
        account.addTransaction(new InitialTransaction(df.parse("1 Jan 2016 12:00"), 1000));
        bank.accounts.add(account);

        accountB = new PersonalAccount(ACCOUNT_NUM_B, "Bccount Bolder", USERNAME_B, PASSWORD_B);
        accountB.addTransaction(new InitialTransaction(df.parse("1 Jan 2016 12:00"), 2000));
        bank.accounts.add(accountB);
    }

    @Test
    public void login() throws Exception {
        try {
            bank.login("NOT_USERNAME", PASSWORD);
            fail("Username and password were not correct");
        } catch (InvalidLogin ignored) {}
        try {
            bank.login(USERNAME, "NOT_PASSWORD");
            fail("Username and password were not correct");
        } catch (InvalidLogin ignored) {}
        try {
            bank.login("NOT_USERNAME", "NOT_PASSWORD");
            fail("Username and password were not correct");
        } catch (InvalidLogin ignored) {}
        try {
            bank.login(USERNAME, PASSWORD);
        } catch (InvalidLogin e) {
            fail("Username and password were correct");
        }
    }

    @Test
    public void notLoggedIn() throws Exception {
        // Operations before being logged in fail
        try {
            bank.deposit(ACCOUNT_NUM, 100, 1);
            fail("Cannot do operations before logging in");
        } catch (InvalidSession ignored) {}

        // Login afterwards with that account number still works
        long sessionID = bank.login(USERNAME, PASSWORD);
        bank.deposit(ACCOUNT_NUM, 100, sessionID);
    }

    @Test
    public void sessions() throws Exception {
        long sessionID = bank.login(USERNAME, PASSWORD);
        try {
            bank.deposit(ACCOUNT_NUM, 100, 1234567);
            fail("Cannot do operations with invalid session ID");
        } catch (InvalidSession ignored) {}
        bank.deposit(ACCOUNT_NUM, 100, sessionID); // Operation succeeds

        // Login as one user, then attempt operations as the other fail
        try {
            bank.deposit(ACCOUNT_NUM_B, 100, sessionID);
            fail("AccountB is not the one that logged in");
        } catch (InvalidSession ignored) {}

        // Logged-in session should be invalidated after attempting an operation with the wrong account number
        try {
            bank.deposit(ACCOUNT_NUM, 100, sessionID);
            fail("Session is now invalided");
        } catch (InvalidSession ignored) {}

        // Can login again
        sessionID = bank.login(USERNAME, PASSWORD);
        bank.deposit(ACCOUNT_NUM, 100, sessionID); // Operation succeeds
    }

    @Test
    public void accountNumbers() throws Exception {
        long sessionID = bank.login(USERNAME, PASSWORD);
        bank.deposit(ACCOUNT_NUM, 100, sessionID); // Operation succeeds

        try {
            bank.deposit(987654, 100, sessionID);
            fail("Cannot do operations with unknown account number");
        } catch (AccountNotFoundException ignored) {}

        sessionID = bank.login(USERNAME, PASSWORD);
        try {
            bank.deposit(ACCOUNT_NUM_B, 100, sessionID);
            fail("Cannot do operations with a different account's number");
        } catch (InvalidSession ignored) {}
    }

    @Test
    public void transactions() throws Exception {
        long sessionID = bank.login(USERNAME, PASSWORD);
        assertEquals(1000, bank.inquiry(ACCOUNT_NUM, sessionID));
        bank.deposit(ACCOUNT_NUM, 100, sessionID);
        assertEquals(1100, bank.inquiry(ACCOUNT_NUM, sessionID));
        bank.deposit(ACCOUNT_NUM, 200, sessionID);
        assertEquals(1300, bank.inquiry(ACCOUNT_NUM, sessionID));
        bank.withdraw(ACCOUNT_NUM, 30, sessionID);
        assertEquals(1270, bank.inquiry(ACCOUNT_NUM, sessionID));
        bank.deposit(ACCOUNT_NUM, 2000, sessionID);
        assertEquals(3270, bank.inquiry(ACCOUNT_NUM, sessionID));
    }

    @Test
    public void getStatement() throws Exception {
        // 2016
        account.addTransaction(new WithdrawalTransaction(df.parse("1 Jun 2016 12:00"), 1));
        // 2017
        account.addTransaction(new DepositTransaction(df.parse("1 Jun 2017 12:00"), 1));
        account.addTransaction(new DepositTransaction(df.parse("1 Jun 2017 12:00"), 1));
        // 2018
        account.addTransaction(new WithdrawalTransaction(df.parse("1 Jun 2018 12:00"), 1));
        account.addTransaction(new WithdrawalTransaction(df.parse("1 Jun 2018 12:00"), 1));
        account.addTransaction(new WithdrawalTransaction(df.parse("1 Jun 2018 12:00"), 1));

        long sessionID = bank.login(USERNAME, PASSWORD);

        Statement stmt = bank.getStatement(ACCOUNT_NUM, df.parse("1 Jan 2016 12:00"), df.parse("31 Dec 2016 12:00"), sessionID);
        List<Transaction> transactionRange = stmt.getTransactions();
        assertEquals(1, transactionRange.size());
        for (Transaction t : transactionRange) {
            assertTrue(t instanceof WithdrawalTransaction);
        }

        stmt = bank.getStatement(ACCOUNT_NUM, df.parse("1 Jan 2017 12:00"), df.parse("31 Dec 2017 12:00"), sessionID);
        transactionRange = stmt.getTransactions();
        assertEquals(2, transactionRange.size());
        for (Transaction t : transactionRange) {
            assertTrue(t instanceof DepositTransaction);
        }

        stmt = bank.getStatement(ACCOUNT_NUM, df.parse("1 Jan 2018 12:00"), df.parse("31 Dec 2018 12:00"), sessionID);
        transactionRange = stmt.getTransactions();
        assertEquals(3, transactionRange.size());
        for (Transaction t : transactionRange) {
            assertTrue(t instanceof WithdrawalTransaction);
        }

        assertEquals("Account Holder", stmt.getAccountName());
    }
}
