package de.minetrain.minechat.features.messagehighlight;

public enum HighlightType {
	FIRST_MESSAGE("First Message"),
	GOODBYE_MESSAGE("Goodbye Message"),
	RETURN_MESSAGE("Return Message"),
	MODERATION("Moderation"),
	SUB("Subscription"),
	FOLLOW("Follow"),
	GIFT_SUB_SMALL("Gift Sub (Small)"),
	GIFT_SUB_LARGE("Gift Sub (Large)"),
	INDIVIDUAL_GIFT_SUB("Individual Gift Sub"),
	CHEER("Cheer"),
	MOD_ANNOUNCEMENT("Mod Announcement"),
	USER_REWARD("User Reward"),
	HIGHLIGHT("Highlight");

	private String displayName;

	HighlightType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
