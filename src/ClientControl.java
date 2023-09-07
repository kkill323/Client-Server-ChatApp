import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/*  
Name: ClientControl Class



Purpose: performs operations on packets from the client that are sent to the server.

Usage: On each packet sent by the client, the message header is chceked in this class including
the destination of the packet, the specific operation being asked of the client (list, bye, message..), and the checksum.

Subroutines/libraries required:
Checksum() - for calculating the checksum of the message being sent 
PrintWiter - used to send out messages to the server and other clients
Buffered Reader - used to read in user input to send to the server
Socket - to control the socket of the client
ArrayList -   to store the client sockets and their usernames

*/

public class ClientControl implements Runnable{

	
	private Socket client;      //the clients socket
	private BufferedReader br;  //reader used to read input stream
	private PrintWriter dout;   //reader used to read output stream
	
	private static ArrayList<ClientControl> clients;  //arraylist of clients 
	private static ArrayList<String> clientUsernames;
	
    String msgin="",username="";
	
	public ClientControl(Socket theClient, ArrayList<ClientControl> clients) throws IOException {
		this.client = theClient; //getting the client socket from the server
		this.setClients(clients);  //getting the arraylist of clients from the server
		dout = new PrintWriter(client.getOutputStream()); // used for printing clients msgs
		br = new BufferedReader(new InputStreamReader(client.getInputStream()));  //used for reading input from other clients
	}
	
	
	/*  
	  Name:	getNameList

	  
	  Purpose: getting the name of the client that has connected and also getting the arraylist full of the names of all clients 
	  connected to the server
	  
	  Usage: Assigns the name to this client, gives this client the arraylist of all connected clients, and invokes the WelcomeMessage() function
	  
	  Subroutines/libraries required:
	  WelcomeMessage() - run this functiom when a new client connects  
	 */
	public void getNamesList(String name, ArrayList<String> user) {
		username = name;
		clientUsernames = user;
		WelcomeMessage();
	}
	
	
	/*  
	  Name:	WelcomeMessage
	  

	  
	  Purpose: When a new client connects to the server, broadcast message is sent to all connected clients informing them of the new client
	  
	  Usage: Sends a message to all clients on the server, welcoming the new client.
	  
	  Subroutines/libraries required:
	  
	 */
	public void WelcomeMessage() {
		for (ClientControl eachC : getClients()) {
			eachC.dout.println(username+ " has just joined the server!");	//sending message to each client 
			eachC.dout.flush(); 		//flushing stream
		}
	}
	
	
	/*  
	  Name:	LeavingMessage
	  
	  
	  Purpose: When a client disconnects from the server, a broadcast message is sent to all connected clients, informing them
	  of the disconnection with the client
	  
	  Usage: Send message to all clients, only when a client has entered "bye", to disconnect from the server
	  
	  Subroutines/libraries required:
	  
	 */
	public void LeavingMessage() {
		for (ClientControl eachCli : getClients()) {
			eachCli.dout.println(username+ " has just left the server! BYE BYE "+username);	//sending message to each client 
			eachCli.dout.flush(); 		//flushing stream
		}
	}
	
	
	
	
	
	/*  
	  Name:	run()

	  
	  Purpose: The Server handling the messages that it received from the client, and sending back acknowledgement or error messages if needed. 
	  
	  Usage: Evaluates the packet header of each message sent to confirm its destination, checksum and message data. Then based on this informartion,
	  performs the proper operations
	  
	  Subroutines/libraries required:
	  Checksum() - method used to evaluate the checksum of the message to see if there were any errors with the message data
	 */
	@Override
	public void run() {
		
		try {
			 
			
			//run while the client does not enter "bye"
			while(true) {
				
				
				
			msgin = br.readLine(); // reads in the clients input
			
			if(msgin.substring(0,3).equals("bye"))  //If the client has entered "bye" (keyword to disconnect from server)
				break;	//if the client types in bye, exit the loop and disconnect from server
			
				
			
			
			 
			if (msgin.substring(0,4).equals("list")) {               //checks if the client entered "list"  (keyword which returns a list of usernames of all connected clients)
				dout.println(clientUsernames);                       //prints out a list of all clients usernames
				dout.flush();
			
			}else if (msgin.contains(":")) {				         //check if client is setting destination
				
				int endIndex = msgin.indexOf(":"); 					 //get the index of where the : char is
				String dest = msgin.substring(0, endIndex); 		 //get the username of the client thats receiving 
				int getChksum = msgin.indexOf("*");
				String checkSum = msgin.substring(getChksum+1);
				String actualMessage = msgin.substring(endIndex+1, getChksum);	 //get the message that the client is sending
				String sumToCheck = msgin.substring(0, getChksum);
				int theCheckSum = Checksum(sumToCheck);  	// Gets message entered by the client and performs checksum function on the packet
				
				
				
				int realChecksum = Integer.parseInt(checkSum);   // Gets the checksum which was generated on the senders side of the packet
				if (realChecksum == theCheckSum) {				 // Checks if both checksums from sending side and receiving side are equal, if so, packet is free of errors
			
				

				
				if (clientUsernames.contains(dest)) {				//check if the username entered exists in the client usernames list
					int indx =clientUsernames.indexOf(dest);		//get the index of the clients username in the list 
					ClientControl temp =clients.get(indx);			//get receiving clients socket to send them message
					temp.dout.println(username+": "+actualMessage); //print message to desired client
					dout.println(username+": "+actualMessage);
					temp.dout.flush();
				}else if (dest.equals("all")) {
					
			for (ClientControl eachC : getClients()) {			    //sending clients message to all clients connected to server 
				eachC.dout.println(username+": "+ actualMessage);   //sending message to each client 
				eachC.dout.flush(); 		                        //flushing stream
			}
			
			}else {   //end else if
					dout.println("That username does not exist");
					dout.flush();
				}
				
		}else { // If true, the packet is invalid due to incorrect checksum
			dout.println("Invalid Checksum input");
			dout.flush();
		}	
				
		}else { // if true, the input from the client is incorrect
			dout.println("Invalid input");
			dout.flush();
		}
			
			
				
			
			
		}// end while
			LeavingMessage();
		client.close();
		
	}catch(Exception e){
		System.out.println("");
	}
		
	}//end run


	/*  
	  Name:	Checksum()
	  
	  Purpose: Evaluates the checksum of each message that is sent from the client, to see if there are any errors
	  
	  Usage: Calculate the sum of all the characters ascii values contained in the message sent. If the results are the same, then
	  the checksum is correct, otherwise it is incorrect. 
	  
	  Subroutines/libraries required:
	  WelcomeMessage() - run this function when a new client connects  
	 */
	public static int Checksum(String msg) {
		
		System.out.println(msg);
		int checksum =0;
		for (int i = 1; i < msg.length(); i++) {
			char character = msg.charAt(i);
			int ascii = (int) character;
			checksum = checksum + ascii;
		}
		
		
		return checksum;
	}
	
	

	/*  
	  Name:	getClients and setClients
	  
	  Purpose: getter and setter functions for the arraylist containing the clients sockets
	  
	  Usage: Server uses these functions to retreive the list of client sockets
	  
	  Subroutines/libraries required:
	 */
	public static ArrayList<ClientControl> getClients() {
		return clients;
	}
	public void setClients(ArrayList<ClientControl> clients) {
		ClientControl.clients = clients;
	}

}//end class
