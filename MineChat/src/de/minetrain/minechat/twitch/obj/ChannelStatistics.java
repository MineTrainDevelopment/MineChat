package de.minetrain.minechat.twitch.obj;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.serializer.persistence.types.Persister;

import com.github.twitch4j.chat.events.channel.CheerEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.common.enums.SubscriptionPlan;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.twitch.TwitchManager;

//dalay_stats.
// id, channel_id, unix_timestamp, messages, total_subs, resubs, gift_subs, new_subs, bits, follower

//user_stats
// channel_user_id, messages, gifted_subs, cheerd_bits
//  141456-54689,

public class ChannelStatistics {
	private final Map<String, UserStatistics> userStatistics = new HashMap<>();//channel_id
	private final Set<Integer> uniqueMessages = new HashSet<>();
	private long totalMessages = 0;
	private long totalSubs = 0;
	private long totalResubs = 0;
	private long totalGirftsubs = 0;
	private long totalNewSubs = 0;
	private long totalBits = 0;
	private String channelId;
	
	public ChannelStatistics(String channelId){
		this.channelId = channelId;
	}
	
	public void addMessage(String senderName, String senderId, String message) {
		totalMessages++;
		uniqueMessages.add(message.hashCode());
		userStatistics.computeIfAbsent(senderId, UserStatistics::new).increaseMessage();
	}

	public void addSub(SubscriptionEvent event) {
		totalSubs++;
		
		if(event.getGifted()){
			totalGirftsubs++;
			userStatistics.computeIfAbsent(event.getUser().getId(), UserStatistics::new).increaseSubs(event.getSubPlan());
		}else{
			if(event.getMonths()>1){totalResubs++;}else{totalNewSubs++;}
		}
	}

	public void addBits(CheerEvent event) {
		this.totalBits = this.totalBits+event.getBits();
		userStatistics.computeIfAbsent(event.getUser().getId(), UserStatistics::new).increaseBits(event.getBits());
	}

	
	
//	/**
//	 * 
//	 * @param commit weather the new database changes should be commited. 
//	 */
//	public void save(boolean commit){
//		DatabaseManager.getChannelStatistics().insert(this, channelId);
//		sendedMessages.clear();
//		giftedSubs.clear();
//		cheerdBits.clear();
//		totalMessages = 0;
//		totalSubs = 0;
//		totalResubs = 0;
//		totalNewSubs = 0;
//		totalBits = 0;
//		totalFollower = 0;
//		if(commit){
//			DatabaseManager.commit();
//		}
//	}
	
	public String getChannelId(){
		return channelId;
	}
	
	public long getSendedMessages() {
		return totalMessages;
	}

	public Long getSendedMessages(String userId) {
		return Optional.ofNullable(userStatistics.get(userId))
	        .map(UserStatistics::getSentMessages)
	        .orElse(0L);
	}
	
	/**
	 * Gets the total gifted subscriptions for each user across all subscription plans.
	 * @return Returns a map where keys are user IDs and values are the total gifted subscriptions of that user.
	 */
	public long getGiftedSubs() {
		return totalGirftsubs;
	}
	
	/**
	 * Gets the total gifted subscriptions for a specific user.
	 *
	 * @param userId The user ID for whom to retrieve gifted subscriptions.
	 * @return Total gifted subscriptions for the specified user.
	 */
	public Long getGiftedSubs(String userId) {
		return Optional.ofNullable(userStatistics.get(userId).getTotalGiftedSubs()).orElse(0L);
	}
	
	/**
	 * Gets the total gifted subscriptions for a specific subscription plan.
	 * <br>If the plan is {@link SubscriptionPlan#NONE}, returns the total across all plans.
	 *
	 * @param plan The subscription plan for which to retrieve total gifted subscriptions.
	 * @return Total amount of gifted subscriptions for the specified plan or across all plans.
	 */
	public Long getTotalGiftedSubs(SubscriptionPlan plan){
		return userStatistics.values().stream().map(stats -> stats.getGiftedSubs(plan)).collect(Collectors.summingLong(Long::longValue));
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
		return Optional.ofNullable(userStatistics.get(userId).getGiftedSubs(plan)).orElse(0L);
	}
	
	public long getCheerdBits() {
		return totalBits;
	}
	
	public Long getCheerdBits(String userId) {
		return Optional.ofNullable(userStatistics.get(userId).getCheerdBits()).orElse(0L);
	}
	
	public long getTotalSelfMessages() {
		String userId = TwitchManager.ownerTwitchUser.getUserId();
		return userStatistics.containsKey(userId) ? userStatistics.get(userId).getSentMessages() : 0l;
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
	
	public long getTotalUniqueMessages(){
		return uniqueMessages.size();
	}
	
	public void saveChannelStatistics(Persister persister){
		persister.store(this);
		persister.store(userStatistics);
		persister.store(uniqueMessages);
	}
	
	public static String getCurentTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(Settings.messageTimeFormat,
				new Locale(System.getProperty("user.language"), System.getProperty("user.country"))));
	}

}
