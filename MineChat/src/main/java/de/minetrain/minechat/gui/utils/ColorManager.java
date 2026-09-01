package de.minetrain.minechat.gui.utils;

import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.paint.Color;

public class ColorManager {

	private static final Cache<String, Color> adjustedColorCache = Caching.getCachingProvider().getCacheManager()
			.createCache("adjustedColorCache",
					new MutableConfiguration<String, Color>()
						.setStoreByValue(false)
						.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.THIRTY_MINUTES))
						.setCacheLoaderFactory(cacheLoader())
						.setReadThrough(true));

	public static final int FONT_DEFAULT = 0xffffffff;
	public static final int FONT_HYPERTEXT =  0x1000ffff;
	public static final int GUI_BORDER_DEFAULT = 0x000000ff;
	public static final int GUI_BACKGROUND_DEFAULT = 0x282828ff;
	public static final int GUI_BACKGROUND_LIGHT_DEFAULT = 0x505050ff;
	public static final int GUI_BUTTON_BACKGROUND_DEFAULT = 0x1e1e1eff;
	public static final int GUI_ACCENT_DEFAULT = 0x008000ff;

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

	private ColorManager() {
		// Prevent instantiation
	}

	public static Color getAdjustedColor(String color) {
		return adjustedColorCache.get(StringUtils.isBlank(color) ? "ffffffff" : color);
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

	public static void encode(int color, StringBuilder stringBuilder) {
		stringBuilder.ensureCapacity(stringBuilder.length() + 9);
		rgbaToHex(rgbaFromInt(color), stringBuilder);
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

	private static double[] hslFromInt(int colorInt) {
		return hslFromRgb((colorInt >> 24 & 0xFF) / 255.0, (colorInt >> 16 & 0xFF) / 255.0, (colorInt >> 8 & 0xFF) / 255.0);
	}

	private static double[] hslFromRgb(double r, double g, double b) {
		double max = Math.max(r, Math.max(g, b));
		double min = Math.min(r, Math.min(g, b));
		double[] hsl = new double[] {0.0 ,0.0 , (max + min) / 2.0};

		if (max != min) {
			double d = max - min;
			hsl[1] = hsl[2] > 0.5 ? d / (2.0 - max - min) : d / (max + min);

			if (max == r) {
				hsl[0] = (g - b) / d + (g < b ? 6.0 : 0.0);
			} else if (max == g) {
				hsl[0] = (b - r) / d + 2.0;
			} else {
				hsl[0] = (r - g) / d + 4.0;
			}
			hsl[0] /= 6.0;
		}
		return hsl;
	}

	private static double[] rgbFromHsl(double h, double s, double l) {
		double[] rgb = new double[] {l, l, l};
		if (s != 0.0) {
			double q = l < 0.5 ? l * (1.0 + s) : l + s - l * s;
			double p = 2.0 * l - q;
			rgb[0] = hueToRgb(p, q, h + 1.0 / 3.0);
			rgb[1] = hueToRgb(p, q, h);
			rgb[2] = hueToRgb(p, q, h - 1.0 / 3.0);
		}
		return rgb;
	}

	private static double hueToRgb(double p, double q, double t) {
		if (t < 0.0) {
			t += 1.0;
		}
		if (t > 1.0) {
			t -= 1.0;
		}
		if (t < 1.0 / 6.0) {
			return p + (q - p) * 6.0 * t;
		}
		if (t < 1.0 / 2.0) {
			return q;
		}
		if (t < 2.0 / 3.0) {
			return p + (q - p) * (2.0 / 3.0 - t) * 6.0;
		}
		return p;
	}

	private static double adjustShade(double brightness, double percent) {
		double multiplier = (1.0f - Math.clamp(percent, 0.0, 1.0));
		return Math.max(0.0f, brightness * multiplier);
	}

	private static double adjustTone(double brightness, double percent) {
		double multiplier = (1.0f + Math.clamp(percent, 0.0, 1.0));
		return Math.min(1.0f, brightness * multiplier);
	}

	private static Color createColorForBackground(Color color, int backgroundColor) {
		double[] hsl = hslFromRgb(color.getRed(), color.getGreen(), color.getBlue());
		double[] bgHsl = hslFromInt(backgroundColor);

		double initialLuminance = hsl[2];

		double backgroundLuminance = Math.min(0.99, bgHsl[2]);
		double toneAdjustment = Math.abs(backgroundLuminance == 0.5 ? 0.0 : (backgroundLuminance - 0.5));

		if (backgroundLuminance < 0.5) {
			if (hsl[0] <= 260.0 && hsl[0] >= 220.0) {
				hsl[2] = adjustTone(hsl[2], toneAdjustment);
				hsl[0] = 210.0;
			} else if (hsl[2] <= 0.3) {
				hsl[2] = adjustTone(hsl[2], toneAdjustment);
			}
		}

		if (backgroundLuminance > 0.5) {
			hsl[2] = adjustShade(hsl[2], toneAdjustment);
		}

		if (initialLuminance > 0.25 && hsl[2] <= 0.25) {
			hsl[2] = 0.25;
		}

		double[] rgba = rgbFromHsl(hsl[0], hsl[1], hsl[2]);
		return new Color(rgba[0], rgba[1], rgba[2], color.getOpacity());
	}

	private static Factory<CacheLoader<String, Color>> cacheLoader() {
		return new FactoryBuilder.SingletonFactory<>(new CacheLoader<>() {
			@Override
			public Color load(String key) throws CacheLoaderException {
				return createColorForBackground(Color.web(key), GUI_BACKGROUND_DEFAULT);
			}

			@Override
			public Map<String, Color> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
				HashMap<String, Color> map = new HashMap<>();
				for (String string : keys) {
					Color value = load(string);
					if(value != null){
						map.put(string, value);
					}
				}
				return map;
			}
		});
	}
}
