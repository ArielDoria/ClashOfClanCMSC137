/*
	Group Name: M.A.E. Clan
	Reference: Daxjoshi (2013) . Java TCP-Chat-Multiple Client. Java Developers Zone. https://javadeveloperszone.wordpress.com/2013/04/20/java-tcp-chat-multiple-client/

*/

import java.net.*;
import java.io.*;
import java.util.logging.*;

public class TCPClient{

	
	public static void main(String args[]) throws Exception {
		String message;
		String modifiedMessage="";
		
		try{
			
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			String serverName = args[0];
			int port = Integer.parseInt(args[1]);
			//Open a ClientSocket and connect with the ServerSocket
			Socket clientSocket = new Socket(serverName, port);
			
			System.out.println("Connecting to " + serverName + " on port " + port);
		while (true) {
			
			/* Send data to the ServerSocket */
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			/* Receive data from the ServerSocket */
			
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			message = inFromUser.readLine();
			outToServer.writeBytes(message + '\n');
			 
			if (message.equals("EXIT")||message==null||modifiedMessage.equals("EXIT")||modifiedMessage==null) {
			 break;//end the connection
			 }
			 	 
			 //read the message from the server and print it
			modifiedMessage = inFromServer.readLine();
			System.out.println("FROM the OTHER: " +modifiedMessage);
			
			}
			 clientSocket.close();//closing the socket from the client side
			 
		}catch(IOException e){
		System.out.println("Cannot find Server");
		}catch(ArrayIndexOutOfBoundsException e){
		System.out.println("Usage: java TCPClient <server ip> <port no.>");
		}
	}
}
