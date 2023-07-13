package de.minetrain.minechat.utils.obj;

import java.awt.Color;

import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.utils.ColorManager;

public class HighlightGiftSubs extends HighlightDefault{
	private final String borderColorCodeBig;
	
	public HighlightGiftSubs(YamlManager settings, String path) {
		super(settings, path);
		super.setColorCode(settings.getString(path+".ColorSmall"));
		this.borderColorCodeBig = settings.getString(path+".ColorBig");
	}

	public Color getBigColor() {
		return ColorManager.decode(borderColorCodeBig);
	}
	
	@Override
	public void save() {
		Settings.settings.setBoolean(getConfigPath()+".Active", isActive());
		Settings.settings.setString(getConfigPath()+".ColorSmall", getColorCode());
		Settings.settings.setString(getConfigPath()+".ColorBig", borderColorCodeBig);
		Settings.settings.saveConfigToFile();
	}
	
}
