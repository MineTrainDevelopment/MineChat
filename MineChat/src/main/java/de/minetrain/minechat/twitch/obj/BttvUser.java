package de.minetrain.minechat.twitch.obj;

import java.util.List;

public class BttvUser {

	private String id;
	private List<BttvEmote> channelEmotes;
	private List<BttvEmote> sharedEmotes;
	private String message;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<BttvEmote> getChannelEmotes() {
		return channelEmotes;
	}

	public void setChannelEmotes(List<BttvEmote> channelEmotes) {
		this.channelEmotes = channelEmotes;
	}

	public List<BttvEmote> getSharedEmotes() {
		return sharedEmotes;
	}

	public void setSharedEmotes(List<BttvEmote> sharedEmotes) {
		this.sharedEmotes = sharedEmotes;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
