package de.minetrain.minechat.util;

import de.minetrain.minechat.gui.objects.ButtonType;

public class MacroObject {
	private final ButtonType buttonType;
	private final String buttonName;
	private final String macroOutput;
	
	public MacroObject(ButtonType buttonType, String buttonName, String macroOutput) {
		this.buttonType = buttonType;
		this.buttonName = buttonName;
		this.macroOutput = macroOutput;
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
	
}
