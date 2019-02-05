package net.teamtrycatch.shared.interfaces;

public class InvalidLogin extends Exception {
    public InvalidLogin(String message) {
        super(message);
    }
}
