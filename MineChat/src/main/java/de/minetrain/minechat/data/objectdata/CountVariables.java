package de.minetrain.minechat.data.objectdata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.serializer.concurrency.LockScope;
import org.eclipse.serializer.persistence.types.PersistenceStoring;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;

public class CountVariables extends LockScope {

	private final Map<String, CountVariable> nameToCountVariable = new HashMap<>();

	public void addCountVariable(CountVariable countVariable) {
		addCountVariable(countVariable, EclipseStoreKeeper.storeManager());
	}

	public void addCountVariable(CountVariable countVariable, PersistenceStoring persister) {
		write(() -> {
			nameToCountVariable.put(countVariable.getName(), countVariable);
			persister.store(nameToCountVariable);
		});
	}

	public CountVariable getCountVariable(String name) {
		return read(() -> nameToCountVariable.get(name));
	}

	public CountVariable getOrCreateCountVariable(String name) {
		CountVariable countVariable = read(() -> nameToCountVariable.get(name));
		if (countVariable == null) {
			countVariable = write(() -> {
				CountVariable cv = nameToCountVariable.get(name);
				if (cv == null) {
					cv = new CountVariable(name, 0L);
					nameToCountVariable.put(name, cv);
					EclipseStoreKeeper.storeManager().store(nameToCountVariable);
				}
				return cv;
			});
		}
		return countVariable;
	}

	public CountVariable removeCountVariable(String name) {
		return removeCountVariable(name, EclipseStoreKeeper.storeManager());
	}

	public CountVariable removeCountVariable(String name, PersistenceStoring persister) {
		return write(() -> {
			CountVariable removed = nameToCountVariable.remove(name);
			persister.store(nameToCountVariable);
			return removed;
		});
	}

	public CountVariable getAndIncrementCountVariable(String name, int increment) {
		return write(() -> {
			CountVariable current = nameToCountVariable.get(name);
			CountVariable updated = current == null
				? new CountVariable(name, increment)
				: current.buildCopy().withValue(current.getValue() + increment).build();
			nameToCountVariable.put(name, updated);
			EclipseStoreKeeper.storeManager().store(nameToCountVariable);
			return updated;
		});
	}

	public Collection<CountVariable> getAllCountVariables() {
		return read(() -> Collections.unmodifiableCollection(nameToCountVariable.values()));
	}
}
