package de.minetrain.minechat.gui.obj.chat.userinput.textarea;

import javax.swing.ImageIcon;

public class SuggestionObj {
	private String text;
	private String displayText;
	private final String iconPath;
	
	public SuggestionObj(String text, String iconPath, String... displayText) {
		this.text = text;
		this.displayText = displayText.length!=0 ? displayText[0] : "";
		this.iconPath = iconPath;
	}

	public SuggestionObj setDisplayText(String displayText) {
		this.displayText = this.displayText.isEmpty() ? displayText : this.displayText;
		return this;
	}
	
	public SuggestionObj setText(String text) {
		this.text = text;
		return this;
	}

	public String getText() {
		return text;
	}

	public String getDisplayText() {
		return displayText.isEmpty() ? text : displayText;
	}

	public String getIconPath() {
		return iconPath;
	}

	public ImageIcon getIcon() {
		return new ImageIcon(iconPath);
	}
	
}
