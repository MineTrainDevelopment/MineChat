package de.minetrain.minechat.gui.obj.chat.userinput.textarea;

import javax.swing.ImageIcon;

import de.minetrain.minechat.gui.emotes.BackgroundImageIcon;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.Emote.EmoteType;
import de.minetrain.minechat.gui.utils.TextureManager;

public class SuggestionObj {
	private String text;
	private String displayText;
	private final String iconPath;
	private final ImageIcon borderImage;
	
	public SuggestionObj(String text, String iconPath, String... displayText) {
		this.text = text;
		this.displayText = displayText.length!=0 ? displayText[0] : "";
		this.iconPath = iconPath;
		this.borderImage = new ImageIcon(TextureManager.texturePath+"emoteBorder.png");
	}
	
	public SuggestionObj(Emote emote) {
		this.text = ":" + emote.getName() + (emote.getEmoteType().equals(EmoteType.BTTV) ? " -- BTTV" : "");
		this.displayText = "";
		this.iconPath = emote.getFilePath();
		this.borderImage = emote.getBorderImage();
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
		if(iconPath == null){return null;}
		return new BackgroundImageIcon(iconPath, borderImage);
	}
	
}
