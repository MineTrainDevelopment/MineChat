package de.minetrain.minechat.main.gui.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class MineButton extends JButton{
	private static final long serialVersionUID = -8580714214977222615L;

	/**
	 * 
	 * @param size
	 * @param location
	 * @param texture
	 * @param actionListener
	 */
	public MineButton(Dimension size, Point location, ImageIcon texture, ActionListener actionListener) {
		setSize(size);
		setLocation(location);
		setIcon(texture);
		addActionListener(actionListener);
	}
	
	/**
	 * 
	 * @param foreGround
	 * @param backGround
	 * @return
	 */
	public MineButton setColors(Color foreGround, Color backGround){
		setForeground(foreGround);
		setBackground(backGround);
		return this;
	}
}
