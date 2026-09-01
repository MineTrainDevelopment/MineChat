package de.minetrain.minechat.data.objectdata;

import java.util.Objects;

import de.minetrain.minechat.utils.audio.AudioVolume;

public class Channel  {
	private final String channelId;
	private final String loginName;
	private final String displayName;
	private final int sortIndex;
	private final String chatRole; //Viwer, MODERATOR, VIP
	private final String chatlogLevel; //null, Highlight, everything
	private final String greetingText; //full string without seperating bye \n
	private final String goodbyeText; //full string without seperating bye \n
	private final String returnText; //full string without seperating bye \n
	private final String audioPath;
	private final AudioVolume audioVolume;
	private final String profileImageUrl;

	public static Builder builder() {
		return new Builder();
	}

	public Channel(String channelId, String loginName,  String displayName, int sortIndex, String chatRole, String chatlogLevel, String greetingText, String goodbyeText, String returnText, String audioPath, AudioVolume audioVolume, String profileImageUrl) {
		this.channelId = channelId;
		this.loginName = loginName;
		this.displayName = displayName;
		this.sortIndex = sortIndex;
		this.chatRole = chatRole;
		this.chatlogLevel = chatlogLevel;
		this.greetingText = greetingText;
		this.goodbyeText = goodbyeText;
		this.returnText = returnText;
		this.audioPath = audioPath;
		this.audioVolume = audioVolume;
		this.profileImageUrl = profileImageUrl;
	}

	public String getChannelId() {
		return channelId;
	}

	public String getLoginName() {
		return loginName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public String getChatRole() {
		return chatRole;
	}

	public String getChatlogLevel() {
		return chatlogLevel;
	}

	public String getGreetingText() {
		return greetingText;
	}

	public String getGoodbyeText() {
		return goodbyeText;
	}

	public String getReturnText() {
		return returnText;
	}

	public String getAudioPath() {
		return audioPath;
	}

	public AudioVolume getAudioVolume() {
		return audioVolume;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	@Override
	public int hashCode() {
		return Objects.hash(channelId);
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
		Channel other = (Channel) obj;
		return Objects.equals(channelId, other.channelId);
	}

	public Builder buildCopy() {
		return builder()
			.withChannelId(channelId)
			.withLoginName(loginName)
			.withDisplayName(displayName)
			.withSortIndex(sortIndex)
			.withChatRole(chatRole)
			.withChatlogLevel(chatlogLevel)
			.withGreetingText(greetingText)
			.withGoodbyText(goodbyeText)
			.withReturnText(returnText)
			.withAudioPath(audioPath)
			.withAudioVolume(audioVolume)
			.withProfileImageUrl(profileImageUrl);
	}

	public static class Builder {
		private String channelId;
		private String loginName;
		private String displayName;
		private int sortIndex;
		private String chatRole;
		private String chatlogLevel;
		private String greetingText;
		private String goodbyeText;
		private String returnText;
		private String audioPath;
		private AudioVolume audioVolume;
		private String profileImageUrl;

		public Builder withChannelId(String channelId) {
			this.channelId = channelId;
			return this;
		}

		public Builder withLoginName(String loginName) {
			this.loginName = loginName;
			return this;
		}

		public Builder withDisplayName(String displayName) {
			this.displayName = displayName;
			return this;
		}

		public Builder withSortIndex(int sortIndex) {
			this.sortIndex = sortIndex;
			return this;
		}

		public Builder withChatRole(String chatRole) {
			this.chatRole = chatRole;
			return this;
		}

		public Builder withChatlogLevel(String chatlogLevel) {
			this.chatlogLevel = chatlogLevel;
			return this;
		}

		public Builder withGreetingText(String greetingText) {
			this.greetingText = greetingText;
			return this;
		}

		public Builder withGoodbyText(String goodbyText) {
			this.goodbyeText = goodbyText;
			return this;
		}

		public Builder withReturnText(String returnText) {
			this.returnText = returnText;
			return this;
		}

		public Builder withAudioPath(String audioPath) {
			this.audioPath = audioPath;
			return this;
		}

		public Builder withAudioVolume(AudioVolume audioVolume) {
			this.audioVolume = audioVolume;
			return this;
		}

		public Builder withProfileImageUrl(String profileImageUrl) {
			this.profileImageUrl = profileImageUrl;
			return this;
		}

		public Channel build() {
			return new Channel(channelId, loginName, displayName, sortIndex, chatRole, chatlogLevel, greetingText, goodbyeText, returnText, audioPath, audioVolume, profileImageUrl);
		}
	}
}
