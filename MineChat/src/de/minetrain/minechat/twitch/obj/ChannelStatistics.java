package de.minetrain.minechat.twitch.obj;

import java.util.HashMap;
import java.util.Map;

import com.github.twitch4j.chat.events.channel.SubscriptionEvent;

import de.minetrain.minechat.twitch.TwitchManager;

public class ChannelStatistics {
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
	public ChannelStatistics(){}
	
	public void setStreamStartupTime(long streamStartupTime) {
		this.streamStartupTime = streamStartupTime;
	}

	public void addMessage(String senderName) {
		totalMessages++;
		sendedMessages.put(senderName, sendedMessages.containsKey(senderName) ? sendedMessages.get(senderName)+1 : 1l);
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
	
}
