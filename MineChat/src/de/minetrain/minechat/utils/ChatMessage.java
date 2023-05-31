package de.minetrain.minechat.utils;

import de.minetrain.minechat.gui.obj.ChannelTab;

public class ChatMessage {
	private final TwitchMessage replyMessage;
	private final String message;
	private final String senderNamem;
	private final ChannelTab channelTab;
	
	public ChatMessage(ChannelTab tab, String senderNamem, String message) {
		this.replyMessage = tab.getChatWindow().replyMessage;
		this.senderNamem = senderNamem;
		this.message = message;
		this.channelTab = tab;
	}
	
	public TwitchMessage getReplyMessage() {
		return replyMessage;
	}

	public String getMessage() {
		return message;
	}

	public String getSenderNamem() {
		return senderNamem;
	}

	public String getSendToChannel() {
		return channelTab.getChannelName();
	}

	public ChannelTab getChannelTab() {
		return channelTab;
	}


	
}
