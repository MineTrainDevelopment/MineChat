package de.minetrain.minechat.gui.emotes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.gui.obj.chat.userinput.textarea.MineTextArea;
import de.minetrain.minechat.gui.obj.chat.userinput.textarea.SuggestionObj;

public class EmoteManager {
	private static final Logger logger = LoggerFactory.getLogger(EmoteManager.class);
	public static final HashMap<String, Boolean> emoteFavorite = new HashMap<String, Boolean>();
	private static final HashMap<String, Emote> globalEmotes = new HashMap<String, Emote>();
	private static final HashMap<String, Emote> defaultEmotes = new HashMap<String, Emote>();
	private static final HashMap<String, Emote> favoriteEmotes = new HashMap<String, Emote>();
	private static final HashMap<String, ChannelEmotes> channelEmotes = new HashMap<String, ChannelEmotes>();
	public static final HashMap<String, String> CACHED_WEB_EMOTES = new HashMap<String, String>();
	public static Map<String, String> emoteCache = new HashMap<String, String>();
	private static YamlManager yaml;
	
	public EmoteManager() {
		yaml = new YamlManager("data/emotes.yml");
	}
	
	public static void load(){
		MineTextArea.clearStaticDictionary();
		globalEmotes.clear();
		defaultEmotes.clear();
		favoriteEmotes.clear();
		channelEmotes.clear();
		emoteCache.clear();
		
		List<String> keys = yaml.entrySet().stream()
	        .map(Map.Entry::getKey)
//	        .map(key -> key.replace("Channel_", ""))
	        .collect(Collectors.toList());
		
		if(keys.contains("Default")){
			Map<String, Object> emoteMap = yaml.getObjectMap("Default.twitch");
			if(emoteMap != null && !emoteMap.isEmpty()){
				List<String> emoteKeys = emoteMap.entrySet().stream()
					.map(Map.Entry::getKey)
					.collect(Collectors.toList());
				
				emoteKeys.forEach(emoteKey -> {
					Emote emote = new Emote("Default.twitch."+emoteKey, yaml, ">default<");
					globalEmotes.put(emote.getName(), emote);
					defaultEmotes.put(emote.getName(), emote);
					if(emote.isFavorite()){
						favoriteEmotes.put(emote.getName(), emote);
					}
				});
			}
		}
		
		keys.remove("Default");
		keys.forEach(key -> {
			ChannelEmotes channel = new ChannelEmotes(key, yaml);
			addEmotes(key, channel, "twitch", "bttv");
			if(channel.size()>0){
				channelEmotes.put(channel.getChannelId(), channel);
			}
		});
		
		getGlobalEmotes().values().forEach(emote -> {
//			MineTextArea.addToStaticDictionary(
//					new SuggestionObj(":" + emote.getName() + (emote.getEmoteType().equals(EmoteType.BTTV) ? " -- BTTV" : ""), 
//							emote.getFilePath()));

			MineTextArea.addToStaticEmoteDictionary(new SuggestionObj(emote));
		});
	}

	private static void addEmotes(String key, ChannelEmotes channel, String... platforms) {
		Arrays.asList(platforms).forEach(platform -> {
			Map<String, Object> emoteMap = yaml.getObjectMap(key+"."+platform);
			if(emoteMap != null && !emoteMap.isEmpty()){
				List<String> emoteKeys = emoteMap.entrySet().stream()
					.map(Map.Entry::getKey)
					.collect(Collectors.toList());
				
				emoteKeys.forEach(emoteKey -> {
					Emote emote = new Emote(key+"."+platform+"."+emoteKey, yaml, yaml.getString(key+".Id"));
					channel.addEmote(emote);
					
					if(emote.isGlobal() && channel.hasPermission(emote)){
						globalEmotes.put(emote.getName(), emote);
					}
					
					if(emote.isFavorite()){
						favoriteEmotes.put(emote.getName(), emote);
					}
				});
			}
		});
	}
	
	public static ChannelEmotes getChannelEmotes(String channelId){
		return channelEmotes.get(channelId);
	}
	
	public static HashMap<String, ChannelEmotes> getChannelEmotes(){
		return channelEmotes;
	}
	
	public static HashMap<String, Emote> getGlobalEmotes(){
		return globalEmotes;
	}
	
	public static HashMap<String, Emote> getDefaultEmotes(){
		return defaultEmotes;
	}
	
	public static HashMap<String, Emote> getFavoriteEmotes(){
		return favoriteEmotes;
	}
	
	public static void addFavoriteEmote(Emote emote){
		favoriteEmotes.put(emote.getName(), emote);
	}
	
	public static void removeFavoriteEmote(Emote emote){
		favoriteEmotes.remove(emote.getName(), emote);
	}
	
	public static YamlManager getYaml(){
		return yaml;
	}
	
	/**
	 * id - imagePath
	 * @return
	 */
	public static Map<String, String> getAllEmotesByName(){
		if(!emoteCache.isEmpty()){
			return emoteCache;
		}
		
		Map<String, String> map = getAllEmotes().values().stream().collect(Collectors.toMap(Emote::getName, Emote::getFilePath));
		emoteCache = map;
		return map;
	}
	
	public static HashMap<String, Emote> getAllEmotes(){
		HashMap<String, Emote> emotes = new HashMap<String, Emote>();
		emotes.putAll(globalEmotes);
		
		List<ChannelEmotes> channels = channelEmotes.values().stream().collect(Collectors.toList());
		
		channels.forEach(channel -> emotes.putAll(channel));
		return emotes;
	}
}
