package de.minetrain.minechat.main.gui.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ChannelTab {
	private final ImageIcon texture;
	private final String tabName;
	private final JLabel tabLabel;
	
	
	public ChannelTab(ImageIcon texture, String tabName) {
		this.texture = texture;
		this.tabName = tabName;
		this.tabLabel = new JLabel(this.tabName, SwingConstants.CENTER);
		tabLabel.setVisible(true);
		tabLabel.setForeground(Color.WHITE);
	}
	
	
	public ImageIcon getTexture(){
		return texture;
	}
	
	public JLabel getTabLabel() {
		return tabLabel;
	}
	
	public JLabel getTabLabelWithData(Point point, Dimension size) {
		tabLabel.setLocation(point);
		tabLabel.setSize(size);
		return tabLabel;
	}
	
	

	
}
