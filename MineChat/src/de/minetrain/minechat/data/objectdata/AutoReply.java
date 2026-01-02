package de.minetrain.minechat.data.objectdata;

import java.util.List;
import java.util.UUID;

public class AutoReply {

	private final UUID uuid;
	private final String channelId;
	private final boolean enabled;
	private final String pattern;
	private final String[] output;
	private final int messagesPerMinute;
	private final int delay;
	private final boolean reply;

	public static Builder builder() {
		return new Builder();
	}

	public AutoReply(UUID uuid, String channelId, boolean enabled, String pattern, String[] output, int messagesPerMinute, int delay, boolean reply) {
		this.uuid = uuid;
		this.channelId = channelId;
		this.enabled = enabled;
		this.pattern = pattern;
		this.output = output;
		this.messagesPerMinute = messagesPerMinute;
		this.delay = delay;
		this.reply = reply;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getChannelId() {
		return channelId;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getPattern() {
		return pattern;
	}

	public String[] getOutput() {
		return output;
	}

	public int getMessagesPerMinute() {
		return messagesPerMinute;
	}

	public int getDelay() {
		return delay;
	}

	public boolean isReply() {
		return reply;
	}

	public Builder buildCopy() {
		return new Builder()
				.withUuid(uuid)
				.withChannelId(channelId)
				.withEnabled(enabled)
				.withPattern(pattern)
				.withOutput(output != null ? List.of(output) : null)
				.withMessagesPerMinute(messagesPerMinute)
				.withDelay(delay)
				.withReply(reply);
	}

	public static class Builder {
		private UUID uuid;
		private String channelId;
		private boolean enabled;
		private String pattern;
		private List<String> output;
		private int messagesPerMinute;
		private int delay;
		private boolean reply;

		public Builder withUuid(UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder withChannelId(String channelId) {
			this.channelId = channelId;
			return this;
		}

		public Builder withEnabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		public Builder withPattern(String pattern) {
			this.pattern = pattern;
			return this;
		}

		public Builder withOutput(List<String> output) {
			this.output = output;
			return this;
		}

		public Builder addOutput(String output) {
			this.output.add(output);
			return this;
		}

		public Builder withMessagesPerMinute(int messagesPerMinute) {
			this.messagesPerMinute = messagesPerMinute;
			return this;
		}

		public Builder withDelay(int delay) {
			this.delay = delay;
			return this;
		}

		public Builder withReply(boolean reply) {
			this.reply = reply;
			return this;
		}

		public UUID getUuid() {
			return uuid;
		}

		public String getChannelId() {
			return channelId;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public String getPattern() {
			return pattern;
		}

		public List<String> getOutput() {
			return output;
		}

		public int getMessagesPerMinute() {
			return messagesPerMinute;
		}

		public int getDelay() {
			return delay;
		}

		public boolean isReply() {
			return reply;
		}

		public AutoReply build() {
			return new AutoReply(uuid, channelId, enabled, pattern, output.toArray(String[]::new), messagesPerMinute, delay, reply);
		}
	}
}
