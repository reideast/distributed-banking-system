package net.teamtrycatch.shared;

public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException() {
    }
}
