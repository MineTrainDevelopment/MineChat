package de.minetrain.minechat.main.gui.buttons;

import javax.swing.ImageIcon;

public class ChannelTab {
	private final ImageIcon texture;
	private final String tabName;
	
	
	public ChannelTab( ImageIcon texture, String tabName) {
		this.texture = texture;
		this.tabName = tabName;
	}
	
	
	public ImageIcon getTexture(){
		return texture;
	}
	
	

	
}
