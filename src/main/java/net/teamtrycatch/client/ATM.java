package net.teamtrycatch.client;

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
            Registry registry = LocateRegistry.getRegistry(registryAddress, port);
            BankInterface bank = (BankInterface) registry.lookup("Bank");

            long sessionID = bank.login("user1", "password1");

            bank.inquiry(1234, sessionID);

        } catch (InvalidLogin e) {
            System.err.println("ATM Client InvalidLogin");
            e.printStackTrace();
        } catch (InvalidSession e) {
            System.err.println("ATM Client InvalidSession");
            e.printStackTrace();
        } catch (RemoteException | NotBoundException e) {
            // Swallow exception
            System.err.println("ATM Client RemoteException!");
            e.printStackTrace();
        }
    }
}
