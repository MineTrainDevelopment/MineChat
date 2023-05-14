package de.minetrain.minechat.config.obj;

import de.minetrain.minechat.gui.obj.buttons.ButtonType;

public class MacroObject {
	private final ButtonType buttonType;
	private final String buttonName;
	private final String macroOutput;
	private final TwitchEmote twitchEmote;
	
	public MacroObject(ButtonType buttonType, String buttonName, String macroOutput) {
		this.buttonType = buttonType;
		this.buttonName = buttonName;
		this.macroOutput = macroOutput;
		this.twitchEmote = null;
	}
	
	public MacroObject(ButtonType buttonType, TwitchEmote emote, String macroOutput) {
		this.buttonType = buttonType;
		this.buttonName = "";
		this.macroOutput = macroOutput;
		this.twitchEmote = emote;
	}

	public ButtonType getButtonType() {
		return buttonType;
	}

	public String getButtonName() {
		return buttonName;
	}

	public String getMacroOutput() {
		return macroOutput;
	}

	public TwitchEmote getTwitchEmote() {
		return twitchEmote;
	}
	
}
