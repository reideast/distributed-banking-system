package net.teamtrycatch.server;

import net.teamtrycatch.shared.InvalidSession;

public class AccountNotFoundException extends InvalidSession {
    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException() {
    }
}
