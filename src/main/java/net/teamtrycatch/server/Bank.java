package net.teamtrycatch.server;

import net.teamtrycatch.shared.BankInterface;
import net.teamtrycatch.shared.InvalidLogin;
import net.teamtrycatch.shared.InvalidSession;
import net.teamtrycatch.shared.Statement;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Bank implements BankInterface {
//DEBUG public class Bank extends UnicastRemoteObject implements BankInterface {
    private List<Account> accounts; // users accounts
    // TODO: Make this into an "Account Database" object

    public Bank() throws RemoteException {
        accounts = new ArrayList<>();
    }

    // TODO: What do I do with the "throws RemoteException". Nothing I'm doing will throw that...
    public long login(String username, String password) throws RemoteException, InvalidLogin {
        for (Account account : accounts) {
            if (account.getUsername().equals(username)) {
                if (account.isAuth(username, password)) {
                    System.out.println("LOGIN SUCCESS!"); // DEBUG
                    return 1234; // TODO: save new session to file! return a random sessionId
                } else {
                    System.err.println("Invalid login " + new Date());
                    throw new InvalidLogin("Username and password was not correct");
                }
            }
        }
        throw new InvalidLogin("Username and password was not correct");
    }

    public void deposit(int account, int amount, long sessionID) throws RemoteException, InvalidSession {
        System.out.println("deposit!");
        // TODO: implementation code
    }

    public void withdraw(int account, int amount, long sessionID) throws RemoteException, InvalidSession {
        System.out.println("withdraw!");
        // TODO: implementation code
    }

    public int inquiry(int account, long sessionID) throws RemoteException, InvalidSession {
        System.out.println("inquery!");
        // TODO: implementation code
        return -1;
    }

    public Statement getStatement(Date from, Date to, long sessionID) throws RemoteException, InvalidSession {
        System.out.println("getStatement!");
        // TODO: implementation code
        return null;
    }

    /**
     * Start the Bank server
     * Much of this code is derived from the Oracle Java RMI Tutorial path
     */
    public static void main(String[] args) {
        // Start a security manager, else RMI will not download classes
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            // Create bank instance (this class)
            Bank bank = new Bank(); // DEBUG: Does this need to be the public, over-the-wire interface?
//            BankInterface bank = new Bank();

            bank.createMockAccounts();

            // Create RMI server as a UnicastRemoteObject
            int port;
            if (args.length != 0) {
                port = Integer.parseInt(args[0]);
            } else {
                // Port 0 means use the default
                port = 0;
            }
            BankInterface stub = (BankInterface) UnicastRemoteObject.exportObject(bank, port);

            // Bind compute engine to name server
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("Bank", stub);
            System.out.println("Bank server has been launched and bound to port " + port);

            //DEBUG:
            try {
                bank.login("username1", "password1");
            } catch (InvalidLogin e) {
                e.printStackTrace();
            }
        } catch (RemoteException e) {
            // Swallow exception
            System.err.println("Bank Server RemoteException!");
            e.printStackTrace();
        }
    }

    private void createMockAccounts() {
        PersonalAccount jack = new PersonalAccount(100, "Jack Doe", "username1", "password1");
        jack.addTransaction(new InitialTransaction(new Date(2018, 2, 22, 16, 21), 1000)); // TODO: Actual date in the past
        // TODO: This Date constructor is depreciated?
        jack.addTransaction(new WithdrawalTransaction(new Date(2018, 3, 1, 11, 30), 311));
        jack.addTransaction(new DepositTransaction(new Date(2018, 3, 23, 10, 0), 1200));
        this.accounts.add(jack);
        // TODO: Continue here!
    }
}
