import java.net.DatagramPacket;
import java.util.HashMap;
import javax.swing.JOptionPane;

//Class for initializing the structures
public class Structures extends DraggableImageComponent{
	
	private static HashMap<String,String> dataToBePassed = new HashMap<String,String>();
	private int hitpoint;
	private int damagepersec;
	private int x = 0;
    private int y = 0;
    private static boolean gameOver=false;
    private int countPrint=0;


	public Structures(String type,int xP,int yP){
		super();
		//assigning values of their hitpoints and damage per second 
		if(type.equals("TownHall")){
			this.type = type;
			this.hitpoint = 5500;
			this.damagepersec=0;
		}
		if(type.equals("Wall")){
			this.type = type;
			this.hitpoint = 300;
			this.damagepersec=0;
		}
		if(type.equals("Cannon")){
			this.type = type;
			this.hitpoint = 420;
			this.damagepersec=10;
		}
		if(type.equals("ArcherTower")){
			this.type = type;
			this.hitpoint = 380;
			this.damagepersec=5;
		}
		if(type.equals("Mortar")){
			this.type = type;
			this.hitpoint = 400;
			this.damagepersec=20;
		}
		if(type.equals("AirDefense")){
			this.type = type;
			this.hitpoint = 800;
			this.damagepersec=80;
		}
		if(type.equals("WizardTower")){
			this.type = type;
			this.hitpoint = 5500;
			this.damagepersec=20;
		}
		this.xPos = xP;	//xPos is based on row position of structure in array representation.
		this.yPos = yP; //yPos is based on column position of structure in array representation.

		final Structures struct = this;
		//thread for detecting the nearby troop
		Thread findingThread = new Thread(new Runnable() {
        	
	    	public void run() { 
	    	
	    		while(true){

	    	    	int xCoordinate = struct.getX();
	    	    	int yCoordinate = struct.getY();
		    			if(struct.getType()!="Walls" &&struct.getType()!="TownHall"){//walls and town hall is unable to attack the nearby troop
		    	
		    			//get the nearby troop
		    			try{
		            		String classComponent =((TCPClient.battleGround.getComponentAt((xCoordinate),(yCoordinate))).getClass()).getCanonicalName();
		            
		            		if(classComponent.equals("Troops")){
		            			Troops troop =  (Troops)(TCPClient.battleGround.getComponentAt((xCoordinate),(yCoordinate)));
		            			//attack the troop
		            			while(troop.alive()&&struct.alive()){
		            				troop.attack(struct.damagepersec);
		            				Thread.sleep(1000);
		            			}
		            		}
		            		
			            	String classComponent2 =((TCPClient.battleGround.getComponentAt((xCoordinate-TCPClient.tileWidth),(yCoordinate-TCPClient.tileHeight))).getClass()).getCanonicalName();
				            if(classComponent2.equals("Troops")){
				            	Troops troop2 =  (Troops)(TCPClient.battleGround.getComponentAt((xCoordinate-TCPClient.tileWidth),(yCoordinate-TCPClient.tileHeight)));
				            	while(troop2.alive()&&struct.alive()){
				            	//attack the troop
				            		troop2.attack(struct.damagepersec);
				            		Thread.sleep(1000);
				            	}
				            }
				            
				            String classComponent3 =((TCPClient.battleGround.getComponentAt((xCoordinate+TCPClient.tileWidth),(yCoordinate+TCPClient.tileHeight))).getClass()).getCanonicalName();
				            if(classComponent3.equals("Troops")){
				            	Troops troop3 =  (Troops)(TCPClient.battleGround.getComponentAt((xCoordinate+TCPClient.tileWidth),(yCoordinate+TCPClient.tileHeight)));
				            	while(troop3.alive()&&struct.alive()){
				            	//attack the troop
				            		troop3.attack(struct.damagepersec);
				            		Thread.sleep(1000);
				            	}
				            }
				            
				            String classComponent4 =((TCPClient.battleGround.getComponentAt((xCoordinate-TCPClient.tileWidth),(yCoordinate+TCPClient.tileHeight))).getClass()).getCanonicalName();
				            if(classComponent4.equals("Troops")){
				            	Troops troop3 =  (Troops)(TCPClient.battleGround.getComponentAt((xCoordinate-TCPClient.tileWidth),(yCoordinate+TCPClient.tileHeight)));
				            	while(troop3.alive()&&struct.alive()){
				            	//attack the troop
				            		troop3.attack(struct.damagepersec);
				            		Thread.sleep(1000);
				            	}
				            }
				            
				            String classComponent5 =((TCPClient.battleGround.getComponentAt((xCoordinate+TCPClient.tileWidth),(yCoordinate-TCPClient.tileHeight))).getClass()).getCanonicalName();
				            if(classComponent5.equals("Troops")){
				            	Troops troop3 =  (Troops)(TCPClient.battleGround.getComponentAt((xCoordinate+TCPClient.tileWidth),(yCoordinate-TCPClient.tileHeight)));
				            	while(troop3.alive()&&struct.alive()){
				            	//attack the troop
				            		troop3.attack(struct.damagepersec);
				            		Thread.sleep(1000);
				            	}
				            }
				            
				            String classComponent6 =((TCPClient.battleGround.getComponentAt((xCoordinate),(yCoordinate+TCPClient.tileHeight))).getClass()).getCanonicalName();
				            if(classComponent6.equals("Troops")){
				            	Troops troop3 =  (Troops)(TCPClient.battleGround.getComponentAt((xCoordinate),(yCoordinate+TCPClient.tileHeight)));
				            	while(troop3.alive()&&struct.alive()){
				            	//attack the troop
				            		troop3.attack(struct.damagepersec);
				            		Thread.sleep(1000);
				            	}
				            }
				            
				            String classComponent7 =((TCPClient.battleGround.getComponentAt((xCoordinate),(yCoordinate-TCPClient.tileHeight))).getClass()).getCanonicalName();
				            if(classComponent7.equals("Troops")){
				            	Troops troop3 =  (Troops)(TCPClient.battleGround.getComponentAt((xCoordinate),(yCoordinate-TCPClient.tileHeight)));
				            	while(troop3.alive()&&struct.alive()){
				            	//attack the troop
				            		troop3.attack(struct.damagepersec);
				            		Thread.sleep(1000);
				            	}
				            }
				            
				            String classComponent8 =((TCPClient.battleGround.getComponentAt((xCoordinate+TCPClient.tileWidth),(yCoordinate))).getClass()).getCanonicalName();
				            if(classComponent8.equals("Troops")){
				            	Troops troop3 =  (Troops)(TCPClient.battleGround.getComponentAt((xCoordinate+TCPClient.tileWidth),(yCoordinate)));
				            	while(troop3.alive()&&struct.alive()){
				            	//attack the troop
				            		troop3.attack(struct.damagepersec);
				            		Thread.sleep(1000);
				            	}
				            }
				            
				            String classComponent9 =((TCPClient.battleGround.getComponentAt((xCoordinate-TCPClient.tileWidth),(yCoordinate))).getClass()).getCanonicalName();
				            if(classComponent9.equals("Troops")){
				            	Troops troop3 =  (Troops)(TCPClient.battleGround.getComponentAt((xCoordinate-TCPClient.tileWidth),(yCoordinate)));
				            	while(troop3.alive()&&struct.alive()){
				            	//attack the troop
				            		troop3.attack(struct.damagepersec);
				            		Thread.sleep(1000);
				            	}
				            }
					        
			            }catch(Exception e){}
	            
	            	}
	            }
	                
	        }
    	});
        
        findingThread.start();
	}
	
	
	//get the hitpoint
	public int getHitPoint(){
		return this.hitpoint;
	}
	
	//get the damage per second of the structure
	public int getDamagePerSec(){
		return this.damagepersec;
	}
	
	//attack the structure
	public void attack(int lessLife){
		this.hitpoint = this.hitpoint-lessLife;
	}
	
	//get the type of the structure
	public String getType(){
		return this.type;	
	}

	//check if the structure has remaining life
	public boolean alive(){
		if(this.hitpoint <= 0){
			updateArrayPositions(this.xPos,this.yPos,true);
			//if(this.type.equals("TownHall")){
					//JOptionPane.showMessageDialog (null, "GAME OVER", "Warning", JOptionPane.ERROR_MESSAGE);
					
			//}
					updateArrayPositions(this.xPos,this.yPos,true);

			TCPClient.printArray();
		try{
			TCPClient.battleGround.remove(this);
			if(this.type.equals("TownHall")&&countPrint==0){				
				countPrint++;
				Troops.ableToMove=false;
				gameOver=true;
			}
		}catch(Exception e){}
			TCPClient.battleGround.repaint();
			return false;
		}
		else{
			return true;
		}
	}

	public static boolean gameOver(){
		if(gameOver){
			gameOver = false;
			return true;
		}
		else{
			return false;
		}
	}

}
