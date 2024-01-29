package de.minetrain.minechat.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.objectdata.ChannelData;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import de.minetrain.minechat.utils.audio.AudioVolume;

public class ChannelManager {
	private static HashMap<String, Channel> channels = new HashMap<String, Channel>();
	private static String selectedChannelId = "";
	
	public ChannelManager() {
		HashMap<String, ChannelData> allChannels = DatabaseManager.getChannel().getAllChannels();
		TwitchManager.getTwitchUsers(TwitchApiCallType.ID, allChannels.keySet().toArray(String[]::new)); // Load from twitch api.
		allChannels.keySet().stream().forEach(ChannelManager::addChannel);
		
		if(!allChannels.isEmpty()){
			setCurrentChannel(allChannels.keySet().iterator().next());
		}
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
	
	/**
	 * Returns null, if the proived ID dose not align with a twitch channel.
	 * @param channelId
	 * @return
	 */
	public static Channel addChannel(String channelId){
		if(DatabaseManager.getChannel().getChannelById(channelId) == null && !createNewChannel(channelId)){
			return null;
		}
		
		return channels.computeIfAbsent(channelId, Channel::new);
	}
	
	public static boolean isValidChannel(String channelId){
		return channels.containsKey(channelId);
	}
	
	public static Channel getChannel(String channelId){
		return channels.get(channelId);
	}
	
	public static Collection<Channel> getAllChannels(){
		return channels.values();
	}
	
	public static void validateUserLogins(){
		TwitchManager.getTwitchUsers(TwitchApiCallType.ID, channels.keySet().toArray(String[]::new)).forEach(user -> {
			if(!user.isDummy()){
				DatabaseManager.getChannel().updateChannelLoginName(user.getUserId(), user.getLoginName());
			}
		});
		
		DatabaseManager.commit();
	}
	
	public static boolean createNewChannel(String channelId){
		TwitchUserObj channel = TwitchManager.getTwitchUser(TwitchApiCallType.ID, channelId);
		if(channel.isDummy()){return false;}
		
		DatabaseManager.getChannel().insert(
				channelId,
				channel.getLoginName(),
				channel.getDisplayName(),
				"Viewer",
				null, //disable chat log
				"Hello {USER} HeyGuys\nWelcome {USER} HeyGuys",
				"By {USER}!\nHave a good one! {USER} <3",
				"Welcome back {USER} <3\nwb {USER} HeyGuys",
				null, //No live notification.
				AudioVolume.VOLUME_100);
		
		DatabaseManager.commit();

		ArrayList<String> list = new ArrayList<String>();
		DatabaseManager.getEmote().insertChannel(channelId, "tier0", list, list, list, list, list);
		TextureManager.downloadChannelEmotes(channelId);
		TextureManager.downloadBttvEmotes(channelId);
		TextureManager.downloadChannelBadges(channelId);
		DatabaseManager.getEmote().getAllChannels();
		DatabaseManager.commit();
		return true;
	}
	
}
