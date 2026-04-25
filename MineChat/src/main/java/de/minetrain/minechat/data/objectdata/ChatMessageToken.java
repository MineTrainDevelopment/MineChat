package de.minetrain.minechat.data.objectdata;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

public final class ChatMessageToken {

	private final String text;
	private final String emoteId;
	private final TokenType type;

	public static Builder builder() {
		return new Builder();
	}

	public static ChatMessageToken createTextToken(String text) {
		return new ChatMessageToken(text, null, StringUtils.isBlank(text) ? TokenType.SPACE : TokenType.TEXT);
	}

	public static ChatMessageToken createEmoteToken(String emoteId, String text) {
		return new ChatMessageToken(text, emoteId, TokenType.EMOTE);
	}

	public static ChatMessageToken createLinkToken(String text) {
		return new ChatMessageToken(text, null, TokenType.LINK);
	}

	public static ChatMessageToken createMentionToken(String text) {
		return new ChatMessageToken(text, null, TokenType.MENTION);
	}

	private ChatMessageToken(String text, String emoteId, TokenType type) {
		this.text = text;
		this.emoteId = emoteId;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public String getEmoteId() {
		return emoteId;
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
		MENTION,
		SPACE
	}

	public static class Builder {

		private String text;
		private String emoteId;
		private TokenType type;

		public Builder withText(String text) {
			this.text = text;
			return this;
		}

		public Builder withEmoteId(String emoteId) {
			this.emoteId = emoteId;
			return this;
		}

		public Builder withType(TokenType type) {
			this.type = type;
			return this;
		}

		public ChatMessageToken build() {
			return new ChatMessageToken(text, emoteId, type);
		}
	}
}
