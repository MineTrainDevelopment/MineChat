package de.minetrain.minechat.gui.objects;

import java.awt.Dimension;
import java.awt.Point;

import de.minetrain.minechat.main.Main;

public class MacroButton extends MineButton{
	private final ButtonType buttonType;
	private final String buttonName;
	private final String macroOutput;

	public MacroButton(Dimension size, Point location, ButtonType type, String channelName) {
		super(size, location, type);
		this.buttonType = type;
		
		String[] config = Main.CONFIG.getString(5451451+".Macros."+type.getConfigIndex()).split("%-%");
		this.buttonName = config[0];
		this.macroOutput = config[1];
	}
	
	

}
