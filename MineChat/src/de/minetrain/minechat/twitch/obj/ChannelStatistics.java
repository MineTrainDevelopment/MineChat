package de.minetrain.minechat.twitch.obj;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.github.twitch4j.chat.events.channel.CheerEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.obj.chat.userinput.textarea.SuggestionObj;
import de.minetrain.minechat.gui.obj.messages.MessageComponent;
import de.minetrain.minechat.twitch.TwitchManager;

//dalay_stats.
// id, channel_id, unix_timestamp, messages, total_subs, resubs, gift_subs, new_subs, bits, follower

//user_stats
// channel_user_id, messages, gifted_subs, cheerd_bits
//  141456-54689,

public class ChannelStatistics {
	private final Map<String, Long> messageTimestamps = new HashMap<>();//User_name
	private final Map<String, Long> sendedMessages = new HashMap<>(); //User_id, value
	private final Map<String, Long> giftedSubs = new HashMap<>(); //User_id, value
	private final Map<String, Long> cheerdBits = new HashMap<>(); //User_id, value
	private long uniqueMessages = 0;
//	private long streamStartupTime = 0;
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
	
//	public void setStreamStartupTime(long streamStartupTime) {
//		this.streamStartupTime = streamStartupTime;
//	}

	public void addMessage(String senderName, String senderId) {
		totalMessages++;
		
		if(!sendedMessages.containsKey(senderId)){
			parentTab.getChatWindow().chatStatusPanel.getinputArea().addToDictionary(new SuggestionObj("@"+senderName, null));
		}
		
		sendedMessages.put(senderId, sendedMessages.containsKey(senderId) ? sendedMessages.get(senderId)+1 : 1l);
		messageTimestamps.put(senderName, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
	}

	public void addSub(SubscriptionEvent event) {
		totalSubs++;
		
		if(event.getGifted()){
			totalGiftSubs++;
			giftedSubs.put(event.getGiftedBy().getId(), giftedSubs.containsKey(event.getGiftedBy().getId()) ? giftedSubs.get(event.getGiftedBy().getId())+1 : 1);
		}else{
			if(event.getMonths()>1){totalResubs++;}else{totalNewSubs++;}
		}
	}

	public void addBits(CheerEvent event) {
		this.totalBits = this.totalBits+event.getBits();
		cheerdBits.put(event.getUser().getId(), giftedSubs.containsKey(event.getUser().getId()) ? giftedSubs.get(event.getUser().getId())+event.getBits() : event.getBits());
	}

	public void addFollower() {
		totalFollower++;
	}
	
	public boolean isEmpty(){
		return totalMessages+totalSubs+totalBits+totalFollower == 0;
	}
	
	
	/**
	 * 
	 * @param commit weather the new database changes should be commited. 
	 */
	public void save(boolean commit){
		if(isEmpty()){
			return;
		}
		
		DatabaseManager.getChannelStatistics().insert(this, parentTab.getConfigID());
		sendedMessages.clear();
		giftedSubs.clear();
		cheerdBits.clear();
		totalMessages = 0;
		totalSubs = 0;
		totalResubs = 0;
		totalGiftSubs = 0;
		totalNewSubs = 0;
		totalBits = 0;
		totalFollower = 0;
		if(commit){
			DatabaseManager.commit();
		}
	}
	
	public String getChannelId(){
		return parentTab.getConfigID();
	}
	
	public Map<String, Long> getSendedMessages() {
		return sendedMessages;
	}

	public Long getSendedMessages(String userId) {
		if(sendedMessages.containsKey(userId)){
			return sendedMessages.get(userId);
		}
		return 0l;
	}
	
	public Map<String, Long> getGiftedSubs() {
		return giftedSubs;
	}
	
	public Long getGiftedSubs(String userId) {
		if(giftedSubs.containsKey(userId)){
			return giftedSubs.get(userId);
		}
		return 0l;
	}
	
	public Map<String, Long> getCheerdBits() {
		return cheerdBits;
	}
	
	public Long getCheerdBits(String userId) {
		if(cheerdBits.containsKey(userId)){
			return cheerdBits.get(userId);
		}
		return 0l;
	}
	
	public Map<String, Long> getMessageTimestamps() {
		return messageTimestamps;
	}
	
//	public long getStreamStartupTime() {
//		return streamStartupTime;
//	}
	
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
	
	public long getTotalUniqueMessages(){
		return uniqueMessages + MessageComponent.getDocumentCacheSize(getChannelId());
	}
	
	public void updateUniqueMessages(long count){
		uniqueMessages = uniqueMessages + count;
	}
	
//	Das hier muss noch in die @user auswahl.
	public String getPresenceStatus(String userName){
		Long epochTime = messageTimestamps.get(userName);
		
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
