package de.minetrain.minechat.features.messagehighlight;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.code.regexp.Pattern;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.DatabaseManager;

public class HighlightString {
	private final String uuid;
	private final String word;
	private final String wordColorCode;
	private final String borderColorCode;
	private final boolean playSound;
	private boolean state;
	
	public HighlightString(ResultSet result) throws SQLException {
		this.uuid = result.getString("uuid");
		this.word = result.getString("word");
		this.wordColorCode = result.getString("word_color");
		this.borderColorCode = result.getString("border_color");
		this.playSound = (result.getString("sound") != null);
		this.state = result.getBoolean("state");
	}
	
	/**
	 * 
	 * @param word
	 * @param wordColor
	 * @param borderColor
	 */
	public static String saveNewWord(String uuid, String word, Color wordColor, Color borderColor) {
		if(Pattern.compile("[{}.]").matcher(word).find()){
			return "\"{\", \"}\" and \".\" are invalid chars!";
		}
		
		DatabaseManager.getMessageHighlight().insert(
				uuid,
				word,
				String.format("#%06x", wordColor.getRGB() & 0x00FFFFFF),
				String.format("#%06x", borderColor.getRGB() & 0x00FFFFFF),
				null,
				true);
		
		DatabaseManager.commit();
		Settings.reloadHighlights();
		return null;
	}

	public String getUuid() {
		return uuid;
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

	public boolean isAktiv() {
		return state;
	}

	public void setAktiv(boolean state) {
		this.state = state;
		DatabaseManager.getMessageHighlight().setState(uuid, state);
	}
	
	public void delete(){
		DatabaseManager.getMessageHighlight().remove(uuid);
		Settings.reloadHighlights();
	}
	
}
