import java.io.*;
import java.net.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.TimeUnit;


public class client {

	 
	/*  
	  Name:	Main Method
	  
	 
	  Purpose: Start the client program and allow it to search and connect to the desired server. Also allow for comminication with other 
	  clients on the server
	  
	  Usage: Send and receieve messages from other clients that are connected to the server
	  
	  Subroutines/libraries required:
	  Socket - needed to connect to the serversocket
	  
	 
	 */
	public static void main(String[] args) {

		//ArrayList<String> usernames = new ArrayList<String>();
		//ArrayList<ClientControl> clients = new ArrayList<ClientControl>();  //List of clients 

		try {
			
			Socket s = new Socket("127.0.0.1",1201); // IP and Port number 
			
			 
			ServerControl servControl = new ServerControl(s); //Used to control clients messages to Server
			servControl.key="this is your aes key, dont tell anyone";
			servControl.encrypt=false;
			PrintWriter dout = new PrintWriter(s.getOutputStream());  //for reading output stream
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); //for getting users input
			
			String msgout="", destination, data;
			int packetNum=1, clientNum;
			double versionNum =1.0;
			
			new Thread(servControl).start(); 	// allows the server control to run on seperate thread					
	
			
		
			
			/*  
			  Name:	While loop
			  
		
			  
			  Purpose: Allow the client to send and receieve messages to other clients connected to the server
			  
			  Usage: Reads the clients input (must by in required formating) and sends it to the server, where it will be checked for errors, then delegated to 
			  the desired clients 
			  
			  Subroutines/libraries required:
			  Checksum() - for calculating the checksum of the message being sent 
			  PrintWiter - used to send out messages to the server and other clients
			  Buffered Reader - used to read in user input to send to the server
			 
			 */
			
			while(true) {
				
				System.out.println("\nMessage Format-(Username:Message)");
				msgout = br.readLine(); 				//clients msg to server
				if(msgout.equals("bye")) break; 		//if the client types in bye, exit the loop and disconnect from server
				if(msgout.equals("encrypt")){
					servControl.encrypt^=true;//this takes the precious encryption state and xors it with true causing it to flip to the oposit
					continue;//skip messaging server this time
				}
				if(servControl.encrypt){
					String target;
					String msg;
					if(msgout.indexOf(":")<0){//badly formed message (or some sort of command) encrypt and send anyway

						target="";
						msg=msgout;
		
					}else{
						target=msgout.substring(0,msgout.indexOf(":")+1);//split message into sender and message1
						msg=msgout.substring(msgout.indexOf(":")+1,msgout.length());
					}
					msg=AES.encrypt(msg,servControl.key);//encrypt just the message
					
					//if you dont want to do aes her you can apply ROT instead by making a ROT.encrypt function
					msgout=target+msg;//rebuild the message packet, remember wew didnt encrypt target or the server wouldnt know who to send it to
				}//everything else as normal
				int checksum = Checksum(msgout);
				
				
				dout.println(msgout+"*"+checksum);		//sending clients msg along with checksum of the oacket to the server 
				dout.flush();							//flushes the stream
				
				
			}
			
			dout.println(msgout);	//sending client disconnect message
			dout.flush();
			
			s.close();   //close the socket if the client types "bye"
			
			
		}catch(IOException e) {
			System.out.println("");
		}
		 	
	}//end main
	
	
	/*  
	  Name:	Checksum
	  
	
	  
	  Purpose: Performs a function on the message entered by the client to create a checksum for this message
	  
	  Usage: Used every time a client sends a message to the server. The checksum is checked to validate that the message 
	  was delivered correctly
	  
	  Subroutines/libraries required:
	 
	 
	 */
	public static int Checksum(String msg) {
		
		int checksum =0;
		for (int i = 1; i < msg.length(); i++) {
			char character = msg.charAt(i);
			int ascii = (int) character;
			checksum = checksum + ascii;
		}
		return checksum;
		
	}
	
	
}//end class
