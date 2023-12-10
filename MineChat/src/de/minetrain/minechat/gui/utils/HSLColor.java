package de.minetrain.minechat.gui.utils;

import java.awt.Color;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NOTE: I stole this code from <a href="https://github.com/jeffjianzhao/DAViewer/blob/master/src/daviewer/HSLColor.java">GitHub</a> and modified it a bit to my needs.
 * <p>
 * The HSLColor class provides methods to manipulate HSL (Hue, Saturation
 * Luminance) values to create a corresponding Color object using the RGB
 * ColorSpace.
 *
 * The HUE is the color, the Saturation is the purity of the color (with respect
 * to grey) and Luminance is the brightness of the color (with respect to black
 * and white)
 *
 * The Hue is specified as an angel between 0 - 360 degrees where red is 0,
 * green is 120 and blue is 240. In between you have the colors of the rainbow.
 * Saturation is specified as a percentage between 0 - 100 where 100 is fully
 * saturated and 0 approaches gray. Luminance is specified as a percentage
 * between 0 - 100 where 0 is black and 100 is white.
 *
 * In particular the HSL color space makes it easier change the Tone or Shade of
 * a color by adjusting the luminance value.
 */
public class HSLColor {
	private float[] hsl;
	private float alpha = 255.0f;

	/**
	 * Create a HSLColor object using an hex code
	 *
	 * @param rgb the RGB Color object
	 */
	public HSLColor(String hexCode) {
		float[] rgb = ColorManager.hexToRGB(hexCode);
		hsl = fromRGB(rgb[0], rgb[1], rgb[2]);
	}

	/**
	 * Create a HSLColor object using an RGB Color object.
	 *
	 * @param rgb the RGB Color object
	 */
	public HSLColor(Color rgb) {
		hsl = fromColor(rgb);
		alpha = rgb.getAlpha() / 255.0f;
	}

	/**
	 * Create a HSLColor object using an an array containing the individual HSL
	 * values and with a default alpha value of 1.
	 *
	 * @param hsl array containing HSL values
	 */
	public HSLColor(float[] hsl) {
		this(hsl, 1.0f);
	}

	/**
	 * Create a HSLColor object using an an array containing the individual HSL
	 * values.
	 *
	 * @param hsl   array containing HSL values
	 * @param alpha the alpha value between 0 - 1
	 */
	public HSLColor(float[] hsl, float alpha) {
		this(hsl[0], hsl[1], hsl[2]);
		this.alpha = alpha;
	}

	/**
	 * Create a HSLColor object using individual HSL values and a default alpha
	 * value of 1.0.
	 *
	 * @param h is the Hue value in degrees between 0 - 360
	 * @param s is the Saturation percentage between 0 - 100
	 * @param l is the Lumanance percentage between 0 - 100
	 */
	public HSLColor(float h, float s, float l) {
		this(h, s, l, 1.0f);
	}

	/**
	 * Create a HSLColor object using individual HSL values.
	 *
	 * @param h     the Hue value in degrees between 0 - 360
	 * @param s     the Saturation percentage between 0 - 100
	 * @param l     the Lumanance percentage between 0 - 100
	 * @param alpha the alpha value between 0 - 1
	 */
	public HSLColor(float h, float s, float l, float alpha) {
		hsl = new float[] {checkBounds(h, 0, 360), checkBounds(s, 0, 100), checkBounds(l, 0, 100)};
		this.alpha = alpha;
	}

	/**
	 * Create a RGB Color object based on this HSLColor with a different Hue value.
	 * The degrees specified is an absolute value.
	 *
	 * @param degrees - the Hue value between 0 - 360
	 * @return itself for method chaining.
	 */
	public HSLColor setHue(float hue) {
		hsl = new float[] {checkBounds(hue, 0, 360), hsl[1], hsl[2]};
		return this;
	}
	
	/**
	 * Create a RGB Color object based on this HSLColor with a different Saturation
	 * value. The percent specified is an absolute value.
	 *
	 * @param percent - the Saturation value between 0 - 100
	 * @return itself for method chaining.
	 */
	public HSLColor setSaturation(float saturation) {
		hsl = new float[] {hsl[0], checkBounds(saturation, 0, 100), hsl[2]};
		return this;
	}
	
	/**
	 * Create a RGB Color object based on this HSLColor with a different Luminance
	 * value. The percent specified is an absolute value.
	 *
	 * @param percent - the Luminance value between 0 - 100
	 * @return itself for method chaining.
	 */
	public HSLColor setLuminance(float luminance) {
		hsl = new float[] {hsl[0], hsl[1], checkBounds(luminance, 0, 100)};
		return this;
	}

	/**
	 * Changing the shade will return a darker color. The percent specified is a
	 * relative value.
	 *
	 * @param percent - the value between 0 - 100
	 * @return itself for method chaining.
	 */
	public HSLColor adjustShade(float percent) {
		float multiplier = (100.0f - checkBounds(percent, 0, 100)) / 100.0f;
		float luminance = Math.max(0.0f, hsl[2] * multiplier);

		setLuminance(luminance);
		return this;
	}

	/**
	 * Changing the tone will return a lighter color. The percent specified is a
	 * relative value.
	 *
	 * @param percent - the value between 0 - 100
	 * @return itself for method chaining.
	 */
	public HSLColor adjustTone(float percent) {
		float multiplier = (100.0f + checkBounds(percent, 0, 100)) / 100.0f;
		float luminance = Math.min(100.0f, hsl[2] * multiplier);

		setLuminance(luminance);
		return this;
	}
	
	public HSLColor adjustForBackground(HSLColor background){
        // This is used to stop colors from getting darker as they should, as long as its not intended.
        boolean isOver25 = getLuminance() >= 25;
        
        float backgroundLuminance = background.getLuminance();
        backgroundLuminance = backgroundLuminance == 100 ? backgroundLuminance = 99 : backgroundLuminance;
		float toneAjustmint = Math.abs(backgroundLuminance == 50 ? 0 : backgroundLuminance-50);

        if(backgroundLuminance < 50){
        	
        	//If the color is in the deep blue range, and the background is on the darker side, take out our increase contrast. 
        	//Disabled the contrast increasing due to a to high color difference and unreadability.
        	if(getHue() <= 260 && getHue() >= 220){
//        		if(getHue() - 20 >= 220){
//        			setHue(260);
//        		}else{
        			adjustTone(toneAjustmint);
        			setHue(210);
//        		}
        	}else if(getLuminance() <= 30){
            	adjustTone(toneAjustmint);
        	}
        }

        System.err.println(backgroundLuminance);
        if(backgroundLuminance > 50){
        	adjustShade(toneAjustmint);
        }
        
        if(isOver25 && getLuminance() <= 25){
    		setLuminance(25);
    	}
        
        System.err.println(getHue()+" - "+getSaturation()+" - "+getLuminance());
    	return this;
    }

	/**
	 * Get the Alpha value.
	 *
	 * @return the Alpha value.
	 */
	public float getAlpha() {
		return alpha;
	}

	/**
	 * Create a RGB Color object that is the complementary color of this HSLColor.
	 * This is a convenience method. The complementary color is determined by adding
	 * 180 degrees to the Hue value.
	 * 
	 * @return the RGB Color object
	 */
	public Color getComplementary() {
		float hue = (hsl[0] + 180.0f) % 360.0f;
		return toRGB(hue, hsl[1], hsl[2]);
	}

	/**
	 * Get the Hue value.
	 *
	 * @return the Hue value.
	 */
	public float getHue() {
		return hsl[0];
	}

	/**
	 * Get the Saturation value.
	 *
	 * @return the Saturation value.
	 */
	public float getSaturation() {
		return hsl[1];
	}

	/**
	 * Get the Luminance value.
	 *
	 * @return the Luminance value.
	 */
	public float getLuminance() {
		return hsl[2];
	}

	/**
	 * Get the HSL values.
	 *
	 * @return the HSL values.
	 */
	public float[] getHSL() {
		return hsl;
	}

	/**
	 * Get the RGB Color object represented by this HDLColor.
	 *
	 * @return the RGB Color object.
	 */
	public Color getColor() {
		return toRGB(hsl);
	}

	/**
	 * Get the hex color code represented by this HDLColor.
	 */
	public String getHex() {
		return toHex(hsl[0], hsl[1], hsl[2]);
	}

	public String toString() {
		return "HSLColor[h=" + hsl[0] + ",s=" + hsl[1] + ",l=" + hsl[2] + ",alpha=" + alpha + "]";
	}

	/**
	 * Convert a RGB Color to it corresponding HSL values.
	 *
	 * @return an array containing the 3 HSL values.
	 */
	public static float[] fromColor(Color color) {
		// Get RGB values in the range 0 - 1
		float[] rgb = color.getRGBColorComponents(null);
		float r = rgb[0];
		float g = rgb[1];
		float b = rgb[2];
		
		return fromRGB(r, g, b);
	}

	/**
	 * Convert a hexcode to it corresponding HSL values.
	 *
	 * @return an array containing the 3 HSL values.
	 */
	public static float[] fromHex(String hexCode) {
		float[] rgb = ColorManager.hexToRGB(hexCode);
		return fromRGB(rgb[0], rgb[1], rgb[2]);
	}
	

	/**
	 * Convert a RGB Color to it corresponding HSL values.
	 *
	 * @return an array containing the 3 HSL values.
	 */
	public static float[] fromRGB(float r, float g, float b) {
		// Minimum and Maximum RGB values are used in the HSL calculations
		float min = Math.min(r, Math.min(g, b));
		float max = Math.max(r, Math.max(g, b));

		// Calculate the Hue
		float h = 0;

		if (max == min)
			h = 0;
		else if (max == r)
			h = ((60 * (g - b) / (max - min)) + 360) % 360;
		else if (max == g)
			h = (60 * (b - r) / (max - min)) + 120;
		else if (max == b)
			h = (60 * (r - g) / (max - min)) + 240;

		// Calculate the Luminance

		float l = (max + min) / 2;
		// System.out.println(max + " : " + min + " : " + l);

		// Calculate the Saturation
		float s = 0;

		if (max == min)
			s = 0;
		else if (l <= .5f)
			s = (max - min) / (max + min);
		else
			s = (max - min) / (2 - max - min);
		return new float[] { h, s * 100, l * 100 };
	}

	/**
	 * Convert HSL values to a RGB Color with a default alpha value of 1. H (Hue) is
	 * specified as degrees in the range 0 - 360. S (Saturation) is specified as a
	 * percentage in the range 1 - 100. L (Lumanance) is specified as a percentage
	 * in the range 1 - 100.
	 *
	 * @param hsl an array containing the 3 HSL values
	 *
	 * @returns the RGB Color object
	 */
	public static Color toRGB(float[] hsl) {
		return toRGB(hsl, 1.0f);
	}

	/**
	 * Convert HSL values to a RGB Color. H (Hue) is specified as degrees in the
	 * range 0 - 360. S (Saturation) is specified as a percentage in the range 1 -
	 * 100. L (Lumanance) is specified as a percentage in the range 1 - 100.
	 *
	 * @param hsl   an array containing the 3 HSL values
	 * @param alpha the alpha value between 0 - 1
	 *
	 * @returns the RGB Color object
	 */
	public static Color toRGB(float[] hsl, float alpha) {
		return toRGB(hsl[0], hsl[1], hsl[2], alpha);
	}

	/**
	 * Convert HSL values to a RGB Color with a default alpha value of 1.
	 *
	 * @param h Hue is specified as degrees in the range 0 - 360.
	 * @param s Saturation is specified as a percentage in the range 1 - 100.
	 * @param l Lumanance is specified as a percentage in the range 1 - 100.
	 *
	 * @returns the RGB Color object
	 */
	public static Color toRGB(float h, float s, float l) {
		return toRGB(h, s, l, 1.0f);
	}

	/**
	 * Convert HSL values to a RGB Color.
	 *
	 * @param h     Hue is specified as degrees in the range 0 - 360.
	 * @param s     Saturation is specified as a percentage in the range 1 - 100.
	 * @param l     Lumanance is specified as a percentage in the range 1 - 100.
	 * @param alpha the alpha value between 0 - 1
	 *
	 * @returns the RGB Color object
	 */
	public static Color toRGB(float h, float s, float l, float alpha) {
		if (s < 0.0f || s > 100.0f) {
			String message = "Color parameter outside of expected range - Saturation["+s+"]";
			throw new IllegalArgumentException(message);
		}

		if (l < 0.0f || l > 100.0f) {
			String message = "Color parameter outside of expected range - Luminance";
			throw new IllegalArgumentException(message);
		}

		if (alpha < 0.0f || alpha > 1.0f) {
			String message = "Color parameter outside of expected range - Alpha";
			throw new IllegalArgumentException(message);
		}

		// Formula needs all values between 0 - 1.

		h = h % 360.0f;
		h /= 360f;
		s /= 100f;
		l /= 100f;

		float q = 0;

		if (l < 0.5)
			q = l * (1 + s);
		else
			q = (l + s) - (s * l);

		float p = 2 * l - q;

		float r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
		float g = Math.max(0, HueToRGB(p, q, h));
		float b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));

		r = Math.min(r, 1.0f);
		g = Math.min(g, 1.0f);
		b = Math.min(b, 1.0f);

		return new Color(r, g, b, alpha);
	}

	private static float HueToRGB(float p, float q, float h) {
		if (h < 0)
			h += 1;

		if (h > 1)
			h -= 1;

		if (6 * h < 1) {
			return p + ((q - p) * 6 * h);
		}

		if (2 * h < 1) {
			return q;
		}

		if (3 * h < 2) {
			return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
		}

		return p;
	}
	
	/**
	 * Convert HSL to hexcode.
	 * @param hue
	 * @param saturation
	 * @param luminance
	 * @return
	 */
	public static String toHex(double hue, double saturation, double luminance) {
		hue /= 360.0;
		saturation /= 100.0;
		luminance /= 100.0;

	    double c = (1 - Math.abs(2 * luminance - 1)) * saturation;
	    double x = c * (1 - Math.abs((hue * 6) % 2 - 1));
	    double m = luminance - c / 2;

	    double r, g, b;
	    if (hue < 1.0 / 6) {
	        r = c;
	        g = x;
	        b = 0;
	    } else if (hue < 2.0 / 6) {
	        r = x;
	        g = c;
	        b = 0;
	    } else if (hue < 3.0 / 6) {
	        r = 0;
	        g = c;
	        b = x;
	    } else if (hue < 4.0 / 6) {
	        r = 0;
	        g = x;
	        b = c;
	    } else if (hue < 5.0 / 6) {
	        r = x;
	        g = 0;
	        b = c;
	    } else {
	        r = c;
	        g = 0;
	        b = x;
	    }

	    int red = (int) ((r + m) * 255);
	    int green = (int) ((g + m) * 255);
	    int blue = (int) ((b + m) * 255);

	    return String.format("#%02X%02X%02X", red, green, blue);
	}
	

	/**
	 * Check if a userinput is within required bounds.
	 * @param value
	 * @param minValue
	 * @param maxValue
	 * @return
	 */
	private float checkBounds(float value, int minValue, int maxValue){
		return value > maxValue ? maxValue : value < minValue ? minValue : value;
	}
}