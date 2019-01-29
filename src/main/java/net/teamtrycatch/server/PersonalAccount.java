package net.teamtrycatch.server;

import net.teamtrycatch.shared.Transaction;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PersonalAccount implements Account {
    private int accountNum;
    private String accountHolderName;
    private String username;
    private byte[] passwordHashed;
    private byte[] passwordSalt;
    private ArrayList<Transaction> transactions;

    public PersonalAccount(int accountNum, String accountHolderName, String username, String password) {
        this.accountNum = accountNum;
        this.accountHolderName = accountHolderName;
        this.username = username;
        this.saveHashedPassword(password);
        transactions = new ArrayList<>();
    }

    public PersonalAccount(int accountNum, String accountHolderName, String username, String password, List<Transaction> importTransactions) {
        this(accountNum, accountHolderName, username, password);
        transactions.addAll(importTransactions);
    }

    @Override
    public int getAccountNum() {
        return accountNum;
    }

    @Override
    public String getAccountName() {
        return accountHolderName;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setPassword(String newPassword) {
        this.saveHashedPassword(newPassword);
    }

    @Override
    public void setAccountName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    @Override
    public List getAllTransactions() {
        return transactions;
    }

    @Override
    public List getTransactionRange(Date beginDate, Date endDate) {
        // TODO
        return null;
    }

    @Override
    public boolean isAuth(String username, String password) {
        return this.username.equals(username) &&
               Arrays.equals(this.passwordHashed, hashPasswordAndSalt(password));
    }

    private void saveHashedPassword(String passwordPlaintext) {
        // Generate a new salt, as shown in: https://www.baeldung.com/java-password-hashing
        SecureRandom rnd = new SecureRandom();
        this.passwordSalt = new byte[16];
        rnd.nextBytes(this.passwordSalt);
        System.out.print("Salt=");
        for (byte elem : this.passwordSalt) {
            System.out.print(elem);
        }
        System.out.println();

        // Hash salt + password plaintext
        this.passwordHashed = hashPasswordAndSalt(passwordPlaintext);
    }

    private byte[] hashPasswordAndSalt(String password) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), this.passwordSalt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            System.out.print("Hashed PW=");
            for (byte elem : hash) {
                System.out.print(elem);
            }
            System.out.println();
            return hash;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RuntimeException(e); // DEBUG: want to add these exceptions to the method signature? Is there a pattern I can use here to justify this?
        }
    }

    @Override
    public void addTransaction(Transaction t) {
        transactions.add(t);
    }
}
