package de.minetrain.minechat.gui.utils;

import java.awt.Color;

import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.YamlManager;

//import javax.swing.JColorChooser;

public class ColorManager {
	public static YamlManager settings;
	public static final Color FONT_DEFAULT = Color.WHITE;
	public static final Color GUI_BORDER_DEFAULT = new Color(14, 14, 14);
	public static final Color GUI_BACKGROUND_DEFAULT = new Color(40, 40, 40);
	public static final Color GUI_BACKGROUND_LIGHT_DEFAULT = new Color(80, 80, 80);
	public static final Color GUI_BUTTON_BACKGROUND_DEFAULT = new Color(30, 30, 30);
	
	public static final Color CHAT_UNIMPORTANT_DEFAULT = Color.GRAY;
    public static final Color CHAT_MODERATION_DEFAULT = Color.CYAN;
    public static final Color CHAT_SPENDING_SMALL_DEFAULT = new Color(180, 80, 0);
    public static final Color CHAT_SPENDING_BIG_DEFAULT = Color.YELLOW;
    public static final Color CHAT_ANNOUNCEMENT_DEFAULT = Color.GREEN;
    public static final Color CHAT_USER_REWARD_DEFAULT = Color.BLUE;
    public static final Color CHAT_TWITCH_HIGHLIGHTED_DEFAULT = new Color(120, 86, 188);
    public static final Color CHAT_MESSAGE_KEY_HIGHLIGHT_DEFAULT = new Color(255, 40, 40);
    public static final Color CHAT_MESSAGE_GREETING_HIGHLIGHT_DEFAULT = new Color(125, 0, 255);
	
	
	/**
	 * (14, 14, 14)
	 */
	public static Color GUI_BORDER = GUI_BORDER_DEFAULT;

	/**
	 * (40, 40, 40)
	 */
	public static Color GUI_BACKGROUND = GUI_BACKGROUND_DEFAULT;
	
	/**
	 * (80, 80, 80)
	 */
	public static Color GUI_BACKGROUND_LIGHT = GUI_BACKGROUND_LIGHT_DEFAULT;
	
	/**
	 * (25, 25, 25)
	 */
	public static Color GUI_BUTTON_BACKGROUND = GUI_BUTTON_BACKGROUND_DEFAULT;
	
    public static Color CHAT_MESSAGE_KEY_HIGHLIGHT = CHAT_MESSAGE_KEY_HIGHLIGHT_DEFAULT;
    public static Color FONT = Color.WHITE;
    
	public ColorManager(YamlManager setting) {
		settings = setting;
		loadSettings();
	}

	public static void loadSettings() {
		FONT = decode(settings.getString("Colors.GUI.Font", encode(FONT_DEFAULT)));
		GUI_BACKGROUND = decode(settings.getString("Colors.GUI.Background", encode(GUI_BACKGROUND_DEFAULT)));
		GUI_BACKGROUND_LIGHT = decode(settings.getString("Colors.GUI.BackgroundLight", encode(GUI_BACKGROUND_LIGHT_DEFAULT)));
		GUI_BORDER = decode(settings.getString("Colors.GUI.Border", encode(GUI_BORDER_DEFAULT)));
		GUI_BUTTON_BACKGROUND = decode(settings.getString("Colors.GUI.ButtonBackground", encode(GUI_BUTTON_BACKGROUND_DEFAULT)));
		CHAT_MESSAGE_KEY_HIGHLIGHT = decode(settings.getString("Colors.GUI.DefaultKeyHighlight", encode(CHAT_MESSAGE_KEY_HIGHLIGHT_DEFAULT)));
	}
	
	public static Color decode(String hexCode){
		hexCode = hexCode.startsWith("#") ? hexCode : "#"+hexCode;
		try {
			return Color.decode(hexCode);
		} catch (NumberFormatException ex) {
			LoggerFactory.getLogger(ColorManager.class).warn("Can´t decode color with following hexCode --> "+hexCode);
			return Color.WHITE;
		}
	}
	
	public static String encode(Color color) {
		return "#"+String.format("%06x", 0xFFFFFF & color.getRGB());
	}
	
	public static YamlManager getSettingsConfig(){
		return settings;
	}

}
