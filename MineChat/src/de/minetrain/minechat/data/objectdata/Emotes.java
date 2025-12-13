package de.minetrain.minechat.data.objectdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.serializer.collections.lazy.LazyHashMap;
import org.eclipse.serializer.concurrency.LockScope;
import org.eclipse.serializer.persistence.types.PersistenceStoring;
import org.eclipse.serializer.reference.Lazy;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;

public class Emotes extends LockScope {

	private final Map<String, Lazy<Set<Emote>>> channelIdToEmotes = new HashMap<>();
	private final Map<String, Lazy<Set<Emote>>> setIdToEmotes = new HashMap<>();
	private final Map<String, Emote> emoteIdToEmote = new LazyHashMap<>();

	public void addEmote(Emote emote) {
		addEmote(emote, EclipseStoreKeeper.storeManager());
	}

	public void addEmote(Emote emote, PersistenceStoring persister) {
		write(() -> {
			List<Object> changedObjects = new ArrayList<>(1);
			addToMap(channelIdToEmotes, emote.getChannelId(), emote, changedObjects);
			addToMap(setIdToEmotes, emote.getSetId(), emote, changedObjects);
			emoteIdToEmote.put(emote.getEmoteId(), emote);
			changedObjects.add(emoteIdToEmote);
			persister.store(changedObjects);
		});
	}

	public void addEmotes(Collection<Emote> emotes) {
		addEmotes(emotes, EclipseStoreKeeper.storeManager());
	}

	public void addEmotes(Collection<Emote> emotes, PersistenceStoring persister) {
		write(() -> {
			List<Object> changedObjects = new ArrayList<>(emotes.size());
			for (Emote emote : emotes) {
				addToMap(channelIdToEmotes, emote.getChannelId(), emote, changedObjects);
				addToMap(setIdToEmotes, emote.getSetId(), emote, changedObjects);
				emoteIdToEmote.put(emote.getEmoteId(), emote);
			}
			if (!changedObjects.isEmpty()) {
				changedObjects.add(emoteIdToEmote);
				persister.store(changedObjects);
			}
		});
	}

	public Emote ofId(String emoteId) {
		return read(() -> emoteIdToEmote.get(emoteId));
	}

	public List<Emote> getEmotesByChannelId(String channelId) {
		return read(() -> {
			Set<Emote> set = Lazy.get(channelIdToEmotes.get(channelId));
			return set != null ? set.stream().toList() : List.of();
		});
	}

	public List<Emote> getEmotesBySetId(String setId) {
		return read(() -> {
			Set<Emote> set = Lazy.get(setIdToEmotes.get(setId));
			return set != null ? set.stream().toList() : List.of();
		});
	}

	public <T> T computeByChannelId(String channelId, Function<Stream<Emote>, T> function) {
		return read(() -> {
			Set<Emote> set = Lazy.get(channelIdToEmotes.get(channelId));
			return function.apply(set != null ? set.stream() : Stream.empty());
		});
	}

	public <T> T computeBySetId(String setId, Function<Stream<Emote>, T> function) {
		return read(() -> {
			Set<Emote> set = Lazy.get(setIdToEmotes.get(setId));
			return function.apply(set != null ? set.stream() : Stream.empty());
		});
	}

	public void clear() {
		write(() -> {
			channelIdToEmotes.clear();
			setIdToEmotes.clear();
			emoteIdToEmote.clear();
		});
	}

	private static <K> void addToMap(Map<K, Lazy<Set<Emote>>> map, K key, Emote emote, List<Object> changedObjects) {
		Lazy<Set<Emote>> lazy = map.get(key);
		if (lazy == null) {
			HashSet<Emote> set = new HashSet<>();
			set.add(emote);
			lazy = Lazy.Reference(set);
			map.put(key, lazy);
			changedObjects.add(map);
		} else {
			Set<Emote> set = lazy.get();
			set.add(emote);
			changedObjects.add(set);
		}
	}
}
