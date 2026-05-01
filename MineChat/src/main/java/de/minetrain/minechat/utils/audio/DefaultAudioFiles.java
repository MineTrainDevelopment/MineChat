package de.minetrain.minechat.utils.audio;

public enum DefaultAudioFiles {
	MESSAGE_0("message0.mp3"),
	MESSAGE_1("message1.mp3"),
	MESSAGE_2("message2.mp3"),
	MESSAGE_3("message3.mp3"),
	MESSAGE_4("message4.mp3"),
	MESSAGE_5("message5.mp3"),
	MESSAGE_6("message6.mp3"),
	MESSAGE_7("message7.mp3"),
	MESSAGE_8("message8.mp3"),

	PING_0("ping0.mp3"),
	PING_1("ping1.mp3"),
	PING_2("ping2.mp3"),
	PING_3("ping3.mp3"),
	PING_4("ping4.mp3"),

	LIVE_0("live0.mp3"),
	LIVE_1("live1.mp3");

	private String resourceName;

	public String getResourceName(){
		return resourceName;
	}

	private DefaultAudioFiles(String resourceName) {
		this.resourceName = resourceName;
	}
}
