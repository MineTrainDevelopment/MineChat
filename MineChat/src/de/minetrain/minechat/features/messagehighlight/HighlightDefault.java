package de.minetrain.minechat.features.messagehighlight;

import java.awt.Color;

import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.utils.ColorManager;

public class HighlightDefault {
	private final String configPath;
	private String borderColorCode;
	private boolean active;
	
	public HighlightDefault(YamlManager settings, String path) {
		this.borderColorCode = settings.getString(path+".Color");
		this.active = settings.getBoolean(path+".Active");
		this.configPath = path;
	}

	public String getColorCode() {
		return borderColorCode;
	}
	
	public Color getColor() {
		return ColorManager.decode(borderColorCode);
	}

	public String getConfigPath() {
		return configPath;
	}

	public boolean isActive() {
		return active;
	}
	
	public void save(){
		Settings.settings.setBoolean(configPath+".Active", active);
		Settings.settings.setString(configPath+".Color", borderColorCode);
		Settings.settings.saveConfigToFile();
	}
	
	public void setActive(boolean active){
		this.active = active;
	}
	
	public void setColorCode(String newColorCode) {
		borderColorCode = newColorCode;
	}
	
}
