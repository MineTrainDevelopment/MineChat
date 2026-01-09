package de.minetrain.minechat.features.messagehighlight;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.gui.utils.ColorManager;
import javafx.scene.paint.Color;

public class HighlightDefault {
	private final String configPath;
	private int borderColor;
	private boolean active;

	private transient Color cachedColor;

	public HighlightDefault(YamlManager settings, String path) {
		this.borderColor = ColorManager.encodeToInt(Color.web(settings.getColor(path + ".Color")));
		this.active = settings.getBoolean(path + ".Active");
		this.configPath = path;
	}

	public int getColor() {
		return borderColor;
	}

	public Color getColorAsColor() {
		if(cachedColor == null) {
			cachedColor = ColorManager.decodeFromInt(borderColor);
		}
		return cachedColor;
	}

	public String getConfigPath() {
		return configPath;
	}

	public boolean isActive() {
		return active;
	}

	public void save(){
		Settings.settings.setBoolean(configPath+".Active", active);
		Settings.settings.setString(configPath+".Color", ColorManager.encode(getColor()));
		Settings.settings.saveConfigToFile();
	}

	public void setActive(boolean active){
		this.active = active;
	}

	public void setColor(int newColor) {
		borderColor = newColor;
	}
}
