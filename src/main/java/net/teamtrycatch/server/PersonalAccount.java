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
    private boolean isAccountInitialised;
    private int balance;

    public PersonalAccount(int accountNum, String accountHolderName, String username, String password) {
        this.accountNum = accountNum;
        this.accountHolderName = accountHolderName;
        this.username = username;
        this.generateHashedPassword(password);
        transactions = new ArrayList<>();
        isAccountInitialised = false;
        balance = 0;
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
        this.generateHashedPassword(newPassword);
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

    private void generateHashedPassword(String passwordPlaintext) {
        // Generate a new salt and store it as a Byte array, as shown in: https://www.baeldung.com/java-password-hashing
        SecureRandom rnd = new SecureRandom();
        this.passwordSalt = new byte[64];
        rnd.nextBytes(this.passwordSalt);

        // Generate a cryptographic hash from salt + password plaintext
        this.passwordHashed = hashPasswordAndSalt(passwordPlaintext);
    }

    private byte[] hashPasswordAndSalt(String password) {
        try {
            // Password hashing method is from: https://www.baeldung.com/java-password-hashing
            // Specify the building blocks of th password: plaintext password+salt with 65536 iterations of the algorithm and key length 128
            KeySpec spec = new PBEKeySpec(password.toCharArray(), this.passwordSalt, 65536, 128);
            // Utilise the PBKDF2 algorithm for hashing
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            // Generate the cryptographic hash based on the spec
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // These exceptions mean that the password hashing will not work, and the entire program SHOULD crash, so convert these to an Unchecked exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getBalance() {
        return balance;
    }

    @Override
    public void addTransaction(Transaction t) {
        if (t instanceof InitialTransaction) {
            if (isAccountInitialised) {
                throw new IllegalStateException("Account has already been initialised with a beginning balance");
            }
            balance = t.getAmount();
        } else if (t instanceof DepositTransaction) {
            balance += t.getAmount();
        } else if (t instanceof WithdrawalTransaction) {
            balance -= t.getAmount();
        } else {
            throw new IllegalArgumentException("Invalid transaction type provided: " + t.getClass().getName());
        }
        transactions.add(t);
    }

    @Override
    public String toString() {
        return "PersonalAccount{" +
                "accountNum=" + accountNum +
                ", accountHolderName='" + accountHolderName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
