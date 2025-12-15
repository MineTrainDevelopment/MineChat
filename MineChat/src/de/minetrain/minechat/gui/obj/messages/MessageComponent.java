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

import com.github.twitch4j.eventsub.domain.chat.Emote.Format;
import com.github.twitch4j.eventsub.events.ChannelChatMessageEvent;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.ChannelActions;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.MineTextFlow;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MessageComponent extends StackPane {
	private HighlightString highlight;
	private boolean isEmoteOnly = true;


	private final MineTextFlow title;
	private final BorderPane content;
	private final MineTextFlow message;

	static long callCount = 0;

	public MessageComponent() {
		setId("message-comp-border");

		title = new MineTextFlow(20);
		title.setId("message-comp-title");
		Pane titlePane = new Pane(title);
		titlePane.setId("message-comp-title-pane");
		StackPane.setAlignment(titlePane, Pos.TOP_LEFT);

		message = new MineTextFlow(16d);
		message.setStyle("-fx-padding: 0 5 0 5;");
		content = new BorderPane();
		content.setId("message-comp-background");
		content.setLeft(createWaveButton());
		content.setCenter(message);

		getChildren().addAll(createReplyButton(), titlePane, content);
	}

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
			twitchMessage.getBadges().forEach(badge -> title.appendImage(badge).appendSpace());
		}

		title.appendString(messageContent.getUserName(), messageContent.getUserColor()).appendString(": ", 20, Color.WHITE);


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
				title.appendString("  -  First MSG");
			}
        }

		setId("message-comp-border");

        //DEBUG
        System.err.println(Instant.now().toEpochMilli()-lastCall+".ms - "+callCount);
    }

	public void applyMessage(ChannelChatMessageEvent event) {
		String color = event.getColor();
		if (StringUtils.isBlank(color)) {
			color = "#ffffff";
		}
		title.appendString(event.getChatterUserName(), ColorManager.decode(color, ColorManager.encode(ColorManager.GUI_BACKGROUND))).appendString(": ", 20, Color.WHITE);

		Instant messageCreated = Instant.now(); // TODO provide message time
		DateTimeFormatter selectDateTimeFormatter = selectDateTimeFormatter(messageCreated);
		message.appendString("[" + selectDateTimeFormatter.format(messageCreated.atZone(ZoneId.systemDefault())) + "] ");

		event.getMessage().getFragments().forEach(fragment -> {
			switch (fragment.getType()) {
				case TEXT -> message.appendString(fragment.getText() + " "); // handle url and bttv
				case EMOTE -> {
					Image emote = Main.getEmoteManager().getEmoteImage1x(fragment.getEmote().getId(), fragment.getEmote().getFormat().contains(Format.ANIMATED));
					if (emote == null) {
						message.appendString(fragment.getText() + " ");
					} else {
						ImageView iv = new ImageView(emote) {

							@Override
							public double getBaselineOffset() {
								return getImage().getHeight() * 0.75;
							}
						};
						message.appendImage(iv).appendSpace();
					}
				}
				default -> message.appendString(fragment.getText() + " ");
			}
		});
	}

	private void formatText(MessageComponentContent messageContent, String channelId){
		message.appendString("["+getTimeStamp(messageContent)+"] ");

		// Cache to prevent unnecessary CPU cycles.
		Map<String, Emote> emotes = Map.of(); // No emotes for now
		List<HighlightString> highlights = Settings.highlightStrings.values().stream().filter(HighlightString::isAktiv).toList();

		for (String word : messageContent.getMessage().split(" ")) {
			Emote emote = emotes.get(word);
			if (emote != null) {
				message.appendEmote(emote);
				message.appendSpace();
				continue;
			}

			isEmoteOnly = false;
			if(word.contains(".") && !word.endsWith(".") && Main.isValidImageURL(word)){
				message.appendHyperLink(word);
				continue;
			}

			// This may take to much time...
			Optional<HighlightString> matchingHighlight = highlights.stream()
				.filter(hs -> hs.getPattern().matcher(word).matches())
				.findFirst();
			if (matchingHighlight.isPresent()) {
				message.appendString(word + " ", matchingHighlight.get().getWordColor());
				if (this.highlight == null) {
					this.highlight = matchingHighlight.get();
				}
				continue;
			}
			message.appendString(word + " ");
		}
	}


	private Button createReplyButton() {
		Button replyButton = new Button();
		replyButton.setPrefSize(28, 28);
		BorderPane.setAlignment(replyButton, Pos.CENTER);
		StackPane.setAlignment(replyButton, Pos.TOP_RIGHT);
		hoverProperty().addListener((ChangeListener<Boolean>) (_, _, newValue) -> replyButton.setVisible(newValue));
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
