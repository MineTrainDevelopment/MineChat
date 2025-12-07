package de.minetrain.minechat.twitch.obj;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import de.minetrain.minechat.gui.utils.ColorManager;
import javafx.scene.paint.Color;

public class UserColorCache {
	private final ConcurrentHashMap<String, String> hexCache = new ConcurrentHashMap<String, String>();//userName, color code
	private static final String DEFAULT_COLOR = "#ffffff";
	
	public void putUser(String userName, Optional<String> colorCode) {
		String colorHex = colorCode.orElse(DEFAULT_COLOR);
		String cachedColor = hexCache.get(userName.toLowerCase());
		if(cachedColor != null && cachedColor.equals(colorHex)){
			return;
		}
		
		if(userName.toLowerCase().equals("sebbbelr")){
//			System.err.println(userName + " - "+ colorHex);
		}
		
		Color color = adjustBrightness(colorCode);
		hexCache.put(userName.toLowerCase(), ColorManager.encode(color));
		System.err.println(color.getHue() + " - " + color.getBrightness());
	}

	public static Color adjustBrightness(Optional<String> colorCode) {
		Color color = Color.web(colorCode.orElse(DEFAULT_COLOR));
		
		//getBrightness min 60%
		//215 - 275 = min 90
		//275 - 290 = min 80

		long hue = Math.round(color.getHue());
		System.err.println(hue + " - " + color.getHue() + " - " + color.getBrightness());
		
		if(hue < 215 || hue > 290){
			return Color.hsb(color.getHue(), color.getSaturation(), Math.max(color.getBrightness(), 0.6));
		}

		if(hue >= 275 && hue <= 290){
			return Color.hsb(color.getHue(), color.getSaturation(), Math.max(color.getBrightness(), 0.8));
		}
		
		return Color.hsb(Math.abs(hue - 215) < Math.abs(hue - 275) ? 215: 275, color.getSaturation(), Math.max(color.getBrightness(), 0.9));
	}

	public String getColorCode(String userName){
		return getColorCodeOptional(userName).orElse(DEFAULT_COLOR);
	}
	
	public Optional<String> getColorCodeOptional(String userName){
		return Optional.ofNullable(hexCache.get(userName.toLowerCase()));
	}
	
	
	
	

}
