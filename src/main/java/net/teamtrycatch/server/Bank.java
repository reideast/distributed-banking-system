package net.teamtrycatch.server;

import net.teamtrycatch.shared.interfaces.AccountNotFoundException;
import net.teamtrycatch.shared.interfaces.BankInterface;
import net.teamtrycatch.shared.interfaces.InvalidLogin;
import net.teamtrycatch.shared.interfaces.InvalidSession;
import net.teamtrycatch.shared.interfaces.ServerException;
import net.teamtrycatch.shared.interfaces.Statement;
import net.teamtrycatch.shared.server.DepositTransaction;
import net.teamtrycatch.shared.server.StatementImpl;
import net.teamtrycatch.shared.server.WithdrawalTransaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

public class Bank implements BankInterface {
    private static Logger logger = Logger.getLogger("Bank");

    AccountDatastore accounts;

    private Random rnd = new Random();
    private static final TemporalAmount FIVE_MINUTES = Duration.ofMinutes(5);
    private static final String SESSION_DIRECTORY = "server-sessions";

    public Bank() throws RemoteException {
        accounts = new AccountDatastoreImpl();
    }

    public long login(String username, String password) throws RemoteException, InvalidLogin, ServerException {
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
     * @throws ServerException Generated if server has critical error (from IO)
     */
    private long startNewSession(int accountNum) throws ServerException {
        long sessionID = Math.abs(rnd.nextLong());

        try (FileWriter file = new FileWriter(SESSION_DIRECTORY + File.separator + sessionID + ".session")) {
            try (PrintWriter writer = new PrintWriter(file)) {
                // Store account number
                writer.println(accountNum);

                // Store session expiration in UNIX milliseconds
                long expirationTime = (new Date()).toInstant().plus(FIVE_MINUTES).toEpochMilli();
                writer.println(expirationTime);
            }
        } catch (IOException e) {
            logger.severe("Could not create session file for '" + sessionID + "'");
            throw new ServerException("Could not create new session on server");
        }

        return sessionID;
    }

    /**
     * Validate that this session currently exists, is valid for this time range, and is for this account number
     * @param sessionID  Session file to locate
     * @param accountNum Number which should be inside that session file
     * @throws InvalidSession Error if session does not exist or if account number is not for this session
     * @throws ServerException Error if server has critical error (from IO)
     */
    private void verifySession(long sessionID, int accountNum) throws InvalidSession, ServerException {
        try (FileReader file = new FileReader(SESSION_DIRECTORY + File.separator + sessionID + ".session")) {
            int accountNumLine;
            long expirationTimeLine;

            try (BufferedReader reader = new BufferedReader(file)) {
                try {
                    accountNumLine = Integer.parseInt(reader.readLine());
                    expirationTimeLine = Long.parseLong(reader.readLine());
                } catch (NumberFormatException e) { // Thrown both if not a valid number or if readLine failed and returned null
                    logger.severe("Malformed session file: " + sessionID);
                    throw new InvalidSession("Session invalid");
                }
            } catch (IOException e) {
                logger.severe("Could not read from session file, IO error" + sessionID);
                throw new ServerException("Could not read session on server");
            }

            // Validate account number in file
            if (accountNumLine != accountNum) {
                logger.warning("User attempted to use session " + sessionID + " (stored account number " + accountNumLine + "), but provided incorrect account number: " + accountNum);
                invalidateSession(sessionID);
                throw new InvalidSession("Session ID is not valid for account number");
            }

            // Validate current time is not after the stored expiration time (in UNIX ms)
            if ((new Date()).after(new Date(expirationTimeLine))) {
                logger.warning("User session has expired: " + sessionID);
                invalidateSession(sessionID);
                throw new InvalidSession("Session has expired");
            }
        } catch (FileNotFoundException e) {
            logger.severe("Client attempted to utilise an invalid session ID: " + sessionID);
            throw new InvalidSession("Session ID not found");
        } catch (IOException e) {
            logger.severe("Could not open session file, IO error: " + sessionID);
            throw new ServerException("Could not read session on server");
        }
    }

    /**
     * Remove this session file from disk so it cannot be used again
     * @param sessionID Session file to find. May or may not exist on disk
     */
    private void invalidateSession(long sessionID) {
        try {
            Files.deleteIfExists((new File(SESSION_DIRECTORY + File.separator + sessionID + ".session")).toPath());
            logger.info("Removing session: " + sessionID);
        } catch (NoSuchFileException e) {
            logger.warning("Removing session, file has already been removed: " + sessionID);
        } catch (IOException e) {
            logger.severe("Cannot remove session, file IO error: " + sessionID);
        }
    }

    public void deposit(int accountNum, int amount, long sessionID) throws RemoteException, InvalidSession, AccountNotFoundException, ServerException {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must not be negative");
        }
        Account account = accounts.findByAccountNum(accountNum);
        verifySession(sessionID, accountNum);
        account.addTransaction(new DepositTransaction(new Date(), amount));
        logger.info("Deposit completed, account " + accountNum + " amount " + amount);
    }

    public void withdraw(int accountNum, int amount, long sessionID) throws RemoteException, InvalidSession, AccountNotFoundException, ServerException {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must not be negative");
        }
        Account account = accounts.findByAccountNum(accountNum);
        verifySession(sessionID, accountNum);
        account.addTransaction(new WithdrawalTransaction(new Date(), amount));
        logger.info("Withdrawal completed, account " + accountNum + " amount " + amount);
    }

    public int inquiry(int accountNum, long sessionID) throws RemoteException, InvalidSession, AccountNotFoundException, ServerException {
        Account account = accounts.findByAccountNum(accountNum);
        verifySession(sessionID, accountNum);
        logger.info("Account balance inquiry completed, account " + accountNum);
        return account.getBalance();
    }

    public Statement getStatement(int accountNum, Date from, Date to, long sessionID) throws RemoteException, InvalidSession, AccountNotFoundException, ServerException {
        if (to.before(from)) {
            throw new IllegalArgumentException("From date must be chronologically before To date");
        }

        verifySession(sessionID, accountNum);
        Account account = accounts.findByAccountNum(accountNum);
        logger.info("Preparing statement for account " + accountNum);
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
            System.out.println("Usage: " + Bank.class.getSimpleName() + " [registry port number]");
            return;
        }

        // Start a security manager, else RMI will not download classes
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            // Create bank instance (this class)
            Bank bank = new Bank();

            // Create RMI server as a UnicastRemoteObject
            int serverPort = (new Random()).nextInt(10000) + 50000; // Ephemeral port range
            BankInterface stub = (BankInterface) UnicastRemoteObject.exportObject(bank, serverPort);

            // Bind compute engine to name server
            Registry registry = LocateRegistry.getRegistry(port);
            registry.rebind("Bank", stub);

            logger.info("Bank server has been launched");

            // Create mock accounts
            AccountDatastoreImpl.createMockAccounts(bank.accounts); // Note: This is for the simplified application only. A real app would use a database for these
        } catch (RemoteException e) {
            logger.severe("Server could not startup due to a remote exception" + e.getMessage());
            throw e; // This SHOULD crash the server process, so re-throw
        }
    }
}
