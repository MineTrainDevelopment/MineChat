package de.minetrain.minechat.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.minetrain.minechat.gui.obj.ChannelTab;

public class TwitchMessage {
	private final String message;
	private final String messageId;
	private final String channelId;
	private final String client_nonce;

	private String replyId;
	private String replyUser;
	
	private final String userName;
	private final String userColorCode;
	private final List<String> emotes;
	
	private final Long epochTime;
	private final ChannelTab parentTab;
	private final boolean dummy;

	public TwitchMessage(ChannelTab parentTab, Map<String, String> data, String message) {
		this.parentTab = parentTab;
		this.message = message;
		this.messageId = data.get("id");
		this.channelId = data.get("room-id");
		this.userName = data.get("display-name");
		this.client_nonce = data.get("client_nonce");
		this.epochTime = Long.parseLong(data.get("tmi-sent-ts"));
		this.userColorCode = (data.get("color") != null ? data.get("color") : "#ffffff");
		this.emotes = (data.get("emotes") != null ? Arrays.asList(data.get("emotes").split("/")) : null);
		this.replyId = (data.containsKey("reply-parent-msg-id") ? data.get("reply-parent-msg-id") : null);
		this.replyUser = (data.containsKey("reply-parent-display-name") ? data.get("reply-parent-display-name") : null);
		this.dummy = false;
	}
	
	public TwitchMessage(ChannelTab parentTab, String userName, String message) {
		this.parentTab = parentTab;
		this.message = message;
		this.messageId = null;
		this.channelId = null;
		this.userName = userName;
		this.client_nonce = null;
		this.epochTime = 0l;
		this.userColorCode = "#ffffff";
		this.emotes = null;
		this.replyId = null;
		this.replyUser = null;
		this.dummy = true;
	}


	public String getMessage() {
		return message;
	}

	public String getMessageId() {
		return messageId;
	}

	public String getReplyId() {
		return (replyId == null) ? messageId : replyId;
	}
	
	public String getReplyUser() {
		return (replyUser == null) ? userName : replyUser;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserColorCode() {
		return userColorCode;
	}

	public List<String> getEmotes() {
		return emotes;
	}

	public Long getEpochTime() {
		return epochTime;
	}

	public ChannelTab getParentTab() {
		return parentTab;
	}
	
	public String getClient_nonce() {
		return client_nonce;
	}
	
	public String getChannelId() {
		return channelId;
	}
	
	public boolean isNull() {
		return dummy;
	}
	
	public boolean isReply() {
		return replyId != null;
	}
	
	public void setReply(String messageId, String userName) {
		this.replyId = messageId;
		this.replyUser = userName;
	}
	
	@Override
	public String toString() {
		return userName+": "+message;
	}
}
