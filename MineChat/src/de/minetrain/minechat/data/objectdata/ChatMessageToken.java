package de.minetrain.minechat.data.objectdata;

import org.slf4j.helpers.MessageFormatter;

public final class ChatMessageToken {

	private final String text;
	private final String emoteId;
	private final boolean animated;
	private final TokenType type;

	public static Builder builder() {
		return new Builder();
	}

	public static ChatMessageToken createTextToken(String text) {
		return new ChatMessageToken(text, null, false, TokenType.TEXT);
	}

	public static ChatMessageToken createEmoteToken(String emoteId, boolean animated, String text) {
		return new ChatMessageToken(text, emoteId, animated, TokenType.EMOTE);
	}

	public static ChatMessageToken createLinkToken(String text) {
		return new ChatMessageToken(text, null, false, TokenType.LINK);
	}

	public static ChatMessageToken createMentionToken(String text) {
		return new ChatMessageToken(text, null, false, TokenType.MENTION);
	}

	private ChatMessageToken(String text, String emoteId, boolean isAnimated, TokenType type) {
		this.text = text;
		this.emoteId = emoteId;
		this.animated = isAnimated;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public String getEmoteId() {
		return emoteId;
	}

	public boolean isAnimated() {
		return animated;
	}

	public TokenType getType() {
		return type;
	}

	@Override
	public String toString() {
		return MessageFormatter.basicArrayFormat("ChatMessageToken [text={}, type={}]", new Object[] { text, type });
	}

	public enum TokenType {
		TEXT,
		EMOTE,
		LINK,
		MENTION
	}

	public static class Builder {

		private String text;
		private String emoteId;
		private boolean animated;
		private TokenType type;

		public Builder withText(String text) {
			this.text = text;
			return this;
		}

		public Builder withEmoteId(String emoteId) {
			this.emoteId = emoteId;
			return this;
		}

		public Builder withAnimated(boolean animated) {
			this.animated = animated;
			return this;
		}

		public Builder withType(TokenType type) {
			this.type = type;
			return this;
		}

		public ChatMessageToken build() {
			return new ChatMessageToken(text, emoteId, animated, type);
		}
	}
}
