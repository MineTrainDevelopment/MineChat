package de.minetrain.minechat.data.objectdata;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import de.minetrain.minechat.data.objectdata.ChatMessageToken.TokenType;

public final class ChatMessage {

	private final String messageId;
	private final String channelId;
	private final String senderId;
	private final String senderName;
	private final String senderColor;
	private final Instant timestamp;
	private final String replyMessageId;
	private final MessageType messageType;
	private final List<ChatMessageToken> tokens;
	private final List<String> badgeIds;
	private final boolean isEmoteOnly;

	public static Builder builder() {
		return new Builder();
	}

	public ChatMessage(String messageId, String channelId, String senderId, String senderName, String senderColor, Instant timestamp, String replyMessageId, MessageType messageType, ChatMessageToken[] tokens, String[] badgeIds) {
		this.messageId = messageId;
		this.channelId = channelId;
		this.senderId = senderId;
		this.senderName = senderName;
		this.senderColor = senderColor;
		this.timestamp = timestamp;
		this.replyMessageId = replyMessageId;
		this.messageType = messageType;
		this.tokens = List.of(tokens);
		this.badgeIds = List.of(badgeIds);
		this.isEmoteOnly = this.tokens.stream().allMatch(token -> token.getType() == TokenType.EMOTE);
	}

	public String getMessageId() {
		return messageId;
	}

	public String getChannelId() {
		return channelId;
	}

	public String getSenderId() {
		return senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public String getSenderColor() {
		return senderColor;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public String getReplyMessageId() {
		return replyMessageId;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public List<ChatMessageToken> getTokens() {
		return tokens;
	}

	public List<String> getBadgeIds() {
		return badgeIds;
	}

	public boolean isHighlighted() {
		return messageType == MessageType.HIGHLIGHTED;
	}

	public boolean isEmoteOnly() {
		return isEmoteOnly;
	}

	public enum MessageType {
		TEXT,
		HIGHLIGHTED
	}

	public static class Builder {
		private String messageId;
		private String channelId;
		private String senderId;
		private String senderName;
		private String senderColor;
		private Instant timestamp;
		private String replyMessageId;
		private MessageType messageType = MessageType.TEXT;
		private final List<ChatMessageToken> tokens = new ArrayList<>();
		private final List<String> badgeIds = new ArrayList<>();

		public Builder withMessageId(String messageId) {
			this.messageId = messageId;
			return this;
		}

		public Builder withChannelId(String channelId) {
			this.channelId = channelId;
			return this;
		}

		public Builder withSenderId(String senderId) {
			this.senderId = senderId;
			return this;
		}

		public Builder withSenderName(String senderName) {
			this.senderName = senderName;
			return this;
		}

		public Builder withSenderColor(String senderColor) {
			this.senderColor = senderColor;
			return this;
		}

		public Builder withTimestamp(Instant timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Builder withReplyMessageId(String replyMessageId) {
			this.replyMessageId = replyMessageId;
			return this;
		}

		public Builder withMessageType(MessageType messageType) {
			this.messageType = messageType;
			return this;
		}

		public Builder addToken(ChatMessageToken part) {
			this.tokens.add(part);
			return this;
		}

		public Builder addBadgeId(String badgeId) {
			this.badgeIds.add(badgeId);
			return this;
		}

		public ChatMessage build() {
			return new ChatMessage(messageId, channelId, senderId, senderName, senderColor, timestamp, replyMessageId, messageType, tokens.toArray(ChatMessageToken[]::new), badgeIds.toArray(String[]::new));
		}
	}
}
