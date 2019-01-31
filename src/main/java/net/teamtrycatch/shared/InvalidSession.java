package net.teamtrycatch.shared;

public class InvalidSession extends Exception {
    public InvalidSession(String message) {
        super(message);
    }

    public InvalidSession() {
    }
}
