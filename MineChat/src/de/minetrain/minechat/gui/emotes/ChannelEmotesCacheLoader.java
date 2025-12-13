package de.minetrain.minechat.gui.emotes;

import static java.util.stream.Collectors.toMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.data.objectdata.Emotes;

public final class ChannelEmotesCacheLoader implements CacheLoader<String, Map<String, Emote>>, Serializable {

	private static final long serialVersionUID = -6726235329870645764L;

	private ChannelEmotesCacheLoader() {
	}

	public static Factory<CacheLoader<String, Map<String, Emote>>> factory() {
		return new FactoryBuilder.SingletonFactory<>(new ChannelEmotesCacheLoader());
	}

	@Override
	public Map<String, Emote> load(String key) throws CacheLoaderException {
		Emotes emotes = EclipseStoreKeeper.root().emotes();
		return emotes.computeByChannelId(key, emoteStream -> emotes.computeByChannelId(EmoteManager.PUBLIC_EMOTE_CHANNEL_ID, publicEmoteStream -> {
			return Stream.concat(emoteStream, publicEmoteStream)
					.collect(toMap(Emote::getName, Function.identity()));
		}));
	}

	@Override
	public Map<String, Map<String, Emote>> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
		HashMap<String, Map<String, Emote>> map = new HashMap<>();
		for (String string : keys) {
			Map<String, Emote> value = load(string);
			map.put(string, value);
		}
		return map;
	}
}
