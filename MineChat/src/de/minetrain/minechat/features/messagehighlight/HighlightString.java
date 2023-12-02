package de.minetrain.minechat.features.messagehighlight;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.code.regexp.Pattern;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.utils.audio.AudioManager;
import de.minetrain.minechat.utils.audio.AudioVolume;

public class HighlightString {
	private final String uuid;
	private final String word;
	private final String wordColorCode;
	private final String borderColorCode;
	private final String soundPath;
	private final AudioVolume soundVolume;
	private boolean state;
	
	public HighlightString(ResultSet result) throws SQLException {
		this.uuid = result.getString("uuid");
		this.word = result.getString("word");
		this.wordColorCode = result.getString("word_color");
		this.borderColorCode = result.getString("border_color");
		this.soundPath = result.getString("sound");
		this.soundVolume = AudioVolume.get(result.getString("sound_volume"));
		this.state = result.getBoolean("state");
	}
	
	public void playSound(){
		if(isPlaySound()){
			Main.getAudioManager().playAudioClip(getSoundUri(), soundVolume);
		}
	}
	
	public AudioVolume getSoundVolume(){
		return soundVolume;
	}
	
	public String getSoundPath(){
		return AudioManager.RAW_AUDIO_PATH.replace("/", "\\")+soundPath;
	}
	
	public String getSoundUri(){
		return AudioManager.createUri(soundPath);
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
		return soundPath != null;
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
