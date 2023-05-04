package de.minetrain.minechat.gui.objects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	public MineButton(Dimension size, Point location, ButtonType type) {
		setSize(size);
		setLocation(location);
		addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(type != null){
					System.out.println(type.toString());
				}
			}
		});
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
	
	public MineButton setInvisible(boolean state){
		setOpaque(!state);
		setContentAreaFilled(!state);
		setBorderPainted(!state);
		return this;
	}
}
