package de.minetrain.minechat.data.objectdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.serializer.concurrency.LockScope;
import org.eclipse.serializer.persistence.types.PersistenceStoring;
import org.eclipse.serializer.reference.Lazy;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;

public class Badges extends LockScope {

	private final Map<String, Lazy<Map<BadgeId, Badge>>> channelIdToBadges = new HashMap<>();

	public void addBadge(Badge badge) {
		addBadge(badge, EclipseStoreKeeper.storeManager());
	}

	public void addBadge(Badge badge, PersistenceStoring persister) {
		write(() -> {
			List<Object> changedObjects = new ArrayList<>(1);
			addToMap(channelIdToBadges, badge.getChannelId(), badge, changedObjects);
			persister.storeAll(changedObjects);
		});
	}

	public void addBadges(Collection<Badge> badges) {
		addBadges(badges, EclipseStoreKeeper.storeManager());
	}

	public void addBadges(Collection<Badge> badges, PersistenceStoring persister) {
		write(() -> {
			List<Object> changedObjects = new ArrayList<>(badges.size());
			for (Badge badge : badges) {
				addToMap(channelIdToBadges, badge.getChannelId(), badge, changedObjects);
			}
			if (!changedObjects.isEmpty()) {
				persister.storeAll(changedObjects);
			}
		});
	}

	public Badge of(String channelId, BadgeId badgeId) {
		return read(() -> {
			Map<BadgeId, Badge> badges = Lazy.get(channelIdToBadges.get(channelId));
			if (badges != null) {
				return badges.get(badgeId);
			}
			return null;
		});
	}

	public Map<BadgeId, Badge> getBadgesByChannelId(String channelId) {
		return read(() -> {
			Map<BadgeId, Badge> map = Lazy.get(channelIdToBadges.get(channelId));
			return map != null ? Collections.unmodifiableMap(map) : Collections.emptyMap();
		});
	}

	public void clear() {
		write(channelIdToBadges::clear);
	}

	private static <K> void addToMap(Map<K, Lazy<Map<BadgeId, Badge>>> map, K key, Badge badge, List<Object> changedObjects) {
		Lazy<Map<BadgeId, Badge>> lazy = map.get(key);
		if (lazy == null) {
			HashMap<BadgeId, Badge> innerMap = new HashMap<>();
			innerMap.put(badge.getBadgeId(), badge);
			lazy = Lazy.Reference(innerMap);
			map.put(key, lazy);
			changedObjects.add(map);
		} else {
			Map<BadgeId, Badge> innerMap = lazy.get();
			innerMap.put(badge.getBadgeId(), badge);
			changedObjects.add(innerMap);
		}
	}
}
