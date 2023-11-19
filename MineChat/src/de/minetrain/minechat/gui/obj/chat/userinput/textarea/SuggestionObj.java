package de.minetrain.minechat.gui.obj.chat.userinput.textarea;

import javax.swing.ImageIcon;

import de.minetrain.minechat.gui.emotes.BackgroundImageIcon;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.Emote.EmoteType;
import de.minetrain.minechat.gui.utils.TextureManager;

public class SuggestionObj {
	private static final ImageIcon defaultBorderImage = new ImageIcon(TextureManager.texturePath+"emoteBorder.png");
	private String text;
	private String displayText;
	private final String iconPath;
	private final ImageIcon borderImage;
	private final Emote emote;
	private boolean sortPriority = false;
	
	public SuggestionObj(String text, String iconPath, String... displayText) {
		this.text = text;
		this.displayText = displayText.length!=0 ? displayText[0] : "";
		this.iconPath = iconPath;
		this.borderImage = defaultBorderImage;
		this.emote = null;
	}
	
	public SuggestionObj(Emote emote) {
		this.text = ":" + emote.getName() + (emote.getEmoteType().equals(EmoteType.BTTV) ? " -- BTTV" : "");
		this.emote = emote;
		this.displayText = "";
		this.iconPath = emote.getFilePath();
		this.borderImage = emote.getBorderImage();
		setSortPriority(emote.isFavorite());;
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
	
	public void setSortPriority(boolean sortPoririty){
		this.sortPriority = sortPoririty;
	}
	
	public boolean getSortPriority(){
		return (emote != null) ? !emote.isFavorite() : !sortPriority;
	}
	
}
