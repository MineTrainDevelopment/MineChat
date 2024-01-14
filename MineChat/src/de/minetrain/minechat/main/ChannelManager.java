package de.minetrain.minechat.main;

import java.util.HashMap;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;

public class ChannelManager {
	private static HashMap<String, Channel> channels = new HashMap<String, Channel>();
	private static String selectedChannelId = "";
	
	public ChannelManager() {
		// TODO Auto-generated constructor stub
	}
	
	public static Channel getCurrentChannel(){
		return getChannel(selectedChannelId);
	}
	
	/**
	 * NOTE: If the channel should be invalid, a new channel for that ID will be createt.
	 * @param channelId
	 * @return
	 */
	public static Channel setCurrentChannel(String channelId){
		selectedChannelId = channelId;
		return addChannel(selectedChannelId);
	}
	
	public static Channel addChannel(String channelId){
		return channels.computeIfAbsent(channelId, Channel::new);
	}
	
	public static boolean isValidChannel(String channelId){
		return channels.containsKey(channelId);
	}
	
	public static Channel getChannel(String channelId){
		return channels.get(channelId);
	}
	
	public static void validateUserLogins(){
		TwitchManager.getTwitchUsers(TwitchApiCallType.ID, channels.keySet().toArray(String[]::new)).forEach(user -> {
			if(!user.isDummy()){
				DatabaseManager.getChannel().updateChannelLoginName(user.getUserId(), user.getLoginName());
			}
		});
		
		DatabaseManager.commit();
	}
	
}
