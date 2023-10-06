package de.minetrain.minechat.utils;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.obj.TitleBar;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.ChannelStatistics;
import de.minetrain.minechat.twitch.obj.TwitchMessage;

public class ChatMessage {
	private static final Logger logger = LoggerFactory.getLogger(ChatMessage.class);
	private final TwitchMessage replyMessage;
	private final String message;
	private final String messageRaw;
	private final String senderNamem;
	private final ChannelTab channelTab;
	
	public ChatMessage(ChannelTab tab, String senderNamem, String message) {
		this.replyMessage = tab.getChatWindow().replyMessage;
		this.senderNamem = senderNamem;
		this.channelTab = tab;
		this.messageRaw = message;
		
//		https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns
		if(message.contains("{")){
			LocalDateTime localDateTime = LocalDateTime.now();
			Locale locale = new Locale(System.getProperty("user.language"), System.getProperty("user.country"));
			ChannelStatistics statistics = TitleBar.currentTab.getStatistics();
			
			String clipBoard = "";
			try {
				clipBoard = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
			} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
				logger.info("Can´t readout the System ClipBoard. It may be empty.");
			} 
			
			this.message = message
					.replace("{TIME}", localDateTime.format(DateTimeFormatter.ofPattern(Settings.timeFormat, locale)))
					.replace("{DATE}", localDateTime.format(DateTimeFormatter.ofPattern(Settings.dateFormat, locale)))
					.replace("{DAY}", localDateTime.format(DateTimeFormatter.ofPattern(Settings.dayFormat, locale)))
					.replace("{STREAMER}", "@"+channelTab.getChannelName())
					.replace("{MYSELF}", "@"+TwitchManager.ownerChannelName)
					.replace("{VIEWER}", "{TODO - VIEWER}")
					.replace("{UPTIME}", "{TODO - UPTIME}")
					.replace("{GAME}", "{TODO - GAME}")
					.replace("{TITLE}", "{TODO - TITLE}")
					.replace("{MY_MESSAGES}", ""+statistics.getTotalSelfMessages())
					.replace("{TOTAL_MESSAGES}", ""+statistics.getTotalMessages())
					.replace("{TOTAL_SUBS}", ""+statistics.getTotalSubs())
					.replace("{TOTAL_RESUBS}", ""+statistics.getTotalResubs())
					.replace("{TOTAL_GIFTSUB}", ""+statistics.getTotalGiftSubs())
					.replace("{TOTAL_NEWSUB}", ""+statistics.getTotalNewSubs())
					.replace("{TOTAL_BITS}", ""+statistics.getTotalBits())
					.replace("{ClipBoard}", clipBoard)
					.replace("{CLIP_BOARD}", clipBoard)
					.replace("{Clip}", clipBoard)
					.replace("{CLIP}", clipBoard);
		}else{
			this.message = message;
		}
	}
	
	public TwitchMessage getReplyMessage() {
		return replyMessage;
	}

	public String getMessage() {
		return message;
	}

	public String getSenderName() {
		return senderNamem;
	}

	public String getSendToChannel() {
		return channelTab.getChannelName();
	}

	public ChannelTab getChannelTab() {
		return channelTab;
	}

	public String getMessageRaw() {
		return messageRaw;
	}


	
}
