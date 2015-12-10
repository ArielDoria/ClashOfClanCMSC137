//Source: http://cs.lmu.edu/~ray/notes/javanetexamples/

import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.border.LineBorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;


import java.net.Inet4Address;

public class TCPClient{
	//Stream Readers and Writers
	BufferedReader input,input2;
	PrintWriter output,output2;

    //Data Structures
    private static HashMap<String,String> dataToBePassed = new HashMap<String,String>();
    private static HashMap<String,String> userLayout = new HashMap<String,String>();
    public static int[][] position = new int[20][20];
    public static LinkedList<int []> defaultPositions = new LinkedList<int[]>();

    //CONSTANT VALUES
    public static final int TILECOLS = 20;
    public static final int TILEROWS = 20;
    public static final int WALLDIAGONAL = 1;
    public static final int CANNONDIAGONAL = 2;
    public static final int TOWNHALLDIAGONAL = 3;
    public static final int MORTARDIAGONAL = 2;
    public static final int ARCHERTOWERDIAGONAL = 2;
    public static final int WIZARDTOWERDIAGONAL = 2;
    public static final int AIRDEFENSEDIAGONAL = 2;
    private static final int PACKETSIZE = 1024 ;
    private static final int FIXINGTIME = 20;
    //Some more defined values
    public static int portAddress = 1111;
    private static int CLIENTPORT = 1112;

    //Initialize the counter to be used in Layout of the building
	public static int cannonCount = 0;
	public static int wallCount = 0;
	public static int archerTowerCount = 0;
	public static int mortarCount = 0;
	public static int airDefenseCount = 0;
	public static int wizardTowerCount = 0;
	public static int tileWidth = 0;
	public static int tileHeight = 0;
	public static int wallsTemp = 1;

    //INITIAL GLOBAL VALUES
    public static int barbarianCount = 100;
    public static int archerCount = 75;
    public static int giantCount = 20;
    public static int wizardCount = 26;
    public static int dragonCount = 5;
    public static int wallBreakerCount = 20;
    public static int hogRiderCount = 10;

    //Globally modifiable variables
    public static int tileW;
    public static int tileH;
    public static String insert = "";
    public static String serverAddress;
    private static InetAddress host = null;
    private DatagramSocket socketUDP = null;
    private static String UserName = "";
    public static int groundWidth = 0;
    public static int groundHeight = 0;
    public static boolean allowedDeployTroops = false;
    public static ActionListener out;
    private static String enemyLayout ="";
    public static boolean startTheGame = false;
//********************************INTERFACE VARIABLES**********************************************
	
	public static JFrame frame = new JFrame("Clash on Campus");
	public static JPanel panel = new JPanel();
    public static JPanel gamePanel = new JPanel();
    public static JPanel chatPanel = new JPanel();
    public static JPanel choicesPanel = new JPanel();
    public static JPanel battleGround = new JPanel();

    public static JTextField textField = new JTextField();
	public static JTextArea chatBox = new JTextArea();
    public JLabel barbarianLabel,archerLabel,giantLabel,wallBreakerLabel;
    public JLabel dragonLabel,wizardLabel,hogRiderLabel;
    public static JLabel timer = new JLabel("0:0");

    public TCPClient() {
    	
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	int windowWidth = (int)screenSize.getWidth();
    	int windowHeight = (int) screenSize.getHeight();
    	tileWidth = (int)(((windowWidth*0.70)-70)/20);
    	tileHeight = (int)((windowHeight*0.75)/20);
    	//Initialize the bigger panel
    	panel.setPreferredSize(new Dimension(windowWidth,windowHeight));
      	panel.setBackground(Color.BLACK);
      	
      	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
      	//Initialize the chat system panel
      	
      	chatPanel.setPreferredSize(new Dimension((int)((windowWidth*0.30)-20),windowHeight));
        chatPanel.setBackground(Color.BLACK);
        textField.setEditable(false);
        chatBox.setEditable(false);
        
        textField.setPreferredSize(new Dimension ((int)((windowWidth*0.30)-40),(int)((windowHeight*0.10))));
        chatBox.setPreferredSize(new Dimension ((int)((windowWidth*0.30)-40),(int)((windowHeight*0.55))));
       
        try{
			BufferedImage myPicture = ImageIO.read(new File("images/Clash_logo.png"));
			JLabel picLabel = new JLabel(new ImageIcon(myPicture));
			picLabel.setPreferredSize(new Dimension((int)((windowWidth*0.30)-40),(int)((windowHeight*0.10))));
			chatPanel.add(picLabel);
	    }catch(Exception e){}
		
        JLabel chatSystem = new JLabel("CHAT SYSTEM");
		chatSystem.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 18));
		chatPanel.add(chatSystem);
	
        chatPanel.add(new JScrollPane(chatBox));
        chatPanel.add(textField);
        JLabel timeLabel = new JLabel("Timer: ");
        timeLabel.setOpaque(true);
        chatPanel.add(timeLabel);
        timer.setOpaque(true);
        timer.setBackground(Color.WHITE);
        chatPanel.add(timer);
        
        
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
      	//Initialize the game panel
        gamePanel.setPreferredSize(new Dimension((int)((windowWidth*0.70)-50),windowHeight));
        gamePanel.setBackground(Color.BLACK);
        groundWidth=(int)((windowWidth*0.70)-80);
        groundHeight=(int)(windowHeight*0.75);
        battleGround.setPreferredSize(new Dimension(groundWidth,groundHeight));
       
        battleGround.setPreferredSize(new Dimension((int)((windowWidth*0.70)-70),(int)(windowHeight*0.80)));
        battleGround.setBackground(Color.GREEN);
        battleGround.setPreferredSize(new Dimension((int)((windowWidth*0.70)-70),(int)(windowHeight*0.75)));
        choicesPanel.setPreferredSize(new Dimension((int)((windowWidth*0.70)-70),(int)(windowHeight*0.18)));
        choicesPanel.setBackground(Color.GRAY);
        battleGround.addMouseListener(new MouseAdapter(){
        @Override
       public void mouseClicked(MouseEvent e) {
            // adding of troops
            int xCoordinate=e.getX()-(e.getX()%tileWidth);
            int yCoordinate=e.getY()-(e.getY()%tileHeight);
            boolean allowed = false;
            if(allowedDeployTroops){
            //check if whin the restricted area
                if(!(xCoordinate<=(groundWidth-(groundWidth/4))&&xCoordinate>=(groundWidth/4)&&yCoordinate<=(groundHeight-(groundHeight/4))&&yCoordinate>=(groundHeight/4))){
    
                    if(insert.equals("Barbarian")){
                        if(barbarianCount>0){
                        	allowed=true;
                        	barbarianCount--;
                        	barbarianLabel.setText("x "+barbarianCount);
                        }
                    }
                    if(insert.equals("Archer")){
                        if(archerCount>0){
                        	allowed=true;
                        	archerCount--;
                        	archerLabel.setText("x "+archerCount);
                        }
                    }
                    if(insert.equals("Giant")){
                        if(giantCount>0){
                        	allowed=true;
                        	giantCount--;
                        	giantLabel.setText("x "+giantCount);
                        }
                    }
                    if(insert.equals("WallBreaker")){
                        if(wallBreakerCount>0){
                        	allowed=true;
                        	wallBreakerCount--;
                        	wallBreakerLabel.setText("x "+wallBreakerCount);
                        }
                    }
                    if(insert.equals("Dragon")){
                        if(dragonCount>0){
                        	allowed=true;
                        	dragonCount--;
                        	dragonLabel.setText("x "+dragonCount);
                        }
                    }
                    if(insert.equals("Wizard")){
                        if(wizardCount>0){
                        	allowed=true;
                        	wizardCount--;
                        	wizardLabel.setText("x "+wizardCount);
                    	}
                    }
                    if(insert.equals("HogRider")){
                        if(hogRiderCount>0){
                        	allowed=true;
            	            hogRiderCount--;
            	            hogRiderLabel.setText("x "+hogRiderCount);
                        }
                    }
                    if(allowed==true){ 
                        
                        Image img = Toolkit.getDefaultToolkit().createImage("images/"+insert+".png");
                        
                        //Creates a troop component and adds loaded image
                        xCoordinate=e.getX()-(e.getX()%tileWidth);
                        yCoordinate=e.getY()-(e.getY()%tileHeight);
                        Troops troop = new Troops(insert,xCoordinate,yCoordinate);
                        battleGround.add(troop);//Adds this component to main container
                        troop.setImage(img);//Sets image
                        troop.setAutoSize(true);//The component get ratio w/h of source image
                        troop.setOverbearing(true);//On click ,this panel gains lowest z-buffer
                        troop.setBorder(null);
                        troop.setSize(tileWidth,tileHeight);
                        troop.setLocation(xCoordinate,yCoordinate);
                    }
                } else {//check if the cursor is inside the restricted area
                    JOptionPane.showMessageDialog (null, "Unable to deploy troops inside the area", "Warning", JOptionPane.WARNING_MESSAGE);
                } 
            }
        }      
        });   
               
        //////////////////////////////////
        //JButton for adding the troops
        JButton barbarian = new JButton();
        barbarian.setPreferredSize(new Dimension((int)((((windowWidth*0.70)-70)/7)-30),((int)(windowHeight*0.18)-45)));
        barbarian.setBackground(Color.GRAY);
        
        try {
            BufferedImage img = ImageIO.read(new File("images/Barbarian.png"));
            img = resizeImage(img,(int)((((windowWidth*0.70)-70)/7)-35),(int)((windowHeight*0.18)-50),1);
            barbarian.setIcon(new ImageIcon(img));
        } catch (IOException ex) { }
        
        barbarian.addActionListener(new ActionListener(){       
            public void actionPerformed(ActionEvent e){
                TCPClient.insert = "Barbarian";
            }
        });
    
        
        JButton archer = new JButton();
        archer.setPreferredSize(new Dimension((int)((((windowWidth*0.70)-70)/7)-30),((int)(windowHeight*0.18)-45)));
        try {
            BufferedImage img = ImageIO.read(new File("images/Archer.png"));
            img = resizeImage(img,(int)((((windowWidth*0.70)-70)/7)-35),(int)((windowHeight*0.18)-50),1);
            archer.setIcon(new ImageIcon(img));
        } catch (IOException ex) { }
            
        archer.addActionListener(new ActionListener(){      
            public void actionPerformed(ActionEvent e){
                TCPClient.insert = "Archer";
            }
        });
    
        JButton dragon = new JButton();
        dragon.setPreferredSize(new Dimension((int)((((windowWidth*0.70)-70)/7)-30),((int)(windowHeight*0.18)-45)));
        try {
            BufferedImage img = ImageIO.read(new File("images/Dragon.png"));
            img = resizeImage(img,(int)((((windowWidth*0.70)-70)/7)-35),(int)((windowHeight*0.18)-50),1);
            dragon.setIcon(new ImageIcon(img));
        } catch (IOException ex) { }
        
        dragon.addActionListener(new ActionListener(){      
            public void actionPerformed(ActionEvent e){
                TCPClient.insert = "Dragon";
            }
        });
        
        JButton hogRider = new JButton();
        hogRider.setPreferredSize(new Dimension((int)((((windowWidth*0.70)-70)/7)-30),((int)(windowHeight*0.18)-45)));
        try {
            BufferedImage img = ImageIO.read(new File("images/HogRider.png"));
            img = resizeImage(img,(int)((((windowWidth*0.70)-70)/7)-35),(int)((windowHeight*0.18)-50),1);
            hogRider.setIcon(new ImageIcon(img));
        } catch (IOException ex) { }
        
        hogRider.addActionListener(new ActionListener(){        
            public void actionPerformed(ActionEvent e){
                TCPClient.insert = "HogRider";
            }
        });
    
        JButton giant = new JButton();
        giant.setPreferredSize(new Dimension((int)((((windowWidth*0.70)-70)/7)-30),((int)(windowHeight*0.18)-45)));
        try {
            BufferedImage img = ImageIO.read(new File("images/Giant.png"));
            img = resizeImage(img,(int)((((windowWidth*0.70)-70)/7)-35),(int)((windowHeight*0.18)-50),1);
            giant.setIcon(new ImageIcon(img));
        } catch (IOException ex) { }
        
        giant.addActionListener(new ActionListener(){       
            public void actionPerformed(ActionEvent e){
                TCPClient.insert = "Giant";
            }
        });
    
        JButton wallBreaker = new JButton();
        wallBreaker.setPreferredSize(new Dimension((int)((((windowWidth*0.70)-70)/7)-30),((int)(windowHeight*0.18)-45)));
        try {
            BufferedImage img = ImageIO.read(new File("images/WallBreaker.png"));
            img = resizeImage(img,(int)((((windowWidth*0.70)-70)/7)-35),(int)((windowHeight*0.18)-50),1);
            wallBreaker.setIcon(new ImageIcon(img));
        } catch (IOException ex) { }
        
        wallBreaker.addActionListener(new ActionListener(){     
            public void actionPerformed(ActionEvent e){
                TCPClient.insert = "WallBreaker";
            }
        });
    
        JButton wizard = new JButton();
        wizard.setPreferredSize(new Dimension((int)((((windowWidth*0.70)-70)/7)-30),((int)(windowHeight*0.18)-45)));
        try {
            BufferedImage img = ImageIO.read(new File("images/Wizard.png"));
            img = resizeImage(img,(int)((((windowWidth*0.70)-70)/7)-35),(int)((windowHeight*0.18)-50),1);
            wizard.setIcon(new ImageIcon(img));
        } catch (IOException ex) { }
        
        wizard.addActionListener(new ActionListener(){      
            public void actionPerformed(ActionEvent e){
                TCPClient.insert = "Wizard";
            }
        }); 
    
        JPanel barbarianChoices = new JPanel();
        barbarianChoices.setPreferredSize(new Dimension((int)((((windowWidth*0.70)-70)/7)-5),((int)(windowHeight*0.18)-15)));
        barbarianLabel = new JLabel("x 100");
        barbarianChoices.add(barbarian);
        barbarianChoices.add(barbarianLabel);
        barbarianChoices.setBackground(Color.GRAY);
        
        JPanel archerChoices = new JPanel();
        archerChoices.setPreferredSize(new Dimension((int)((((windowWidth*0.70)-70)/7)-5),((int)(windowHeight*0.18)-15)));
        archerLabel = new JLabel("x 75");
        archerChoices.add(archer);
        archerChoices.add(archerLabel);
        archerChoices.setBackground(Color.GRAY);
        
        JPanel dragonChoices = new JPanel();
        dragonChoices.setPreferredSize(new Dimension((int)((((windowWidth*0.70)-70)/7)-5),((int)(windowHeight*0.18)-15)));
        dragonLabel = new JLabel("x 5");
        dragonChoices.add(dragon);
        dragonChoices.add(dragonLabel);
        dragonChoices.setBackground(Color.GRAY);
        
        JPanel hogRiderChoices = new JPanel();
        hogRiderChoices.setPreferredSize(new Dimension((int)((((windowWidth*0.70)-70)/7)-5),((int)(windowHeight*0.18)-15)));
        hogRiderLabel = new JLabel("x 10");
        hogRiderChoices.add(hogRider);
        hogRiderChoices.add(hogRiderLabel);
        hogRiderChoices.setBackground(Color.GRAY);
        
        JPanel giantChoices = new JPanel();
        giantChoices.setPreferredSize(new Dimension((int)((((windowWidth*0.70)-70)/7)-5),((int)(windowHeight*0.18)-15)));
        giantLabel = new JLabel("x 10");
        giantChoices.add(giant);
        giantChoices.add(giantLabel);
        giantChoices.setBackground(Color.GRAY);
        
        JPanel wallBreakerChoices = new JPanel();
        wallBreakerChoices.setPreferredSize(new Dimension((int)((((windowWidth*0.70)-70)/7)-5),((int)(windowHeight*0.18)-15)));
        wallBreakerLabel = new JLabel("x 20");
        wallBreakerChoices.add(wallBreaker);
        wallBreakerChoices.add(wallBreakerLabel);
        wallBreakerChoices.setBackground(Color.GRAY);
        
        JPanel wizardChoices= new JPanel();
        wizardChoices.setPreferredSize(new Dimension((int)((((windowWidth*0.70)-70)/7)-5),((int)(windowHeight*0.18)-15)));
        wizardLabel = new JLabel("x 20");
        wizardChoices.add(wizard);
        wizardChoices.add(wizardLabel);
        wizardChoices.setBackground(Color.GRAY);
        
        choicesPanel.add(barbarianChoices);
        choicesPanel.add(archerChoices);
        choicesPanel.add(dragonChoices);
        choicesPanel.add(hogRiderChoices);
        choicesPanel.add(giantChoices);
        choicesPanel.add(wallBreakerChoices);
        choicesPanel.add(wizardChoices);
        
        //Rearranging
        battleGround.setLayout(null);        
        

        gamePanel.add(battleGround);
        gamePanel.add(choicesPanel);
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        //Add to the window
        panel.add(gamePanel);
        panel.add(chatPanel);
        
        frame.setContentPane(panel);
        frame.setResizable(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
       
        //load the pictures for the structures
      	java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                initializeBattleGround(readStructConfig());
                loadPhotos();
            }
        });
        
//***************************************************************************************

        out = new ActionListener() {
          //Make an action listener for the text field
            public void actionPerformed(ActionEvent e) {
                output.println(UserName+": "+textField.getText());
                textField.setText("");
            }
        };
        textField.addActionListener(out);

    }

    public static boolean isAllowable(int i, int j){
        if(position[i][j] != 0)
            return false;
        return true;
    }
    
    //Pop up for getting the IP address of the server
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            frame,
            "Enter IP Address of the Server:",
            "Welcome to the Clash on Campus",
            JOptionPane.QUESTION_MESSAGE);
    }
    public static String getServAdd(){
    	return serverAddress;
    }

    /**
     * Prompt for and return the desired screen name.
     */
    private String getUserName() {
        return JOptionPane.showInputDialog(
            frame,
            "Enter your User name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }

    private void run() throws IOException {
        String temp;
        // Make connection and initialize streams
        serverAddress = getServerAddress();
        System.out.print("||"+serverAddress);
        Socket socket = new Socket(serverAddress, 1111);
        Socket newSocket;
        input = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) {
            String line = input.readLine();
            String IP = Inet4Address.getLocalHost().getHostAddress();//InetAddress.getLocalHost();
            System.out.println(IP);
            if (line.startsWith("GETUSERNAME")) {
                UserName = getUserName();
                output.println(UserName);
                output.println(IP);
            } else if (line.startsWith("USERNAMEACCEPTED")) {
                textField.setEditable(true);
            } else if (line.startsWith("MESSAGE")) {
                System.out.println(line);
                
                if(line.contains("NAMEWINNER:")){ // a winner has been found.
                    JOptionPane.showMessageDialog (null, "Congratulation to "+line.substring(20).toUpperCase()+
                            " for destroying the townhall of his enemy", "Notifications", 
                                JOptionPane.INFORMATION_MESSAGE);
                    setDisableTroops();
                    battleGround.setEnabled(false);
                }
                else if(line.contains("MESSAGE LAYOUT; ")){
                    String[] tempo = line.split("; ");
                    userLayout.put(tempo[1], tempo[2]);
                }
                else if(line.contains("START THE GAME!")){
                    startTheGame = true;
                }
                else if(!line.contains("IamManInTheMiddle: /")){
                    chatBox.append(line.substring(8) + "\n");
                }
                else{    
                    temp = line.substring(8);
                    break;
                }
            }
        }

        try{
            //create the connection with the server
                
            portAddress = 1234;
            String[] t = temp.split(" ");
            host = InetAddress.getByName(t[1].substring(1));
            newSocket = new Socket(host, 1234);
            
            //create a new buffered reader
            input2 = new BufferedReader(new InputStreamReader(newSocket.getInputStream()));
            output2 = new PrintWriter(newSocket.getOutputStream(), true);
            
            textField.removeActionListener(out);
            ActionListener out2 = new ActionListener() {
                //Make an action listener for the text field
                public void actionPerformed(ActionEvent e) {
                    output2.println(UserName+": "+textField.getText());
                    textField.setText("");
                }
            };
             
            textField.addActionListener(out2);
            textField.repaint();
            textField.revalidate();
       
        }catch(Exception e){e.printStackTrace();}
        //output the message
        while (true) {
            String line = input.readLine();
            if (line.startsWith("MESSAGE")) {
                if(line.contains("NAMEWINNER:")){
                    JOptionPane.showMessageDialog (null, "Congratulation to "+line.substring(20).toUpperCase()+
                            " for destroying the townhall of his enemy.", "Notifications", 
                                JOptionPane.INFORMATION_MESSAGE);
                    setDisableTroops();
                    battleGround.setEnabled(false);
                }
                else if(line.contains("MESSAGE LAYOUT; ")){
                    String[] tempo = line.split("; ");
                    userLayout.put(tempo[1], tempo[2]);
                }
                else if(line.contains("START THE GAME!")){
                    startTheGame=true;
                }
                else if(!line.contains("IamManInTheMiddle: /")){
                    chatBox.append(line.substring(8) + "\n");
                }
            }
        }
    }

    public static void loadPhotos() {
    //loading all the structure to be use
        battleGround.removeAll();
        positionStructs();
    }
    
  //this returns the read string from the conf file specified , (String parameter : file extension to be read)
    public static String readStructConfig(){
        String content = "";
        File file = new File("config/struct_config.conf");

        try {
            FileReader reader = new FileReader(file);			//adapted from stackoverflow.com solution
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            reader.close();
            content = new String(chars);
        }catch(Exception e){
           JOptionPane.showMessageDialog(null,"struct_config.conf does not exist.","Error Encountered!",JOptionPane.PLAIN_MESSAGE);
           return null;
        }
        return content;
    }
    
    public static void initializeBattleGround(String positions){
    	StringTokenizer strtok = new StringTokenizer(positions,"\n");
    	int i = 0;
    	int j = 0;
    	while (strtok.hasMoreTokens()){
    		StringTokenizer strtokSpace = new StringTokenizer(strtok.nextToken()," ");
    		while(strtokSpace.hasMoreTokens()){
    			position[i][j] = Integer.parseInt(strtokSpace.nextToken().replaceAll("(\\r|\\n|\\r\\n)",""));
    			j++;
    		}
    		i++;
    		j = 0;
    	}
    	printArray();
    	createList();
    }


    public static void createList(){
        for(int i=0;i<20;i++){
            int[] array = new int[3];
            array[0] = 1;   array[1] = 0;   array[2] = i;
            defaultPositions.add(array);
        }
        for(int i=0;i<10;i++){
            int[] array = new int[3];
            array[0] = 1;   array[1] = 1;   array[2] = i;
            defaultPositions.add(array);
        }
        
        int[] array = new int[3];
        array[0] = 2;   array[1] = 1;   array[2] = 10;
        defaultPositions.add(array);
        
        for(int i=0;i<10;i=i+2){
            array = new int[3];
            array[0] = 3;   array[1] = 4;   array[2] = i;
            defaultPositions.add(array);
        }
        for(int i=10;i<16;i=i+2){
            array = new int[3];
            array[0] = 4;   array[1] = 4;   array[2] = i;
            defaultPositions.add(array);
        }
        for(int i=0;i<6;i=i+2){
            array = new int[3];
            array[0] = 5;   array[1] = 6;   array[2] = i;
            defaultPositions.add(array);
        }
        for(int i=6;i<10;i=i+2){
            array = new int[3];
            array[0] = 6;   array[1] = 6;   array[2] = i;
            defaultPositions.add(array);
        }
        for(int i=10;i<14;i=i+2){
            array = new int[3];
            array[0] = 7;   array[1] = 6;   array[2] = i;
            defaultPositions.add(array);
        }
    }

    public static void printArray(){
        for(int i=0;i<TILEROWS;i++){
            for(int j=0;j<TILECOLS;j++)
                System.out.print(position[i][j] + " ");
            System.out.println();
        }
    }

    public static void positionStructs(){
        int windowWidth = (int)battleGround.getWidth();
        int windowHeight = (int) battleGround.getHeight();
        tileW = windowWidth/TILECOLS;
        tileH = windowHeight/TILEROWS;
        int picWidth,picHeight;
        Image img;
        Structures photo;
        String fileName;
        int x,y;

        picHeight = picWidth = x = y = 0;
        fileName = "";
        for(int i=0;i<defaultPositions.size();i++){
            int[] listEntry = defaultPositions.get(i);
            switch(listEntry[0]){
                    case 1: //Walls
                        picWidth = tileW;
                        picHeight = tileH;
                        fileName = "Wall";
                        break;
                    case 2: //Town Hall
                        picWidth = tileW * TOWNHALLDIAGONAL;
                        picHeight = tileH * TOWNHALLDIAGONAL;
                        fileName = "TownHall";
                        break;
                    case 3: //Cannons
                        picWidth = tileW * CANNONDIAGONAL;
                        picHeight = tileH * CANNONDIAGONAL;
                        fileName = "Cannon";
                        break;
                    case 4: //Mortars
                        picWidth = tileW * MORTARDIAGONAL;
                        picHeight = tileH * MORTARDIAGONAL;
                        fileName = "Mortar";
                        break;
                    case 5: //Archer Towers
                        picWidth = tileW * ARCHERTOWERDIAGONAL;
                        picHeight = tileH * ARCHERTOWERDIAGONAL;
                        fileName = "ArcherTower";
                        break;
                    case 6: //Wizard Towers
                        picWidth = tileW * WIZARDTOWERDIAGONAL;
                        picHeight = tileH * WIZARDTOWERDIAGONAL;
                        fileName = "WizardTower";
                        break;
                    case 7: //Air defenses
                        picWidth = tileW * AIRDEFENSEDIAGONAL;
                        picHeight = tileH * AIRDEFENSEDIAGONAL;
                        fileName = "AirDefense";
                        break;
            }
            x = tileWidth * listEntry[2];
            y = tileHeight * listEntry[1];
            img = Toolkit.getDefaultToolkit().createImage("images/"+fileName+".png");
            photo = new Structures(fileName,listEntry[1],listEntry[2]);
            battleGround.add(photo);
            photo.setImage(img);//Sets image
            photo.setAutoSize(true);//The component get ratio w/h of source image
            photo.setOverbearing(true);//On click ,this panel gains lowest z-buffer
            photo.setBorder(new LineBorder(Color.GREEN, 1));
            photo.setSize(picWidth, picHeight);
            photo.setLocation(x,y);
                
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height, int type){  
        BufferedImage resizedImage = new BufferedImage(width, height, type);  
        Graphics2D g = resizedImage.createGraphics();  
        g.drawImage(originalImage, 0, 0, width, height, null);  
        g.dispose(); 
        return resizedImage;  
    } 
    
    public void setEnableTroops(){
    	allowedDeployTroops = true;
    }
    
    public void setDisableTroops(){
    	allowedDeployTroops = false;
    }
    
    public void timerStart(){
    	// Convert the arguments first, to ensure that they are valid
        try {
           
            host = InetAddress.getByName( serverAddress );
            // Construct the socket
            socketUDP = new DatagramSocket(CLIENTPORT);
        }catch (Exception e1) {
            e1.printStackTrace();
        }

        Thread timerThread = new Thread(new Runnable() {
    		String [] listOfNames;
            String[] choicesName;
            String selectedEnemy;
            int x = 0;
    		int min = 0;
    		int sec = 0;
            public void run() {
            	while(x <= FIXINGTIME){
            		min = x / 60;
            		sec = x % 60;
            		try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            		
                    timer.setText(min + ":" + sec);
            		x++;
            	}
            	if(x > FIXINGTIME){
            		try{
                        host = InetAddress.getByName( serverAddress );
            			DatagramPacket receivedPacket = new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ;
            			DatagramPacket packet = new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ;
            	        
                        // Construct the data gram packet
                        String pos="";//send the position of the structures
                        for(int x=0;x<TILEROWS;x++){
                            for(int y=0;y<TILECOLS;y++){
                                pos = pos+position[x][y];
                                pos=pos+" ";
                            }
                        }
                        System.out.println(pos);
                        String mes="LAYOUT; " + UserName+ "; " + pos;
                        dataToBePassed.put("Message",mes);
            			dataToBePassed.put("Status","ToServer");
            			dataToBePassed.put("Address"," ");
            			dataToBePassed.put("Port",Integer.toString(1112));
            	        byte [] data = dataToBePassed.toString().getBytes() ;
            	        packet = new DatagramPacket( data, data.length, host, portAddress ) ;

            	        // Send it
            	        socketUDP.send( packet ) ;    

            	        dataToBePassed.clear();


                        dataToBePassed.put("Message","AskUserName");
                        dataToBePassed.put("Status","ToServer");
                        dataToBePassed.put("Address"," ");
                        dataToBePassed.put("Port",Integer.toString(1112));
                        data = dataToBePassed.toString().getBytes() ;
                        packet = new DatagramPacket( data, data.length, host, portAddress ) ;
            	      
                        // Send it
                        socketUDP.send( packet ) ; 


                        // Wait for a response from the server
            	        socketUDP.receive( receivedPacket ) ;
            	         
            	        //decode the usernames
            	        String dataReceived = new String(receivedPacket.getData());
            	        System.out.println(dataReceived);
            	         
            	        dataReceived = dataReceived.substring(1, dataReceived.length()-(PACKETSIZE-dataReceived.lastIndexOf('}')));
			            System.out.println(dataReceived);
			            String[] num = dataReceived.split(", ");
			           
			            for(int y=0;y<num.length;y++){
			            	String[] temp = num[y].split("=");
			            	dataToBePassed.put(temp[0],temp[1]);
			            } 			           
 			          
 			           String temp=dataToBePassed.get("Message");
 			           temp = temp.substring(1,temp.lastIndexOf(']'));
            	       listOfNames = temp.split(";");
            	         
            	         //erasing the name of self in the choices
            	         int count=0;
            	         for(int y=0;y<listOfNames.length;y++ ){
            	        	 if(!(listOfNames[y].contains(UserName)||listOfNames[y].contains("IamManInTheMiddle"))){
            	        		 count++;
            	        	 }
            	         }
            	         choicesName = new String[count];
            	         int cnt=0;
            	         for(int y=0;y<listOfNames.length;y++ ){
            	        	 if(!(listOfNames[y].contains(UserName)||listOfNames[y].contains("IamManInTheMiddle"))){
                	        	 choicesName[cnt]=listOfNames[y];
                	        	 cnt++;
            	        	 }
            	         }            	         
                        
                  }catch( Exception e ){
                     System.out.println(e);
                  }
                  finally{
                     if(socketUDP != null)
                        socketUDP.close();
                  }
                    
                    if(choicesName.length<=0){
                        JOptionPane.showMessageDialog(null,"You are the only player in the game", "WARNING",JOptionPane.WARNING_MESSAGE);
                    }else{
                        //make a pop-up to show the players
                        selectedEnemy = (String) JOptionPane.showInputDialog(null,"Choose one village you want to attack", "Input",JOptionPane.INFORMATION_MESSAGE, null,choicesName, choicesName[0]);
                        if(selectedEnemy!=null){
                            JOptionPane.showMessageDialog(null,"You can now start the game", "Start",JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
            	}
                //********Delete this code if you will test with the other player;
                selectedEnemy=UserName;
                //wait for starting the game
                while(startTheGame==false){
                   
                }

                //Load the base
                System.out.println(userLayout.toString());
                String pos=userLayout.get(selectedEnemy);
                String[] pos1=pos.split(" ");
                pos="";
                for(int x=0;x<pos1.length;x++){
                    
                    pos=pos+pos1[x];
                    if((x+1)%TILECOLS==0){
                        pos=pos+"\n";
                    }
                    else{pos=pos+" ";}
                }
                System.out.println(">>"+pos);
                
                //load the base of the enemy
                //battleGround.removeAll();
                //battleGround.repaint();

                System.out.println("Panel components removed.");
                
                java.awt.EventQueue.invokeLater(new Runnable() {

                    public void run() {
                            //processPos();//call the loading function
                    }});
                   
                battleGround.repaint();

                //Set the all structures undraggable
               setUnDraggable();
                
                //Enabled the deploying troop
                setEnableTroops();
                
                //declaring the winner
                while(true){
                    boolean state = Structures.gameOver();
                    if(state==true){
                        break;
                    }                   
                }               
                try {
                    socketUDP = new DatagramSocket(CLIENTPORT);
                } catch (SocketException e1) {
                    
                    e1.printStackTrace();
                }
                JOptionPane.showMessageDialog (null, "GAME OVER", "Warning", JOptionPane.ERROR_MESSAGE);
                String mes="GAMEDONE;"+UserName+";"+barbarianCount+";"+
                    archerCount+";"+giantCount+";"+wallBreakerCount+";"+dragonCount+";"+
                    wizardCount+";"+hogRiderCount+";";
                System.out.println(mes);
                DatagramPacket packetDone =new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ;
                 // Construct the data gram packet
                dataToBePassed.put("Message",mes);
                dataToBePassed.put("Status","ToServer");
                dataToBePassed.put("Address"," ");
                dataToBePassed.put("Port",Integer.toString(1112));
                byte [] data = dataToBePassed.toString().getBytes() ;
                packetDone = new DatagramPacket( data, data.length, host, portAddress ) ;

                 // Send it
                 try {
                    socketUDP.send( packetDone ) ;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                 if( socketUDP != null )
                    socketUDP.close() ;
                }
            }
    });
    	timerThread.start();
    	
    }

    public static void setUnDraggable(){
        Component[] components = battleGround.getComponents();
        for(int x=0;x<components.length;x++){
            Structures struct = (Structures)components[x];
            struct.setDraggable(false);
        }
    }
   
    public static void processPos(){
    	Image imgWall = Toolkit.getDefaultToolkit().createImage("images/Wall.png");
    	Image imgTownHall = Toolkit.getDefaultToolkit().createImage("images/TownHall.png");
    	Image imgCannon = Toolkit.getDefaultToolkit().createImage("images/Cannon.png");
    	Image imgMortar = Toolkit.getDefaultToolkit().createImage("images/Mortar.png");
    	Image imgArcherTower = Toolkit.getDefaultToolkit().createImage("images/ArcherTower.png");
    	Image imgWizardTower = Toolkit.getDefaultToolkit().createImage("images/WizardTower.png");
    	Image imgAirDefense = Toolkit.getDefaultToolkit().createImage("images/AirDefense.png");
        
        String positions = enemyLayout;
        int windowWidth = (int)battleGround.getWidth();
        int windowHeight = (int) battleGround.getHeight();
        tileW = windowWidth/TILECOLS;
        tileH = windowHeight/TILEROWS;
        int picWidth,picHeight;
        Image img = null;
        Structures photo;
        String fileName;
        int x,y;

        picHeight = picWidth = x = y = 0;
        fileName = "";
        
        StringTokenizer strtok = new StringTokenizer(positions,"\n");
        int i=0;
        int j=0;
        while (strtok.hasMoreTokens()){
            StringTokenizer strtokSpace = new StringTokenizer(strtok.nextToken()," ");
            while(strtokSpace.hasMoreTokens()){
                //System.out.println(strtokSpace.nextToken());
                position[i][j] = Integer.parseInt(strtokSpace.nextToken().replaceAll("(\\r|\\n|\\r\\n)",""));
                j++;
            }
            i++;
            j = 0;
        }
        
        //make the multiple number to be as one to avoid multiple rendering of the structure
        for(int a=0;a<TILEROWS;a++){
            for(int b=0;b<TILECOLS;b++){
                int number = position[a][b];
                switch(number){
               
                    case 2: //Town Hall
                        position[a][b+1]=0;
                        position[a][b+2]=0;
                        position[a+1][b]=0;
                        position[a+1][b+1]=0;
                        position[a+1][b+2]=0;
                        position[a+2][b]=0;
                        position[a+2][b+1]=0;
                        position[a+2][b+2]=0;
                        break;
                    case 3: //Cannons
                        position[a][b+1]=0;
                        position[a+1][b]=0;
                        position[a+1][b+1]=0;
                        break;
                    case 4: //Cannons
                        position[a][b+1]=0;
                        position[a+1][b]=0;
                        position[a+1][b+1]=0;
                        break;
                    case 5: //Cannons
                        position[a][b+1]=0;
                        position[a+1][b]=0;
                        position[a+1][b+1]=0;
                        break;
                    case 6: //Cannons
                        position[a][b+1]=0;
                        position[a+1][b]=0;
                        position[a+1][b+1]=0;
                        break;
                    case 7: //Cannons
                        position[a][b+1]=0;
                        position[a+1][b]=0;
                        position[a+1][b+1]=0;
                        break;
                }
            }
        }
        printArray();
        int counter = 0;
        //Loading the images.
        for(int a=0;a<TILEROWS;a++){
            for(int b=0;b<TILECOLS;b++){
                x=0;
                y=0;
                fileName="";
                        
                switch(position[a][b]){
                    case 1: //Walls
                        picWidth = tileW;
                        picHeight = tileH;
                        fileName = "Wall";
                        break;
                    case 2: //Town Hall
                        picWidth = tileW * TOWNHALLDIAGONAL;
                        picHeight = tileH * TOWNHALLDIAGONAL;
                        fileName = "TownHall";
                        break;
                    case 3: //Cannons
                        picWidth = tileW * CANNONDIAGONAL;
                        picHeight = tileH * CANNONDIAGONAL;
                        fileName = "Cannon";
                        break;
                    case 4: //Mortars
                        picWidth = tileW * MORTARDIAGONAL;
                        picHeight = tileH * MORTARDIAGONAL;
                        fileName = "Mortar";
                        break;
                    case 5: //Archer Towers
                        picWidth = tileW * ARCHERTOWERDIAGONAL;
                        picHeight = tileH * ARCHERTOWERDIAGONAL;
                        fileName = "ArcherTower";
                        break;
                    case 6: //Wizard Towers
                        picWidth = tileW * WIZARDTOWERDIAGONAL;
                        picHeight = tileH * WIZARDTOWERDIAGONAL;
                        fileName = "WizardTower";
                        break;
                    case 7: //Air defenses
                        picWidth = tileW * AIRDEFENSEDIAGONAL;
                        picHeight = tileH * AIRDEFENSEDIAGONAL;
                        fileName = "AirDefense";
                        break;
                    case 0:
                        break;
            }
        
            if(position[x][y]!=0){
                x = tileWidth * b;
                y = tileHeight * a;
                
                photo = new Structures(fileName,a,b);
                battleGround.add(photo);
                switch(fileName){
            		case "Wall" :
            			img = imgWall;
            			break;
            		case "TownHall" :
            			img = imgTownHall;
            			break;
            		case "Cannon" :
            			img = imgCannon;
            			break;
            		case "Mortar" :
            			img = imgMortar;
            			break;
            		case "ArcherTower" :
            			img = imgArcherTower;
            			break;
            		case "WizardTower" :
            			img = imgWizardTower;
            			break;
            		case "AirDefense" :
            			img = imgAirDefense;
            			break;
                }

                photo.setImage(img);//Sets image
                photo.setAutoSize(true);//The component get ratio w/h of source image
                photo.setOverbearing(true);//On click ,this panel gains lowest z-buffer
                photo.setBorder(new LineBorder(Color.GREEN, 1));
                photo.setSize(picWidth, picHeight);
                photo.setLocation(x,y);
                photo.setDraggable(false);
            }
            System.out.println(counter++);
        }
    }
}

    public static void main(String[] args) throws Exception {
		//What we need to do is halt the showing of the frame here until newLogIn gets response.
    	//startWindow sw = new startWindow();
    	frame.pack();
        frame.setVisible(true);
        
        TCPClient player = new TCPClient();
        Object[] options = { "OK","Cancel"};
        Object selected=JOptionPane.showOptionDialog(null, "You will be given 2 minutes to arrange you base.\n Make sure that your town hall is secured! \n ENJOY THE GAME !!", "Instruction",JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,null, options, options[0]);
         
        if((int)selected == 0){
        	player.timerStart();
        }
        
        player.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        player.frame.setVisible(true);
        player.run();
        
    }
}
