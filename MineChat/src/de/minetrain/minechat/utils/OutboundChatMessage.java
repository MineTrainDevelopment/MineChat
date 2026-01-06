package de.minetrain.minechat.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;

public class OutboundChatMessage {
	private static final Logger logger = LoggerFactory.getLogger(OutboundChatMessage.class);
	private final String message;
	private final String messageRaw;
	private final String senderNamem;
	private final ChannelViewModel channelViewModel;
	private final String replyId;
	private long sendTime;

	public OutboundChatMessage(ChannelViewModel channelViewModel, String senderNamem, String message, String replyId) {
		this.channelViewModel = channelViewModel;
		this.messageRaw = message;
		this.senderNamem = senderNamem;
		this.replyId = replyId;

//		https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns
		if(message.contains("{")){
			LocalDateTime localDateTime = LocalDateTime.now();
			Locale locale = new Locale(System.getProperty("user.language"), System.getProperty("user.country"));

			message = message
					.replace("{TIME}", localDateTime.format(DateTimeFormatter.ofPattern(Settings.timeFormat, locale)))
					.replace("{DATE}", localDateTime.format(DateTimeFormatter.ofPattern(Settings.dateFormat, locale)))
					.replace("{DAY}", localDateTime.format(DateTimeFormatter.ofPattern(Settings.dayFormat, locale)));
			this.message = message;
		}else{
			this.message = message;
		}
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

	public String getMessageRaw() {
		return messageRaw;
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
