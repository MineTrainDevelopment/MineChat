package de.minetrain.minechat.gui.obj.messages;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.objectdata.ChatMessage;
import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.ChannelActions;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.MineTextFlow;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MessageComponent extends StackPane {
	private HighlightString highlight;
	private boolean isEmoteOnly = true;


	private final MineTextFlow titleFlow;
	private final BorderPane content;
	private final MineTextFlow messageFlow;

	static long callCount = 0;

	public MessageComponent() {
		setId("message-comp-border");

		titleFlow = new MineTextFlow(20);
		titleFlow.setId("message-comp-title");
		Pane titlePane = new Pane(titleFlow);
		titlePane.setId("message-comp-title-pane");
		StackPane.setAlignment(titlePane, Pos.TOP_LEFT);

		messageFlow = new MineTextFlow(16d);
		messageFlow.setStyle("-fx-padding: 0 5 0 5;");
		content = new BorderPane();
		content.setId("message-comp-background");
		content.setLeft(createWaveButton());
		content.setCenter(messageFlow);

		getChildren().addAll(titlePane, content, createReplyButton());
	}

	@Deprecated
	public MessageComponent(ChannelActions channel, MessageComponentContent messageContent) {
		this();
		//filter out emote only messages
		if(!messageContent.isValid() || Settings.displayEmoteOnly ? false : messageContent.isEmoteOnly()){
			return;
		}

		long lastCall = Instant.now().toEpochMilli();
		callCount++;

		TwitchMessage twitchMessage = messageContent.twitchMessage();

		if(twitchMessage != null && !twitchMessage.getBadges().isEmpty()){
			twitchMessage.getBadges().forEach(badge -> titleFlow.appendImage(badge).appendSpace());
		}

		titleFlow.appendString(messageContent.getUserName(), messageContent.getUserColor()).appendString(": ", 20, Color.WHITE);


		formatText(messageContent, channel.getChannelId());

        //Check for emote only again, bcs of bttv emotes.
        if(isEmoteOnly && !Settings.displayEmoteOnly){
        	content.setVisible(false);
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
				content.setStyle("-fx-background-color: "+Settings.displayTwitchHighlighted.getColorCode()+";");
			}

			if(twitchMessage.isFirstMessage() && Settings.highlightUserFirstMessages.isActive()){
				content.setStyle("-fx-background-color: "+Settings.highlightUserFirstMessages.getColorCode()+";");
				setStyle("-fx-border-color: "+Settings.highlightUserFirstMessages.getColorCode()+";");
				titleFlow.appendString("  -  First MSG");
			}
        }

		setId("message-comp-border");

        //DEBUG
        System.err.println(Instant.now().toEpochMilli()-lastCall+".ms - "+callCount);
    }

	public void applyMessage(ChatMessage message) {
		clearMessage();
		String color = message.getSenderColor();
		if (StringUtils.isBlank(color)) {
			color = "#ffffff";
		}

		message.getBadgeIds().forEach(badgeId -> titleFlow.appendSpace().appendBadge(message.getChannelId(), badgeId));
		titleFlow.appendSpace().appendString(message.getSenderName(), ColorManager.decode(color, ColorManager.encode(ColorManager.GUI_BACKGROUND))).appendString(": ", 20, Color.WHITE);

		Instant messageCreated = message.getTimestamp();
		DateTimeFormatter selectDateTimeFormatter = selectDateTimeFormatter(messageCreated);
		messageFlow.appendString("[" + selectDateTimeFormatter.format(messageCreated.atZone(ZoneId.systemDefault())) + "] ");

		message.getTokens().forEach(messageFlow::appendToken);
	}

	public void clearMessage() {
		messageFlow.clear();
		titleFlow.clear();
	}

	@Deprecated
	private void formatText(MessageComponentContent messageContent, String channelId){
		messageFlow.appendString("["+getTimeStamp(messageContent)+"] ");

		// Cache to prevent unnecessary CPU cycles.
		Map<String, Emote> emotes = Map.of(); // No emotes for now
		List<HighlightString> highlights = Settings.highlightStrings.values().stream().filter(HighlightString::isAktiv).toList();

		for (String word : messageContent.getMessage().split(" ")) {
			Emote emote = emotes.get(word);
			if (emote != null) {
				messageFlow.appendEmote(emote);
				messageFlow.appendSpace();
				continue;
			}

			isEmoteOnly = false;
			if(word.contains(".") && !word.endsWith(".") && Main.isValidImageURL(word)){
				messageFlow.appendHyperLink(word);
				continue;
			}

			// This may take to much time...
			Optional<HighlightString> matchingHighlight = highlights.stream()
				.filter(hs -> hs.getPattern().matcher(word).matches())
				.findFirst();
			if (matchingHighlight.isPresent()) {
				messageFlow.appendString(word + " ", matchingHighlight.get().getWordColor());
				if (this.highlight == null) {
					this.highlight = matchingHighlight.get();
				}
				continue;
			}
			messageFlow.appendString(word + " ");
		}
	}

	private Button createReplyButton() {
		Button replyButton = new Button();
		replyButton.setPrefSize(28, 28);
		BorderPane.setAlignment(replyButton, Pos.CENTER);
		StackPane.setAlignment(replyButton, Pos.TOP_RIGHT);
		replyButton.visibleProperty().bind(hoverProperty());
		// TODO set action
		return replyButton;
	}

	private Button createWaveButton() {
		Button waveButton = new Button();
		waveButton.setPrefSize(25, 25);
		BorderPane.setAlignment(waveButton, Pos.CENTER);
		return waveButton;
	}

	private static DateTimeFormatter selectDateTimeFormatter(Instant instant) {
		Instant now = Instant.now();
		if (!instant.isBefore(now.minus(Period.ofDays(1)))) {
			return DateTimeFormatter.ofPattern(Settings.messageTimeFormat);
		}

		if (!instant.isBefore(now.minus(Period.ofDays(7)))) {
			String pattern = Settings.dayFormat + " | " + Settings.messageTimeFormat;
			return DateTimeFormatter.ofPattern(pattern);
		}

		return DateTimeFormatter.ofPattern(Settings.dateFormat + " | " + Settings.messageTimeFormat);
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
