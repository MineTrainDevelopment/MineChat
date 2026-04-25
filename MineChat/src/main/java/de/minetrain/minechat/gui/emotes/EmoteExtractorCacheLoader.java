package de.minetrain.minechat.gui.emotes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.gui.utils.LazyGifValidatingInputStream;
import javafx.scene.image.Image;

public final class EmoteExtractorCacheLoader implements CacheLoader<String, Image>, Serializable {

	private static final long serialVersionUID = 3552933840188982530L;

	private static final String TWITCH_EMOTE_URL = "https://static-cdn.jtvnw.net/emoticons/v2/{}/default/dark/1.0";

	private static final Logger LOG = LoggerFactory.getLogger(EmoteExtractorCacheLoader.class);

	private transient Function<Emote, Image> extractor;
	private final boolean urlFallback;

	private EmoteExtractorCacheLoader(Function<Emote, Image> extractor, boolean urlFallback) {
		this.extractor = extractor;
		this.urlFallback = urlFallback;
	}

	/// Creates a factory that loads from EclipseStore only.
	public static Factory<CacheLoader<String, Image>> factoryOf(Function<Emote, Image> extractor) {
		return new FactoryBuilder.SingletonFactory<>(new EmoteExtractorCacheLoader(extractor, false));
	}

	/// Creates a factory that loads from EclipseStore, falling back to a lazy URL download if not found.
	/// The extractor is used when the emote is found in the store;
	/// for unknown emote IDs a [LazyGifValidatingInputStream]-backed [Image] is returned.
	public static Factory<CacheLoader<String, Image>> imageFactoryWithUrlFallback(Function<Emote, Image> extractor) {
		return new FactoryBuilder.SingletonFactory<>(new EmoteExtractorCacheLoader(extractor, true));
	}

	@Override
	public Image load(String key) throws CacheLoaderException {
		Emote emote = EclipseStoreKeeper.root().emotes().ofId(key);
		if (emote != null) {
			LOG.debug("Using locally stored emote image for id: {}", key);
			return extractor.apply(emote);
		}
		if (urlFallback) {
			LOG.info("Downloading not cached emote image for id: {}", key);
			String url = MessageFormatter.basicArrayFormat(TWITCH_EMOTE_URL, new Object[] { key });
			return new Image(new LazyGifValidatingInputStream(url), true);
		}
		return null;
	}

	@Override
	public Map<String, Image> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
		HashMap<String, Image> map = new HashMap<>();
		for (String key : keys) {
			Image value = load(key);
			if (value != null) {
				map.put(key, value);
			}
		}
		return map;
	}
}
