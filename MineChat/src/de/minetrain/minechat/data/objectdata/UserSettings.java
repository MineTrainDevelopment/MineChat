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
import de.minetrain.minechat.features.messagehighlight.Highlight;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.features.messagehighlight.HighlightType;

public class UserSettings extends LockScope {

	private final Map<UUID, HighlightString> highlightStrings = new HashMap<>();
	private final Map<HighlightType, Highlight> highlights = new HashMap<>(); // NOSONAR: EnumMap is currently not supported by Eclipse Serializer
	private boolean isInitialized = false;

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

	public void setHighlight(Highlight highlight) {
		setHighlight(highlight, EclipseStoreKeeper.storeManager());
	}

	public void setHighlight(Highlight highlight, PersistenceStoring persister) {
		write(() -> {
			highlights.put(highlight.getType(), highlight);
			persister.store(highlights);
		});
	}

	public boolean isInitialized() {
		return read(() -> !isInitialized);
	}

	public void setInitialized() {
		write(() -> {
			isInitialized = true;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public <T> T computeHighlightStrings(Function<Stream<HighlightString>, T> function) {
		return read(() -> function.apply(highlightStrings.values().stream()));
	}

	public Collection<HighlightString> getAllHighlightStrings() {
		return read(() -> Collections.unmodifiableCollection(highlightStrings.values()));
	}

	public Highlight getHighlight(HighlightType type) {
		return read(() -> highlights.get(type));
	}

	public Map<HighlightType, Highlight> getAllHighlights() {
		return read(() -> Collections.unmodifiableMap(highlights));
	}
}
