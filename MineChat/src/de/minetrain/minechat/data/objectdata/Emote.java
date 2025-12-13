package de.minetrain.minechat.data.objectdata;

import java.util.Objects;

import org.eclipse.serializer.reference.Lazy;

import de.minetrain.minechat.gui.emotes.EmoteLegacy.EmoteType;

public class Emote {

	private final String emoteId;
	private final String setId;
	private final String channelId;
	private final String name;
	private final EmoteType emoteType;
	private final boolean favorite;
	private final boolean animated;
	private final String fileFormat;

	private final Lazy<byte[]> image1x;
	private final Lazy<byte[]> image2x;
	private final Lazy<byte[]> image4x;

	public Emote(String emoteId, String setId, String channelId, String name, EmoteType emoteType, boolean favorite, boolean animated, String fileFormat, byte[] image1x, byte[] image2x, byte[] image4x) {
		this.emoteId = emoteId;
		this.setId = setId;
		this.channelId = channelId;
		this.name = name;
		this.emoteType = emoteType;
		this.favorite = favorite;
		this.animated = animated;
		this.fileFormat = fileFormat;
		this.image1x = Lazy.Reference(image1x);
		this.image2x = Lazy.Reference(image2x);
		this.image4x = Lazy.Reference(image4x);
	}

	public Emote(String emoteId, String setId, String channelId, String name, EmoteType emoteType, boolean favorite, boolean animated, String fileFormat, Lazy<byte[]> image1x, Lazy<byte[]> image2x, Lazy<byte[]> image4x) {
		this.emoteId = emoteId;
		this.setId = setId;
		this.channelId = channelId;
		this.name = name;
		this.emoteType = emoteType;
		this.favorite = favorite;
		this.animated = animated;
		this.fileFormat = fileFormat;
		this.image1x = image1x;
		this.image2x = image2x;
		this.image4x = image4x;
	}

	public String getEmoteId() {
		return emoteId;
	}

	public String getSetId() {
		return setId;
	}

	public String getChannelId() {
		return channelId;
	}

	public String getName() {
		return name;
	}

	public EmoteType getEmoteType() {
		return emoteType;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public boolean isAnimated() {
		return animated;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public byte[] getImage1x() {
		return image1x.get();
	}

	public byte[] getImage2x() {
		return image2x.get();
	}

	public byte[] getImage4x() {
		return image4x.get();
	}

	@Override
	public int hashCode() {
		return Objects.hash(emoteId);
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
		Emote other = (Emote) obj;
		return Objects.equals(emoteId, other.emoteId);
	}

	public Builder buildCopy() {
		return new Builder()
			.withEmoteId(emoteId)
			.withSetId(setId)
			.withChannelId(channelId)
			.withName(name)
			.withEmoteType(emoteType)
			.withFavorite(favorite)
			.withAnimated(animated)
			.withFileFormat(fileFormat)
			.withImage1x(image1x)
			.withImage2x(image2x)
			.withImage4x(image4x);
	}

	public static class Builder {

		private String emoteId;
		private String setId;
		private String channelId;
		private String name;
		private EmoteType emoteType;
		private boolean favorite;
		private boolean animated;
		private String fileFormat;
		private Lazy<byte[]> image1x;
		private Lazy<byte[]> image2x;
		private Lazy<byte[]> image4x;

		public Builder withEmoteId(String emoteId) {
			this.emoteId = emoteId;
			return this;
		}

		public Builder withSetId(String setId) {
			this.setId = setId;
			return this;
		}

		public Builder withChannelId(String channelId) {
			this.channelId = channelId;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withEmoteType(EmoteType emoteType) {
			this.emoteType = emoteType;
			return this;
		}

		public Builder withFavorite(boolean favorite) {
			this.favorite = favorite;
			return this;
		}

		public Builder withAnimated(boolean animated) {
			this.animated = animated;
			return this;
		}

		public Builder withFileFormat(String fileFormat) {
			this.fileFormat = fileFormat;
			return this;
		}

		public Builder withImage1x(byte[] image1x) {
			this.image1x = Lazy.Reference(image1x);
			return this;
		}

		public Builder withImage1x(Lazy<byte[]> image1x) {
			this.image1x = image1x;
			return this;
		}

		public Builder withImage2x(byte[] image2x) {
			this.image2x = Lazy.Reference(image2x);
			return this;
		}

		public Builder withImage2x(Lazy<byte[]> image2x) {
			this.image2x = image2x;
			return this;
		}

		public Builder withImage4x(byte[] image4x) {
			this.image4x = Lazy.Reference(image4x);
			return this;
		}

		public Builder withImage4x(Lazy<byte[]> image4x) {
			this.image4x = image4x;
			return this;
		}

		public Emote build() {
			return new Emote(
				emoteId,
				setId,
				channelId,
				name,
				emoteType,
				favorite,
				animated,
				fileFormat,
				image1x,
				image2x,
				image4x
			);
		}
	}
}
