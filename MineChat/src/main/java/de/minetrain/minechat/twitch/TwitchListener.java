package de.minetrain.minechat.twitch;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.eventsub.domain.chat.Fragment;
import com.github.twitch4j.eventsub.domain.chat.Reply;
import com.github.twitch4j.eventsub.events.ChannelChatMessageEvent;
import com.github.twitch4j.eventsub.events.ChannelChatSettingsUpdateEvent;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.BadgeId;
import de.minetrain.minechat.data.objectdata.ChatMessage;
import de.minetrain.minechat.data.objectdata.ChatMessage.MessageType;
import de.minetrain.minechat.data.objectdata.ChatMessageToken;
import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.features.autoreply.AutoReplyManager;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.utils.WebUtils;
import javafx.application.Platform;

/// Listens to Twitch events and handles them accordingly.
/// This class is responsible for processing chat messages and channel settings updates.
public class TwitchListener {

	private static final Logger LOG = LoggerFactory.getLogger(TwitchListener.class);

	private static final Pattern SPLIT_PATTERN = Pattern.compile("\\s+");

	private AutoReplyManager autoReplyManager;

	public TwitchListener(AutoReplyManager autoReplyManager) {
		this.autoReplyManager = autoReplyManager;
	}

	/// Handles the event when the chat settings of a channel are updated.
	///
	/// @param event The [ChannelChatSettingsUpdateEvent] object containing information about the updated settings.
	@EventSubscriber
	public void onChannelChatSettingsUpdate(ChannelChatSettingsUpdateEvent event) {
		Main.getChannelManager().getChannelViewModel(event.getBroadcasterUserId()).setSlowModeWaitTime(event.isSlowMode().booleanValue() ? event.getSlowModeWaitTimeSeconds() : 0);
	}

	/// Handles the event when a message is sent in the channel.
	///
	/// @param event The [ChannelChatMessageEvent] object containing information about the message.
	@EventSubscriber
	public void onChannelMessage(ChannelChatMessageEvent event) {
		Instant timestamp = Instant.now();
		LOG.debug("EventSub ChannelMessage: {} | {}", event.getChatterUserName(), event.getMessage().getText());

		ChatMessage chatMessage = createChatMessage(event, timestamp);
		Platform.runLater(() ->{
			ChannelViewModel cvm = Main.getChannelManager().getChannelViewModel(event.getBroadcasterUserId());
			int index = EclipseStoreKeeper.root().messages().addMessage(chatMessage);
			chatMessage.setFirstSessionMessage(cvm.getParticipatedUserIds().add(chatMessage.getSenderId()));
			cvm.getMessages().notifyAdd(index);
		});
		autoReplyManager.handleMessage(event.getBroadcasterUserId(), event.getMessageId(), event.getMessage().getCleanedText(), timestamp);
	}

	private static ChatMessage createChatMessage(ChannelChatMessageEvent event, Instant timestamp) {
		List<ChatMessageToken> tokenList = new ArrayList<>();
		event.getMessage().getFragments().stream().forEach(fragment -> createChatMessageToken(event.getBroadcasterUserId(), fragment, tokenList));
		ChatMessageToken[] tokens = tokenList.toArray(ChatMessageToken[]::new);
		BadgeId[] badgeIds = event.getBadges().stream().map(badge -> new BadgeId(badge.getSetId(), badge.getId())).toArray(BadgeId[]::new);
		Reply reply = event.getReply();

		LOG.debug("Created ChatMessage tokens: {}", (Object) tokens);

		return new ChatMessage(
				event.getMessageId(),
				event.getBroadcasterUserId(),
				event.getChatterUserId(),
				event.getChatterUserName(),
				event.getColor(),
				timestamp,
				reply != null ? reply.getParentMessageId() : null,
				mapMessageType(event.getMessageType()),
				tokens,
				badgeIds
		);
	}

	private static MessageType mapMessageType(com.github.twitch4j.eventsub.domain.chat.MessageType messageType) {
		return switch (messageType) {
			case CHANNEL_POINTS_HIGHLIGHTED -> MessageType.HIGHLIGHTED;
			case USER_INTRO -> MessageType.FIRST_MESSAGE;
			default -> MessageType.TEXT;
		};
	}

	private static void createChatMessageToken(String channelId, Fragment fragment, List<ChatMessageToken> tokenList) {
		switch (fragment.getType()) {
			case EMOTE -> tokenList.add(ChatMessageToken.createEmoteToken(fragment.getEmote().getId(), fragment.getText()));
			case MENTION -> tokenList.add(ChatMessageToken.createMentionToken(fragment.getText()));
			default ->  tokenizeText(channelId, fragment.getText(), tokenList);
		}
	}

	private static void tokenizeText(String channelId, String text, List<ChatMessageToken> tokenList) {
		String[] words = SPLIT_PATTERN.splitWithDelimiters(text, 0);
		for (String word : words) {
			if (word.isEmpty()) {
				continue;
			}
			if (word.length() >= 2 && StringUtils.isNotBlank(word)) {
				if (WebUtils.isValidUrl(word)) {
					tokenList.add(ChatMessageToken.createLinkToken(word));
					continue;
				}
				Emote emote = Main.getEmoteManager().getBttvEmoteByName(channelId, word);
				if (emote != null) {
					tokenList.add(ChatMessageToken.createEmoteToken(emote.getEmoteId(), word));
					continue;
				}
			}
			tokenList.add(ChatMessageToken.createTextToken(word));
		}
	}

}
