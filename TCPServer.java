/*
	Group Name: M.A.E. Clan
	Reference: Daxjoshi (2013) . Java TCP-Chat-Multiple Client. Java Developers Zone. https://javadeveloperszone.wordpress.com/2013/04/20/java-tcp-chat-multiple-client/

*/
import java.net.*;
import java.io.*;
import java.util.logging.*;
import java.util.LinkedList;

public class TCPServer {
	public static LinkedList<Socket> socketData = new LinkedList<Socket>();//Linkedlist for the socket data
 
	public static void main(String args[]) throws Exception {
 	      try
	      {
		int port = Integer.parseInt(args[0]);
		ServerSocket chatSocket = new ServerSocket(port);
	      	Respond res = new Respond();
	      	//server will run in infinite time to wait for the client to connect
	      	while(true){
		      	Socket connectionSocket = chatSocket.accept();
		      	socketData.add(connectionSocket);
		      	System.out.println("Just connected to " + connectionSocket.getRemoteSocketAddress());
		      	//for every client, thread must be initialize but it must be synchronized
		      	Thread t = new Thread(new ChatServer(res, connectionSocket));
	 		t.start();//start a thread
 		}
	      }catch(IOException e)
	      {
		 //e.printStackTrace();
		 System.out.println("Usage: java TCPServer <port no.>");
	      }catch(ArrayIndexOutOfBoundsException e)
	      {
		 System.out.println("Usage: java TCPServer <port no.> ");
	      }

	}
}

class Respond{
	String serverMessage;
 	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
 	
 	//if the client send EXIT the conversation will stop 
 	//else return true to continue the conversation
 	 synchronized public boolean responderMethod(Socket connectionSocket) {
 		try {
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			String clientMessage = inFromClient.readLine();
	 
			// if client process terminates it get null, so close connection
			 if (clientMessage == null || clientMessage.equals("EXIT")) {
			 return false;
			 }
			 
			System.out.println(clientMessage+"\n");
			
			for(int i=0;i<TCPServer.socketData.size();i++){
			//send the message to all the client in the linked list
				DataOutputStream outToClient = new DataOutputStream(TCPServer.socketData.get(i).getOutputStream());
				outToClient.writeBytes(clientMessage+ '\n');
				}
			
			return true;
		}catch (SocketException e) {
		 System.out.println("Disconnected");
		 return false;
		 }catch (Exception e) {
		 e.printStackTrace();
		 return false;
		 }
}
}

class ChatServer implements Runnable{
	Respond res;
	Socket chatSocket;
	
	public ChatServer(Respond res, Socket chatSocket){
	this.res = res;
	this.chatSocket = chatSocket; 
	}
	
	@Override
 	public void run() {
 	
 	while (res.responderMethod(chatSocket)) {
	 	try {
	 	//give chance to other thread to have conversation with the server.
		 Thread.sleep(0);
		 } catch (InterruptedException ex) {
		 ex.printStackTrace();
		 }
		 }
		 
		try {
		 chatSocket.close();
		 } catch (IOException ex) {
		 Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
		 }
	 }

}
