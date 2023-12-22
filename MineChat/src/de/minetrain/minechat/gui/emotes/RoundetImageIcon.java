package de.minetrain.minechat.gui.emotes;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.slf4j.LoggerFactory;

/**
 * I stolle the code from <a href="URL#https://stackoverflow.com/a/7603815/20851022">Stack Overflow</a> and fixed it for gif usage.
 * @author Philipp Reichart
 */
public class RoundetImageIcon extends ImageIcon {
	private static final long serialVersionUID = 8678114301574404172L;
	int cornerRadius = 0;

	public RoundetImageIcon(Path path, int cornerRadius) {
        super(makeRoundedCorner(createBufferedImage(path), cornerRadius, Color.WHITE));
    }

    public RoundetImageIcon(ImageIcon icon, int cornerRadius) {
        super(makeRoundedCorner(convertImageToBufferedImage(icon), cornerRadius, Color.WHITE));
    }

	public RoundetImageIcon(Path path, int cornerRadius, Color background) {
        super(makeRoundedCorner(createBufferedImage(path), cornerRadius, background));
    }

    public RoundetImageIcon(ImageIcon icon, int cornerRadius, Color background) {
        super(makeRoundedCorner(convertImageToBufferedImage(icon), cornerRadius, background));
    }
    
    private static BufferedImage createBufferedImage(Path path) {
		try {
			return ImageIO.read(path.toFile());
		} catch (IOException ex) {
			LoggerFactory.getLogger(RoundetImageIcon.class).warn("Can´t read image -> "+path, ex);
			return new BufferedImage(1, 1, 1);
		}
	}
    
    
    /**
     * Converts an Image object to a BufferedImage.
     *
     * @param image The Image to be converted.
     * @return The converted BufferedImage.
     */
    public static BufferedImage convertImageToBufferedImage(ImageIcon icon) {
        // Create a BufferedImage with the same width and height as the Image
        BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

        // Get the Graphics2D object from the BufferedImage
        Graphics2D g2d = bufferedImage.createGraphics();

        // Draw the Image onto the BufferedImage
        g2d.drawImage(icon.getImage(), 0, 0, null);

        // Dispose of the Graphics2D object to free up resources
        g2d.dispose();

        return bufferedImage;
    }
    
    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius, Color background) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();
        
        // This is what we want, but it only does hard-clipping, i.e. aliasing
        // g2.setClip(new RoundRectangle2D ...)

        // so instead fake soft-clipping by first drawing the desired clip shape
        // in fully opaque white with antialiasing enabled...
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(background);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
        
        // ... then compositing the image on top,
        // using the white shape from above as alpha source
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        
        g2.dispose();
        
        return output;
    }

}