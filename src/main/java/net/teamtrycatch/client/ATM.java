package net.teamtrycatch.client;

import net.teamtrycatch.shared.AccountNotFoundException;
import net.teamtrycatch.shared.BankInterface;
import net.teamtrycatch.shared.InvalidLogin;
import net.teamtrycatch.shared.InvalidSession;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ATM {
    public static void main (String[] args) {
        // get user's input, and perform the operations

        String registryAddress = "localhost"; // TODO: args[0]
        int port = 7777; // TODO: args[1]

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
//            Registry registry = LocateRegistry.getRegistry(registryAddress, port);
            Registry registry = LocateRegistry.getRegistry();
            BankInterface bank = (BankInterface) registry.lookup("Bank");

            long sessionID = bank.login("username1", "password1");

            bank.inquiry(200, sessionID);

        } catch (InvalidLogin | InvalidSession | AccountNotFoundException e) {
            System.err.println("ATM Client InvalidLogin");
            e.printStackTrace();
        } catch (RemoteException | NotBoundException e) {
            // Swallow exception
            System.err.println("ATM Client RemoteException!");
            e.printStackTrace();
        }
    }
}
