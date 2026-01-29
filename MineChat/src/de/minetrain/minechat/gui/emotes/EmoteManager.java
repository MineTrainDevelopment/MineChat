package de.minetrain.minechat.gui.emotes;

import java.io.ByteArrayInputStream;
import java.util.Map;

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

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.BadgeId;
import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.gui.viewmodel.EmoteViewModel;
import de.minetrain.minechat.gui.viewmodel.IEmoteViewModel;
import javafx.scene.image.Image;
import javafx.util.Pair;

public class EmoteManager {

	public static final String PUBLIC_EMOTE_CHANNEL_ID = "ID_PUBLIC";

	private static final Logger LOG = LoggerFactory.getLogger(EmoteManager.class);

	private static final String TWITCH_EMOTE_URL = "https://static-cdn.jtvnw.net/emoticons/v2/{}/default/dark/1.0";

	private final Cache<String, Image> emoteImage1xCache;

	private final Cache<String, Map<String, Emote>> channelIdBttvNameToEmoteCache;

	private final Cache<Pair<String, BadgeId>, Image> badgeImage1xCache;

	private final Cache<String, Map<String, EmoteViewModel>> channelIdNameToEmoteCache;

	public static Emote getEmoteById(String emoteId){
		return  EclipseStoreKeeper.root().emotes().ofId(emoteId);
	}

	public EmoteManager() {
		CachingProvider cachingProvider = Caching.getCachingProvider();
		CacheManager cacheManager = cachingProvider.getCacheManager();
		MutableConfiguration<String,Image> image1xConfig = new MutableConfiguration<String, Image>()
			.setStoreByValue(false)
			.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.THIRTY_MINUTES))
			.setCacheLoaderFactory(EmoteExtractorCacheLoader.factoryOf(emote -> new Image(new ByteArrayInputStream(emote.getImage1x()))))
			.setReadThrough(true);
		emoteImage1xCache = cacheManager.createCache("emoteImageCacheSmall", image1xConfig);

		MutableConfiguration<String, Map<String, Emote>> channelIdBttvNameToEmoteConfig = new MutableConfiguration<String, Map<String, Emote>>()
			.setStoreByValue(false)
			.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.ONE_HOUR))
			.setCacheLoaderFactory(ChannelBttvEmotesCacheLoader.factory())
			.setReadThrough(true);

		channelIdBttvNameToEmoteCache = cacheManager.createCache("channelIdBttvNameToIdCache", channelIdBttvNameToEmoteConfig);

		MutableConfiguration<Pair<String, BadgeId>,Image> badgeImage1xConfig = new MutableConfiguration<Pair<String, BadgeId>, Image>()
			.setStoreByValue(false)
			.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.THIRTY_MINUTES))
			.setCacheLoaderFactory(BadgeExtractorCacheLoader.factoryOf(badge -> new Image(new ByteArrayInputStream(badge.getImage1x()))))
			.setReadThrough(true);
		badgeImage1xCache = cacheManager.createCache("badgeImageCacheSmall", badgeImage1xConfig);

		MutableConfiguration<String, Map<String, EmoteViewModel>> channelIdNameToEmoteConfig = new MutableConfiguration<String, Map<String, EmoteViewModel>>()
			.setStoreByValue(false);
		channelIdNameToEmoteCache = cacheManager.createCache("channelIdNameToEmoteCache", channelIdNameToEmoteConfig);
	}

	public void cacheAvailableEmotesByName(String channelId, Map<String, EmoteViewModel> availableEmotes) {
		channelIdNameToEmoteCache.put(channelId, availableEmotes);
	}

	public IEmoteViewModel getEmoteByName(String channelId, String emoteName) {
		Map<String, EmoteViewModel> nameToEmoteMap = channelIdNameToEmoteCache.get(channelId);
		if (nameToEmoteMap != null) {
			return nameToEmoteMap.get(emoteName);
		}
		return null;
	}

	/// Get emote image from cache or download it if not cached yet.
	///
	/// @param emoteId The ID of the emote.
	public Image getEmoteImage1x(String emoteId) {
		Image image = emoteImage1xCache.get(emoteId);
		if (image == null) {
			LOG.info("Downloading not cached emote image for id: {}", emoteId);
			String url = MessageFormatter.basicArrayFormat(TWITCH_EMOTE_URL, new Object[] { emoteId });
			image = new Image(url, true);
			emoteImage1xCache.put(emoteId, image);
		}
		return image;
	}

	public Map<String, Emote> getChannelBttvNameToEmoteMap(String channelId) {
		return channelIdBttvNameToEmoteCache.get(channelId);
	}

	public Emote getBttvEmoteByName(String channelId, String emoteName) {
		Map<String, Emote> nameToIdMap = getChannelBttvNameToEmoteMap(channelId);
		return nameToIdMap != null ? nameToIdMap.get(emoteName) : null;
	}

	public Image getBadgeImage1x(String channelId, BadgeId badgeId) {
		return badgeImage1xCache.get(new Pair<>(channelId, badgeId));
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
}

