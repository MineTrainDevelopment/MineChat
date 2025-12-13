package de.minetrain.minechat.gui.emotes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.Emote;

public final class EmoteExtractorCacheLoader<P> implements CacheLoader<String, P>, Serializable {

	private static final long serialVersionUID = 3552933840188982530L;

	private transient Function<Emote, P> extractor;

	private EmoteExtractorCacheLoader(Function<Emote, P> extractor) {
		this.extractor = extractor;
	}

	public static <P> Factory<CacheLoader<String, P>> factoryOf(Function<Emote, P> extractor) {
		return new FactoryBuilder.SingletonFactory<>(new EmoteExtractorCacheLoader<>(extractor));
	}

	@Override
	public P load(String key) throws CacheLoaderException {
		Emote emote = EclipseStoreKeeper.root().emotes().ofId(key);
		return emote != null ? extractor.apply(emote) : null;
	}

	@Override
	public Map<String, P> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
		HashMap<String, P> map = new HashMap<>();
		for (String string : keys) {
			P value = load(string);
			if(value != null){
				map.put(string, value);
			}
		}
		return map;
	}
}
