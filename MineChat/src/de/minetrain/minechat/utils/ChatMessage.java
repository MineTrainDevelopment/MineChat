package de.minetrain.minechat.utils;

import com.github.twitch4j.chat.events.AbstractChannelMessageEvent;

import de.minetrain.minechat.gui.obj.ChannelTab;

public class ChatMessage {
	private final AbstractChannelMessageEvent messageEvent;
	private final String message;
	private final String senderNamem;
	private final ChannelTab channelTab;
	
	public ChatMessage(ChannelTab tab, String senderNamem, String message) {
		this.messageEvent = tab.getChatWindow().messageEvent;
		this.senderNamem = senderNamem;
		this.message = message;
		this.channelTab = tab;
	}
	
	public AbstractChannelMessageEvent getMessageEvent() {
		return messageEvent;
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
