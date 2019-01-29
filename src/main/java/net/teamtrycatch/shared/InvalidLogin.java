package net.teamtrycatch.shared;

public class InvalidLogin extends Exception {
    public InvalidLogin(String message) {
        super(message);
    }
}
