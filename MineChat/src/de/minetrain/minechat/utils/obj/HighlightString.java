package de.minetrain.minechat.utils.obj;

import java.awt.Color;
import java.util.List;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.main.Main;

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
	public static void saveNewWord(String word, Color wordColor, Color borderColor) {
		List<String> stringList = Main.CONFIG.getStringList("Highlights");
		word = word.replaceAll("[\\[\\]{}()\\\\^$|?.+*]", "");
		String removeFromList = "";
		for(String string : stringList){
			if(string.toLowerCase().startsWith(word.toLowerCase().replace("%-%", "%"))){
				removeFromList = string;
			}
		}
		
		stringList.remove(removeFromList);
		
		stringList.add(
				word.replace("%-%", "%")+"%-%"+
				String.format("#%06x", wordColor.getRGB() & 0x00FFFFFF)+"%-%"+
				String.format("#%06x", borderColor.getRGB() & 0x00FFFFFF)+"%-%"+
				"true");
		
		Main.CONFIG.setStringList("Highlights", stringList, true);
		Settings.reloadHighlights();
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
