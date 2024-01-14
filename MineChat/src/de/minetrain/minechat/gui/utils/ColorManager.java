package de.minetrain.minechat.gui.utils;

import java.util.HashMap;

import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.YamlManager;
import javafx.scene.paint.Color;

//import javax.swing.JColorChooser;

public class ColorManager {
	/**hexcode, adjustedColor*/
	public static final HashMap<String, Color> adjustedColors = new HashMap<String, Color>();
	/**hexcode, adjustedHexcode*/
	public static final HashMap<String, String> adjustedHexcodes = new HashMap<String, String>();
	
	public static YamlManager settings;
	private String TODO_Against_settings = "";
	public static final Color FONT_DEFAULT = Color.WHITE;
	public static final Color FONT_HYPERTEXT = decode("#1000FF");
	public static final Color GUI_BORDER_DEFAULT = colorOf(14, 14, 1);
	public static final Color GUI_BACKGROUND_DEFAULT = colorOf(40, 40, 40);
	public static final Color GUI_BACKGROUND_LIGHT_DEFAULT = colorOf(80, 80, 80);
	public static final Color GUI_BUTTON_BACKGROUND_DEFAULT = colorOf(30, 30, 30);
	public static final Color GUI_ACCENT_DEFAULT = Color.GREEN;

	public static final Color CHAT_UNIMPORTANT_DEFAULT = Color.GRAY;
    public static final Color CHAT_MODERATION_DEFAULT = Color.CYAN;
    public static final Color CHAT_SPENDING_SMALL_DEFAULT = colorOf(180, 80, 0);
    public static final Color CHAT_SPENDING_BIG_DEFAULT = Color.YELLOW;
    public static final Color CHAT_ANNOUNCEMENT_DEFAULT = Color.GREEN;
    public static final Color CHAT_USER_REWARD_DEFAULT = Color.BLUE;
    public static final Color CHAT_TWITCH_HIGHLIGHTED_DEFAULT = colorOf(120, 86, 188);
    public static final Color CHAT_MESSAGE_KEY_HIGHLIGHT_DEFAULT = colorOf(255, 40, 40);
    public static final Color CHAT_MESSAGE_GREETING_HIGHLIGHT_DEFAULT = colorOf(125, 0, 255);
    
    /**
     * 
     * @param red
     * @param green
     * @param blue
     * @return
     */
    private static final Color colorOf(int red, int green, int blue){
    	return new Color(red/255d, green/255d, blue/255d, 1);
    }
	
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
			return Color.valueOf(hexCode);
		} catch (NumberFormatException ex) {
			LoggerFactory.getLogger(ColorManager.class).warn("Can´t decode color with following hexCode --> "+hexCode);
			return Color.WHITE;
		}
	}
	
	public static Color decode(String hexCode, String backgroundAdjustmentHexCode){
		return adjustedColors.computeIfAbsent(hexCode+backgroundAdjustmentHexCode, key -> {
			return new HSLColor(hexCode).adjustForBackground(new HSLColor(backgroundAdjustmentHexCode)).getColor();
		});
	}
	
	public static String adjustHexcode(String hexCode, String backgroundAdjustmentHexCode){
		return adjustedHexcodes.computeIfAbsent(hexCode+backgroundAdjustmentHexCode, key -> {
			return new HSLColor(hexCode).adjustForBackground(new HSLColor(backgroundAdjustmentHexCode)).getHex();
		});
	}
	
	public static String encode(Color color) {
        return String.format("#%02X%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255),
            (int) (color.getOpacity() * 255));
    }
	
	public static YamlManager getSettingsConfig(){
		return settings;
	}
	
	public static double[] hexToRGB(String hexCode){
		hexCode = hexCode.replace("#", "");
		int red = Integer.valueOf(hexCode.substring(0, 2), 16);
	    int green = Integer.valueOf(hexCode.substring(2, 4), 16);
	    int blue = Integer.valueOf(hexCode.substring(4, 6), 16);
	    return new double[]{red/255d, green/255d, blue/255d};
	}
	
}
