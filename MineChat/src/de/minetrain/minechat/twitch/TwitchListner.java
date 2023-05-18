package de.minetrain.minechat.twitch;

import java.awt.Color;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.ITwitchChat;
import com.github.twitch4j.chat.events.AbstractChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;

import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.obj.TitleBar;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.TwitchChatUser;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserStatistics;
import de.minetrain.minechat.utils.ChatMessage;

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
		ChannelTab channelTab = null;
		TitleBar titleBar = Main.MAIN_FRAME.getTitleBar();
		
		if(titleBar.getMainTab().getConfigID().equals(event.getChannel().getId())){
			channelTab = titleBar.getMainTab();
		}
		
		if(titleBar.getSecondTab().getConfigID().equals(event.getChannel().getId())){
			channelTab = titleBar.getSecondTab();
		}
		
		if(titleBar.getThirdTab().getConfigID().equals(event.getChannel().getId())){
			channelTab = titleBar.getThirdTab();
		}
		
		if(channelTab == null){
			return;
		}

		System.out.println(titleBar.getName());
		TwitchChatUser twitchUser = TwitchUserStatistics.getTwitchUser(event.getUser().getId());
		if(twitchUser == null){
			channelTab.getChatWindow().displayMessage(event.getMessage(), event.getUser().getName(), Color.WHITE, event);
		}else{
			channelTab.getChatWindow().displayMessage(event.getMessage(), twitchUser.getUserName(), twitchUser.getColor(), event);
		}
		
	}

}
