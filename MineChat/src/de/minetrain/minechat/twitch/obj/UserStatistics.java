package de.minetrain.minechat.twitch.obj;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.slf4j.LoggerFactory;

import com.github.twitch4j.common.enums.SubscriptionPlan;

public class UserStatistics {
	private long lastMessageTimestamps = 0;
	private long previousLastMessageTimestamps = 0;
	
	private long cheerdBits = 0;
	private long sentMessages = 0;
	private long giftedSubs_1 = 0;
	private long giftedSubs_2 = 0;
	private long giftedSubs_3 = 0;
	
	public UserStatistics(String userId){} //constructor for UserStatistics::new
	
	public void increaseMessage(){
		sentMessages++;
		previousLastMessageTimestamps = lastMessageTimestamps;
		lastMessageTimestamps = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
	}
	
	public void increaseBits(long amound){
		cheerdBits = cheerdBits + amound;
	}
	
	public void increaseSubs(SubscriptionPlan plan){
		switch (plan) {
			case TIER1: giftedSubs_1++; break;
			case TIER2: giftedSubs_2++; break;
			case TIER3: giftedSubs_3++; break;
			default: LoggerFactory.getLogger(UserStatistics.class)
				.warn("Unexpected value:", new IllegalArgumentException(plan.name()+" - is a new subscription plan, thas currently not suportet.")); 
				break;
		}
	}
	
	

	public long getLastMessageTimestamps() {
		return lastMessageTimestamps;
	}

	public long getPreviousLastMessageTimestamps() {
		return previousLastMessageTimestamps;
	}

	public long getCheerdBits() {
		return cheerdBits;
	}

	public long getSentMessages() {
		return sentMessages;
	}

	/**
	 * The summ of all sub gifts across all tiers.
	 * @return
	 */
	public Long getTotalGiftedSubs() {
		return giftedSubs_1 + giftedSubs_2 + giftedSubs_3;
	}

	/**
	 * NOTE: {@link SubscriptionPlan#NONE} returns all sub plans combined.
	 * @param plan
	 * @return
	 */
	public Long getGiftedSubs(SubscriptionPlan plan) {
		switch (plan) {
			case TIER1: return giftedSubs_1;
			case TIER2: return giftedSubs_2;
			case TIER3: return giftedSubs_3;
			default: return getTotalGiftedSubs();
		}
	}

}
