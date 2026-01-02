package de.minetrain.minechat.data.objectdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.serializer.concurrency.LockScope;
import org.eclipse.serializer.persistence.types.PersistenceStoring;
import org.eclipse.serializer.reference.Lazy;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;

public class AutoReplies extends LockScope {

	private final Map<String, Lazy<Set<AutoReply>>> channelIdToAutoReplies = new HashMap<>();
	private final Map<UUID, AutoReply> idToAutoReply = new HashMap<>();

	public void addAutoReply(AutoReply autoReply) {
		addAutoReply(autoReply, EclipseStoreKeeper.storeManager());
	}

	public void addAutoReply(AutoReply autoReply, PersistenceStoring persister) {
		write(() -> {
			List<Object> changedObjects = new ArrayList<>(3);
			AutoReply old = ofId(autoReply.getUuid());
			if (old != null && !Objects.equals(old.getChannelId(), autoReply.getChannelId())) {
				Set<AutoReply> oldSet = channelIdToAutoReplies.get(old.getChannelId()).get();
				oldSet.remove(old);
				changedObjects.add(oldSet);
			}
			addToMap(channelIdToAutoReplies, autoReply.getChannelId(), autoReply, changedObjects);
			idToAutoReply.put(autoReply.getUuid(), autoReply);
			changedObjects.add(idToAutoReply);
			persister.storeAll(changedObjects);
		});
	}

	public void addAutoReplies(Collection<AutoReply> autoReplies) {
		addAutoReplies(autoReplies, EclipseStoreKeeper.storeManager());
	}

	public void addAutoReplies(Collection<AutoReply> autoReplies, PersistenceStoring persister) {
		write(() -> {
			List<Object> changedObjects = new ArrayList<>(autoReplies.size() * 2 + 1);
			for (AutoReply autoReply : autoReplies) {
				AutoReply old = ofId(autoReply.getUuid());
				if (old != null && !Objects.equals(old.getChannelId(), autoReply.getChannelId())) {
					Set<AutoReply> oldSet = channelIdToAutoReplies.get(old.getChannelId()).get();
					oldSet.remove(old);
					changedObjects.add(oldSet);
				}
				addToMap(channelIdToAutoReplies, autoReply.getChannelId(), autoReply, changedObjects);
				idToAutoReply.put(autoReply.getUuid(), autoReply);
			}
			if (!changedObjects.isEmpty()) {
				changedObjects.add(idToAutoReply);
				persister.storeAll(changedObjects);
			}
		});
	}

	public boolean removeAutoReply(UUID autoReplyId) {
		return removeAutoReply(autoReplyId, EclipseStoreKeeper.storeManager());
	}

	public boolean removeAutoReply(UUID autoReplyId, PersistenceStoring persister) {
		return write(() -> {
			AutoReply autoReply = ofId(autoReplyId);
			if (autoReply != null) {
				List<Object> changedObjects = new ArrayList<>(2);
				Set<AutoReply> set = channelIdToAutoReplies.get(autoReply.getChannelId()).get();
				set.remove(autoReply);
				changedObjects.add(set);
				idToAutoReply.remove(autoReplyId);
				changedObjects.add(idToAutoReply);
				persister.storeAll(changedObjects);
				return true;
			}
			return false;
		});
	}

	public List<AutoReply> all() {
		return read(() -> idToAutoReply.values().stream().toList());
	}

	public int size() {
		return read(idToAutoReply::size);
	}

	public <T> T computeByChannelId(String channelId, Function<Stream<AutoReply>, T> function) {
		return read(() -> {
			Set<AutoReply> set = Lazy.get(channelIdToAutoReplies.get(channelId));
			return function.apply(set != null ? set.stream() : Stream.empty());
		});
	}

	public <T> T compute(Function<Stream<AutoReply>, T> function) {
		return read(() -> function.apply(idToAutoReply.values().stream()));
	}

	public AutoReply ofId(UUID autoReplyId) {
		return read(() -> idToAutoReply.get(autoReplyId));
	}

	public void clear() {
		write(() -> {
			channelIdToAutoReplies.clear();
			idToAutoReply.clear();
		});
	}

	private static <K> void addToMap(Map<K, Lazy<Set<AutoReply>>> map, K key, AutoReply autoReply, List<Object> changedObjects) {
		Lazy<Set<AutoReply>> lazy = map.get(key);
		if (lazy == null) {
			HashSet<AutoReply> set = new HashSet<>();
			set.add(autoReply);
			lazy = Lazy.Reference(set);
			map.put(key, lazy);
			changedObjects.add(map);
		} else {
			Set<AutoReply> set = lazy.get();
			set.remove(autoReply);
			set.add(autoReply);
			changedObjects.add(set);
		}
	}
}
