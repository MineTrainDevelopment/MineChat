package de.minetrain.minechat.gui.emotes;

import java.io.ByteArrayInputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.gui.emotes.EmoteLegacy.EmoteType;
import javafx.scene.image.Image;

public class EmoteManager {

	public static final String PUBLIC_EMOTE_CHANNEL_ID = "ID_PUBLIC";

	private static final Logger LOG = LoggerFactory.getLogger(EmoteManager.class);

	private static final String TWITCH_EMOTE_URL = "https://static-cdn.jtvnw.net/emoticons/v2/{}/{}/dark/1.0"; // id, format(static, animated)

	private final Cache<String, Image> emoteImage1xCache;

	private final Cache<String, Map<String, Emote>> channelEmoteCache;

	private final Cache<String, Map<String, Emote>> setEmoteCache;


	/**emoteId, emoteName*/
	private static final HashMap<String, String> emoteIdToName = new HashMap<>();
	/**emoteName, emoteId*/
	private static final HashMap<String, String> emoteNameToId = new HashMap<>();
	/**emoteId, emote*/
	private static final HashMap<String, EmoteLegacy> emotes = new HashMap<>();
	/**channelId, ChannelEmotes*/
	private static final HashMap<String, ChannelEmotes> channelEmotes = new HashMap<>();

	public EmoteManager() {
		LOG.info("Initiating EmoteManager");
		DatabaseManager.getEmote().getAll();
		DatabaseManager.getEmote().getAllChannels();
//		load();
		LOG.info("Emotes loaded...");

		CachingProvider cachingProvider = Caching.getCachingProvider();
		CacheManager cacheManager = cachingProvider.getCacheManager();
		MutableConfiguration<String,Image> image1xConfig = new MutableConfiguration<String, Image>()
			.setStoreByValue(false)
			.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.FIVE_MINUTES))
			.setCacheLoaderFactory(EmoteExtractorCacheLoader.factoryOf(emote -> new Image(new ByteArrayInputStream(emote.getImage1x()))))
			.setReadThrough(true);
		emoteImage1xCache = cacheManager.createCache("emoteImageCacheSmall", image1xConfig);

		MutableConfiguration<String, Map<String, Emote>> channelEmoteConfig = new MutableConfiguration<String, Map<String, Emote>>()
			.setStoreByValue(false)
			.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.FIVE_MINUTES))
			.setCacheLoaderFactory(ChannelEmotesCacheLoader.factory())
			.setReadThrough(true);
		channelEmoteCache = cacheManager.createCache("channelEmoteCache", channelEmoteConfig);

		MutableConfiguration<String, Map<String, Emote>> setEmoteConfig = new MutableConfiguration<String, Map<String, Emote>>()
			.setStoreByValue(false)
			.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.FIVE_MINUTES))
			.setCacheLoaderFactory(SetEmotesCacheLoader.factory())
			.setReadThrough(true);
		setEmoteCache = cacheManager.createCache("setEmoteCache", setEmoteConfig);
	}

	/// @Deprecated Maybe actually useless...
	@Deprecated
	public Map<String, Emote> getEmotes(String channelId) {
		return channelEmoteCache.get(channelId);
	}

	public Map<String, Emote> getEmoteSet(String setId) {
		return channelEmoteCache.get(setId);
	}

	public Image getEmoteImage1x(String emoteId, boolean animated) {
		Image image = emoteImage1xCache.get(emoteId);
		if (image == null) {
			String url = MessageFormatter.basicArrayFormat(TWITCH_EMOTE_URL, new Object[] {emoteId, animated ? "animated" : "static"});
			image = new Image(url, true);
			emoteImage1xCache.put(emoteId, image);
		}
		return image;
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
		LOG.debug("Clear all emotes from cache.");
		emotes.clear();
		emoteIdToName.clear();
		emoteNameToId.clear();
	}

	public static void clearChannel(){
		LOG.debug("Clear all channel emotes from cache.");
		channelEmotes.clear();
	}

	public static void addEmote(EmoteLegacy emote){
		LOG.debug("Adding emote -> "+emote.getName()+":"+emote.getEmoteId());
		emotes.put(emote.getEmoteId(), emote);
		emoteIdToName.put(emote.getEmoteId(), emote.getName());
		emoteNameToId.put(emote.getName(), emote.getEmoteId());
	}

	public static void addChannel(String channelId, ChannelEmotes channelEmote){
		LOG.debug("Adding channel emote set -> "+channelId);
		channelEmotes.put(channelId, channelEmote);
	}

	// TODO Zocki: Remove static methods and use instance methods with caching

	public static HashMap<String, EmoteLegacy> getAllEmotes(){
		return emotes;
	}

	public static List<EmoteLegacy> getAllFavoriteEmotes(boolean considerNameDuplication){
		List<EmoteLegacy> emotes = getAllEmotes().values().stream()
				.filter(emote -> emote.isFavorite())
				.collect(Collectors.toList());

		if(considerNameDuplication){
			emotes = emotes.stream()
				.collect(Collectors.toMap(EmoteLegacy::getName, emote -> emote, (existing, replacement) -> existing))
				.values().stream().collect(Collectors.toList());
		}

		return emotes.stream().sorted(Comparator.comparing(EmoteLegacy::getName)).collect(Collectors.toList());
	}

	public static List<EmoteLegacy> getAllDefaultEmotes(){
		return getAllEmotes().values().stream()
				.filter(emote -> emote.getEmoteType().equals(EmoteType.DEFAULT))
				.sorted(Comparator.comparing(EmoteLegacy::getName))
				.collect(Collectors.toList());
	}

	public static EmoteLegacy getEmoteById(String emoteId){
		return emotes.containsKey(emoteId) ? emotes.get(emoteId) : null;
	}

	/**
	 * @param channelId
	 * @return may be null, if no emotes are installed for the user.
	 */
	public static ChannelEmotes getChannelEmotes(String channelId){
		return channelEmotes.get(channelId);
	}

	public static HashMap<String, ChannelEmotes> getChannelEmotes(){
		return channelEmotes;
	}

	public static EmoteLegacy getEmoteByName(String emoteName){
		return emoteNameToId.containsKey(emoteName) ? getEmoteById(emoteNameToId.get(emoteName)) : null;
	}

	public static EmoteLegacy getChannelEmoteByName(String channelId, String emoteName){
		if(getChannelEmotes(channelId) == null){
			return null;
		}

		HashMap<String, String> emotesByName = getChannelEmotes(channelId).getEmotesByName();
		return emotesByName.containsKey(emoteName) ? getEmoteById(emotesByName.get(emoteName)) : null;
	}

	public static Map<String, EmoteLegacy> getPublicEmotes(){
		return getAllEmotes().entrySet().stream().filter(entry -> entry.getValue().isGlobal()).collect(Collectors.toMap(Entry::getKey, Entry::getValue, (old, neew) -> old));
	}
}
