package net.teamtrycatch.shared.interfaces;

public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException() {
    }
}
