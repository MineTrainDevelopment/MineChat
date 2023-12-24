package de.minetrain.minechat.utils;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.obj.TitleBar;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.ChannelStatistics;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;

public class ChatMessage {
	private static final Logger logger = LoggerFactory.getLogger(ChatMessage.class);
	private TwitchMessage replyMessage;
	private final String message;
	private final String messageRaw;
	private final String senderNamem;
	private final ChannelTab channelTab;
	private boolean replyOveride;
	
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
			
			if(message.contains("{VIEWER}") || message.contains("{UPTIME}") || message.contains("{GAME}") || message.contains("{TITLE}") || message.contains("{TAGS}")){
				String channelId = channelTab.getConfigID();
				List<TwitchUserObj> liveUseres = TwitchManager.getLiveUseres(TwitchApiCallType.ID, channelId).stream()
						.filter(user -> user.getUserId().equals(channelId)).toList();
				
				if(!liveUseres.isEmpty()){
					TwitchUserObj channel = liveUseres.get(0);
					message = message
						.replace("{VIEWER}", String.valueOf(channel.getStreamViewer()))
						.replace("{UPTIME}", channel.getStreamLiveSince())
						.replace("{GAME}", channel.getStreamGame())
						.replace("{TITLE}", channel.getStreamTitle())
						.replace("{TAGS}", String.join(", ", channel.getStreamTags()));
				}
			}
			
			message = message
					.replace("{TIME}", localDateTime.format(DateTimeFormatter.ofPattern(Settings.timeFormat, locale)))
					.replace("{DATE}", localDateTime.format(DateTimeFormatter.ofPattern(Settings.dateFormat, locale)))
					.replace("{DAY}", localDateTime.format(DateTimeFormatter.ofPattern(Settings.dayFormat, locale)))
					.replace("{STREAMER}", "@"+channelTab.getChannelName())
					.replace("{MYSELF}", "@"+TwitchManager.ownerChannelName)
					.replace("{VIEWER}", "0")
					.replace("{UPTIME}", "0")
					.replace("{GAME}", "\"\"")
					.replace("{TITLE}", "\"\"")
					.replace("{TAGS}", "\"\"")
					.replace("{MY_MESSAGES}", ""+statistics.getTotalSelfMessages())
					.replace("{TOTAL_MESSAGES}", ""+statistics.getTotalMessages())
					.replace("{TOTAL_UNIQUE_MESSAGES}", ""+statistics.getTotalUniqueMessages())
					.replace("{TOTAL_SUBS}", ""+statistics.getTotalSubs())
					.replace("{TOTAL_RESUBS}", ""+statistics.getTotalResubs())
					.replace("{TOTAL_GIFTSUB}", ""+statistics.getTotalGiftSubs())
					.replace("{TOTAL_NEWSUB}", ""+statistics.getTotalNewSubs())
					.replace("{TOTAL_BITS}", ""+statistics.getTotalBits())
					.replace("{ClipBoard}", clipBoard)
					.replace("{CLIP_BOARD}", clipBoard)
					.replace("{Clip}", clipBoard)
					.replace("{CLIP}", clipBoard);
			

			//{C_TEST}
			//{C_D_TEST}
			//{C_123_TEST}
			//{COUNT_TEST}
			//{COUNT_DISPLAY_TEST}
			//{COUNT_123_TEST}
			//NOTE: 123 stands for any nummber to increase the value.
			
			if(message.contains("{C_") || message.contains("{COUNT_")){
				Map<String, Long> counterVariables = Arrays.stream(message.split(" "))
					.filter(word -> word.startsWith("{C_") || word.startsWith("{COUNT_"))
					.collect(Collectors.toMap(var -> var, var -> {
						return var.startsWith("{C_D_") || var.startsWith("{COUNT_DISPLAY_")
								? DatabaseManager.getCountVariableDatabase().getValue(var)
								: DatabaseManager.getCountVariableDatabase().increaseValue(var);
						},(existingValue, newValue) -> newValue
					));
				
				
				for(Entry<String, Long> entry : counterVariables.entrySet()){
					message = message.replace(entry.getKey(), String.valueOf(entry.getValue()));
				}
			}
			
			this.message = message;
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
	
	public boolean isReplyOveride(){
		return replyOveride;
	}
	
	public void overrideReplyMessage(TwitchMessage message){
		this.replyMessage = message;
		this.replyOveride = true;
	}
	

	
}
