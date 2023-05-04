package de.minetrain.minechat.gui.objects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ChannelTab {
	private final TabButtonType tabType;
	private final ImageIcon texture;
	private final JButton tabButton;
	private final String tabName;
	private final JLabel tabLabel;
	
	
	public ChannelTab(JButton button, ImageIcon texture, String tabName, TabButtonType tabType) {
		this.tabButton = button;
		this.texture = texture;
		this.tabName = tabName;
		this.tabType = tabType;
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
	
	public ChannelTab offsetButton(TabButtonType offset){
		Point location = tabButton.getLocation();
		location.setLocation(location.getX(), offset.getOffset(tabType, location.y));
		tabButton.setLocation(location);
		return this;
	}
	
	

	
}
