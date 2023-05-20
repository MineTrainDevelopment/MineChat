package de.minetrain.minechat.twitch;

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.AbstractChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.BitsBadgeEarnedEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.CheerEvent;
import com.github.twitch4j.chat.events.channel.ClearChatEvent;
import com.github.twitch4j.chat.events.channel.DeleteMessageEvent;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.github.twitch4j.chat.events.channel.GiftSubscriptionsEvent;
import com.github.twitch4j.chat.events.channel.ModAnnouncementEvent;
import com.github.twitch4j.chat.events.channel.RaidCancellationEvent;
import com.github.twitch4j.chat.events.channel.RaidEvent;
import com.github.twitch4j.chat.events.channel.RewardGiftEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.chat.events.channel.UserBanEvent;
import com.github.twitch4j.chat.events.channel.UserTimeoutEvent;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;

import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.obj.TitleBar;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.TwitchChatUser;
import de.minetrain.minechat.twitch.obj.TwitchUserStatistics;
import de.minetrain.minechat.utils.Settings;

/**
 * A listener for Twitch events such as streams going live or offline and channel messages.
 * Provides methods to handle these events and execute commands.
 * 
 * @author MineTrain/Justin
 * @since 28.04.2023
 * @version 1.1
 */
public class TwitchListner {
	private static final Logger logger = LoggerFactory.getLogger(TwitchListner.class);
	
	/**
	 * Handles the event when a stream goes live and sends a message to the channel with a randomized stream-up sentence.
	 * @param event The {@link ChannelGoLiveEvent} object containing information about the stream.
	 */
	@EventSubscriber
	public void onStreamUp(ChannelGoLiveEvent event){
		logger.info("Twtich livestram startet: "+event.getStream().getUserName()+" | "+event.getStream().getViewerCount()+" | "+event.getStream().getTitle());
	}

	/**
	 * Handles the event when a stream goes offline and sends a message to the channel with a randomized stream-down sentence.
	 * @param event The {@link ChannelGoOfflineEvent} object containing information about the channel.
	 */
	@EventSubscriber
	public void onStreamDown(ChannelGoOfflineEvent event){
		logger.info("Twtich livestram Offline: "+event.getChannel().getName());
	}
	
	/**
	 * Handles the event when a message is sent in the channel and executes the command if the cooldown time has elapsed.
	 * @param event The {@link ChannelMessageEvent} object containing information about the message.
	 */
	@EventSubscriber
	public void onAbstractChannelMessage(AbstractChannelMessageEvent event){
		logger.info("User: "+event.getUser().getName()+" | Message --> "+event.getMessage());
		ChannelTab channelTab = getCurrentChannelTab(event.getChannel());
		
		if(channelTab == null){
			return;
		}

		TwitchChatUser twitchUser = TwitchUserStatistics.getTwitchUser(event.getUser().getId());
		if(twitchUser == null){
			channelTab.getChatWindow().displayMessage(event.getMessage(), event.getUser().getName(), Color.WHITE, event);
		}else{
			channelTab.getChatWindow().displayMessage(event.getMessage(), twitchUser.getUserName(), twitchUser.getColor(), event);
		}
	}

	private ChannelTab getCurrentChannelTab(EventChannel eventChannel) {
		TitleBar titleBar = Main.MAIN_FRAME.getTitleBar();
		ChannelTab channelTab = null;
		if(titleBar.getMainTab().getConfigID().equals(eventChannel.getId())){
			channelTab = titleBar.getMainTab();
		}
		
		if(titleBar.getSecondTab().getConfigID().equals(eventChannel.getId())){
			channelTab = titleBar.getSecondTab();
		}
		
		if(titleBar.getThirdTab().getConfigID().equals(eventChannel.getId())){
			channelTab = titleBar.getThirdTab();
		}
		
		return channelTab;
	}
	
    @EventSubscriber
    public void onFollow(FollowEvent event) {
    	if(!Settings.displaySubs_Follows){return;}
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
			.displaySystemInfo("New follower", "@"+event.getUser().getName()+" just followed!", unimportant);
    	
    }

    @EventSubscriber
    public void onCheer(CheerEvent event) {
    	if(!Settings.displayBitsCheerd){return;}
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
			.displaySystemInfo("New follower", "@"+event.getUser().getName()+" just followed!", unimportant);
    }

    @EventSubscriber
    public void onSub(SubscriptionEvent event) {
        if(!event.getGifted() && Settings.displaySubs_Follows) {
        	getCurrentChannelTab(event.getChannel()).getChatWindow()
    			.displaySystemInfo("New subscription", "@"+event.getUser().getName()+" just subscribed!",
    					(event.getMonths()>12) ? spendingSmall : unimportant);
        }else if(!Settings.displayGiftedSubscriptions){
        	getCurrentChannelTab(event.getChannel()).getChatWindow()
    			.displaySystemInfo("New subscription", "@"+event.getGiftedBy().getName()
    					+" just gifted a sub to @"+event.getUser().getName()
    					+"! ("+event.getGiftMonths()+".months)", unimportant);
        }
    }

    @EventSubscriber
    public void onGiftSubscriptions(GiftSubscriptionsEvent event) { //ONLY Random sub gifed
    	if(!Settings.displayGiftedSubscriptions){return;}
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
			.displaySystemInfo("Subscription gift", 
				"From: "+event.getUser().getName()
				+" \nTier: "+event.getSubscriptionPlan()
				+" \nAmound: "+event.getCount()
				+" \nThis user gifted "+event.getTotalCount()+" subs on this Channel!"
				, (event.getCount() > 5) ? spendingBig : spendingSmall);
    }
    
//    @EventSubscriber
//    public void on(ExtendSubscriptionEvent event){
//    	getCurrentChannelTab(event.getChannel()).getChatWindow()
//			.displaySystemInfo("Subscription extend", "@"+event.getUser().getName()+" just extend his subscription to"+event.getCumulativeMonths()+".months!", unimportant);
//    }  
//
//    @EventSubscriber
//    public void on(PrimeSubUpgradeEvent event){ //Prime to paid
//    	getCurrentChannelTab(event.getChannel()).getChatWindow()
//			.displaySystemInfo("Subscription Upgraded", "@"+event.getUser().getName()+" just Upgrade his subscription from prime to Tier "+event.getSubscriptionPlan()+"!", unimportant);
//    }
//
//    @EventSubscriber
//    public void on(GiftedMultiMonthSubCourtesyEvent event){
//    	
//    }
//
//    @EventSubscriber
//    public void on(GiftSubUpgradeEvent event){ //from gift to paid
//    	
//    }

    @EventSubscriber
    public void onModAnnouncement(ModAnnouncementEvent event){
    	if(!Settings.displayAnnouncement){return;}
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
			.displaySystemInfo("Announcement", "From: "+event.getAnnouncer().getName()+" \nMessage: "+event.getMessage(), announcement);
    }

    @EventSubscriber
    public void onIncumingRaid(RaidEvent event){
    	if(!Settings.displayAnnouncement){return;}
//    	getCurrentChannelTab(event.getChannel()).getChatWindow()
//			.displaySystemInfo("Incuming raid", "@"+event.getRaider().getName()+" is raiding with "+event.getViewers()+" raiders!", announcement);
    	
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
			.displaySystemInfo("Incuming raid", "Frome: "+event.getRaider().getName()+" \nViewers: "+event.getViewers(), announcement);
    }
    
    @EventSubscriber
    public void onRewardGift(RewardGiftEvent event){ //Someone got a reward
    	if(!Settings.displayUserRewards){return;}
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
			.displaySystemInfo("Reward granted ", event.getUser().getName()+" got a "+event.getTriggerType()+ " reward", userReward);
    	
    }
    
    @EventSubscriber
    public void onBitsBadgeEarned(BitsBadgeEarnedEvent event){
    	if(!Settings.displayUserRewards){return;}
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
			.displaySystemInfo("BitBadge Earned ", event.getUser().getName()+" got a new bit badge! -> "+event.getBitsThreshold(), userReward);
    }

    @EventSubscriber
    public void onClearChat(ClearChatEvent event){
    	if(!Settings.displayModActions){return;}
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
			.displaySystemInfo("Chat cleared", "All chat-messages got cleared!", moderationColor);
    }

    @EventSubscriber
    public void onDeleteMessage(DeleteMessageEvent event){
    	if(!Settings.displayModActions){return;}
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
    		.displaySystemInfo("Message deleted", "@"+event.getUserName()+": "+event.getMessage(), moderationColor);
    }

    @EventSubscriber
    public void onRaidCancellation(RaidCancellationEvent event){
    	if(!Settings.displayModActions){return;}
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
			.displaySystemInfo("Raid cancelled", "The current raid got cancelled!", moderationColor);
    }

    @EventSubscriber
    public void onUserBan(UserBanEvent event){
    	if(!Settings.displayModActions){return;}
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
    		.displaySystemInfo("User banned", "@"+event.getUser().getName()+" got banned!", moderationColor);
    }

    @EventSubscriber
    public void onUserTimeout(UserTimeoutEvent event){
    	if(!Settings.displayModActions){return;}
//    	getCurrentChannelTab(event.getChannel()).getChatWindow()
//			.displaySystemInfo("User Timeout", "@"+event.getUser().getName()+" got a timeout for "+event.getDuration()+".sec! Reason: "+event.getReason(), moderationColor);
    	
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
			.displaySystemInfo("User Timeout", "User: "+event.getUser().getName()+" g\nDuration: "+event.getDuration()+".sec! \nReason: "+event.getReason(), moderationColor);
    }


//    @EventSubscriber
//    public void on( event){
//    	
//    }

    private static final Color unimportant = Color.GRAY;
    private static final Color moderationColor = Color.CYAN;
    private static final Color spendingSmall = Color.ORANGE;
    private static final Color spendingBig = Color.YELLOW;
    private static final Color announcement = Color.GREEN;
    private static final Color userReward = Color.BLUE;
}

//Moderator actions - Cyan
//Spending < 100 bits | < 4 subs  - Orange
//Spending > 100 bits | > 4 Subs  -  Yellow
//Announcement  -  Green
//User Reward -  Blue
