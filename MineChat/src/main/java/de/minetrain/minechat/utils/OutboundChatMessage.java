package de.minetrain.minechat.utils;

import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;

public class OutboundChatMessage {

	private final String message;
	private final String senderNamem;
	private final ChannelViewModel channelViewModel;
	private final String replyId;
	private long sendTime;

	public OutboundChatMessage(ChannelViewModel channelViewModel, String senderNamem, String message, String replyId) {
		this.channelViewModel = channelViewModel;
		this.message = message;
		this.senderNamem = senderNamem;
		this.replyId = replyId;
	}

	public String getMessage() {
		return message;
	}

	public String getSenderName() {
		return senderNamem;
	}

	public ChannelViewModel getChannelViewModel() {
		return channelViewModel;
	}

	public String getReplyId() {
		return replyId;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}
}
