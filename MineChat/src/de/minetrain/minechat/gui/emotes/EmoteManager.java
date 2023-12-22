package de.minetrain.minechat.gui.emotes;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.gui.emotes.Emote.EmoteType;
import de.minetrain.minechat.gui.obj.chat.userinput.textarea.MineTextArea;
import de.minetrain.minechat.gui.obj.chat.userinput.textarea.SuggestionObj;

public class EmoteManager {
	private static final Logger logger = LoggerFactory.getLogger(EmoteManager.class);
	
	/**emoteId, emoteName*/
	private static final HashMap<String, String> emoteIdToName = new HashMap<String, String>();
	/**emoteName, emoteId*/
	private static final HashMap<String, String> emoteNameToId = new HashMap<String, String>();
	/**emoteId, emote*/
	private static final HashMap<String, Emote> emotes = new HashMap<String, Emote>();
	/**channelId, ChannelEmotes*/
	private static final HashMap<String, ChannelEmotes> channelEmotes = new HashMap<String, ChannelEmotes>();
	
	public EmoteManager() {
		logger.info("Initiating EmoteManager");
		DatabaseManager.getEmote().getAll();
		DatabaseManager.getEmote().getAllChannels();
		load();
		logger.info("Emotes loaded...");
	}
	
	public static void load(){
		MineTextArea.clearStaticEmoteDictionary();
		emotes.values().stream().filter(emote -> emote.getEmoteType().equals(EmoteType.DEFAULT)).forEach(emote -> MineTextArea.addToStaticEmoteDictionary(new SuggestionObj(emote)));
		
		getChannelEmotes().values().stream().filter(channel -> channel.isSub()).forEach(channel -> {
			System.err.println("Channel sub -> "+channel.getSubLevel());
			channel.getAllEmotes().stream()
				.filter(emote -> emote.isGlobal())
				.filter(emote -> emote.isSubOnly() ? channel.isSub() : false)
				.forEach(emote -> {
					MineTextArea.addToStaticEmoteDictionary(new SuggestionObj(emote));
				});
			
		});
	}
	
	public static void addEmote(Emote emote){
		logger.debug("Adding emote -> "+emote.getName()+":"+emote.getEmoteId());
		emotes.put(emote.getEmoteId(), emote);
		emoteIdToName.put(emote.getEmoteId(), emote.getName());
		emoteNameToId.put(emote.getName(), emote.getEmoteId());
	}
	
	public static void addChannel(String channelId, ChannelEmotes channelEmote){
		logger.debug("Adding channel emote set -> "+channelId);
		channelEmotes.put(channelId, channelEmote);
	}
	
	public static HashMap<String, Emote> getAllEmotes(){
		return emotes;
	}
	
	public static Emote getEmoteById(String emoteId){
		return emotes.containsKey(emoteId) ? emotes.get(emoteId) : null;
	}
	
	public static ChannelEmotes getChannelEmotes(String channelId){
		return channelEmotes.containsKey(channelId) ? channelEmotes.get(channelId) : null;
	}
	
	public static HashMap<String, ChannelEmotes> getChannelEmotes(){
		return channelEmotes;
	}
	
	public static Emote getEmoteByName(String emoteName){
		return emoteNameToId.containsKey(emoteName) ? getEmoteById(emoteNameToId.get(emoteName)) : null;
	}
	
	public static Emote getChannelEmoteByName(String channelId, String emoteName){
		if(getChannelEmotes(channelId) == null){
			return null;
		}
		
		HashMap<String, String> emotesByName = getChannelEmotes(channelId).getEmotesByName();
		return emotesByName.containsKey(emoteName) ? getEmoteById(emotesByName.get(emoteName)) : null;
	}
}
