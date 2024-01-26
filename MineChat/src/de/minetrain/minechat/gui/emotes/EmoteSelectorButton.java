package de.minetrain.minechat.gui.emotes;

import de.minetrain.minechat.gui.emotes.Emote.EmoteSize;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;

public class EmoteSelectorButton extends Button {
	
	public EmoteSelectorButton(Emote emote, EmoteSize size, int borderWidth) {
		this(emote, size, emote.getBorderType(), borderWidth);
	}
	
	public EmoteSelectorButton(Emote emote, EmoteSize size, EmoteBorderType borderType) {
		this(emote, size, borderType, 2);
	}
	
	public EmoteSelectorButton(Emote emote, EmoteSize size, EmoteBorderType borderType, int borderWidth) {
		setId("emote_border");
		setStyle("-fx-border-color: "+borderType.getHexCode()+"; -fx-border-width: "+borderWidth+"px;");
		setGraphic(emote.getEmoteNode(size, size.getSize()));
		setTooltip(new Tooltip(emote.getName()));
		
		int borderSize = borderWidth*2;
		setMinSize(size.getSize() + borderSize, size.getSize()+borderSize);
		setMaxSize(size.getSize() + borderSize, size.getSize() + borderSize);
	}
	
	
	
	
	
	
	public enum EmoteBorderType{
		DEFAULT("#0e0e0e"), 
		TIER_2("#9a9a00"), 
		TIER_3("#9a0000"), 
		BITS("#a400ff"), 
		FOLLOW("#0054de");

		private String hexCode;
		public String getHexCode(){return hexCode;}
		public Color getColor(){return Color.web(hexCode);}
		EmoteBorderType(String hexCode) {
			this.hexCode = hexCode;
		}
	};

}
