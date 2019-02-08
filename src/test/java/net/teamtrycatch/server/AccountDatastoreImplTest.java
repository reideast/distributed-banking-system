package net.teamtrycatch.server;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountDatastoreImplTest {
    AccountDatastoreImpl db;

    @Before
    public void setup() {
        db = new AccountDatastoreImpl();
    }

    @Test
    public void cannotAddDuplicate() throws Exception {
        Account a = new PersonalAccount(100, "Name", "user", "pass");
        Account dupNum = new PersonalAccount(100, "Name2", "user2", "pass2");
        Account dupUsername = new PersonalAccount(200, "Name3", "user", "pass3");

        db.add(a);

        try {
            db.add(dupNum);
            fail("Should have failed with duplicate account number");
        } catch (DuplicateAccountInformationException ignored) {}

        try {
            db.add(dupUsername);
            fail("Should have failed with duplicate username");
        } catch (DuplicateAccountInformationException ignored) {}
    }

    @Test
    public void findByAccountNum() throws Exception {
        Account a = new PersonalAccount(100, "Name", "user", "pass");

        db.add(a);

        assertEquals(a, db.findByAccountNum(100));
    }

    @Test
    public void findByUsername() throws Exception {
        Account a = new PersonalAccount(100, "Name", "user", "pass");

        db.add(a);

        assertEquals(a, db.findByUsername("user"));
    }
}
