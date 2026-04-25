package de.minetrain.minechat.data.objectdata;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.minetrain.minechat.features.macros.MacroType;

public class Macro {

	private final UUID uuid;
	private final String channelId;
	private final MacroType macroType;
	private final int index;
	private final String title;
	private final String emoteId;
	private final String[] output;

	public static Builder builder() {
		return new Builder();
	}

	public Macro(UUID uuid, String channelId, MacroType macroType,  int index, String title, String emoteId, String[] output) {
		this.uuid = uuid;
		this.channelId = channelId;
		this.macroType = macroType;
		this.index = index;
		this.title = title;
		this.emoteId = emoteId;
		this.output = output;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getChannelId() {
		return channelId;
	}

	public MacroType getMacroType() {
		return macroType;
	}

	public int getIndex() {
		return index;
	}

	public String getTitle() {
		return title;
	}

	public String getEmoteId() {
		return emoteId;
	}

	public String[] getOutput() {
		return output;
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Macro other = (Macro) obj;
		return Objects.equals(uuid, other.uuid);
	}

	public Builder buildCopy() {
		return Macro.builder()
				.withUuid(uuid)
				.withChannelId(channelId)
				.withMacroType(macroType)
				.withIndex(index)
				.withTitle(title)
				.withEmoteId(emoteId)
				.withOutput(output != null ? List.of(output) : null);
	}

	public static class Builder {
		private UUID uuid;
		private String channelId;
		private MacroType macroType;
		private int index;
		private String title;
		private String emoteId;
		private List<String> output;

		public Builder withUuid(UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder withChannelId(String channelId) {
			this.channelId = channelId;
			return this;
		}

		public Builder withMacroType(MacroType macroType) {
			this.macroType = macroType;
			return this;
		}

		public Builder withIndex(int index) {
			this.index = index;
			return this;
		}

		public Builder withTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder withEmoteId(String emoteId) {
			this.emoteId = emoteId;
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

		public UUID getUuid() {
			return uuid;
		}

		public String getChannelId() {
			return channelId;
		}

		public MacroType getMacroType() {
			return macroType;
		}

		public int getIndex() {
			return index;
		}

		public String getTitle() {
			return title;
		}

		public String getEmoteId() {
			return emoteId;
		}

		public List<String> getOutput() {
			return output;
		}

		public Macro build() {
			return new Macro(uuid, channelId, macroType, index, title,  emoteId, output.toArray(String[]::new));
		}
	}
}
