package de.minetrain.minechat.gui.emotes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.gui.emotes.Emote.EmoteType;

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
//		load();
		logger.info("Emotes loaded...");
	}
	
	//This used to load the emote autocompetion.
//	public static void load(){
//		MineTextArea.clearStaticEmoteDictionary();
//		emotes.values().stream().filter(emote -> emote.getEmoteType().equals(EmoteType.DEFAULT)).forEach(emote -> MineTextArea.addToStaticEmoteDictionary(new SuggestionObj(emote)));
//		
//		getChannelEmotes().values().stream().filter(channel -> channel.isSub()).forEach(channel -> {
//			System.err.println("Channel sub -> "+channel.getSubLevel());
//			channel.getAllEmotes().stream()
//				.filter(emote -> emote.isGlobal())
//				.filter(emote -> emote.isSubOnly() ? channel.isSub() : false)
//				.forEach(emote -> MineTextArea.addToStaticEmoteDictionary(new SuggestionObj(emote)));
//		});
//	}
	
	public static void clear(){
		logger.debug("Clear all emotes from cache.");
		emotes.clear();
		emoteIdToName.clear();
		emoteNameToId.clear();
	}
	
	public static void clearChannel(){
		logger.debug("Clear all channel emotes from cache.");
		channelEmotes.clear();
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
	
	public static List<Emote> getAllFavoriteEmotes(boolean considerNameDuplication){
		List<Emote> emotes = getAllEmotes().values().stream()
				.filter(emote -> emote.isFavorite())
				.collect(Collectors.toList());
		
		if(considerNameDuplication){
			emotes = emotes.stream()
				.collect(Collectors.toMap(Emote::getName, emote -> emote, (existing, replacement) -> existing))
				.values().stream().collect(Collectors.toList());
		}

		return emotes.stream().sorted(Comparator.comparing(Emote::getName)).collect(Collectors.toList());
	}
	
	public static List<Emote> getAllDefaultEmotes(){
		return getAllEmotes().values().stream()
				.filter(emote -> emote.getEmoteType().equals(EmoteType.DEFAULT))
				.sorted(Comparator.comparing(Emote::getName))
				.collect(Collectors.toList());
	}
	
	public static Emote getEmoteById(String emoteId){
		return emotes.containsKey(emoteId) ? emotes.get(emoteId) : null;
	}
	
	/**
	 * @param channelId
	 * @return may be null, if no emotes are installed for the user.
	 */
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
	
	public static Map<String, Emote> getPublicEmotes(){
		return getAllEmotes().entrySet().stream().filter(entry -> entry.getValue().isGlobal()).collect(Collectors.toMap(Entry::getKey, Entry::getValue, (old, neew) -> old));
	}
}
