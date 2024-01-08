package de.minetrain.minechat.features.autoreply;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.config.enums.AutoReplyState;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.main.ChannelManager;
import de.minetrain.minechat.twitch.obj.TwitchMessage;

public class AutoReplyManager {
	/** ChannelID, (trigger, autoReply) */
	private static Map<String, HashMap<String, AutoReply>> autoReplys = new HashMap<String, HashMap<String, AutoReply>>();
	
	public AutoReplyManager() {
		DatabaseManager.getAutoReply().getAll();
	}
	
	public static void addAutoReply(AutoReply autoReply){
		autoReplys.computeIfAbsent(autoReply.getChannelId(), k -> new HashMap<>());
		autoReplys.get(autoReply.getChannelId()).put(autoReply.getTrigger(), autoReply);

	}
	
	public static void deleteAutoReply(AutoReply autoReply){
		autoReplys.computeIfAbsent(autoReply.getChannelId(), k -> new HashMap<>());
		HashMap<String, AutoReply> channelReplys = autoReplys.get(autoReply.getChannelId());
		
		if(channelReplys.containsKey(autoReply.getTrigger())){
			channelReplys.remove(autoReply.getTrigger());
			DatabaseManager.getAutoReply().remove(autoReply.getUuid());
		}

	}
	
	public static void recordMessage(TwitchMessage message){
		if(!autoReplys.containsKey(message.getChannelId())){
			return;
		}
		
		if(Settings.autoReplyState.equals(AutoReplyState.CURRENT_TAB) && !(ChannelManager.getCurrentChannel().getChannelId().equals(message.getChannelId()))){
			return;
		}
		
		List<String> usedTrigger = getAutoReplyTrigger(message.getChannelId()).stream()
	        .filter(word -> message.getMessage().toLowerCase().contains(word.toLowerCase()))
	        .collect(Collectors.toList());
		
		usedTrigger.forEach(trigger -> autoReplys.get(message.getChannelId()).get(trigger).fire(message));
		
	}
	
	/**
	 * ChannelId, replys
	 * @return
	 */
	public static Map<String, HashMap<String, AutoReply>> getAutoReplys(){
		return autoReplys;
	}
	
	/**
	 * @return All Triggers from a channel.
	 */
	public static List<String> getAutoReplyTrigger(String channelId){
		autoReplys.computeIfAbsent(channelId, k -> new HashMap<>());
		return autoReplys.get(channelId).entrySet().stream()
		        .map(Map.Entry::getKey)
		        .collect(Collectors.toList());
	}
	
}
