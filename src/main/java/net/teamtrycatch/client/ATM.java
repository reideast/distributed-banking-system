package net.teamtrycatch.client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.security.auth.login.AccountNotFoundException;

import net.teamtrycatch.server.Account;
import net.teamtrycatch.server.Bank;
import net.teamtrycatch.shared.BankInterface;
import net.teamtrycatch.shared.InvalidLogin;
import net.teamtrycatch.shared.InvalidSession;
import net.teamtrycatch.shared.Statement;

public class ATM {
	static int  account,amount;
    static String process, username, password;
	static long customer;
    static long sessionID;
    static BankInterface bank;
    static Date startDate, endDate;
      
    	
        private ATM() {
        	super();
        }

        public static void main (String[] args) throws net.teamtrycatch.shared.AccountNotFoundException {

        	if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            String host = (args.length < 1) ? null : args[0];
            try {
                Registry registry = LocateRegistry.getRegistry(host);
               // Bank stub = (Bank) registry.lookup("Bank");
                BankInterface Bi = (BankInterface) registry.lookup("Bank");
                getUserArgs(args);
               
                //This section takes in user input from command line
               /* System.out.println(args[0]);
                System.out.println(args[1]);
                Scanner sc = new Scanner(System.in);
                System.out.println("Enter UserName");
                String userName = sc.nextLine();
                System.out.println("Enter PassWord");
                String pass = sc.nextLine();
                num=Bi.login(userName, pass);
                if(num == 1234){
                	System.out.println("Success");
                }*/
                
                // if success
                //switch statement to withdraw
             
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
            
  

			switch (process){
            case "login":
                try {
                    //Login with username and password
                    customer = bank.login(username, password);
                    System.out.println("Session active for 5 minutes");
                    System.out.println("Use SessionID " + customer + " for all other operations");
                //Catch exceptions that can be thrown from the server
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InvalidLogin e) {
                    e.printStackTrace();
                }
                break;

            case "deposit":
                try {
                    //Make bank deposit and get updated balance
                  
                    bank.deposit(account, amount, sessionID);
                    System.out.println("Successfully deposited " + amount + " into account " + account);
                    System.out.println("New balance: " + amount);
                //Catch exceptions that can be thrown from the server
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InvalidSession e) {
                    System.out.println(e.getMessage());
                }
                break;

            case "withdraw":
                try {
                    //Make bank withdrawal and get updated balance
                	bank.withdraw(account, amount, sessionID);
                    System.out.println("Successfully withdrew E" + amount + " from account " + account +
                                       "\nRemaining Balance: E" + amount);
                //Catch exceptions that can be thrown from the server
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InvalidSession e) {
                    System.out.println(e.getMessage());
                } 

                break;

            case "inquiry":
                try {
                    //Get account details from bank
                    bank.inquiry(account,sessionID);
                    System.out.println("Account:" +account);
                         
                //Catch exceptions that can be thrown from the server
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InvalidSession e) {
                    System.out.println(e.getMessage());
                }
                break;

            case "statement":
                Statement s = null;
                //Get statement for required dates
				s.getAccountName();

				//format statement for printing to the window
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				System.out.println("Statement for Account " + account + " between " +
				                   dateFormat.format(startDate) + " and " + dateFormat.format(endDate));
               
				System.out.println("Date\t\t\tTransaction Type\tAmount\t\tBalance");
               

				for(Object t : s.getTransactions()) {
				    System.out.println(t);
				}
            
                break;

            default:
                break;
        }
    }
        
    	
	@SuppressWarnings("deprecation")
	public static void getUserArgs(String args[]) throws AccountNotFoundException{
		if(args.length < 4){
			throw new AccountNotFoundException();
		}

		process = args[2];
		switch (process) {
		case "login":
			username = args[3];
			password = args[4];
			break;
		case "withdraw":
		case "deposit":
			amount = (int) Double.parseDouble(args[4]);
			account = Integer.parseInt(args[3]);
			sessionID = Long.parseLong(args[5]);
			break;
		case "inquiry":
			account = Integer.parseInt(args[3]);
			sessionID = Long.parseLong(args[4]);
			break;
		case "statement":
			account = Integer.parseInt(args[3]);
			startDate = new Date(args[4]);
			endDate = new Date(args[5]);
			sessionID = Long.parseLong(args[6]);
			break;
		}

	}
}
