package net.teamtrycatch.shared.interfaces;

public class InvalidSession extends Exception {
    public InvalidSession(String message) {
        super(message);
    }

    public InvalidSession() {
    }
}
