import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*  
	  Name:	Server Class
	  
	
	  
	  Purpose: Start the server so that it is able to establish Client connections (maximum of 5 clients)]
	  
	  Usage: Server must be started first so that it can listen for clients that want to connect to each other 
	  
	  Subroutines/libraries required:
	  ServerSocket to run the server 
	  ArrayList for storing clients sockets and names
	  ClientControl - class used to perform the servers functions on packets from clients 
	 
	 */

public class server {

	private static ArrayList<ClientControl> clients = new ArrayList<ClientControl>();  //List of clients 
	private static ExecutorService exs = Executors.newFixedThreadPool(5); 			   //max # of clients that can connect to server
	private static ArrayList<String> usernames= new ArrayList<String>();			   //List to store clients usernames
	
	
	
	
	public static void main(String[] args) throws IOException {
			
			
			ServerSocket ss = new ServerSocket(1201); //Creating Server Socket and passing port number
			System.out.println("[Server]: Open for new connections"); //Server msg
			String username="client";
			int clientCount =1;
			
			while(true) {
			    Socket s = ss.accept();				//Accepting request from client to connect 
				System.out.println("Server has connected to Client");   //Message from server
				
				ClientControl theThread = new ClientControl(s, clients);  //sending clients socket and client arraylist to ClientControl class
				clients.add(theThread);  //adding client socket to client arraylist
				
				exs.execute(theThread); //executing the thread
				String newUsername = username + Integer.toString(clientCount);
				usernames.add(newUsername);				//adding username to arraylist
				clientCount++;
				theThread.getNamesList(newUsername, usernames); 			//sending the username and usernames arraylist to the ClientControl thread
				
			}
			
	}//end main
	
}//end class
