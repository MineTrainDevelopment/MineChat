package de.minetrain.minechat.gui.obj.messages;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.main.ChannelActions;
import de.minetrain.minechat.main.Main;
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
	public MessageComponent(ChannelActions channel, MessageComponentContent messageContent) {
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

		contentPane.setCenter(formatText(messageContent, channel.getChannelId()));

        //Check for emote only again, bcs of bttv emotes.
        if(isEmoteOnly && !Settings.displayEmoteOnly){
        	contentPane = null;
        	highlight = null;
        	return;
        }

        //message highlights
        if(highlight != null){
        	setStyle("-fx-border-color: "+highlight.getBorderColorCode()+";");
        }

        //twitch highlights.
		if(twitchMessage != null){
			if(twitchMessage.isFirstMessageOfInstance()  && Settings.highlightUserFirstMessages.isActive()){
				setStyle("-fx-border-color: "+Settings.highlightUserFirstMessages.getColorCode()+";");
			}

			if(twitchMessage.isHighlighted() && Settings.displayTwitchHighlighted.isActive()){
				contentPane.setStyle("-fx-background-color: "+Settings.displayTwitchHighlighted.getColorCode()+";");
			}

			if(twitchMessage.isFirstMessage() && Settings.highlightUserFirstMessages.isActive()){
				contentPane.setStyle("-fx-background-color: "+Settings.highlightUserFirstMessages.getColorCode()+";");
				setStyle("-fx-border-color: "+Settings.highlightUserFirstMessages.getColorCode()+";");
				title.appendString("  -  First MSG");
			}
        }

		setId("message-comp-border");
        getChildren().addAll(titlePane, contentPane);

        //DEBUG
        System.err.println(Instant.now().toEpochMilli()-lastCall+".ms - "+callCount);
    }

	private MineTextFlow formatText(MessageComponentContent messageContent, String channelId){
		MineTextFlow textFlow = new MineTextFlow(16d);
		textFlow.appendString("["+getTimeStamp(messageContent)+"] ");

		// Cache to prevent unnecessary CPU cycles.
		System.err.println("Emote sets: " + messageContent.getEmoteSets());
		Map<String, Emote> emotes = messageContent.getEmoteSets().stream().map(setId -> Main.getEmoteManager().getEmoteSet(setId))
			.reduce(new HashMap<>(), (map, emoteMap) -> {
				map.putAll(emoteMap);
				return map;
			});
		List<HighlightString> highlights = Settings.highlightStrings.values().stream().filter(HighlightString::isAktiv).toList();

		for (String word : messageContent.getMessage().split(" ")) {
			Emote emote = emotes.get(word);
			if (emote != null) {
				textFlow.appendEmote(emote);
				textFlow.appendSpace();
				continue;
			}

			isEmoteOnly = false;
			if(word.contains(".") && !word.endsWith(".") && Main.isValidImageURL(word)){
				textFlow.appendHyperLink(word);
				continue;
			}

			// This may take to much time...
			Optional<HighlightString> matchingHighlight = highlights.stream()
				.filter(hs -> hs.getPattern().matcher(word).matches())
				.findFirst();
			if (matchingHighlight.isPresent()) {
				textFlow.appendString(word + " ", matchingHighlight.get().getWordColor());
				if (this.highlight == null) {
					this.highlight = matchingHighlight.get();
				}
				continue;
			}
			textFlow.appendString(word + " ");
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
