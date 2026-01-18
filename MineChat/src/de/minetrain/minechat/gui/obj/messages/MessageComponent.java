package de.minetrain.minechat.gui.obj.messages;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.objectdata.ChatMessage;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.features.messagehighlight.HighlightType;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.viewmodel.HighlightViewModel;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.utils.MineTextFlow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MessageComponent extends StackPane {

	private final StackPane border;
	private final MineTextFlow titleFlow;
	private final BorderPane content;
	private final MineTextFlow messageFlow;

	public MessageComponent() {
		getStyleClass().add("message-component");

		border = new StackPane();
		border.getStyleClass().add("border");

		titleFlow = new MineTextFlow(20);
		titleFlow.getStyleClass().add("title");
		Pane titleWrapper = new Pane(titleFlow);

		messageFlow = new MineTextFlow(16d);
		content = new BorderPane();
		content.getStyleClass().add("content");
		content.setLeft(createWaveButton());
		content.setCenter(messageFlow);
		BorderPane.setMargin(content.getLeft(), new Insets(5D));
		BorderPane.setMargin(content.getCenter(), new Insets(5D, 5D, 5D, 0D));

		getChildren().addAll(border, titleWrapper, content, createReplyButton());
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
		border.setStyle(null);
		content.setStyle(null);
		setVisible(true);
	}

	private void applyHighlighting(ChatMessage message, HighlightString highlight) {
		HighlightViewModel firstMessageHighlight = Main.getSettingsViewModel().getHighlightViewModel(HighlightType.FIRST_MESSAGE);
		if (message.getMessageType() == ChatMessage.MessageType.FIRST_MESSAGE && firstMessageHighlight.isActive()) {
			content.setStyle(firstMessageHighlight.getBackgroundStyle());
			border.setStyle(firstMessageHighlight.getBorderStyle());
			titleFlow.appendString("  -  First MSG");
		} else {
			HighlightViewModel userHighlighted = Main.getSettingsViewModel().getHighlightViewModel(HighlightType.HIGHLIGHT);
			if (message.getMessageType() == ChatMessage.MessageType.HIGHLIGHTED && userHighlighted.isActive()) {
				content.setStyle(userHighlighted.getBackgroundStyle());
			}

			if (message.isFirstSessionMessage() && firstMessageHighlight.isActive()) {
				border.setStyle(firstMessageHighlight.getBorderStyle());
			} else if (highlight != null) {
				border.setStyle(highlight.getBorderStyle());
			}
		}
	}

	private Button createReplyButton() {
		Button replyButton = new Button();
		BorderPane.setAlignment(replyButton, Pos.CENTER);
		StackPane.setAlignment(replyButton, Pos.TOP_RIGHT);
		replyButton.visibleProperty().bind(hoverProperty());
		// TODO set action
		return replyButton;
	}

	private Button createWaveButton() {
		Button waveButton = new Button();
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
