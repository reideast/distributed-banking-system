package net.teamtrycatch.server;

import net.teamtrycatch.shared.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.logging.Logger;

public class Bank implements BankInterface {
    AccountDatastore accounts;
    private static Logger logger = Logger.getLogger("Bank");

    public Bank() throws RemoteException {
        accounts = new AccountDatastoreImpl();
    }

    // TODO: What do I do with the "throws RemoteException". Nothing I'm doing will throw that...
    public long login(String username, String password) throws RemoteException, InvalidLogin {
        try {
            Account accountForUsername = accounts.findByUsername(username);
            if (accountForUsername.isAuth(username, password)) {
                logger.info("Login success, accountNum=" + accountForUsername.getAccountNum() + ", name='" + accountForUsername.getAccountName() + "'");
                return startNewSession(accountForUsername.getAccountNum());
            } else {
                logger.warning("Invalid login (password '" + password + "' was incorrect for username '" + username + "')" + new Date());
                throw new InvalidLogin("Username and password was not correct");
            }
        } catch (AccountNotFoundException e) {
            logger.warning("Invalid login (username '" + username + "' not found) " + new Date());
            // This translation to an InvalidLogin exception is for security, so that client does not get information about which was invalid, username or password
            throw new InvalidLogin("Username and password was not correct");
        }
    }

    /**
     * Begin a new session, generating a session ID, and saving expiration time and account number to a file
     * @param accountNum Details of account to save
     * @return Generated session ID
     */
    private long startNewSession(int accountNum) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Validate that this session currently exists, is valid for this time range, and is for this account number
     * @param sessionID Session file to locate
     * @param accountNum Number which should be inside that session file
     * @throws InvalidSession Error if session does not exist or if account number is not for this session
     */
    private void verifySession(long sessionID, int accountNum) throws InvalidSession {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Remove this session file from disk so it cannot be used again
     * @param sessionID Session file to find. May or may not exist on disk
     */
    private void invalidateSession(long sessionID) {
        throw new RuntimeException("Not implemented");
    }

    public void deposit(int accountNum, int amount, long sessionID) throws RemoteException, InvalidSession, AccountNotFoundException {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must not be negative");
        }
        Account account = accounts.findByAccountNum(accountNum);
        verifySession(sessionID, accountNum);
        account.addTransaction(new DepositTransaction(new Date(), amount));
    }

    public void withdraw(int accountNum, int amount, long sessionID) throws RemoteException, InvalidSession, AccountNotFoundException {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must not be negative");
        }
        Account account = accounts.findByAccountNum(accountNum);
        verifySession(sessionID, accountNum);
        account.addTransaction(new WithdrawalTransaction(new Date(), amount));
    }

    public int inquiry(int accountNum, long sessionID) throws RemoteException, InvalidSession, AccountNotFoundException {
        Account account = accounts.findByAccountNum(accountNum);
        verifySession(sessionID, accountNum);
        return account.getBalance();
    }

    public Statement getStatement(int accountNum, Date from, Date to, long sessionID) throws RemoteException, InvalidSession, AccountNotFoundException {
        if (to.before(from)) {
            throw new IllegalArgumentException("From date must be chronologically before To date");
        }

        verifySession(sessionID, accountNum);
        Account account = accounts.findByAccountNum(accountNum);
        return new StatementImpl(account.getAccountNum(), account.getAccountName(), from, to,
                account.getTransactionRange(from, to));
    }

    /**
     * Start the Bank server
     * Much of this code is derived from the Oracle Java RMI Tutorial path
     */
    public static void main(String[] args) throws RemoteException {
        // Parse command line arguments
        int port;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        } else {
            System.out.println("Usage: " + Bank.class.getSimpleName() + " [port number]");
            return;
        }

        // Start a security manager, else RMI will not download classes
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            // Create bank instance (this class)
            Bank bank = new Bank();

            AccountDatastoreImpl.createMockAccounts(bank.accounts); // Note: This is for the simplified application only. A real app would use a database for these

            // Create RMI server as a UnicastRemoteObject
            BankInterface stub = (BankInterface) UnicastRemoteObject.exportObject(bank, port);

            // Bind compute engine to name server
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("Bank", stub);

            logger.info("Bank server has been launched and bound to port " + port);
        } catch (RemoteException e) {
            logger.severe("Server could not startup due to a remote exception" + e.getMessage());
            throw e; // This SHOULD crash the server process, so re-throw
        }
    }
}
