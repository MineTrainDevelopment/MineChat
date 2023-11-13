package de.minetrain.minechat.features.messagehighlight;

import java.awt.Color;
import java.util.List;

import com.google.code.regexp.Pattern;

import de.minetrain.minechat.config.Settings;

public class HighlightString {
	private final String word;
	private final String wordColorCode;
	private final String borderColorCode;
	private final boolean playSound;
	
	public HighlightString(String configString) {
		String[] segments = configString.split("%-%");
		this.word = segments[0];;
		this.wordColorCode = segments[1];
		this.borderColorCode = segments[2];
		this.playSound = segments[3].equalsIgnoreCase("true") ? true : false;
	}
	
	/**
	 * 
	 * @param word
	 * @param wordColor
	 * @param borderColor
	 */
	public static String saveNewWord(String word, Color wordColor, Color borderColor) {
		List<String> stringList = Settings.settings.getStringList("Highlights.MessageHighlights.KeyWods.List");
//		word = word.replaceAll("[\\[\\]{}()\\\\^$|?.+*]", "");
		
		if(Pattern.compile("[{}.]").matcher(word).find()){
			return "\"{\", \"}\" and \".\" are invalid chars!";
		}
		
		String removeFromList = "";
		for(String string : stringList){
			if(string.toLowerCase().equalsIgnoreCase(word.toLowerCase().replace("%-%", "%"))){
				removeFromList = string;
			}
		}
		
		stringList.remove(removeFromList);
		
		stringList.add(
				word.replace("%-%", "%")+"%-%"+
				String.format("#%06x", wordColor.getRGB() & 0x00FFFFFF)+"%-%"+
				String.format("#%06x", borderColor.getRGB() & 0x00FFFFFF)+"%-%"+
				"true");
		
		Settings.settings.setStringList("Highlights.MessageHighlights.KeyWods.List", stringList, true);
		Settings.reloadHighlights();
		return null;
	}

	public String getWord() {
		return word;
	}

	public String getWordColorCode() {
		return wordColorCode;
	}

	public String getBorderColorCode() {
		return borderColorCode;
	}

	public boolean isPlaySound() {
		return playSound;
	}
	
}
