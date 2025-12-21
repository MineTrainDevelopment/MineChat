package de.minetrain.minechat.data.objectdata;

import java.util.Objects;

import org.eclipse.serializer.reference.Lazy;

public class Badge {

	private final BadgeId badgeId;
	private final String channelId;
	private final String title;
	private final String description;
	private final String clickAction;
	private final String clickUrl;

	private final Lazy<byte[]> image1x;
	private final Lazy<byte[]> image2x;
	private final Lazy<byte[]> image4x;

	public static Builder builder() {
		return new Builder();
	}

	public Badge(BadgeId badgeId, String channelId, String title, String description, String clickAction, String clickUrl, byte[] image1x, byte[] image2x, byte[] image4x) {
		this.badgeId = badgeId;
		this.channelId = channelId;
		this.title = title;
		this.description = description;
		this.clickAction = clickAction;
		this.clickUrl = clickUrl;
		this.image1x = Lazy.Reference(image1x);
		this.image2x = Lazy.Reference(image2x);
		this.image4x = Lazy.Reference(image4x);
	}

	public Badge(BadgeId badgeId, String channelId, String title, String description, String clickAction, String clickUrl, Lazy<byte[]> image1x, Lazy<byte[]> image2x, Lazy<byte[]> image4x) {
		this.badgeId = badgeId;
		this.channelId = channelId;
		this.title = title;
		this.description = description;
		this.clickAction = clickAction;
		this.clickUrl = clickUrl;
		this.image1x = image1x;
		this.image2x = image2x;
		this.image4x = image4x;
	}

	public BadgeId getBadgeId() {
		return badgeId;
	}

	public String getChannelId() {
		return channelId;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getClickAction() {
		return clickAction;
	}

	public String getClickUrl() {
		return clickUrl;
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
		return Objects.hash(badgeId);
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
		Badge other = (Badge) obj;
		return Objects.equals(badgeId, other.badgeId);
	}

	public Builder buildCopy() {
		return new Builder()
			.withBadgeId(badgeId)
			.withChannelId(channelId)
			.withTitle(title)
			.withDescription(description)
			.withClickAction(clickAction)
			.withClickUrl(clickUrl)
			.withImage1x(image1x)
			.withImage2x(image2x)
			.withImage4x(image4x);
	}

	public static class Builder {
		private BadgeId badgeId;
		private String channelId;
		private String title;
		private String description;
		private String clickAction;
		private String clickUrl;

		private Lazy<byte[]> image1x;
		private Lazy<byte[]> image2x;
		private Lazy<byte[]> image4x;

		public Builder withBadgeId(BadgeId badgeId) {
			this.badgeId = badgeId;
			return this;
		}

		public Builder withChannelId(String channelId) {
			this.channelId = channelId;
			return this;
		}

		public Builder withTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder withClickAction(String clickAction) {
			this.clickAction = clickAction;
			return this;
		}

		public Builder withClickUrl(String clickUrl) {
			this.clickUrl = clickUrl;
			return this;
		}

		public Builder withImage1x(Lazy<byte[]> image1x) {
			this.image1x = image1x;
			return this;
		}

		public Builder withImage2x(Lazy<byte[]> image2x) {
			this.image2x = image2x;
			return this;
		}

		public Builder withImage4x(Lazy<byte[]> image4x) {
			this.image4x = image4x;
			return this;
		}

		public Badge build() {
			return new Badge(badgeId, channelId, title, description, clickAction, clickUrl, image1x, image2x, image4x);
		}
	}
}
