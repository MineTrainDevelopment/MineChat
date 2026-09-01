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
import de.minetrain.minechat.data.objectdata.Badge;
import de.minetrain.minechat.data.objectdata.BadgeId;
import javafx.util.Pair;

public final class BadgeExtractorCacheLoader<P> implements CacheLoader<Pair<String, BadgeId>, P>, Serializable {

	private static final long serialVersionUID = -6883509265950472475L;

	private transient Function<Badge, P> extractor;

	private BadgeExtractorCacheLoader(Function<Badge, P> extractor) {
		this.extractor = extractor;
	}

	public static <P> Factory<CacheLoader<Pair<String, BadgeId>, P>> factoryOf(Function<Badge, P> extractor) {
		return new FactoryBuilder.SingletonFactory<>(new BadgeExtractorCacheLoader<>(extractor));
	}

	@Override
	public P load(Pair<String, BadgeId> key) throws CacheLoaderException {
		Badge badge = EclipseStoreKeeper.root().badges().of(key.getKey(), key.getValue());
		if (badge == null) {
			badge = EclipseStoreKeeper.root().badges().of("public", key.getValue());
		}
		return badge != null ? extractor.apply(badge) : null;
	}

	@Override
	public Map<Pair<String, BadgeId>, P> loadAll(Iterable<? extends Pair<String, BadgeId>> keys) throws CacheLoaderException {
		HashMap<Pair<String, BadgeId>, P> map = new HashMap<>();
		for (Pair<String, BadgeId> key : keys) {
			P value = load(key);
			if(value != null){
				map.put(key, value);
			}
		}
		return map;
	}
}
