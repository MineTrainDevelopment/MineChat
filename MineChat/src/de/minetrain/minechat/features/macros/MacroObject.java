package de.minetrain.minechat.features.macros;

import java.util.Random;

import de.minetrain.minechat.data.objectdata.MacroData;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.EmoteManager;

public class MacroObject {
	private static final Random random = new Random();
	private final MacroType macroType;
	private final String title;
	private final String emote_id;
	private final String[] output;
	private final Long databaseId;
	private final int buttonId;
	private final String channelId;
	private int previousRandom = 0;
	
	public MacroObject(MacroType macroType, String emote_id, int button_id, String title, String channelId, String[] output) {
		this.macroType = macroType;
		this.title = title;
		this.output = output;
		this.emote_id = emote_id;
		this.buttonId = button_id;
		this.channelId = channelId;
		this.databaseId = null;
	}
	
	public MacroObject(MacroData macroData) {
		this.macroType = macroData.getMacro_type();
		this.title = macroData.getTitle();
		this.output = macroData.getOutput();
		this.emote_id = macroData.getEmoteId();
		this.databaseId = macroData.getId();
		this.buttonId = macroData.getButton_id();
		this.channelId = macroData.getChannelId();
	}
	
	public MacroType getMacroType() {
		return macroType;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * @return a random output from the {@link MacroObject#getAllOutputs()}
	 */
	public String getRandomOutput() {
		int newRandom = 0;
		while(this.output.length > 1 && (newRandom == previousRandom)){
			newRandom = random.nextInt(this.output.length);
		}
		
		previousRandom = newRandom;
		return output[newRandom];
	}

	public String getTrulyRandomOutput() {
		return output[random.nextInt(output.length)];
	}

	public String getAllOutputsAsString() {
		return String.join(" \n", output);
	}

	public String[] getAllOutputs() {
		return output;
	}

	public String getEmoteId() {
		return emote_id;
	}
	
	public Long getDatabaseId(){
		return databaseId;
	}
	
	public int getButtonId() {
		return buttonId;
	}
	
	public String getChannelId() {
		return channelId;
	}

	
	/**
	 * @return may be null if no emote with the provided id is loaded.
	 */
	public Emote getEmote() {
		if(emote_id == null || emote_id.equalsIgnoreCase("null")){
			return null;
		}
		
		Emote emoteById  = EmoteManager.getEmoteById(emote_id);
		return emoteById;
	}
	
	public MacroObject getThis() {
		return this;
	}
	
}
