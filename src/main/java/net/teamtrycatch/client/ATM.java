package net.teamtrycatch.client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.teamtrycatch.server.Bank;
import net.teamtrycatch.shared.BankInterface;
import net.teamtrycatch.shared.IllegalArguementException;
import net.teamtrycatch.shared.InvalidLogin;
import net.teamtrycatch.shared.InvalidSession;
import net.teamtrycatch.shared.Statement;

public class ATM {
	static int  account,amount,port;
    static String process, username, password,host;
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

            //String host = (args.length < 1) ? null : args[0];
            try {
            	getUserArgs(args);
            	String BI = "Bank";
                Registry registry = LocateRegistry.getRegistry(host);
                //Bank stub = (Bank) registry.lookup("Bank");
                bank = (BankInterface) registry.lookup(BI);
                
               
             
             
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
            	System.out.println("Sorry can't do that for you!");
                break;
        }
    }
        
    	

	public static void getUserArgs(String args[]) throws IllegalArguementException{
		if(args.length < 2){
			throw new IllegalArguementException("You must enter a hosting port");
			
		}
		if(args.length < 3){
			throw new IllegalArguementException("You must enter a process to continue Login,Withdraw,Deposit,Inquiry,Statement");
			
		}
		host = args[0];
		port = Integer.parseInt(args[1]);
        process= args[2];
       
		switch (process) {
		
		case "login":
			if(args.length < 5){
				throw new IllegalArguementException("Please enter user name and password");
				
			}
			username = args[3];
			password = args[4];
			break;
		case "withdraw":
		case "deposit":
			if(args.length < 5){
				throw new IllegalArguementException("Please enter amount and account");
				
			}
			amount = (int) Double.parseDouble(args[4]);
			account = Integer.parseInt(args[3]);
			//sessionID = Long.parseLong(args[5]);
			break;
		case "inquiry":
			if(args.length < 4){
				throw new IllegalArguementException("Please enter account number");
				
			}
			account = Integer.parseInt(args[3]);
			//sessionID = Long.parseLong(args[4]);
			break;
		case "statement":
			if(args.length < 6){
				throw new IllegalArguementException("Please enter account number and start and end date");
				
			}
			account = Integer.parseInt(args[3]);
			try {
				startDate = DateFormat.getDateInstance().parse(args[4]);
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
			try {
				endDate = DateFormat.getDateInstance().parse(args[5]);
			} catch (ParseException e) {
			
				e.printStackTrace();
			}
			//sessionID = Long.parseLong(args[6]);
			break;
			default:
				throw new IllegalArguementException("Computer Says no");
				
		}

	}


}
