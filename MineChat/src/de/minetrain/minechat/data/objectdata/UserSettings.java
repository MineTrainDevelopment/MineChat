package de.minetrain.minechat.data.objectdata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.serializer.concurrency.LockScope;
import org.eclipse.serializer.persistence.types.PersistenceStoring;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.features.messagehighlight.HighlightString;

public class UserSettings extends LockScope {

	private Map<UUID, HighlightString> highlightStrings = new HashMap<>();

	public void addHighlightString(HighlightString highlightString) {
		addHighlightString(highlightString, EclipseStoreKeeper.storeManager());
	}

	public void addHighlightString(HighlightString highlightString, PersistenceStoring persister) {
		write(() -> {
			highlightStrings.put(highlightString.getUuid(), highlightString);
			persister.store(highlightStrings);
		});
	}

	public HighlightString removeHighlightString(UUID uuid) {
		return removeHighlightString(uuid, EclipseStoreKeeper.storeManager());
	}

	public HighlightString removeHighlightString(UUID uuid, PersistenceStoring persister) {
		return write(() -> {
			HighlightString removedHighlightString = highlightStrings.remove(uuid);
			persister.store(highlightStrings);
			return removedHighlightString;
		});
	}

	public boolean isInitialized() {
		return read(() -> !highlightStrings.isEmpty());
	}

	public <T> T computeHighlightStrings(Function<Stream<HighlightString>, T> function) {
		return read(() -> function.apply(highlightStrings.values().stream()));
	}

	public Collection<HighlightString> getAllHighlightStrings() {
		return read(() -> Collections.unmodifiableCollection(highlightStrings.values()));
	}
}
