package de.minetrain.minechat.gui.obj.messages;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.MineTextFlow;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MessageComponent extends StackPane {
	
	private MineTextFlow title;
	private BorderPane contentPane;
	
	private static final Button replyButton = new Button() {{
		setPrefSize(28, 28);
        BorderPane.setAlignment(this, Pos.CENTER);
        StackPane.setAlignment(this, Pos.TOP_RIGHT);
	}};
	
	public MessageComponent() {
		title = new MineTextFlow(20);
		title.appendString("user_name");
		title.setId("message-comp-title");
		
        Pane titlePane = new Pane(title);
        titlePane.setId("message-comp-title-pane");
        StackPane.setAlignment(titlePane, Pos.TOP_LEFT);
        
        contentPane = new BorderPane();
        contentPane.setId("message-comp-background");
        contentPane.setCenter(new Label("test"));
        
//        hoverProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
//        	if(newValue){
////        		contentPane.setRight(replyButton);
//        		getChildren().add(replyButton);
//        		replyButton.setOnAction(event -> {
//        			//TODO: Call reply logic.
//        		});
//        	}else{
////        		contentPane.setRight(null);
//        		getChildren().remove(replyButton);
//        	}
//			
//		});

        if(true){
        	Button waveButton = new Button();
        	waveButton.setPrefSize(25, 25);
            contentPane.setLeft(waveButton);
            BorderPane.setAlignment(waveButton, Pos.CENTER);
        }
        
//        contentPane.setCenter(formatText(messageContent));
		
		setId("message-comp-border");
        getChildren().addAll(titlePane, contentPane);
	}
	
	public void fillData(MessageComponentContent messageContent){
		if(!messageContent.isValid() || Settings.displayEmoteOnly ? false : messageContent.isEmoteOnly()){
			return;
		}
		
		TwitchMessage twitchMessage = messageContent.getTwitchMessage();
		
		title.clear();
		if(twitchMessage != null && !twitchMessage.getBadges().isEmpty()){
			twitchMessage.getBadges().forEach(badge -> title.appendImage(badge).appendSpace());
		}
		
		title.appendString(messageContent.getUserName(), messageContent.getUserColor()).appendString(": ", 20, Color.WHITE);
		
		MineTextFlow messageFlow = messageContent.formatText();
        contentPane.setCenter(messageFlow);
        
        //Check for emote only again, bcs of bttv emotes.
        if(messageContent.isEmoteOnly() && !Settings.displayEmoteOnly){
        	contentPane = null;
        	return;
        }

//        //message highlights
//		HighlightString highlight = messageFlow.getHighlight();
//		if(highlight != null){
//        	setStyle("-fx-border-color: "+highlight.getBorderColorCode()+";");
//        }
//
//        //twitch highlights.
//		if(twitchMessage != null){
//			if(twitchMessage.isFirstMessageOfInstance()  && Settings.highlightUserFirstMessages.isActive()){
//				setStyle("-fx-border-color: "+Settings.highlightUserFirstMessages.getColorCode()+";");
//			}
//			
//			if(twitchMessage.isHighlighted() && Settings.displayTwitchHighlighted.isActive()){
//				contentPane.setStyle("-fx-background-color: "+Settings.displayTwitchHighlighted.getColorCode()+";");
//			}
//			
//			if(twitchMessage.isFirstMessage() && Settings.highlightUserFirstMessages.isActive()){
//				contentPane.setStyle("-fx-background-color: "+Settings.highlightUserFirstMessages.getColorCode()+";");
//				setStyle("-fx-border-color: "+Settings.highlightUserFirstMessages.getColorCode()+";");
//				title.appendString("  -  First MSG");
//			}
//        }
	}
	
	public void free(){
		title.clear();
		title.appendString("user_name");
		
		contentPane.setCenter(null);
		contentPane.setStyle(null);
		
		setStyle(null);
	}

}
