package de.minetrain.minechat.gui.frames.emote_selector;

import de.minetrain.minechat.main.Channel;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;

public class EmoteSelectorChannelButton extends Button {
	private static final String default_style = "-fx-min-width: 34; -fx-max-width: 34;";
	private EmoteSelectorBatche emoteBatche;
	
	public EmoteSelectorChannelButton(Channel channel, EmoteSelector emoteSelector) {
		setFocusTraversable(false);
        setId("channel-tab");
        setStyle(default_style);
        
        Rectangle imageView = channel.getProfilePic(24);
        imageView.setTranslateX(-5);
		setGraphic(imageView);
		
		setOnAction(event -> {
			if(emoteBatche != null){
				emoteSelector.scrollToEmoteBatch(emoteBatche);
			}
		});
	}
	
	
	public void setColor(boolean select){
		if(select){
			setStyle(default_style+" -fx-background-color: green;");
			return;
		}
		
		setStyle(default_style);
	}
	
	public void setParentBatche(EmoteSelectorBatche emoteBatche){
		this.emoteBatche = emoteBatche;
	}

}
