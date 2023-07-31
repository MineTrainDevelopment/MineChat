package de.minetrain.minechat.gui.emotes;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.ImageIcon;

public class BackgroundImageIcon extends ImageIcon {
	private static final long serialVersionUID = -3589677272275003236L;
	private final int iconGap = 4;
    private final ImageIcon background;

    public BackgroundImageIcon(Emote emote) {
        super(emote.getFilePath());
        this.background = emote.getBorderImage();
    }
    
    public BackgroundImageIcon(String iconPath, ImageIcon background) {
        super(iconPath);
        this.background = background;
    }
    
    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
    	background.paintIcon(c, g, x, y);
        super.paintIcon(c, g.create(), x+iconGap, y+iconGap);
    }

    @Override
    public int getIconWidth() {
        return background.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return background.getIconHeight();
    }
}