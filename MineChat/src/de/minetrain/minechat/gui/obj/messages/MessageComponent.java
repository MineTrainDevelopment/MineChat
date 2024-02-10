package de.minetrain.minechat.gui.obj.messages;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Channel;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.GreetingsManager;
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
	private HighlightString highlight;
	private boolean isEmoteOnly = true;
	
	private static final Button replyButton = new Button() {{
		setPrefSize(28, 28);
        BorderPane.setAlignment(this, Pos.CENTER);
        StackPane.setAlignment(this, Pos.TOP_RIGHT);
	}};
	
	static long callCount = 0;
	public MessageComponent(Channel channel, MessageComponentContent messageContent) {
		//filter out emote only messages
		if(!messageContent.isValid() || Settings.displayEmoteOnly ? false : messageContent.isEmoteOnly()){
			return;
		}
		
		long lastCall = Instant.now().toEpochMilli();
		callCount++;
		
		
		MineTextFlow title = new MineTextFlow(20);
		TwitchMessage twitchMessage = messageContent.twitchMessage();
		
		if(twitchMessage != null && !twitchMessage.getBadges().isEmpty()){
			twitchMessage.getBadges().forEach(badge -> title.appendImage(badge).appendSpace());
		}
		
		title.appendString(messageContent.getUserName(), messageContent.getUserColor()).appendString(": ", 20, Color.WHITE);
		title.setId("message-comp-title");
		
        Pane titlePane = new Pane(title);
        titlePane.setId("message-comp-title-pane");
        StackPane.setAlignment(titlePane, Pos.TOP_LEFT);
        
        BorderPane contentPane = new BorderPane();
        contentPane.setId("message-comp-background");
        contentPane.setCenter(new Label("test"));
        
        //twitch highlite test.
        if(messageContent.twitchMessage() != null && messageContent.twitchMessage().isHighlighted()){
        	contentPane.setStyle("-fx-background-color: "+Settings.displayTwitchHighlighted.getColorCode()+";");
        }
        
        hoverProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
        	if(newValue){
//        		contentPane.setRight(replyButton);
        		getChildren().add(replyButton);
        		replyButton.setOnAction(event -> {
        			//TODO: Call reply logic.
        		});
        	}else{
//        		contentPane.setRight(null);
        		getChildren().remove(replyButton);
        	}
			
		});

        if(true){
        	Button waveButton = new Button();
        	waveButton.setPrefSize(25, 25);
            contentPane.setLeft(waveButton);
            BorderPane.setAlignment(waveButton, Pos.CENTER);
        }
        
        contentPane.setCenter(formatText(messageContent));
        
        //Check for emote only again, bcs of bttv emotes.
        if(isEmoteOnly && !Settings.displayEmoteOnly){
        	contentPane = null;
        	highlight = null;
        	return;
        }

        setId("message-comp-border");
        
        //Das hier nicht richtig!
        GreetingsManager greetingsManager = channel.getGreetingsManager();
		if(!greetingsManager.isMentioned(messageContent.getUserName()) && !greetingsManager.contains(messageContent.getUserName())){
			setStyle("-fx-border-color: "+Settings.highlightUserFirstMessages.getColorCode()+";");
		}else if(highlight != null){
        	setStyle("-fx-border-color: "+highlight.getBorderColorCode()+";");
        }
		
        getChildren().addAll(titlePane, contentPane);
        
        //DEBUG
        System.err.println(Instant.now().toEpochMilli()-lastCall+".ms - "+callCount);
    }
	
	private MineTextFlow formatText(MessageComponentContent messageContent){
		MineTextFlow textFlow = new MineTextFlow(16d);
		textFlow.appendString("["+getTimeStamp(messageContent)+"] ");
		
		for(String word : messageContent.getMessage().split(" ")){
			if(word.contains(".") && !word.endsWith(".") && Main.isValidImageURL(word)){
				textFlow.appendHyperLink(word);
				continue;
			}
			
			if(messageContent.getEmoteSet().containsKey(word)){
				textFlow.appendEmote(messageContent.getEmoteSet().get(word));
				textFlow.appendSpace();
				continue;
			}
			
			int elements = textFlow.getChildren().size();
			
			Settings.highlightStrings.values().stream().filter(highlight -> highlight.isAktiv()).forEach(highlight -> {
	    		Pattern pattern = Pattern.compile("\\b" + highlight.getWord() + "\\b", Pattern.CASE_INSENSITIVE);
	            Matcher matcher = pattern.matcher(word);
	            if (matcher.find()) {
	    			textFlow.appendString(word+" ", highlight.getWordColor());
	    			if(this.highlight == null){
	    				this.highlight = highlight;
	    			}
	    		}
	    	});
			
			if(elements == textFlow.getChildren().size()){
				isEmoteOnly = false;
				textFlow.appendString(word+" ");
			}
		}
		
		textFlow.setStyle("-fx-padding: 0 5 0 5;");
		return textFlow;
	}
	
	
	public static String getTimeStamp(MessageComponentContent messageContent) {
		String pattern = Settings.messageTimeFormat;
		
		TwitchMessage message = messageContent.twitchMessage();
		if(message != null && message.isOlderThanHours(24)){
			String dateFormat = message.isOlderThanDays(7) ? Settings.dateFormat : Settings.dayFormat;
		    pattern = dateFormat + " | " + pattern;
		}
		
		return LocalDateTime.ofEpochSecond(messageContent.getEpochSec()/1000, 0, ZoneId.systemDefault().getRules().getOffset(Instant.now()))
			.format(DateTimeFormatter.ofPattern(pattern, new Locale(System.getProperty("user.language"), System.getProperty("user.country"))));
	}

}
