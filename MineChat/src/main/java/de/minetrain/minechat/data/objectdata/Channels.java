package de.minetrain.minechat.data.objectdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.serializer.concurrency.LockScope;
import org.eclipse.serializer.persistence.types.PersistenceStoring;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;

public class Channels extends LockScope {

	private final Map<String, Channel> idToChannel = new HashMap<>();

	public void addChannel(Channel channel) {
		addChannel(channel, EclipseStoreKeeper.storeManager());
	}

	public void addChannel(Channel channel, PersistenceStoring persister) {
		write(() -> {
			idToChannel.put(channel.getChannelId(), channel);
			persister.store(idToChannel);
		});
	}

	public void addChannels(Iterable<Channel> channels) {
		addChannels(channels, EclipseStoreKeeper.storeManager());
	}

	public void addChannels(Iterable<Channel> channels, PersistenceStoring persister) {
		write(() -> {
			channels.forEach(channel -> idToChannel.put(channel.getChannelId(), channel));
			persister.store(idToChannel);
		});
	}

	public List<Channel> all() {
		return read(() -> idToChannel.values().stream().toList());
	}

	public int size() {
		return read(idToChannel::size);
	}

	public <T> T compute(Function<Stream<Channel>, T> function) {
		return read(() -> function.apply(idToChannel.values().stream()));
	}

	public Channel ofId(String channelId) {
		return read(() -> idToChannel.get(channelId));
	}
}
