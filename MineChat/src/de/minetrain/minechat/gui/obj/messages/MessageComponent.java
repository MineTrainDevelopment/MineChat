package de.minetrain.minechat.gui.obj.messages;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.objectdata.ChatMessage;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.utils.MineTextFlow;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MessageComponent extends StackPane {

	private final MineTextFlow titleFlow;
	private final BorderPane content;
	private final MineTextFlow messageFlow;
	private final String defaultStyle;
	private final String contentDefaultStyle;

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
		defaultStyle = getStyle();
		contentDefaultStyle = content.getStyle();
	}

	public void applyMessage(ChatMessage message) {
		clearMessage();

		if (message.isEmoteOnly() && !Settings.displayEmoteOnly) {
			setVisible(false);
			return;
		}

		String color = message.getSenderColor();
		if (StringUtils.isBlank(color)) {
			color = "#ffffff";
		}

		message.getBadgeIds().forEach(badgeId -> titleFlow.appendSpace().appendBadge(message.getChannelId(), badgeId));
		titleFlow.appendSpace().appendString(message.getSenderName(), ColorManager.decode(color, ColorManager.encode(ColorManager.GUI_BACKGROUND))).appendString(": ", 20, Color.WHITE);

		Instant messageCreated = message.getTimestamp();
		DateTimeFormatter selectDateTimeFormatter = selectDateTimeFormatter(messageCreated);
		messageFlow.appendString("[" + selectDateTimeFormatter.format(messageCreated.atZone(ZoneId.systemDefault())) + "] ");

		HighlightString highlight = null;
		for (var token : message.getTokens()) {
			HighlightString appliedHighlight = messageFlow.appendToken(token);
			if (highlight == null && appliedHighlight != null) {
				highlight = appliedHighlight;
			}
		}

		applyHighlighting(message, highlight);
	}

	public void clearMessage() {
		messageFlow.clear();
		titleFlow.clear();
		setStyle(defaultStyle);
		content.setStyle(contentDefaultStyle);
		setVisible(true);
	}

	private void applyHighlighting(ChatMessage message, HighlightString highlight) {
		if (Settings.highlightUserFirstMessages.isActive() && message.getMessageType() == ChatMessage.MessageType.FIRST_MESSAGE) {
			content.setStyle("-fx-background-color: " + Settings.highlightUserFirstMessages.getColorCode() + ";");
			applyBorderColor(Settings.highlightUserFirstMessages.getColorCode());
			titleFlow.appendString("  -  First MSG");
		} else {
			if (message.getMessageType() == ChatMessage.MessageType.HIGHLIGHTED && Settings.displayTwitchHighlighted.isActive()) {
				content.setStyle("-fx-background-color: " + Settings.displayTwitchHighlighted.getColorCode() + ";");
			}

			if (message.isFirstSessionMessage() && Settings.highlightUserFirstMessages.isActive()) {
				applyBorderColor(Settings.highlightUserFirstMessages.getColorCode());
			} else if (highlight != null) {
				applyBorderColor(highlight.getBorderColorCode());
			}
		}
	}

	private void applyBorderColor(String colorCode) {
		setStyle("-fx-border-color: " + colorCode + ";");
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
}
