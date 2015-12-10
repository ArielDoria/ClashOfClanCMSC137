
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JOptionPane;

//Author: Ariel B. Doria
public class manInTheMiddle {
	private static int MANINTHEMIDDLEPORT= 1234;
	private static int SERVERPORT = 1111;
	private static int CLIENTPORT =1112;
	private final static int PACKETSIZE = 1024 ;
	private static HashMap<String,String> dataToBePassed = new HashMap<String,String>();
	private static InetAddress host;

	private static int[] dropPackets = {0, 25, 50, 75};
	
	public static void main(String args[]) throws Exception {
		
			//Runnable Thread for TCP Connection
			 Thread TCP = new Thread(new Runnable() {
		       	 Socket socket ;
		       	public void run() { 
		       		String serverAddress = JOptionPane.showInputDialog(null,
		       	            "Enter IP Address of the Server:",
		       	            "Welcome to the Clash on Campus",
		       	            JOptionPane.QUESTION_MESSAGE);
		       		try{
		       		System.out.println(serverAddress);
		       		host = InetAddress.getByName( serverAddress );
		       		
		            int port_server=1111;
		            socket = new Socket(host, port_server);
		 
		            //Introduce the Man in the middle to server
		            BufferedReader input = new BufferedReader(new InputStreamReader(
		                    socket.getInputStream()));
		            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
		            
		            String sendMessage = "IamManInTheMiddle" + "\n";
		            output.println(sendMessage);
		            InetAddress IP = InetAddress.getLocalHost();
		            output.println(IP.getHostAddress());
		            
		            //ServerSocketb for accepting the client that will connect
		       		ServerSocket mitm = new ServerSocket(MANINTHEMIDDLEPORT);
		       		while(true){
		       			Socket client = mitm.accept();
		       			BufferedReader is = new BufferedReader(new InputStreamReader(
		                        client.getInputStream()));
		       			DataOutputStream os = new DataOutputStream(client.getOutputStream());
		            //Reading the input of the and out writing the output
		            while (true) {
						String line = input.readLine();//read from the server 
						System.out.println(line);
						
						String line2 = is.readLine();//read from the client
		                if (line == null) {
	                        return;
	                    }
		                if(!line.contains("IamManInTheMiddle: /")){
		                	System.out.print(">>"+line.substring(8)+ "\n");
		                }
		                if(!line2.contains("IamManInTheMiddle: /")){
		                	System.out.print("++"+line2+ "\n");
		                }
		                if(random()==true){
		                	output.println(line2);
		                }
		                else{
			            	System.out.println("Drop the packet");
			            }
		            	}
		       		}
		       		}catch(Exception e){}
		       	 finally
		         {
		             try
		             {
		                 socket.close();
		             }
		             catch(Exception e){}
		         }		       	
		       	}});
			 TCP.start();
			 
			 //Thread for UDP
			 Thread UDP = new Thread(new Runnable() {
				 DatagramSocket socket = new DatagramSocket( 1234 );
				@Override
				public void run() {
					while( true )
		            {
		       			
		       		try{
		               // Create a packet
		               DatagramPacket packet = new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ;

		               // Receive a packet
		               socket.receive( packet ) ;
		               String dataReceived = new String(packet.getData());
			            System.out.println(dataReceived);
			            dataReceived=dataReceived.substring(1, dataReceived.length()-(PACKETSIZE-dataReceived.lastIndexOf('}')));
			            //Decode the received data
			            String[] num = dataReceived.split(", ");
			           
			            for(int y=0;y<num.length;y++){
			            	String[] temp = num[y].split("=");
			            	dataToBePassed.put(temp[0],temp[1]);
			            }
			            dataToBePassed.put("Address",packet.getAddress().toString());
		          
		            //If the message it to server
		            if(dataToBePassed.get("Status").contains("ToServer")){
		            	 dataToBePassed.put("Port",Integer.toString(packet.getPort()));
			            byte [] data = dataToBePassed.toString().getBytes();
				        packet = new DatagramPacket( data, data.length, host, SERVERPORT);
			            if(random()==true){//for dropping packet
			            	socket.send(packet);
			            }
			            else{
			            	System.out.println("Drop the packet");
			            }
			        }
				    else{
				    //If the message is to client
				    	int sendingport=Integer.parseInt(dataToBePassed.get("Port"));
				        InetAddress add = InetAddress.getByName(dataToBePassed.get("Address").substring(1));
				        byte [] data = dataToBePassed.toString().getBytes() ;
						DatagramPacket response = new DatagramPacket( data, data.length, add, sendingport );
						if(random()==true){//for dropping packet
							socket.send( response ) ;
						}
						 else{
				            	System.out.println("Drop the packet");
				            }
				    }
		
		       		}catch(IOException e){
		       			System.out.println(e);
		       		}
		            }
				}
			 });
			 UDP.start();
			 }
	
	public static boolean random(){//randomizing 0%,25%,50%,75% in dropping packets
		Random ran = new Random();
		 int temp = ran.nextInt(4);
		 int var = dropPackets[temp];
		 int random = ran.nextInt(101);
		 if(random<=var){
			 return false;
		 }
		 else{
		 return true;
		 }
	 }
	}
