import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class BackgroundPanel extends JPanel {
	private Image guiImage;
    private static final long serialVersionUID = 1L;

    public BackgroundPanel(){
       super();
       setOpaque(false);
      
    }
    public void setImage(BufferedImage setGuiImage)
    {
    	 guiImage = setGuiImage;
    }
    @Override
    public void paintComponent(final Graphics g)
    {
       super.paintComponent(g);
       final Dimension d = getSize();
       g.drawImage(guiImage, 0, 0, d.width, d.height, null);
    }
}


   