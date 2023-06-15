package de.minetrain.minechat.config.obj;

import de.minetrain.minechat.gui.obj.buttons.ButtonType;

public class MacroObject {
	private final ButtonType buttonType;
	private final String buttonName;
	private final String macroOutput;
	private final String emotePath;
	
	public MacroObject(ButtonType buttonType, String emotePath, String buttonName, String macroOutput) {
		this.buttonType = buttonType;
		this.buttonName = buttonName;
		this.macroOutput = macroOutput;
		this.emotePath = emotePath;
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

	public String getEmotePath() {
		return emotePath;
	}
	
}
