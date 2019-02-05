package net.teamtrycatch.shared.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface BankInterface extends Remote {
    public long login(String username, String password) throws RemoteException, InvalidLogin, ServerException;

    public void deposit(int accountnum, int amount, long sessionID) throws RemoteException, InvalidSession, AccountNotFoundException, ServerException;

    public void withdraw(int accountnum, int amount, long sessionID) throws RemoteException, InvalidSession, AccountNotFoundException, ServerException;

    public int inquiry(int accountnum, long sessionID) throws RemoteException, InvalidSession, AccountNotFoundException, ServerException;

    public Statement getStatement(int accountnum, Date from, Date to, long sessionID) throws RemoteException, InvalidSession, AccountNotFoundException, ServerException;
}
