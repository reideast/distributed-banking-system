package net.teamtrycatch.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.RuntimeErrorException;

import net.teamtrycatch.server.Bank;
import net.teamtrycatch.shared.AccountNotFoundException;
import net.teamtrycatch.shared.BankInterface;
import net.teamtrycatch.shared.IllegalArguementException;
import net.teamtrycatch.shared.InvalidLogin;
import net.teamtrycatch.shared.InvalidSession;
import net.teamtrycatch.shared.ServerException;
import net.teamtrycatch.shared.Statement;

public class ATM {

	private ATM() {
		super();
	}

	public static void main(String[] args) throws net.teamtrycatch.shared.AccountNotFoundException, IOException,
			IllegalArguementException, NotBoundException {
		int account = 0, amount = 0, port;
		String process, username = null, password = null, host;
		long customer;
		long sessionID;
		BankInterface bank;
		Date startDate = null, endDate = null;

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		// String host = (args.length < 1) ? null : args[0];

		if (args.length < 2) {
			throw new IllegalArguementException("You must enter a hosting port");

		}
		if (args.length < 3) {
			throw new IllegalArguementException(
					"You must enter a process to continue Login,Withdraw,Deposit,Inquiry,Statement");

		}
		host = args[0];
		port = Integer.parseInt(args[1]);
		process = args[2];

		switch (process) {

		case "login":
			if (args.length < 5) {
				throw new IllegalArguementException("Please enter user name and password");

			}
			username = args[3];
			password = args[4];
			break;
		case "withdraw":
		case "deposit":
			if (args.length < 5) {
				throw new IllegalArguementException("Please enter amount and account");

			}
			amount = (int) Double.parseDouble(args[4]);
			account = Integer.parseInt(args[3]);
			// sessionID = Long.parseLong(args[5]);
			break;
		case "inquiry":
			if (args.length < 4) {
				throw new IllegalArguementException("Please enter account number");

			}
			account = Integer.parseInt(args[3]);
			// sessionID = Long.parseLong(args[4]);
			break;
		case "statement":
			if (args.length < 6) {
				throw new IllegalArguementException("Please enter account number and start and end date");

			}
			account = Integer.parseInt(args[3]);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			try {

				startDate = dateFormat.parse(args[4]);
			} catch (ParseException e) {

				e.printStackTrace();
			}
			try {
				endDate = dateFormat.parse(args[5]);
			} catch (ParseException e) {

				e.printStackTrace();
			}
			// sessionID = Long.parseLong(args[6]);
			break;
		default:
			throw new IllegalArguementException("Computer Says no");

		}
		bank = connection(host);

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
			System.out.println("Sorry can't do that for you!");
			break;
		}
	}

	private static BankInterface connection(String host) throws RemoteException, NotBoundException, AccessException {
		BankInterface bank;
		String BI = "Bank";
		Registry registry = LocateRegistry.getRegistry(host);
		// Bank stub = (Bank) registry.lookup("Bank");
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

			System.out.println("Date\t\t\tTransaction Type\tAmount\t\tBalance");

			for (Object t : s.getTransactions()) {
				System.out.println(t);
			}
		} catch (InvalidSession e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Get statement for required dates

		// call bank to get statement
	}

	private static void deposit(int account, int amount, BankInterface bank)
			throws AccountNotFoundException, IOException {
		try {
			// Make bank deposit and get updated balance
			bank.deposit(account, amount, getActiveSession());
			System.out.println("Successfully deposited " + amount + " into account " + account);
			System.out.println("New balance: " + amount);
			// Catch exceptions that can be thrown from the server
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvalidSession e) {
			System.out.println(e.getMessage());
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void withdraw(int account, int amount, BankInterface bank)
			throws AccountNotFoundException, IOException {
		try {
			// Make bank withdrawal and get updated balance
			bank.withdraw(account, amount, getActiveSession());
			System.out.println("Successfully withdrew E" + amount + " from account " + account
					+ "\nRemaining Balance: E" + amount);
			// Catch exceptions that can be thrown from the server
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvalidSession e) {
			System.out.println(e.getMessage());
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void inquiry(int account, BankInterface bank) throws AccountNotFoundException, IOException {
		try {
			int balance;
			balance = bank.inquiry(account, getActiveSession());
			// Get account details from bank

			System.out.println("Account:" + account + "Balance:" + balance);

			// Catch exceptions that can be thrown from the server
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvalidSession e) {
			System.out.println(e.getMessage());
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void login(String username, String password, BankInterface bank) throws IOException {
		long customer;
		try {

			// Login with username and password
			customer = bank.login(username, password);
			startNewSession(customer);
			System.out.println("Session active for 5 minutes");
			System.out.println("Use SessionID " + customer + " for all other operations");// TODO
																							// Debug
																							// ,remove
																							// customer
			// Catch exceptions that can be thrown from the server
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvalidLogin e) {
			e.printStackTrace();
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void startNewSession(long sessionID) throws IOException {
		// long sessionID = Math.abs(rnd.nextLong());

		try (FileWriter file = new FileWriter(".session")) {
			try (PrintWriter writer = new PrintWriter(file)) {

				writer.println(sessionID);

			}
		} catch (IOException e) {
			System.out.println("Could not create session file for '" + sessionID + "'");
			throw new IOException(e);
		}

	}

	private static long getActiveSession() throws IOException {
		try (FileReader file = new FileReader(".session")) {
			int accountNumLine;
			long sessionID;

			try (BufferedReader reader = new BufferedReader(file)) {

				sessionID = Long.parseLong(reader.readLine());
			} catch (NumberFormatException e) { // Thrown both if not a
												// valid number or if
												// readLine failed and
												// returned null
				System.err.println(".session file broken");
				throw new IOException(e);

			} catch (IOException e) {
				System.err.println("Could not read from session file, IO error .session");
				throw new IOException(e);
			}
			return sessionID;
		}

	}

}
