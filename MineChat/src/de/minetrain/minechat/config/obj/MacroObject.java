package de.minetrain.minechat.config.obj;

import java.util.Random;

import de.minetrain.minechat.data.objectdata.MacroData;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;

public class MacroObject {
	private static final Random random = new Random();
	private static final Emote dummyEmote = new Emote(false);
	private final ButtonType buttonType;
	private final String title;
	private final String emote_id;
	private final String[] output;
	private final Long macroId;
	private int previousRandom = 0;
	
	public MacroObject(ButtonType buttonType, String emote_id, String title, String[] output) {
		this.buttonType = buttonType;
		this.title = title;
		this.output = output;
		this.emote_id = emote_id;
		this.macroId = null;
	}
	
	public MacroObject(MacroData macroData) {
		this.buttonType = macroData.getButton_type();
		this.title = macroData.getTitle();
		this.output = macroData.getOutput();
		this.emote_id = macroData.getEmoteId();
		this.macroId = macroData.getId();
	}
	
	public ButtonType getButtonType() {
		return buttonType;
	}

	public String getTitle() {
		return title;
	}

	public String getOutput() {
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

	public String getRawOutput() {
		return String.join(" \n ", output);
	}

	public String[] getOutputArray() {
		return output;
	}

	public String getEmoteId() {
		return emote_id;
	}
	
	public Long getMacroId(){
		return macroId;
	}

	public Emote getEmote() {
		if(emote_id == null || emote_id.equalsIgnoreCase("null")){
			return dummyEmote;
		}
		
		return EmoteManager.getEmoteById(emote_id);
	}
	
}
