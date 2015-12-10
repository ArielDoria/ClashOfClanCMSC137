import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.border.LineBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;


public class Troops extends JComponent implements ImageObserver{
    
    private String type;
    private int hitpoint;
    private int damagepersec;
    protected Image image;
        private boolean autoSize = false;
        private Dimension autoSizeDimension = new Dimension(0, 0);
        protected boolean overbearing = false;  
        private int x=0;
        private int y=0;
        public static boolean ableToMove = true;
    
    public Troops(String type, int x, int y){
        setOpaque(true);
        setLayout(null) ;
        this.x=x;
        this.y=y;
         
         //Instantiate the troops
        if(type.equals("Barbarian")){
            this.type = type;
            this.hitpoint = 45;
            this.damagepersec=10;
        }
        if(type.equals("Archer")){
            this.type = type;
            this.hitpoint = 20;
            this.damagepersec=5;
        }
        if(type.equals("Dragon")){
            this.type = type;
            this.hitpoint = 1000;
            this.damagepersec=210;
        }
        if(type.equals("HogRider")){
            this.type = type;
            this.hitpoint = 270;
            this.damagepersec=60;
        }
        if(type.equals("Giant")){
            this.type = type;
            this.hitpoint = 300;
            this.damagepersec=20;
        }
        if(type.equals("WallBreaker")){
            this.type = type;
            this.hitpoint = 20;
            this.damagepersec=480;
        }
        if(type.equals("Wizard")){
            this.type = type;
            this.hitpoint = 75;
            this.damagepersec=75;
        }
        
        final Troops tro = this;
        
        //Create thread for the moving of troops
        Thread animationThread = new Thread(new Runnable() {
            public void run() { 
                int x = tro.x;
                int y = tro.y;
                boolean move = true;
                boolean forward = true;
                boolean downward = true;
                int currentX = 0;
                int currentY = 0;
                
                while (ableToMove) {
                    //check if the troop is on the side.
                    if(x == (TCPClient.groundWidth-10)){
                        forward = false;
                        if(downward){
                            y = y + TCPClient.tileHeight;
                        }
                        else{
                            y = y - TCPClient.tileHeight;
                        }
                    }
                    if(x == 0){
                        forward = true;
                        if(downward == true){
                            y = y + TCPClient.tileHeight;
                        }
                        else{
                            y = y - TCPClient.tileHeight;
                        }
                    }
            
                    if(y > TCPClient.groundHeight-50){
                        downward = false;
                    }
                    if(y < 50){
                        downward = true;
                    }
                    
                    tro.setLocation(x,y);
                    
                    if(forward == true){
                        x++;
                    }
                    else{
                        x--;
                    }
                    TCPClient.battleGround.repaint();
                        try {Thread.sleep(5);} catch (Exception ex) {}
                       
                        //get the component at x and y coordinate and check if there is structure along the vicinity
                        try{
                        	if(forward){
                        	if(tro.type.equals("Archer")||tro.type.equals("Dragon")||tro.type.equals("Wizard")){
                               
                        		String classComponent1 =((TCPClient.battleGround.getComponentAt(x+TCPClient.tileWidth,y)).getClass()).getCanonicalName();
                                if(!classComponent1.isEmpty()){
                                    if(classComponent1.equals("Structures")){
                                        boolean attack =true;
                                        //create a structure for the nearby structure
                                        Structures obj =  (Structures)(TCPClient.battleGround.getComponentAt(x+TCPClient.tileWidth,y));
                                        if(obj.getType().equals("Wall") && !tro.type.equals("WallBreaker")){
                                            attack = false;
                                                if(downward){
                                                    y = y + TCPClient.tileHeight;
                                                }else{
                                                    y = y - TCPClient.tileHeight;
                                                }
                                        }
                                    
                                        if(attack){
                                            while(obj.alive() && tro.alive()){
                                                //attack the nearby structure
                                                obj.attack(tro.damagepersec);
                                                Thread.sleep(1000);
                                            }
                                        }
                                    
                                    }
                                    }
                                String classComponent2 =((TCPClient.battleGround.getComponentAt(x+(2*TCPClient.tileWidth),y)).getClass()).getCanonicalName();
                                if(!classComponent2.isEmpty()){
                                    if(classComponent2.equals("Structures")){
                                        boolean attack =true;
                                        //create a structure for the nearby structure
                                        Structures obj =  (Structures)(TCPClient.battleGround.getComponentAt(x+(2*TCPClient.tileWidth),y));
                                        if(obj.getType().equals("Wall")&&!tro.type.equals("WallBreaker")){
                                        
                                            attack = false;
                                                if(downward){
                                                    y = y + TCPClient.tileHeight;
                                                }else{
                                                    y = y - TCPClient.tileHeight;
                                                }
                                        }
                                    
                                        if(attack){
                                            while(obj.alive() && tro.alive()){
                                                //attack the nearby structure
                                                obj.attack(tro.damagepersec);
                                                Thread.sleep(1000);
                                            }
                                        }
                                    
                                    }
                                }
                                String classComponent3 =((TCPClient.battleGround.getComponentAt(x+(3*TCPClient.tileWidth),y)).getClass()).getCanonicalName();
                                if(!classComponent3.isEmpty()){
                                    if(classComponent3.equals("Structures")){
                                        boolean attack =true;
                                        //create a structure for the nearby structure
                                        Structures obj =  (Structures)(TCPClient.battleGround.getComponentAt(x+(3*TCPClient.tileWidth),y));
                                        if(obj.getType().equals("Wall")&&!tro.type.equals("WallBreaker")){
                                        
                                            attack = false;
                                            if(downward){
                                                y = y + TCPClient.tileHeight;
                                            }else{
                                                y = y - TCPClient.tileHeight;
                                            }
                                        }
                                    
                                        if(attack){
                                            while(obj.alive()&&tro.alive()){
                                                //attack the nearby structure
                                                obj.attack(tro.damagepersec);
                                                Thread.sleep(1000);
                                            }
                                        }
                                    
                                    }
                                }
                        	}else if(!forward){
                        		String classComponent1 = ((TCPClient.battleGround.getComponentAt(x-TCPClient.tileWidth,y)).getClass()).getCanonicalName();
                                if(!classComponent1.isEmpty()){
                                    if(classComponent1.equals("Structures")){
                                        boolean attack = true;
                                        //create a structure for the nearby structure
                                        Structures obj =  (Structures)(TCPClient.battleGround.getComponentAt(x-TCPClient.tileWidth,y));
                                        if(obj.getType().equals("Wall")&&!tro.type.equals("WallBreaker")){
                                            attack = false;
                                                if(downward){
                                                    y = y + TCPClient.tileHeight;
                                                }else{
                                                    y = y - TCPClient.tileHeight;
                                                }
                                        }
                                    
                                        if(attack){
                                            while(obj.alive() && tro.alive()){
                                                //attack the nearby structure
                                                obj.attack(tro.damagepersec);
                                                Thread.sleep(1000);
                                            }
                                        }
                                    
                                    }
                                }
                                
                                String classComponent2 = ((TCPClient.battleGround.getComponentAt(x-(2*TCPClient.tileWidth),y)).getClass()).getCanonicalName();
                                if(!classComponent2.isEmpty()){
                                    if(classComponent2.equals("Structures")){
                                        boolean attack = true;
                                        //create a structure for the nearby structure
                                        Structures obj =  (Structures)(TCPClient.battleGround.getComponentAt(x-(2*TCPClient.tileWidth),y));
                                        if(obj.getType().equals("Wall") && !tro.type.equals("WallBreaker")){
                                        
                                            attack = false;
                                            if(downward){
                                                y = y + TCPClient.tileHeight;
                                            }else{
                                                y = y - TCPClient.tileHeight;
                                            }
                                        }
                                    
                                        if(attack){
                                            while(obj.alive()&&tro.alive()){
                                                //attack the nearby structure
                                                obj.attack(tro.damagepersec);
                                                Thread.sleep(1000);
                                            }
                                        }
                                    
                                    }
                                }
                                String classComponent3 = ((TCPClient.battleGround.getComponentAt(x-(3*TCPClient.tileWidth),y)).getClass()).getCanonicalName();
                                if(!classComponent3.isEmpty()){
                                    if(classComponent3.equals("Structures")){
                                        boolean attack = true;
                                        //create a structure for the nearby structure
                                        Structures obj =  (Structures)(TCPClient.battleGround.getComponentAt(x-(3*TCPClient.tileWidth),y));
                                        if(obj.getType().equals("Wall") && !tro.type.equals("WallBreaker")){
                                            attack = false;
                                            if(downward){
                                                y = y + TCPClient.tileHeight;
                                            }else{
                                                 y = y - TCPClient.tileHeight;
                                            }
                                        }
                                        if(attack){
                                            while(obj.alive() && tro.alive()){
                                                //attack the nearby structure
                                                obj.attack(tro.damagepersec);
                                                Thread.sleep(1000);
                                            }
                                        }
                                    
                                    }
                                }
                        	}
                                
                                }
                        	if(!tro.type.equals("WallBreaker")){
                                String classComponent = ((TCPClient.battleGround.getComponentAt(x,y)).getClass()).getCanonicalName();
                               
                                if(!classComponent.isEmpty()){
                                    if(classComponent.equals("Structures")){
                                        boolean attack = true;
                                        //create a structure for the nearby structure
                                        Structures obj = (Structures)(TCPClient.battleGround.getComponentAt(x,y));
                                        if(obj.getType().equals("Wall") && !tro.type.equals("WallBreaker")){
                                        
                                            attack = false;
                                                if(downward){
                                                    y = y + TCPClient.tileHeight;
                                                }else{
                                                    y = y - TCPClient.tileHeight;
                                                }
                                        }
                                    
                                        if(attack){
                                            while(obj.alive()&&tro.alive()){
                                                //attack the nearby structure
                                                obj.attack(tro.damagepersec);
                                                Thread.sleep(1000);
                                            }
                                        }
                                    
                                    }
                                }
                        	}
                        	if(tro.type.equals("WallBreaker")){
                                String classComponent = ((TCPClient.battleGround.getComponentAt(x,y)).getClass()).getCanonicalName();
                               
                                if(!classComponent.isEmpty()){
                                    if(classComponent.equals("Structures")){
                                        boolean attack =true;
                                        //create a structure for the nearby structure
                                        Structures obj =  (Structures)(TCPClient.battleGround.getComponentAt(x,y));
                                        if(!obj.getType().equals("Wall")&&tro.type.equals("WallBreaker")){
                                        
                                            attack = false;
                                            if(downward){
                                                y = y + TCPClient.tileHeight;
                                            }else{
                                                y = y - TCPClient.tileHeight;
                                            }
                                        }
                                    
                                        if(attack){
                                            while(obj.alive()&&tro.alive()){
                                                //attack the nearby structure
                                                obj.attack(tro.damagepersec);
                                                Thread.sleep(1000);
                                            }
                                        }
                                    
                                    }
                                }
                            }
                        }catch(Exception e){}                                      
                }
            }
            
        });

        animationThread.start();    //start the thread
    }
        
    public int getHitPoint(){
        return this.hitpoint;
    }
    
    public int getDamagePerSec(){
        return this.damagepersec;
    }

        
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (image != null) {
            setAutoSizeDimension();
            g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    
    private Dimension adaptDimension(Dimension source, Dimension dest) {
        int sW = source.width;
        int sH = source.height;
        int dW = dest.width;
        int dH = dest.height;
        double ratio = ((double) sW) / ((double) sH);
        if (sW >= sH) {
            sW = dW;
            sH = (int) (sW / ratio);
        } else {
            sH = dH;
            sW = (int) (sH * ratio);
        }
        return new Dimension(sW, sH);
    }
    
        
    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {
        if (infoflags == ALLBITS) {
            repaint();
            setAutoSizeDimension();
            return false;
        }
        return true;
    }

    /**
     * This method is used to resize component considering w/h ratio of image. 
     */
    private void setAutoSizeDimension() {
        if (!autoSize) {
            return;
        }
        if (image != null) {
            if (image.getHeight(null) == 0 || getHeight() == 0) {
                return;
            }
            if ((getWidth() / getHeight()) == (image.getWidth(null) / (image.getHeight(null)))) {
                return;
            }
            autoSizeDimension = adaptDimension(new Dimension(image.getWidth(null), image.getHeight(null)), this.getSize());
            setSize(autoSizeDimension.width, autoSizeDimension.height);
        }
    }

    /**
     * It is used to Resize component when it has an AutoSize value setted on TRUE
     * @param pixels
     */
    public void grow(int pixels) {
        double ratio = getWidth() / getHeight();
        setSize(getSize().width + pixels, (int) (getSize().height + (pixels / ratio)));
    }

    /**
     * Get the value of autoSize
     *
     * @return the value of autoSize
     */
    public boolean isAutoSize() {
        return autoSize;
    }

    /**
     * Set the value of autoSize
     *
     * @param autoSize new value of autoSize
     */
    public void setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
    }

    /**
     * Get the value of image
     *
     * @return the value of image
     */
    public Image getImage() {
        return image;
    }

    /**
     * Set the value of image by String name. Use ToolKit to create image from file.
     * use setImage(Image image) if you just have an image.
     *
     * @param image fileName of image
     */
    public void setImage(String image) {
        setImage(Toolkit.getDefaultToolkit().getImage(image));
    }

    /**
     * Set the value of image
     *
     * @param image new value of image
     */
    public void setImage(Image image) {
        this.image = image;
        repaint();
        setAutoSizeDimension();
    }
    
    public void setOverbearing(boolean overbearing) {
        this.overbearing = overbearing;
    }
    
    public int getXComponent(){
        return this.getX();
    }
    
    public int getYComponent(){
        return this.getY();
    }
    
    //function for attacking the troop
    public void attack(int less){
        this.hitpoint=this.hitpoint-less;
    }
    
    //check if the troop is alive
    public boolean alive(){
        
        if(this.hitpoint <= 0){
            //System.out.println(this.type+" has died.");
            //remove the troop in the field
            try{
                TCPClient.battleGround.remove(this);
            }catch(Exception e){}
            
            TCPClient.battleGround.repaint();
            return false;
        }

        else{
            return true;
        }
    }
}
