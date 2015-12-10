/***
*    This class extends the DraggableComponent. Its main difference from its parent 
*    is that here is where we set the image of the component. This is where we load the 
*    JComponent's looks.
*    It implements  ImageObserver for Image loading problems. it repaints itself after
*    image is full loaded.
**/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;

public class DraggableImageComponent extends DraggableComponent implements ImageObserver {

    protected Image image;                  //how the component looks like.
    private boolean autoSize = false;       
    private Dimension autoSizeDimension = new Dimension(0, 0);

    public DraggableImageComponent() {
        super();
        setLayout(null);
        setBackground(Color.black);
    }

    /**
     * This overrided method paints image on Component if any. Else it paints a Background color.
     * If autoSize is TRUE , it paints image with original ration, on a Background Color box
     * if opaque.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (image != null) {
            setAutoSizeDimension();
            g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);  //draw the
        } else {
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * It is a simple tecnique to retrieve dimensions of Image, preserving ratio w/h of image
     * and make a best matching on the parent box.
     */
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

    /**
     * Checks if image is full loaded.
     *
     * @param img - target image
     * @param infoflags - is equal to ALLBITS when loading is finished
     * @param x - x position
     * @param y - y position
     * @param w - width
     * @param h - height
     * @return TRUE if image can generate events, FALSE otherwise
     */
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
     * @param autoSize - new value of autoSize
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
     * @param image - fileName of image
     */
    public void setImage(String image) {
        setImage(Toolkit.getDefaultToolkit().getImage(image));
    }

    /**
     * Set the value of image
     *
     * @param image - new value of image
     */
    public void setImage(Image image) {
        this.image = image;
        repaint();
        setAutoSizeDimension();
    }
}
