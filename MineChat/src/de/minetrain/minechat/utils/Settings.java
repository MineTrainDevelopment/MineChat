package de.minetrain.minechat.utils;

import java.util.ArrayList;
import java.util.List;

public class Settings {
	public static final String timeFormat = "HH:mm";
	public static final List<String> highlightStrings = new ArrayList<String>();
	public static final boolean displayModActions = true;
	public static final boolean displaySubs_Follows = true;
	public static final boolean displayGiftedSubscriptions = true;
	public static final boolean displayBitsCheerd = true;
	public static final boolean displayAnnouncement = true;
	public static final boolean displayUserRewards = true;
	
	
	public Settings() {
		highlightStrings.add("minetrainlp");
		highlightStrings.add("minetrain");
		highlightStrings.add("mine");
	}
}
