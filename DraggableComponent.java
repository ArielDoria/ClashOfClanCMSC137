/**
*    This class extends the DraggableComponent. Its main difference from its parent 
*    is that here is where we set the image of the component. This is where we load the 
*    JComponent's looks.
**/

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import javax.swing.JComponent;

public class DraggableComponent extends JComponent {

    private static Point position = null;       //This is the position where the drag ended.(Left and top most corner of picture.)
    private boolean draggable = true;           //If set as TRUE, this component is draggable
    protected Point anchorPoint;                //2D Point representing the coordinate where the mouse is, relative parent container 
    protected Cursor draggingCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);  //Default mouse cursor look for dragging action
    protected boolean overbearing = false;      // If set as TRUE when dragging component, it will be painted over each other.(z-Buffer change)
    protected String type;                      // This sets the structure type.
    protected int xPos;                         // This is the tile position of the component.(row)
    protected int yPos;                         // This is the tile position of the component.(column)

    /*CONSTRUCTOR function.
     * 		This is where mouseListeners and mouseDragListeners are being added. 
     * 		Opacity and background also being set here.
     * 		@param : -
     * 		@return : DraggableComponent instance
     */
    public DraggableComponent() {
        final DraggableComponent instance = this;
    	this.addMouseListener(new MouseListener(){       //add Mouse Listeners. Some are empty because all mouse events are needed to be implemented.
            public void mousePressed(MouseEvent e) {
            	updateArrayPositions(instance.xPos,instance.yPos,true);
            }
             
            public void mouseReleased(MouseEvent e) {   //This is for the snapping of the structures into the grid.
                setLocation(snapIntoGrid(position));
            }
             
            public void mouseEntered(MouseEvent e) {
            }
             
            public void mouseExited(MouseEvent e) {
            }
             
            public void mouseClicked(MouseEvent e) {
            }
        });
        addDragListeners();                             //This is to add the actions linked when we drag.
        setOpaque(true);                                //This paints every pixel within its bounds.
        setBackground(new Color(240,240,240));          
    }

    /** We have to define this method because a JComponent is a void box. So we have to define how it will be painted. 
    *   We create a simple filled rectangle.
    **/
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());  //fills the inside of the rectangle with the specified color.
        }
    }

    /**
     * Add Mouse Motion Listener with drag function
     */
    private void addDragListeners() {
        final DraggableComponent handle = this; //This handle is a reference to THIS beacause inside new MouseAdapter, "this" is not allowed 
        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {  //Invoked when a mouse button is pressed on a component and then dragged. Works with 
                anchorPoint = e.getPoint();                                 //get the point where mouse is
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));  //set how the cursor looks like
            }

            @Override
            public void mouseDragged(MouseEvent e) {  //Invoked when the dragging happens and mouse button has not been released.
                int anchorX = anchorPoint.x;                                //get x position of Point anchorPoint
                int anchorY = anchorPoint.y;                                //get y position of Point anchorPoint
                //getLocationOnScreen() gets the location of this component in the form of a point specifying the component's top-left corner in the screen's coordinate space.
                Point parentOnScreen = getParent().getLocationOnScreen();   //getParent() gets the next container housing this component 
                Point mouseOnScreen = e.getLocationOnScreen();
                
                position = new Point(mouseOnScreen.x - parentOnScreen.x - anchorX, mouseOnScreen.y - parentOnScreen.y - anchorY);
                
                setLocation(position);  //set the position based on computed point locations above.

                //Change Z-Buffer if it is "overbearing"
                if (overbearing) {
                    getParent().setComponentZOrder(handle, 0);
                    repaint();
                }
                

            }
        });
    }

    /**
     * Remove all Mouse Motion Listener. Freeze component.
     */
    private void removeDragListeners() {
        for (MouseMotionListener listener : this.getMouseMotionListeners()) { //for each MouseMotionListener, we get the instance and sort of turn off the mouse listener for awhile.
            removeMouseMotionListener(listener);
        }
        setCursor(Cursor.getDefaultCursor());
    }

    /**
     * Get the value of draggable
     */
    public boolean isDraggable() {
        return draggable;
    }

    /**
     * Set the value of draggable
     */
    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
        if (draggable) {    //turn on the mouseDragListeners
            addDragListeners();
        } else {            //turn off the mouseDragListeners
            removeDragListeners();
        }

    }

    /**
     * Get the value of draggingCursor
     */
    public Cursor getDraggingCursor() {
        return draggingCursor;
    }

    /**
     * Set the value of draggingCursor
     */
    public void setDraggingCursor(Cursor draggingCursor) {
        this.draggingCursor = draggingCursor;
    }

    /**
     * Get the value of overbearing
     */
    public boolean isOverbearing() {
        return overbearing;
    }

    /**
     * Set the value of overbearing
     */
    public void setOverbearing(boolean overbearing) {
        this.overbearing = overbearing;
    }

    //This checks the grid positioning of the component once it has been dragged into a location. It then snaps it onto the nearest grid that contains no structure.
    private Point snapIntoGrid(Point position){
        Point returnThis;
        //get the x and y positions nearest the position where dragging stopped.
        int x = detXTile(position.x);		//x is the column
        int y = detYTile(position.y);		//y is the rows

        boolean isSnappable = true;         //This determines if the new position is valid. Otherwise snap back into original position.
        switch(this.type){
            case "TownHall":
                for(int i=y;i<y+TCPClient.TOWNHALLDIAGONAL;i++){
                    for(int j=x;j<x+TCPClient.TOWNHALLDIAGONAL;j++){
                        if(TCPClient.position[i][j] != 0){
                            isSnappable = false;
                            break;
                        }
                        if(!isSnappable)break;
                    }
                }
                break;
            case "Wall":
                if(TCPClient.position[y][x] != 0)
                    isSnappable = false;
                break;
            case "Cannon":
            	for(int i=y;i<y+TCPClient.CANNONDIAGONAL;i++){
                    for(int j=x;j<x+TCPClient.CANNONDIAGONAL;j++){
                        if(TCPClient.position[i][j] != 0){
                            isSnappable = false;
                            break;
                        }
                        if(!isSnappable)break;
                    }
                }
                break;
            case "ArcherTower":
            	for(int i=y;i<y+TCPClient.ARCHERTOWERDIAGONAL;i++){
                    for(int j=x;j<x+TCPClient.ARCHERTOWERDIAGONAL;j++){
                        if(TCPClient.position[i][j] != 0){
                            isSnappable = false;
                            break;
                        }
                        if(!isSnappable)break;
                    }
                }
                break;
            case "Mortar":
            	for(int i=y;i<y+TCPClient.MORTARDIAGONAL;i++){
                    for(int j=x;j<x+TCPClient.MORTARDIAGONAL;j++){
                        if(TCPClient.position[i][j] != 0){
                            isSnappable = false;
                            break;
                        }
                        if(!isSnappable)break;
                    }
                }
                break;
            case "AirDefense":
            	for(int i=y;i<y+TCPClient.MORTARDIAGONAL;i++){
                    for(int j=x;j<x+TCPClient.MORTARDIAGONAL;j++){
                        if(TCPClient.position[i][j] != 0){
                            isSnappable = false;
                            break;
                        }
                        if(!isSnappable)break;
                    }
                }
                break;
            case "WizardTower":
            	for(int i=y;i<y+TCPClient.WIZARDTOWERDIAGONAL;i++){
                    for(int j=x;j<x+TCPClient.WIZARDTOWERDIAGONAL;j++){
                        if(TCPClient.position[i][j] != 0){
                            isSnappable = false;
                            break;
                        }
                        if(!isSnappable)break;
                    }
                }
                break;
        }
        if(isSnappable){ //also update the array of positions.
        	updateArrayPositions(y,x,false);
        	x = x * TCPClient.tileW;
            y = y * TCPClient.tileH;
        }
        else{
            x = this.yPos * TCPClient.tileW;
            y = this.xPos * TCPClient.tileH;
        }
        returnThis = new Point(x,y);
        return returnThis;//return the new position since the position is valid.
    }

    //This returns the x tile found given a raw x position.
    private int detXTile(int x){
        x = x / TCPClient.tileW;
        return x;
    }

    //This returns the y tile found given a raw y position.
    private int detYTile(int y){
        y = y / TCPClient.tileH;
        return y;
    }

	protected void updateArrayPositions(int x,int y,boolean removed){
		 int value = 0;
		
		 switch(this.type){
         case "TownHall":
        	 if(removed)		value = 0;
        	 else			value = 2;
        	 
        	 for(int i=x;i<x+TCPClient.TOWNHALLDIAGONAL;i++){
                 for(int j=y;j<y+TCPClient.TOWNHALLDIAGONAL;j++){
                	 TCPClient.position[i][j] = value;
                 }
             }
             break;
         case "Wall":
        	 if(removed)
        		value = 0;
        	 else
        		value = 1;
        	 TCPClient.position[x][y] = value;
             
             break;
         case "Cannon":
             break;
         case "ArcherTower":
             break;
         case "Mortar":
             break;
         case "AirDefense":
             break;
         case "WizardTower":
             break;
		 }
		 if(!removed){
			 this.xPos = x; // might be erroneous since coordinate system is vague
			 this.yPos = y;
		 }
	}
}

