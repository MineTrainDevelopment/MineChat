package de.minetrain.minechat.utils;

import java.util.ArrayList;
import java.util.List;

public class Settings {
	public static final String timeFormat = "HH:mm";
	public static final List<String> highlightStrings = new ArrayList<String>();
	
	public Settings() {
		highlightStrings.add("minetrainlp");
		highlightStrings.add("minetrain");
		highlightStrings.add("mine");
	}
}
