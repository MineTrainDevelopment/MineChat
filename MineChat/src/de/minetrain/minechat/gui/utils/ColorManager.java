package de.minetrain.minechat.gui.utils;

import java.util.HashMap;
import java.util.HexFormat;

import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.YamlManager;
import javafx.scene.paint.Color;

//import javax.swing.JColorChooser;

public class ColorManager {
	/** hexcode, adjustedColor */
	public static final HashMap<String, Color> adjustedColors = new HashMap<>();
	/** hexcode, adjustedHexcode */
	public static final HashMap<String, String> adjustedHexcodes = new HashMap<>();

	public static YamlManager settings;
	private String TODO_Against_settings = "";
	public static final Color FONT_DEFAULT = Color.WHITE;
	public static final Color FONT_HYPERTEXT = decode("#1000FF");
	public static final Color GUI_BORDER_DEFAULT = Color.rgb(14, 14, 1);
	public static final Color GUI_BACKGROUND_DEFAULT = Color.rgb(40, 40, 40);
	public static final Color GUI_BACKGROUND_LIGHT_DEFAULT = Color.rgb(80, 80, 80);
	public static final Color GUI_BUTTON_BACKGROUND_DEFAULT = Color.rgb(30, 30, 30);
	public static final Color GUI_ACCENT_DEFAULT = Color.GREEN;

	public static final int CHAT_UNIMPORTANT_DEFAULT = 0x808080ff;
	public static final int CHAT_MODERATION_DEFAULT = 0x00ffffff;
	public static final int CHAT_SPENDING_SMALL_DEFAULT = 0xb45000ff;
	public static final int CHAT_SPENDING_BIG_DEFAULT = 0xffff00ff;
	public static final int CHAT_ANNOUNCEMENT_DEFAULT = 0x008000ff;
	public static final int CHAT_USER_REWARD_DEFAULT = 0x0000ffff;
	public static final int CHAT_TWITCH_HIGHLIGHTED_DEFAULT = 0x7856bcff;
	public static final int CHAT_MESSAGE_KEY_HIGHLIGHT_DEFAULT = 0xff2828ff;
	public static final int CHAT_MESSAGE_GREETING_HIGHLIGHT_DEFAULT = 0x7d00ffff;

	private static final HexFormat RGBA_HEX_FORMAT = HexFormat.of().withUpperCase();

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

	public static int CHAT_MESSAGE_KEY_HIGHLIGHT = CHAT_MESSAGE_KEY_HIGHLIGHT_DEFAULT;
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
		CHAT_MESSAGE_KEY_HIGHLIGHT = encodeToInt(Color.web(settings.getString("Colors.GUI.DefaultKeyHighlight", encode(CHAT_MESSAGE_KEY_HIGHLIGHT_DEFAULT))));
	}

	public static Color decode(String hexCode) {
		hexCode = hexCode.startsWith("#") ? hexCode : "#" + hexCode;
		try {
			return Color.valueOf(hexCode);
		} catch (NumberFormatException ex) {
			LoggerFactory.getLogger(ColorManager.class).warn("Can´t decode color with following hexCode --> " + hexCode);
			return Color.WHITE;
		}
	}

	public static Color decode(String hexCode, String backgroundAdjustmentHexCode) {
		return adjustedColors.computeIfAbsent(hexCode + backgroundAdjustmentHexCode, key -> {
			return new HSLColor(hexCode).adjustForBackground(new HSLColor(backgroundAdjustmentHexCode)).getColor();
		});
	}

	public static String adjustHexcode(String hexCode, String backgroundAdjustmentHexCode) {
		return adjustedHexcodes.computeIfAbsent(hexCode + backgroundAdjustmentHexCode, key -> {
			return new HSLColor(hexCode).adjustForBackground(new HSLColor(backgroundAdjustmentHexCode)).getHex();
		});
	}

	public static int encodeToInt(Color color) {
		int r = (int) (color.getRed() * 255);
		int g = (int) (color.getGreen() * 255);
		int b = (int) (color.getBlue() * 255);
		int a = (int) (color.getOpacity() * 255);
		return (r << 24) | (g << 16) | (b << 8) | a;
	}

	public static Color decodeFromInt(int colorInt) {
		int r = (colorInt >> 24) & 0xFF;
		int g = (colorInt >> 16) & 0xFF;
		int b = (colorInt >> 8) & 0xFF;
		int a = colorInt & 0xFF;
		return Color.rgb(r, g, b, a / 255.0);
	}

	public static String encode(Color color) {
		StringBuilder sb = new StringBuilder(9);
		appendEncode(color, sb);
		return sb.toString();
	}

	public static String encode(Color color, String prefix, String suffix) {
		StringBuilder sb = new StringBuilder(9 + prefix.length() + suffix.length())
			.append(prefix);
		appendEncode(color, sb);
		return sb.append(suffix).toString();
	}

	public static String encode(int color) {
		StringBuilder sb = new StringBuilder(9);
		rgbaToHex(rgbaFromInt(color), sb);
		return sb.toString();
	}

	public static String encode(int color, String prefix, String suffix) {
		StringBuilder sb = new StringBuilder(9 + prefix.length() + suffix.length())
			.append(prefix);
		rgbaToHex(rgbaFromInt(color), sb);
		return sb.append(suffix).toString();
	}

	public static void encode(Color color, StringBuilder stringBuilder) {
		stringBuilder.ensureCapacity(stringBuilder.length() + 9);
		appendEncode(color, stringBuilder);
	}

	private static void appendEncode(Color color, StringBuilder stringBuilder) {
		rgbaToHex(new byte[] { (byte) (color.getRed() * 255), (byte) (color.getGreen() * 255), (byte) (color.getBlue() * 255), (byte) (color.getOpacity() * 255) }, stringBuilder);
	}

	private static void rgbaToHex(byte[] rgba, StringBuilder stringBuilder) {
		stringBuilder.append('#');
		RGBA_HEX_FORMAT.formatHex(stringBuilder, rgba);
	}

	private static byte[] rgbaFromInt(int colorInt) {
		byte r = (byte) ((colorInt >> 24) & 0xFF);
		byte g = (byte) ((colorInt >> 16) & 0xFF);
		byte b = (byte) ((colorInt >> 8) & 0xFF);
		byte a = (byte) (colorInt & 0xFF);
		return new byte[] { r, g, b, a };
	}

	public static YamlManager getSettingsConfig() {
		return settings;
	}
}
