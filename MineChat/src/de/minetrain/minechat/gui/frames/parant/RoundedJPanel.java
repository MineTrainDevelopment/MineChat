package de.minetrain.minechat.gui.frames.parant;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class RoundedJPanel extends JPanel{
	private static final long serialVersionUID = 6284082896929966056L;
	private int cornerRadius = 15;

	public RoundedJPanel(Color backgroundColor, Color borderColor, int width, int height, Integer... cornerRadius) {
		super();
		setForeground(borderColor);
		setBackground(backgroundColor);
		setSize(width, height);
		
		if(cornerRadius != null && cornerRadius.length > 0){
			this.cornerRadius = cornerRadius[0];
		}
	}
	
	public RoundedJPanel setCornerRadius(int radius){
		this.cornerRadius = radius;
		return this;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension arcs = new Dimension(cornerRadius, cornerRadius);
		Graphics2D graphics = (Graphics2D) g;
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draws the rounded opaque panel with borders.
		graphics.setColor(getBackground());
		graphics.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcs.width, arcs.height);// paint background
		graphics.setColor(getForeground());
		graphics.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcs.width, arcs.height);// paint border
		this.setOpaque(false);
	}
}
