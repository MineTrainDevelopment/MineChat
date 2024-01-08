package de.minetrain.minechat.twitch.obj;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.twitch4j.chat.events.channel.CheerEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.common.enums.SubscriptionPlan;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.twitch.TwitchManager;

//dalay_stats.
// id, channel_id, unix_timestamp, messages, total_subs, resubs, gift_subs, new_subs, bits, follower

//user_stats
// channel_user_id, messages, gifted_subs, cheerd_bits
//  141456-54689,

public class ChannelStatistics {
	private final Map<String, Long> messageTimestamps = new HashMap<>();//User_name
	private final Map<String, Long> previousMessageTimestamps = new HashMap<>();//User_name
	private final Map<String, Long> sendedMessages = new HashMap<>(); //User_id, value
	private final Map<SubscriptionPlan, Map<String, Long>> giftedSubs = new HashMap<>(); //subPlan - User_id, value
	private final Map<String, Long> cheerdBits = new HashMap<>(); //User_id, value
	private final Set<Integer> uniqueMessages = new HashSet<>();
//	private long streamStartupTime = 0;
	private long totalMessages = 0;
	private long totalSubs = 0;
	private long totalResubs = 0;
	private long totalNewSubs = 0;
	private long totalBits = 0;
	private long totalFollower = 0;
	private String channelId;
	
	public ChannelStatistics(String channelId){
		this.channelId = channelId;
	}
	
//	public void setStreamStartupTime(long streamStartupTime) {
//		this.streamStartupTime = streamStartupTime;
//	}

	public void addMessage(String senderName, String senderId, String message) {
		totalMessages++;
		
		if(!sendedMessages.containsKey(senderId)){
//			Add user to username suggestion?
//			parentTab.getChatWindow().chatStatusPanel.getinputArea().addToDictionary(new SuggestionObj("@"+senderName, null));
		}
		
		sendedMessages.put(senderId, sendedMessages.containsKey(senderId) ? sendedMessages.get(senderId)+1 : 1l);
		uniqueMessages.add(message.hashCode());
		messageTimestamps.compute(senderName, (key, value) -> {
		    if (value != null) {
		        previousMessageTimestamps.put(key, value);
		    }
		    return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
		});
	}

	public void addSub(SubscriptionEvent event) {
		totalSubs++;
		
		if(event.getGifted()){
			giftedSubs.computeIfAbsent(event.getSubPlan(), key -> new HashMap<>()).compute(event.getUser().getId(), (key, value) -> value == null ? 1 : value+1);
		}else{
			if(event.getMonths()>1){totalResubs++;}else{totalNewSubs++;}
		}
	}

	public void addBits(CheerEvent event) {
		this.totalBits = this.totalBits+event.getBits();
		cheerdBits.put(event.getUser().getId(), cheerdBits.containsKey(event.getUser().getId()) ? cheerdBits.get(event.getUser().getId())+event.getBits() : event.getBits());
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
		
		DatabaseManager.getChannelStatistics().insert(this, channelId);
		sendedMessages.clear();
		giftedSubs.clear();
		cheerdBits.clear();
		totalMessages = 0;
		totalSubs = 0;
		totalResubs = 0;
		totalNewSubs = 0;
		totalBits = 0;
		totalFollower = 0;
		if(commit){
			DatabaseManager.commit();
		}
	}
	
	public String getChannelId(){
		return channelId;
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
	
	/**
	 * Gets the total gifted subscriptions for each user across all subscription plans.
	 * @return Returns a map where keys are user IDs and values are the total gifted subscriptions of that user.
	 */
	public Map<String, Long> getGiftedSubs() {
		return giftedSubs.values().stream()
				.flatMap(map -> map.entrySet().stream())
				.collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingLong(Map.Entry::getValue)));
	}
	
	/**
	 * Gets the total gifted subscriptions for a specific user.
	 *
	 * @param userId The user ID for whom to retrieve gifted subscriptions.
	 * @return Total gifted subscriptions for the specified user.
	 */
	public Long getGiftedSubs(String userId) {
		return giftedSubs.values().stream()
				.flatMap(map -> map.entrySet().stream())
				.filter(entry -> userId.equals(entry.getKey()))
				.collect(Collectors.summingLong(Map.Entry::getValue));
	}
	
	/**
	 * Gets the total gifted subscriptions for a specific subscription plan.
	 * <br>If the plan is {@link SubscriptionPlan#NONE}, returns the total across all plans.
	 *
	 * @param plan The subscription plan for which to retrieve total gifted subscriptions.
	 * @return Total amount of gifted subscriptions for the specified plan or across all plans.
	 */
	public Long getTotalGiftedSubs(SubscriptionPlan plan){
		if(plan.equals(SubscriptionPlan.NONE)){
			return getGiftedSubs().values().stream().collect(Collectors.summingLong(Long::longValue));
		}
		
		giftedSubs.computeIfAbsent(plan, k -> new HashMap<>());
		return giftedSubs.get(plan).values().stream().collect(Collectors.summingLong(Long::longValue));
	}

	/**
	 * Gets the total gifted subscriptions for a specific subscription plan and user.
	 * If the plan is SubscriptionPlan.NONE, returns the total for the specified user across all plans.
	 *
	 * @param plan   The subscription plan for which to retrieve total gifted subscriptions.
	 * @param userId The user ID for whom to retrieve gifted subscriptions.
	 * @return Total amount of gifted subscriptions for the specified plan and user or across all plans for the user.
	 */
	public Long getTotalGiftedSubs(SubscriptionPlan plan, String userId){
		if(plan.equals(SubscriptionPlan.NONE)){
			return getGiftedSubs(userId);
		}
		
		giftedSubs.computeIfAbsent(plan, k -> new HashMap<>());
		return giftedSubs.get(plan).entrySet().stream()
				.filter(entry -> userId.equals(entry.getKey()))
				.collect(Collectors.summingLong(Map.Entry::getValue));
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
	
	/**
	 * NOTE: Timestamps are in {@link ZoneOffset#UTC}
	 * @return
	 */
	public Map<String, Long> getMessageTimestamps() {
		return messageTimestamps;
	}
	
	/**
	 * NOTE: Timestamps are in {@link ZoneOffset#UTC}
	 * @return
	 */
	public Map<String, Long> getPreviousMessageTimestamps() {
		return previousMessageTimestamps;
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
		return uniqueMessages.size();
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
