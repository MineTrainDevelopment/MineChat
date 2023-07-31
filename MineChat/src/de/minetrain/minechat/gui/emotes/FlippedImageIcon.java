package de.minetrain.minechat.gui.emotes;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * I stolle the code from <a href="URL#https://stackoverflow.com/a/1708909/20851022">Stack Overflow</a> and modified it for vertical flipping.
 * @author Savvas Dalkitsis
 */
public class FlippedImageIcon extends ImageIcon {
	private static final long serialVersionUID = 8678114301574404172L;

	public FlippedImageIcon(String filename) {
        super(filename);
    }
    
    public FlippedImageIcon(Image image) {
        super(image);
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.translate(0, getIconHeight()); 
        g2.scale(1, -1);
        super.paintIcon(c, g2, x, -y);
    }

}