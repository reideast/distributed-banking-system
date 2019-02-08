package net.teamtrycatch.client;

import net.teamtrycatch.shared.client.IllegalArgumentException;
import net.teamtrycatch.shared.interfaces.AccountNotFoundException;
import net.teamtrycatch.shared.interfaces.BankInterface;
import net.teamtrycatch.shared.interfaces.InvalidLogin;
import net.teamtrycatch.shared.interfaces.InvalidSession;
import net.teamtrycatch.shared.interfaces.ServerException;
import net.teamtrycatch.shared.interfaces.Statement;
import net.teamtrycatch.shared.interfaces.Transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ATM {

	private ATM() {
		super();
	}

	public static void main(String[] args) throws AccountNotFoundException, IOException,
			IllegalArgumentException, NotBoundException {
		int account = 0, amount = 0, port;
		String process, username = null, password = null, host;
		BankInterface bank;
		Date startDate = null, endDate = null;

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		if (args.length < 1) {
			throw new IllegalArgumentException("You must enter a hostname");

		}
		if (args.length < 2) {
			throw new IllegalArgumentException("You must enter a port");

		}
		if (args.length < 3) {
			throw new IllegalArgumentException(
					"You must enter a process to continue\n Login\n Withdraw\n Deposit\n Inquiry\n Statement\n"
					+ "eg: run-client.ps1 login userName pass deposit account# and amount");

		}
		host = args[0];
		port = Integer.parseInt(args[1]);
		process = args[2];

		switch (process) {

		case "login":
			if (args.length < 5) {
				throw new IllegalArgumentException("Please enter user name and password");

			}
			username = args[3];
			password = args[4];
			break;
		case "withdraw":
		case "deposit":
			if (args.length < 5) {
				throw new IllegalArgumentException("Please enter account and amount");

			}
			amount = (int) Double.parseDouble(args[4]);
			account = Integer.parseInt(args[3]);
			
			break;
		case "inquiry":
			if (args.length < 4) {
				throw new IllegalArgumentException("Please enter account number");

			}
			account = Integer.parseInt(args[3]);
		
			break;
		case "statement":
			if (args.length < 6) {
				throw new IllegalArgumentException("Please enter account number and start and end date");

			}
			account = Integer.parseInt(args[3]);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			try {

				startDate = dateFormat.parse(args[4]);
			} catch (ParseException e) {
			    throw new IllegalArgumentException("Start date must be in format DD/MM/YYYY");
			}
			try {
				endDate = dateFormat.parse(args[5]);
			} catch (ParseException e) {
				throw new IllegalArgumentException("End date must be in format DD/MM/YYYY");
			}
			
			break;
		default:
			throw new IllegalArgumentException(
					"You must enter a process that is one of:\n Login\n Withdraw\n Deposit\n Inquiry\n Statement\n"
							+ "eg: run-client.ps1 login userName pass deposit account# and amount");

		}

		bank = connection(host, port);

		switch (process) {
		case "login":
			login(username, password, bank);
			break;

		case "deposit":
			deposit(account, amount, bank);
			break;

		case "withdraw":
			withdraw(account, amount, bank);

			break;

		case "inquiry":
			inquiry(account, bank);
			break;

		case "statement":

			statement(account, bank, startDate, endDate);

			break;

		default:
			throw new IllegalArgumentException(
					"You must enter a process that is one of:\n Login\n Withdraw\n Deposit\n Inquiry\n Statement\n"
							+ "eg: run-client.ps1 login userName pass deposit account# and amount");
		}
	}

	private static BankInterface connection(String host, int port) throws RemoteException, NotBoundException, AccessException {
		BankInterface bank;
		String BI = "Bank";
		Registry registry = LocateRegistry.getRegistry(host, port);
		bank = (BankInterface) registry.lookup(BI);
		return bank;
	}

	private static void statement(int account, BankInterface bank, Date startDate, Date endDate)
			throws RemoteException, AccountNotFoundException, IOException {
		try {
			Statement s = bank.getStatement(account, startDate, endDate, getActiveSession());
			s.getAccountName();

			// format statement for printing to the window
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			System.out.println("Statement for Account " + account + " between " + dateFormat.format(startDate) + " and "
					+ dateFormat.format(endDate));

			System.out.printf("%-15s%-20s%10s%n", "Date", "Transaction Type", "Amount");

			for (Transaction t : s.getTransactions()) {
				System.out.printf("%-15s%-20s%10s%n", dateFormat.format(t.getDate()), t.getDescription(), "€" + t.getAmount());
			}
		} catch (InvalidSession e) {
			System.err.println(e.getMessage());
		} catch (ServerException e) {
			System.err.println(e.getMessage());
		}
		// Get statement for required dates

		// call bank to get statement
	}

	private static void deposit(int account, int amount, BankInterface bank)
			throws AccountNotFoundException, IOException {
		try {
			// Make bank deposit and get updated balance
			bank.deposit(account, amount, getActiveSession());
			System.out.println("Successfully deposited €" + amount + " into account " + account);
			// Catch exceptions that can be thrown from the server
		} catch (RemoteException e) {
			System.err.println("RMI ERROR");
		} catch (InvalidSession e) {
			System.err.println(e.getMessage());
		} catch (ServerException e) {
			System.err.println(e.getMessage());
		}
	}

	private static void withdraw(int account, int amount, BankInterface bank)
			throws AccountNotFoundException, IOException {
		try {
			// Make bank withdrawal and get updated balance
			bank.withdraw(account, amount, getActiveSession());
			System.out.println("Successfully withdrew €" + amount + " from account " + account);
			// Catch exceptions that can be thrown from the server
		} catch (RemoteException e) {
			System.err.println("RMI ERROR");
		} catch (InvalidSession e) {
			System.err.println(e.getMessage());
		} catch (ServerException e) {
			System.err.println(e.getMessage());
		}
	}

	private static void inquiry(int account, BankInterface bank) throws AccountNotFoundException, IOException {
		try {
			int balance;
			balance = bank.inquiry(account, getActiveSession());
			// Get account details from bank

			System.out.println("The current balance of account " + account + " is €" + balance);

			// Catch exceptions that can be thrown from the server
		} catch (RemoteException e) {
			System.err.println("RMI ERROR");
		} catch (InvalidSession e) {
			System.err.println(e.getMessage());
		} catch (ServerException e) {
			System.err.println(e.getMessage());
		}
	}

	private static void login(String username, String password, BankInterface bank) throws IOException {
		long customer;
		try {

			// Login with username and password
			customer = bank.login(username, password);
			startNewSession(customer);
			System.out.println("Successful login for " + username + ". Session is valid for 5 minutes");
			
			// Catch exceptions that can be thrown from the server
		} catch (RemoteException e) {
			System.err.println("RMI ERROR");
		} catch (InvalidLogin e) {
			System.err.println("Wrong login credentials please try again");
		} catch (ServerException e) {
			System.err.println(e.getMessage());
		}
	}

	private static void startNewSession(long sessionID) throws IOException {
		try (FileWriter file = new FileWriter(".session")) {
			try (PrintWriter writer = new PrintWriter(file)) {

				writer.println(sessionID);

			}
		} catch (IOException e) {
			System.out.println("Could not create session file for '" + sessionID + "'");
			throw e;
		}

	}

	private static long getActiveSession() throws IOException {
		try (FileReader file = new FileReader(".session")) {
			long sessionID;

			try (BufferedReader reader = new BufferedReader(file)) {

				sessionID = Long.parseLong(reader.readLine());
			} catch (NumberFormatException e) { // Thrown both if not a valid number or if readLine failed and returned null
				System.err.println(".session file broken");
				throw new IOException(e);

			} catch (IOException e) {
				System.err.println("Could not read from session file, IO error .session");
                throw e;
			}
			return sessionID;
		}

	}

}
