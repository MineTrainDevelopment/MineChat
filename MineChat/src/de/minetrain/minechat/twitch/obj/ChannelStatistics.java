package de.minetrain.minechat.twitch.obj;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.github.twitch4j.chat.events.channel.SubscriptionEvent;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.obj.chat.userinput.textarea.SuggestionObj;
import de.minetrain.minechat.twitch.TwitchManager;

public class ChannelStatistics {
	private final Map<String, Long> messageTimestamps = new HashMap<>();
	private final Map<String, Long> sendedMessages = new HashMap<>();
	private final Map<String, Long> giftedSubs = new HashMap<>();
	private final Map<String, Long> cheerdBits = new HashMap<>();
	private long streamStartupTime = 0;
	private long totalMessages = 0;
	private long totalSubs = 0;
	private long totalResubs = 0;
	private long totalGiftSubs = 0;
	private long totalNewSubs = 0;
	private long totalBits = 0;
	private long totalFollower = 0;
	private ChannelTab parentTab;
	
	public ChannelStatistics(ChannelTab parentTab){
		this.parentTab = parentTab;
	}
	
	public void setStreamStartupTime(long streamStartupTime) {
		this.streamStartupTime = streamStartupTime;
	}

	public void addMessage(String senderName) {
		totalMessages++;
		
		if(!sendedMessages.containsKey(senderName)){
			parentTab.getChatWindow().chatStatusPanel.getinputArea().addToDictionary(new SuggestionObj("@"+senderName, null));
		}
		
		sendedMessages.put(senderName, sendedMessages.containsKey(senderName) ? sendedMessages.get(senderName)+1 : 1l);
		getMessageTimestamps().put(senderName, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
	}

	public void addSub(SubscriptionEvent event) {
		totalSubs++;
		
		if(event.getGifted()){
			totalGiftSubs++;
			giftedSubs.put(event.getGiftedBy().getName(), giftedSubs.containsKey(event.getGiftedBy().getName()) ? giftedSubs.get(event.getGiftedBy().getName())+1 : 1);
		}else{
			if(event.getMonths()>1){totalResubs++;}else{totalNewSubs++;}
		}
	}

	public void addBits(long totalBits) {
		this.totalBits = this.totalBits+totalBits;
	}

	public void addFollower() {
		totalFollower++;
	}
	
	
	
	
	
	public Map<String, Long> getSendedMessages() {
		return sendedMessages;
	}
	
	public Map<String, Long> getGiftedSubs() {
		return giftedSubs;
	}
	
	public Map<String, Long> getCheerdBits() {
		return cheerdBits;
	}
	
	public Map<String, Long> getMessageTimestamps() {
		return messageTimestamps;
	}
	
	public long getStreamStartupTime() {
		return streamStartupTime;
	}
	
	public long getTotalSelfMessages() {
		return sendedMessages.containsKey(TwitchManager.ownerChannelName) ? sendedMessages.get(TwitchManager.ownerChannelName) : 0l;
	}
	
	public long getTotalMessages() {
		return totalMessages;
	}
	
	public long getTotalSubs() {
		return totalSubs;
	}
	
	public long getTotalResubs() {
		return totalResubs;
	}
	
	public long getTotalGiftSubs() {
		return totalGiftSubs;
	}
	
	public long getTotalNewSubs() {
		return totalNewSubs;
	}
	
	public long getTotalBits() {
		return totalBits;
	}

	public long getTotalFollower() {
		return totalFollower;
	}
	
//	Das hier muss noch in die @user auswahl.
	public String getPresenceStatus(String userName){
		Long epochTime = getMessageTimestamps().get(userName);
		
		if(epochTime == null){
			return null;
		}
		
		Long timeDifference = (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - epochTime) / 60 ; // min

		if(timeDifference > 60){
			return "Extended-Away";
		}
		
		if(timeDifference > 30){
			return "Away";
		}
		
		if(timeDifference > 15){
			return "Lurk";
		}
		
		return null;
	}
	
	public static String getCurentTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(Settings.messageTimeFormat,
				new Locale(System.getProperty("user.language"), System.getProperty("user.country"))));
	}

}
