//Source: http://cs.lmu.edu/~ray/notes/javanetexamples/

import java.awt.List;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Iterator;

public class TCPServer {
    //Values Being Used Globally
    private static final int PORT = 1111;                                                    //port where server will listen
    private final static int PACKETSIZE = 1024 ;                                             // constant PACKETSIZE defined.
    public static int portAddress =1111;

    //Data Structures Being Used
    private static HashSet<String> listOfNames = new HashSet<String>();                             // The set of names in the chat system
    private static HashSet<String> ipList = new HashSet<String>();                                  // The set of Ip addresses of the users
    private static HashSet<PrintWriter> listOfWriters = new HashSet<PrintWriter>();                 // Set of all print writers for all client
    private static HashMap<String,String> userLayout = new HashMap<String,String>();
    private static HashMap<String,String> dataToBePassed = new HashMap<String,String>();            // The Mapping of Attributes and its values of a packet.
    public static HashMap<InetAddress,Integer> listOfAddress = new HashMap<InetAddress,Integer>();  // The Mapping of IpAddresses and corresponding array Index
            
    //Values being globally modified.
    public static boolean done = false;                                                             //This tells us if the game is finished
    public static String nameOfTheWinner = "";                                                      //Who has been declared the winner
    public static boolean mitAccepted = true;
    public static boolean winner = true;
    
    public static void main(String[] args) throws Exception {
        System.out.println("The server is now ready to accept Players.");
        ServerSocket server = new ServerSocket(PORT);                                         // Open up the server
        
        /*********************** UDP Server ****************************/
        Thread udpServer = new Thread(new Runnable() {
        
            DatagramSocket socket = new DatagramSocket(portAddress);
            public void run() {   
                while(true){
                    
                    try{
                        DatagramPacket packet = new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ; // Create a packet
                        socket.receive( packet ) ;                                                       // Receive a packet (blocking)
                        
                        String data = new String(packet.getData()) ;                                       //create a response message
                        System.out.println(data);      
                        
                        InetAddress IPAddress=packet.getAddress();                                        //get the IP Address of the packet (or of this client)
                        int portAdd=packet.getPort();   //print the data
                        
                        //This asks for the player size of the users.
                        if(data.contains("AskPlayerSize")){                 //The sent packet contained the CLIENT message : AskPlayerSize
                       	  String playerSize = String.valueOf(ipList.size());
                       	  byte [] response = playerSize.getBytes() ; 	//convert the String to accepted packet format which is byte array
                       	  
                       	  DatagramPacket responsePacket = new DatagramPacket (response,response.length,IPAddress,portAdd); //construct the packet which will contain the generated data
           	 	          
                       	  socket.send(responsePacket) ;                 // Return the packet to the sender which is the client
                        }
                        if(data.contains("AskIpList")){                 //The sent packet contained the CLIENT message : AskPlayerSize
                        	  System.out.println(ipList);
                        	  byte [] response =ipList.toString().getBytes() ; 	//convert the String to accepted packet format which is byte array
                        	  DatagramPacket responsePacket = new DatagramPacket (response,response.length,IPAddress,portAdd); //construct the packet which will contain the generated data
            	 	          socket.send(responsePacket) ;                 // Return the packet to the sender which is the client
                        }
                        
                        
                        String dataReceived = new String(packet.getData());                               //this will contain the packetData
         	            dataReceived = dataReceived.substring(1, dataReceived.length()-(PACKETSIZE-dataReceived.lastIndexOf('}')));
         	            
         	            String[] num = dataReceived.split(", ");                                          //split the contents of the packet using ",". Each split is an attribute.
         	            dataToBePassed.clear();                                                           //reset the HashMap
         	            for(int y = 0; y<num.length; y++){                                                    //further split the attributes to get the values of each attribute
         	            	String[] temp = num[y].split("=");
         	            	dataToBePassed.put(temp[0],temp[1]);                                          //place in HashMap dataToBePassed the attributes mapped with its values.
         	            }

                                                                         //get the port address of this client
                        String mes = dataToBePassed.get("Message");                                       //put in mes the message contained in the packet.
                        System.out.println(mes);
                       /*****This is the checking section which determines which operation is being communicated by the client*******/
                       //This operation asks for the username list of connected users to server
                       if(mes.contains("AskUserName")){                 //The sent packet contained the CLIENT message : AskUserName
                     	  dataToBePassed.put("Status", "ToClient");     //add a Status key in HashMap
                     	  String names = listOfNames.toString();        //turns the set into a String
                     	  names = names.replace(",",";");               //replace commas with semicolons
                     	  dataToBePassed.put("Message",names);          //place the usernames of connected users as value of key Message
                     	  byte [] response = dataToBePassed.toString().getBytes() ; //convert the String to accepted packet format which is byte array
                     	  DatagramPacket responsePacket = new DatagramPacket (response,response.length,IPAddress,portAdd); //construct the packet which will contain the generated data
         	 	          socket.send(responsePacket) ;                 // Return the packet to the sender which is the client
                       }
                       
                       //This operation tells server that the game is done
                       if(mes.contains("GAMEDONE;")){ //declaring the winner
                     	  
                     	  String[] temp = dataReceived.split(";");      //This splits the message of the received packet using ";"
                     	  nameOfTheWinner = temp[1];                    //Get the name of the winner, which is after ";" 
                     	  done = true;                                  //Toggle the game, tell them the game is done.
                     	  if(done){                                     //This means that when the game is done ,
                           	for (PrintWriter writer : listOfWriters) {  //iterate for each of the listOfWriters ...
                           		writer.println("MESSAGE NAMEWINNER: " + nameOfTheWinner); //return this message to the CLIENT.
                            }
                           	done = false;                               //reset winning state.       
                           	//winner = false;
                          }
                       }

                       if(mes.contains("LAYOUT;")){ //sending the layout to all the client
                          String[] temp = mes.split("; ");
                          userLayout.put(temp[1], temp[2]);
                          for (PrintWriter writer : listOfWriters) {
                                writer.println("MESSAGE LAYOUT; " + temp[1]+"; "+temp[2]);
                            }
                          System.out.println(userLayout.size());
                          System.out.println(listOfNames.size());                 
                          if(userLayout.size()==listOfNames.size()){
                              for (PrintWriter writer : listOfWriters) {
                                writer.println("MESSAGE START THE GAME!");
                              }
                          }
                       }
                    }catch(Exception ee){
                        System.out.println(ee);
                    }
               } 
            }
       });
        
       udpServer.start(); //starting the UDPServer 
   
        try {
            while (true) {
                new Responder(server.accept()).start();
            }
        }finally{
            server.close();
        }
       
    }

    public static HashSet<String> getIpList(){
        return ipList;
    }
    
    public static int getPlayerSize(){
        return listOfNames.size();
    }
    
    public static int getPort(){
        return PORT;
    }

	//Responder is responsible in handling the conversations of the client
    private static class Responder extends Thread {
        private String clientName;
        private Socket socket;
        private InetAddress address;
        private int port;
        private BufferedReader input;
        private PrintWriter output;

        public Responder(Socket socket) {//create a responder
            this.socket = socket;
            this.port = socket.getPort();
            this.address = socket.getLocalAddress();
        }
	
	   //the main function for responding on the client 
        public void run() {
            try {
                // Create character streams for the client's socket.
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);

                while (true) {                                   //Request name from the client until the name is unique and not in use
                    output.println("GETUSERNAME");
                    clientName = input.readLine();               
                    String ipAddress = input.readLine();
                    if (clientName == null) {
                        return;
                    }
                    
                    synchronized (listOfNames) {
                        if (!listOfNames.contains(clientName)) {
                            if(!clientName.contains("IamManInTheMiddle")){ // Do not add man in the middle in the list of players
                                listOfNames.add(clientName);
                                System.out.println("Added ip address : " + ipAddress);
                                ipList.add(ipAddress);
                                synchronized(listOfAddress){
                                    listOfAddress.put(this.address, this.port);
                                }
                            }
                            break;
                        }
                    }
                }
                output.println("USERNAMEACCEPTED");             //Add the socket's print writer to the listOfWriters
                listOfWriters.add(output);
                
                while (true) {                                  // Accept messages from this client and broadcast to the chat system
                    String message = input.readLine();
                    System.out.println(message);
                    if (message == null) {
                        return;
                    }
                    
                   System.out.println(message);
                    if(clientName.contains("IamManInTheMiddle") && mitAccepted == true){
                        message = "IamManInTheMiddle: /" + message;
                        System.out.println(message);
                        mitAccepted = false;
                    }

                    for (PrintWriter writer : listOfWriters) {
                        writer.println("MESSAGE " + message);
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
               
                if (clientName != null) {                       //Remove the name and its print writer from the list
                    listOfNames.remove(clientName);
                }
                if (output != null) {
                    listOfWriters.remove(output);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
}
