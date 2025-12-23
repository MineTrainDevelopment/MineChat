package de.minetrain.minechat.data.objectdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.serializer.concurrency.LockScope;
import org.eclipse.serializer.persistence.types.PersistenceStoring;
import org.eclipse.serializer.reference.Lazy;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;

public class Macros extends LockScope {

	private final Map<String, Lazy<Set<Macro>>> channelIdToMacros = new HashMap<>();
	private final Map<UUID, Macro> idToMacro = new HashMap<>();

	public void addMacro(Macro macro) {
		addMacro(macro, EclipseStoreKeeper.storeManager());
	}

	public void addMacro(Macro macro, PersistenceStoring persister) {
		write(() -> {
			List<Object> changedObjects = new ArrayList<>(2);
			addToMap(channelIdToMacros, macro.getChannelId(), macro, changedObjects);
			idToMacro.put(macro.getUuid(), macro);
			changedObjects.add(idToMacro);
			persister.storeAll(changedObjects);
		});
	}

	public void addMacros(Collection<Macro> macros) {
		addMacros(macros, EclipseStoreKeeper.storeManager());
	}

	public void addMacros(Collection<Macro> macros, PersistenceStoring persister) {
		write(() -> {
			List<Object> changedObjects = new ArrayList<>(macros.size() + 1);
			for (Macro macro : macros) {
				addToMap(channelIdToMacros, macro.getChannelId(), macro, changedObjects);
				idToMacro.put(macro.getUuid(), macro);
			}
			if (!changedObjects.isEmpty()) {
				changedObjects.add(idToMacro);
				persister.storeAll(changedObjects);
			}
		});
	}

	public List<Macro> all() {
		return read(() -> idToMacro.values().stream().toList());
	}

	public int size() {
		return read(idToMacro::size);
	}

	public <T> T computeByChannelId(String channelId, Function<Stream<Macro>, T> function) {
		return read(() -> {
			Set<Macro> set = Lazy.get(channelIdToMacros.get(channelId));
			return function.apply(set != null ? set.stream() : Stream.empty());
		});
	}

	public <T> T compute(Function<Stream<Macro>, T> function) {
		return read(() -> function.apply(idToMacro.values().stream()));
	}

	public Macro ofId(UUID macroId) {
		return read(() -> idToMacro.get(macroId));
	}

	private static <K> void addToMap(Map<K, Lazy<Set<Macro>>> map, K key, Macro macro, List<Object> changedObjects) {
		Lazy<Set<Macro>> lazy = map.get(key);
		if (lazy == null) {
			HashSet<Macro> set = new HashSet<>();
			set.add(macro);
			lazy = Lazy.Reference(set);
			map.put(key, lazy);
			changedObjects.add(map);
		} else {
			Set<Macro> set = lazy.get();
			set.remove(macro);
			set.add(macro);
			changedObjects.add(set);
		}
	}
}
