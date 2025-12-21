package de.minetrain.minechat.twitch;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.AbstractChannelMessageEvent;
import com.github.twitch4j.chat.events.ChatConnectionStateEvent;
import com.github.twitch4j.chat.events.channel.BitsBadgeEarnedEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.CheerEvent;
import com.github.twitch4j.chat.events.channel.ClearChatEvent;
import com.github.twitch4j.chat.events.channel.DeleteMessageEvent;
import com.github.twitch4j.chat.events.channel.GiftSubscriptionsEvent;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.chat.events.channel.ModAnnouncementEvent;
import com.github.twitch4j.chat.events.channel.RaidCancellationEvent;
import com.github.twitch4j.chat.events.channel.RaidEvent;
import com.github.twitch4j.chat.events.channel.RewardGiftEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.chat.events.channel.UserBanEvent;
import com.github.twitch4j.chat.events.channel.UserTimeoutEvent;
import com.github.twitch4j.chat.events.roomstate.SlowModeEvent;
import com.github.twitch4j.eventsub.domain.chat.Emote.Format;
import com.github.twitch4j.eventsub.domain.chat.Fragment;
import com.github.twitch4j.eventsub.domain.chat.Reply;
import com.github.twitch4j.eventsub.events.ChannelChatMessageEvent;
import com.github.twitch4j.eventsub.events.ChannelModeratorAddEvent;
import com.github.twitch4j.eventsub.events.ChannelModeratorRemoveEvent;
import com.github.twitch4j.pubsub.events.MidrollRequestEvent;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.BadgeId;
import de.minetrain.minechat.data.objectdata.ChatMessage;
import de.minetrain.minechat.data.objectdata.ChatMessage.MessageType;
import de.minetrain.minechat.data.objectdata.ChatMessageToken;
import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.features.autoreply.AutoReplyManager;
import de.minetrain.minechat.gui.emotes.ChannelEmotes;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.main.ChannelActions;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.WebUtils;
import de.minetrain.minechat.utils.audio.AudioVolume;
import de.minetrain.minechat.utils.audio.DefaultAudioFiles;
import de.minetrain.minechat.utils.events.MineChatEventType;

/**
 * A listener for Twitch events such as streams going live or offline and channel messages.
 * Provides methods to handle these events and execute commands.
 *
 * @author MineTrain/Justin
 * @since 28.04.2023
 * @version 1.1
 */
public class TwitchListener {

	private static final Logger LOG = LoggerFactory.getLogger(TwitchListener.class);

	private static final Pattern SPLIT_PATTERN = Pattern.compile("\\s+");

	public static int messagesTEMP = 0;
//	private LiveNotification liveNotification = new LiveNotification();

	@EventSubscriber
	public void onMidrollRequest(MidrollRequestEvent event){
		Main.audioManager.playAudioClip(DefaultAudioFiles.LIVE_0, AudioVolume.VOLUME_100);
		for(int i=0; i>50; i++){
			System.err.println("Ad brake - "+event.getChannelId());
		}
	}

	/// Handles the event when a message is sent in the channel.
	///
	/// @param event The [ChannelChatMessageEvent] object containing information about the message.
	@EventSubscriber
	public void onChannelMessage(ChannelChatMessageEvent event) {
		LOG.debug("EventSub ChannelMessage: {} | {}", event.getChatterUserName(), event.getMessage().getText());
		ChannelActions channel = Main.getChannelManager().getChannelActions(event.getBroadcasterUserId());
		channel.getStatistics().addMessage(event.getChatterUserName(), event.getChatterUserId(), event.getMessage().getText());
		TwitchMessage twitchMessage = new TwitchMessage(event);

		if (event.getChatterUserId().equals(TwitchHelper.getSelfUser().getUserId())) {
			channel.getMessageHistory().addSendedMessages(event.getMessage().getText());
			MessageManager.setLastMessage(event.getMessage().getText());
			DatabaseManager.getOwnerCache().insert(twitchMessage);

			// TODO unnecessary?
//			ChannelEmotes channelEmotes = EmoteManager.getChannelEmotes(event.getBroadcasterUserId());
//			if (channelEmotes != null) {
//				channelEmotes.setSubTier("tier" + event.getSubscriptionTier());
//			}
		}

		ChatMessage chatMessage = createChatMessage(event);
		EclipseStoreKeeper.root().messages().addMessage(chatMessage);
		channel.notifyMessageAdded(chatMessage);

		AutoReplyManager.recordMessage(twitchMessage);
	}

	private static ChatMessage createChatMessage(ChannelChatMessageEvent event) {
		List<ChatMessageToken> tokenList = new ArrayList<>();
		event.getMessage().getFragments().stream().forEach(fragment -> createChatMessageToken(event.getBroadcasterUserId(), fragment, tokenList));
		ChatMessageToken[] tokens = tokenList.toArray(ChatMessageToken[]::new);
		BadgeId[] badgeIds = event.getBadges().stream().map(badge -> new BadgeId(badge.getSetId(), badge.getId())).toArray(BadgeId[]::new);
		Reply reply = event.getReply();

		LOG.info("Created ChatMessage tokens: {}", (Object) tokens);

		return new ChatMessage(
				event.getMessageId(),
				event.getBroadcasterUserId(),
				event.getChatterUserId(),
				event.getChatterUserName(),
				event.getColor(),
				Instant.now(),
				reply != null ? reply.getParentMessageId() : null,
				mapMessageType(event.getMessageType()),
				tokens,
				badgeIds
		);
	}

	private static MessageType mapMessageType(com.github.twitch4j.eventsub.domain.chat.MessageType messageType) {
		return switch (messageType) {
			case CHANNEL_POINTS_HIGHLIGHTED -> MessageType.HIGHLIGHTED;
			case USER_INTRO -> MessageType.FIRST_MESSAGE;
			default -> MessageType.TEXT;
		};
	}

	private static void createChatMessageToken(String channelId, Fragment fragment, List<ChatMessageToken> tokenList) {
		switch (fragment.getType()) {
			case EMOTE -> tokenList.add(ChatMessageToken.createEmoteToken(fragment.getEmote().getId(), fragment.getEmote().getFormat().contains(Format.ANIMATED), fragment.getText()));
			case MENTION -> tokenList.add(ChatMessageToken.createMentionToken(fragment.getText()));
			default ->  tokenizeText(channelId, fragment.getText(), tokenList);
		};
	}

	private static void tokenizeText(String channelId, String text, List<ChatMessageToken> tokenList) {
		String[] words = SPLIT_PATTERN.splitWithDelimiters(text, 0);
		for (String word : words) {
			if (word.isEmpty()) {
				continue;
			}
			if (word.length() >= 2 && StringUtils.isNotBlank(word)) {
				if (WebUtils.isValidUrl(word)) {
					tokenList.add(ChatMessageToken.createLinkToken(word));
					continue;
				}
				Emote emote = Main.getEmoteManager().getBttvEmoteByName(channelId, word);
				if (emote != null) {
					tokenList.add(ChatMessageToken.createEmoteToken(emote.getEmoteId(), emote.isAnimated(), word));
					continue;
				}
			}
			tokenList.add(ChatMessageToken.createTextToken(word));
		}
	}

	/**
	 * Handles the event when a message is sent in the channel and executes the command if the cooldown time has elapsed.
	 * @param event The {@link ChannelMessageEvent} object containing information about the message.
	 *
	 * @deprecated Use {@link #onChannelMessage(ChannelChatMessageEvent)} instead.
	 */
	@EventSubscriber
	@Deprecated
	public void onAbstractChannelMessage(AbstractChannelMessageEvent event){
		if (!Main.isGuiOpen || Main.getChannelManager().getChannel(event.getChannel().getId()) == null) {
			return;
		}
		LOG.info("User: {} | Message --> {}", event.getUser().getName(), event.getMessage());

		ChannelActions channel = Main.getChannelManager().getChannelActions(event.getChannel().getId());
		channel.getStatistics().addMessage(event.getUser().getName(), event.getUser().getId(), event.getMessage());
		TwitchMessage twitchMessage = new TwitchMessage(event.getMessageEvent(), event.getMessage());
		Main.eventManager.fireEvent(MineChatEventType.INCOMING_MESSAGE, twitchMessage);


		if(event.getUser().getName().equals(TwitchManager.ownerChannelName)){
			channel.getMessageHistory().addSendedMessages(event.getMessage());
    		MessageManager.setLastMessage(event.getMessage());
    		DatabaseManager.getOwnerCache().insert(twitchMessage);

    		ChannelEmotes channelEmotes = EmoteManager.getChannelEmotes(event.getChannel().getId());
    		if(channelEmotes != null){
    			channelEmotes.setSubTier("tier"+event.getSubscriptionTier());
    		}

		}


//		if(twitchMessage.isFirstMessage()){
////			currentChannelTab.getChatWindow()
////				.displaySystemInfo("First channel Message.", "@"+event.getUser().getName()+" just left his first chat message on this channel.\n\n"+event.getMessage(),
////					Settings.highlightUserFirstMessages.getColor(), getButton(currentChannelTab, Main.TEXTURE_MANAGER.getWaveButton(), "Say hello to "+event.getUser().getName(), EventButtonType.GREETING, event.getUser().getName()));
//		}

//		channel.displayMessage(twitchMessage);
		AutoReplyManager.recordMessage(twitchMessage);
	}


    @EventSubscriber
    public void onCheer(CheerEvent event) {
//    	ChannelTab currentChannelTab = getCurrentChannelTab(event.getChannel());
//    	currentChannelTab.getStatistics().addBits(event);
    	if(!Settings.displayBitsCheerd.isActive()){return;}
    }

    @EventSubscriber
    public void onSub(SubscriptionEvent event) {
//    	ChannelTab currentChannelTab = getCurrentChannelTab(event.getChannel());
//    	currentChannelTab.getStatistics().addSub(event);

        if(!event.getGifted() && Settings.displaySubs.isActive()) {

        }else if(Settings.displayGiftedSubs.isActive() && Settings.displayIndividualGiftedSubs.isActive()){

        }
    }

    @EventSubscriber
    public void onGiftSubscriptions(GiftSubscriptionsEvent event) { //ONLY Random sub gifed
    	if(!Settings.displayGiftedSubs.isActive()){return;}

    }


//    I Thing these are disabled, bcs they fire simutanisly to SubscriptionEvent.
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

    }

    @EventSubscriber
    public void onIncumingRaid(RaidEvent event){
    	if(!Settings.displayAnnouncement.isActive()){return;}

    }

    @EventSubscriber
    public void onRewardGift(RewardGiftEvent event){ //Someone got a reward
    	if(!Settings.displayUserRewards.isActive()){return;}

    }

    @EventSubscriber
    public void onBitsBadgeEarned(BitsBadgeEarnedEvent event){
    	if(!Settings.displayUserRewards.isActive()){return;}

    }

    @EventSubscriber
    public void onClearChat(ClearChatEvent event){
    	if(!Settings.displayModActions.isActive()){return;}

    }

    @EventSubscriber
    public void onDeleteMessage(DeleteMessageEvent event){
    	if(!Settings.displayModActions.isActive()){return;}

    }

    @EventSubscriber
    public void onRaidCancellation(RaidCancellationEvent event){
    	if(!Settings.displayModActions.isActive()){return;}

    }

    @EventSubscriber
    public void onChannelMod(ChannelModeratorAddEvent event){
    	if(!Settings.displayModActions.isActive()){return;}

    }

    @EventSubscriber
    public void onChannelMod(ChannelModeratorRemoveEvent event){
    	if(!Settings.displayModActions.isActive()){return;}

    }

    @EventSubscriber
    public void onUserBan(UserBanEvent event){
    	if(!Settings.displayModActions.isActive()){return;}

    }

    @EventSubscriber
    public void onUserTimeout(UserTimeoutEvent event){
    	if(event.getUser().getName().equals(TwitchManager.ownerChannelName)){
    		//Check for own to display it accordingly.
    	}

    	if(!Settings.displayModActions.isActive()){return;}

    }


    /**
     * NOTE: This is currently broken in Twitch4J:1.18
     */
    @EventSubscriber
    public void onSlowMode(SlowModeEvent event){
    	LOG.info("Change slow mode to -> "+event.getTime());
    	MessageManager.channelSlowMods.put(event.getChannel().getId(), event.getTime()*1000);
    }

    /**
     * This is temporary, untill the {@link SlowModeEvent} is patched.
     * @param event
     */
    @EventSubscriber
    public void onIRCMessageMode(IRCMessageEvent event){
    	if(event.getEscapedTags().containsKey("slow")){
        	MessageManager.channelSlowMods.put(event.getChannel().getId(), Long.valueOf(String.valueOf(event.getEscapedTags().get("slow")))*1000);
        	System.err.println(event.getEscapedTags());
    	}
    }


    @EventSubscriber
    public void onChatConnectionState(ChatConnectionStateEvent event){
    	LOG.info(event.getPreviousState()+" -> "+event.getState());

    	switch (event.getState()) {
		case CONNECTED:
			break;

		case CONNECTING: case DISCONNECTING: case RECONNECTING:
			break;

		case DISCONNECTED: case LOST:
			break;

		default: break;
    	}
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
