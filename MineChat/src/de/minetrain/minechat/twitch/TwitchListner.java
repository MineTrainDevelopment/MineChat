package de.minetrain.minechat.twitch;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.AbstractChannelMessageEvent;
import com.github.twitch4j.chat.events.ChatConnectionStateEvent;
import com.github.twitch4j.chat.events.channel.BitsBadgeEarnedEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.ChannelModEvent;
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

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.obj.ChatStatusPanel;
import de.minetrain.minechat.gui.obj.ChatWindowMessageComponent;
import de.minetrain.minechat.gui.obj.TitleBar;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.obj.buttons.MineButton;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.TwitchMessage;

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
	private static final Dimension buttonSize = new Dimension(28, 28);
	
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
		
//		System.out.println(event.getMessageEvent().getTagValue("badges"));
//		System.out.println(event.getPermissions());
//		BROADCASTER
//		MODERATOR
//		VIP
//		FOUNDER		frist sub on channel
		
		if(channelTab == null){
			return;
		}
		
		channelTab.getStatistics().addMessage(event.getUser().getName());
		TwitchMessage twitchMessage = new TwitchMessage(channelTab, event.getMessageEvent(), event.getMessage());
		
		if(event.getUser().getName().equals(TwitchManager.ownerChannelName)){
    		channelTab.getChatWindow().chatStatusPanel.getMessageHistory().addSendedMessages(event.getMessage());
    		MessageManager.setLastMessage(event.getMessage());
    		
			String[] splitMessage = event.getMessage().split(" ");
			List<String> words = Arrays.asList(splitMessage);
			words.forEach(word -> {
//				System.out.println("Word -> "+word);
				if(word.startsWith("@")){
					channelTab.getChatWindow().greetingsManager.setMentioned(word.toLowerCase().replace("@", ""));
				}
			});
		}

		channelTab.getChatWindow().displayMessage(twitchMessage);
	}
	
	
    @EventSubscriber
    public void onFollow(FollowEvent event) {
    	ChannelTab currentChannelTab = getCurrentChannelTab(event.getChannel());
    	currentChannelTab.getStatistics().addFollower();
    	if(!Settings.displayFollows.isActive()){return;}
    	
    	currentChannelTab.getChatWindow()
			.displaySystemInfo("New follower", "@"+event.getUser().getName()+" just followed!", Settings.displayFollows.getColor(),
					getButton(currentChannelTab, Main.TEXTURE_MANAGER.getWaveButton(), "%GREET%"+event.getUser().getName(), "Say hello to "+event.getUser().getName()));
    }
    
    @EventSubscriber
    public void onCheer(CheerEvent event) {
    	ChannelTab currentChannelTab = getCurrentChannelTab(event.getChannel());
    	currentChannelTab.getStatistics().addBits(event.getBits());
    	if(!Settings.displayBitsCheerd.isActive()){return;}

    	currentChannelTab.getChatWindow()
			.displaySystemInfo("Bit cheer", "@"+event.getUser().getName()+" just cheered "+event.getBits()+" bits!\n"+event.getMessage(), Settings.displayBitsCheerd.getColor(),
					getButton(currentChannelTab, Main.TEXTURE_MANAGER.getWaveButton(), "%LOVE%", "Send some love!"));
    }

    @EventSubscriber
    public void onSub(SubscriptionEvent event) {
    	ChannelTab currentChannelTab = getCurrentChannelTab(event.getChannel());
    	currentChannelTab.getStatistics().addSub(event);
    	
        if(!event.getGifted() && Settings.displaySubs.isActive()) {
        	String message = (event.getMessage().isEmpty()) ? "" : event.getMessage().get();
			currentChannelTab.getChatWindow().displaySystemInfo("New subscription", "@"+event.getUser().getName()+" just subscribed for "+event.getMonths()+"month! \n "+message,
    			(event.getMonths()>12) ? Settings.displayGiftedSubs.getColor() : Settings.displaySubs.getColor(),
					getButton(currentChannelTab, Main.TEXTURE_MANAGER.getWaveButton(), "%GREET%"+event.getUser().getName(), "Say hello to "+event.getUser().getName()));
        }else if(Settings.displayGiftedSubs.isActive() && Settings.displayIndividualGiftedSubs.isActive()){
        	currentChannelTab.getChatWindow().displaySystemInfo("New subscription", "@"+event.getGiftedBy().getName()
				+" just gifted a sub to @"+event.getUser().getName()
				+"! ("+event.getGiftMonths()+".months)", Settings.displayIndividualGiftedSubs.getColor(),
					getButton(currentChannelTab, Main.TEXTURE_MANAGER.getLoveButton(), "%LOVE%", "Send some love!"));
        }
    }

    @EventSubscriber
    public void onGiftSubscriptions(GiftSubscriptionsEvent event) { //ONLY Random sub gifed
    	if(!Settings.displayGiftedSubs.isActive()){return;}
    	
    	ChannelTab currentChannelTab = getCurrentChannelTab(event.getChannel());
    	currentChannelTab.getChatWindow()
			.displaySystemInfo("Subscription gift", 
				"From: "+event.getUser().getName()
				+" \nTier: "+event.getSubscriptionPlan()
				+" \nAmound: "+event.getCount()
				+" \nThis user gifted "+(event.getCount()+event.getTotalCount())+" subs on this Channel!"
				,(event.getCount() >= 5) ? Settings.displayGiftedSubs.getBigColor() : Settings.displayGiftedSubs.getColor(),
					getButton(currentChannelTab, Main.TEXTURE_MANAGER.getLoveButton(), "%LOVE%", "Send some love!"));
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
    	if(!Settings.displayAnnouncement.isActive()){return;}
    	
    	ChannelTab currentChannelTab = getCurrentChannelTab(event.getChannel());
    	currentChannelTab.getChatWindow()
			.displaySystemInfo("Announcement", "From: "+event.getAnnouncer().getName()+" \nMessage: "+event.getMessage(), Settings.displayAnnouncement.getColor(),
					getButton(currentChannelTab, Main.TEXTURE_MANAGER.getReplyButton(), "%REPLY%"+event.getAnnouncer().getName(), "Say something about this announcement!"));
    }

    @EventSubscriber
    public void onIncumingRaid(RaidEvent event){
    	if(!Settings.displayAnnouncement.isActive()){return;}
//    	getCurrentChannelTab(event.getChannel()).getChatWindow()
//			.displaySystemInfo("Incuming raid", "@"+event.getRaider().getName()+" is raiding with "+event.getViewers()+" raiders!", announcement);

    	ChannelTab currentChannelTab = getCurrentChannelTab(event.getChannel());
    	currentChannelTab.getChatWindow()
			.displaySystemInfo("Incuming raid", "Frome: "+event.getRaider().getName()+" \nViewers: "+event.getViewers(), Settings.displayAnnouncement.getColor(),
					getButton(currentChannelTab, Main.TEXTURE_MANAGER.getReplyButton(), "%RAID%", "Say hello to the Raiders!"));
    }
    
    @EventSubscriber
    public void onRewardGift(RewardGiftEvent event){ //Someone got a reward
    	if(!Settings.displayUserRewards.isActive()){return;}
    	ChannelTab currentChannelTab = getCurrentChannelTab(event.getChannel());
    	currentChannelTab.getChatWindow()
    		.displaySystemInfo("Reward granted ", event.getUser().getName()+" got a "+event.getTriggerType()+ " reward", Settings.displayUserRewards.getColor(),
				getButton(currentChannelTab, Main.TEXTURE_MANAGER.getReplyButton(), "%REPLY%"+event.getUser().getName(), "Congratulate this user!"));
    	
    }
    
    @EventSubscriber
    public void onBitsBadgeEarned(BitsBadgeEarnedEvent event){
    	if(!Settings.displayUserRewards.isActive()){return;}
    	ChannelTab currentChannelTab = getCurrentChannelTab(event.getChannel());
		currentChannelTab.getChatWindow()
			.displaySystemInfo("BitBadge Earned ", event.getUser().getName()+" got a new bit badge! -> "+event.getBitsThreshold(), Settings.displayUserRewards.getColor(),
					getButton(currentChannelTab, Main.TEXTURE_MANAGER.getReplyButton(), "%REPLY%"+event.getUser().getName(), "Congratulate this user!"));
    }

    @EventSubscriber
    public void onClearChat(ClearChatEvent event){
    	if(!Settings.displayModActions.isActive()){return;}
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
			.displaySystemInfo("Chat cleared", "All chat-messages got cleared!", Settings.displayModActions.getColor(), null);
    }

    @EventSubscriber
    public void onDeleteMessage(DeleteMessageEvent event){
    	if(!Settings.displayModActions.isActive()){return;}
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
    		.displaySystemInfo("Message deleted", "@"+event.getUserName()+": "+event.getMessage(), Settings.displayModActions.getColor(), null);
    }

    @EventSubscriber
    public void onRaidCancellation(RaidCancellationEvent event){
    	if(!Settings.displayModActions.isActive()){return;}
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
			.displaySystemInfo("Raid cancelled", "The current raid got cancelled!", Settings.displayModActions.getColor(), null);
    }
    
    @EventSubscriber
    public void onChannelMod(ChannelModEvent event){
    	if(!Settings.displayModActions.isActive()){return;}
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
			.displaySystemInfo("Mod status changed", event.getUser().getName()+" Gained or lost the mod status!", Settings.displayModActions.getColor(), null);
    }

    @EventSubscriber
    public void onUserBan(UserBanEvent event){
    	if(!Settings.displayModActions.isActive()){return;}
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
    		.displaySystemInfo("User banned", "@"+event.getUser().getName()+" got banned!", Settings.displayModActions.getColor(), null);
    }

    @EventSubscriber
    public void onUserTimeout(UserTimeoutEvent event){
    	if(event.getUser().getName().equals(TwitchManager.ownerChannelName)){
    		
    	}
    	
    	if(!Settings.displayModActions.isActive()){return;}
//    	getCurrentChannelTab(event.getChannel()).getChatWindow()
//			.displaySystemInfo("User Timeout", "@"+event.getUser().getName()+" got a timeout for "+event.getDuration()+".sec! Reason: "+event.getReason(), moderationColor);
    	
    	getCurrentChannelTab(event.getChannel()).getChatWindow()
			.displaySystemInfo("User Timeout", "User: "+event.getUser().getName()+" \nDuration: "+event.getDuration()+".sec! \nReason: "+event.getReason(), Settings.displayModActions.getColor(), null);
    }
    
    @EventSubscriber
    public void onChatConnectionState(ChatConnectionStateEvent event){
    	if(Main.MAIN_FRAME == null){return;}
    	System.err.println(event.getPreviousState()+" -> "+event.getState());
    	
    	new Thread(() -> {
	    	switch (event.getState()) {
			case CONNECTED: 
				Main.MAIN_FRAME.statusButtonText.setText("Online");
				Main.MAIN_FRAME.statusButton.setIcon(Main.TEXTURE_MANAGER.getStatusButton_1());
				break;
	
			case CONNECTING: case DISCONNECTING: case RECONNECTING:
				Main.MAIN_FRAME.statusButtonText.setText("Loading");
				Main.MAIN_FRAME.statusButton.setIcon(Main.TEXTURE_MANAGER.getStatusButton_2());
				break;
				
			case DISCONNECTED: case LOST:
				Main.MAIN_FRAME.statusButtonText.setText("Offline");
				Main.MAIN_FRAME.statusButton.setIcon(Main.TEXTURE_MANAGER.getStatusButton_3());
				break;
	
			default: break;
	    	}
    	}).start();
    	
    	Main.MAIN_FRAME.getTitleBar().getMainTab().getChatWindow().chatStatusPanel.setConectionStatus(event.getState());
    	Main.MAIN_FRAME.getTitleBar().getSecondTab().getChatWindow().chatStatusPanel.setConectionStatus(event.getState());
    	Main.MAIN_FRAME.getTitleBar().getThirdTab().getChatWindow().chatStatusPanel.setConectionStatus(event.getState());
    }

	private ChannelTab getCurrentChannelTab(EventChannel eventChannel) {
		if(Main.MAIN_FRAME == null){return null;}
		
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

	
	private MineButton getButton(ChannelTab tab, ImageIcon icon, String outputMessage, String toolTip){
		MineButton actionButton = new MineButton(buttonSize, null, ButtonType.NON);//.setInvisible(!MainFrame.debug);
		actionButton.setPreferredSize(buttonSize);
		actionButton.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		actionButton.setBorderPainted(false);
		actionButton.setIcon(icon);
		actionButton.setToolTipText(toolTip);
		actionButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e){
				ChatStatusPanel statusPanel = tab.getChatWindow().chatStatusPanel;
				
				statusPanel.overrideCurrentInputCache(false);
				
				if(outputMessage.startsWith("%GREET%")){
			    	if(tab.getChatWindow().greetingsManager.isMentioned("")){
			    		return;
			    	}
			    	
			    	String userName = outputMessage.replace("%GREET%", "");
			    	tab.getChatWindow().greetingsManager.add(userName);
					String greeting = tab.getGreetingTexts().get(ChatWindowMessageComponent.random.nextInt(tab.getGreetingTexts().size()));
					statusPanel.overrideUserInput(greeting.replace("{USER}", "@"+userName).trim().replaceAll(" +", " "));
				}

				if(outputMessage.startsWith("%REPLY%")){
					statusPanel.overrideUserInput("@"+outputMessage.replace("%REPLY%", "")+" ");
				}

				if(outputMessage.startsWith("%RAID%")){
					statusPanel.overrideUserInput("%RAID% - [TODO]");
				}

				if(outputMessage.startsWith("%LOVE%")){
					statusPanel.overrideUserInput("%LOVE% - [TODO]");
				}
			}
		});
		
		return actionButton;
	}


//    @EventSubscriber
//    public void on( event){
//    	
//    }

}

//Moderator actions - Cyan
//Spending < 100 bits | < 4 subs  - Orange
//Spending > 100 bits | > 4 Subs  -  Yellow
//Announcement  -  Green
//User Reward -  Blue
