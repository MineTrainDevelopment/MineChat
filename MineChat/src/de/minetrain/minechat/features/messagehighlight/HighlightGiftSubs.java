package de.minetrain.minechat.features.messagehighlight;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.gui.utils.ColorManager;
import javafx.scene.paint.Color;

public class HighlightGiftSubs extends HighlightDefault {

	private final int borderColorBig;

	private transient Color cachedBigColor;

	public HighlightGiftSubs(YamlManager settings, String path) {
		super(settings, path);
		setColor(ColorManager.encodeToInt(Color.web(settings.getColor(path + ".ColorSmall"))));
		borderColorBig = ColorManager.encodeToInt(Color.web(settings.getColor(path + ".ColorBig")));
	}

	public int getBigColor() {
		return borderColorBig;
	}

	public Color getBigColorAsColor() {
		if(cachedBigColor == null) {
			cachedBigColor = ColorManager.decodeFromInt(borderColorBig);
		}
		return cachedBigColor;
	}

	@Override
	public void save() {
		Settings.settings.setBoolean(getConfigPath() + ".Active", isActive());
		Settings.settings.setString(getConfigPath() + ".ColorSmall", ColorManager.encode(getColor()));
		Settings.settings.setString(getConfigPath() + ".ColorBig", ColorManager.encode(getBigColor()));
		Settings.settings.saveConfigToFile();
	}

}
