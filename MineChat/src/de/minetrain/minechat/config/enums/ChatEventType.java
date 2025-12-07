package de.minetrain.minechat.config.enums;

public enum ChatEventType {
	NEW_SUB("New Subscriber"),
	RE_SUB("Resubscriber"),
	GIFT_SUB("Gifted Subscription"),
	
	CHEERD_BITS("Cheered bits"),
	
	RAID_START("Raid started"),
	RAID_CANCEL("Raid cancelled"),
	INCUMING_RAID("Incoming Raid"),
	
	STREAM_UP("Stream start"),
	STREAM_DOWN("Stream stopped"),
	
	MOD_ACTIONS("{ACTION}"),
	
	USER_REWARDS("{ACTION}"),
	
	SLOW_CHAT("Changed Slowchat"),

	INCOMING_ANNOUNCEMENT("Announcement"),
	INCOMING_CHANNEL_POINTS("{ACTION}"),
	
	INCOMING_MESSAGE("New Message"),
	INCOMING_MESSAGE_HIGHLITE("Highlite");
	
	public String getDisplayName(){return displayName;}
	private String displayName;
	
	private ChatEventType(String displayName) {
		this.displayName = displayName;
	}

}
