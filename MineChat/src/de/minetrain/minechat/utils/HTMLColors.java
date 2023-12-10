package de.minetrain.minechat.utils;

import java.util.Random;

/**
 * 
 */
public enum HTMLColors {
    AQUA("#00FFFF"),
    BLACK("#000000"),
    BLUE("#0000FF"),
    FUCHSIA("#FF00FF"),
    GRAY("#808080"),
    GREEN("#008000"),
    LIME("#00FF00"),
    MAROON("#800000"),
    NAVY("#000080"),
    OLIVE("#808000"),
    PURPLE("#800080"),
    RED("#FF0000"),
    SILVER("#C0C0C0"),
    TEAL("#008080"),
    WHITE("#FFFFFF"),
    CYAN("#1a9fff"),
    YELLOW("#FFFF00");

	private static final Random random = new Random();
    private final String code;

    HTMLColors(String code) {
        this.code = code;
    }

    public String getColorCode() {
        return code;
    }

    public static HTMLColors getRandomColor() {
        HTMLColors[] values = HTMLColors.values();
        return values[random.nextInt(values.length)];
    }
    
    @Override
    public String toString() {
    	return getColorCode();
    }
}