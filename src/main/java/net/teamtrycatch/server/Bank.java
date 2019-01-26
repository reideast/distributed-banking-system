package net.teamtrycatch.server;

import net.teamtrycatch.shared.BankInterface;
import net.teamtrycatch.shared.InvalidLogin;
import net.teamtrycatch.shared.InvalidSession;
import net.teamtrycatch.shared.Statement;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.List;

public class Bank extends UnicastRemoteObject implements BankInterface {
    private List<Account> accounts; // users accounts

    public Bank() throws RemoteException {
    }

    public long login(String username, String password) throws RemoteException, InvalidLogin {
        //implementation code
        return -1;
    }

    public void deposit(int account, int amount, long sessionID) throws RemoteException, InvalidSession {
        // implementation code
    }

    public void withdraw(int account, int amount, long sessionID) throws RemoteException, InvalidSession {
        // implementation code
    }

    public int inquiry(int account, long sessionID) throws RemoteException, InvalidSession {
        // implementation code
        return -1;
    }

    public Statement getStatement(Date from, Date to, long sessionID) throws RemoteException, InvalidSession {
        // implementation code
        return null;
    }

    public static void main(String[] args) throws Exception {
        // initialise Bank server - see sample code in the notes and online RMI tutorials for details
    }
}
