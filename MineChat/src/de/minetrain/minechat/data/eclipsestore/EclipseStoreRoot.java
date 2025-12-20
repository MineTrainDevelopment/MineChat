package de.minetrain.minechat.data.eclipsestore;

import java.util.Map;

import org.eclipse.serializer.collections.lazy.LazyHashMap;

import de.minetrain.minechat.data.objectdata.Channels;
import de.minetrain.minechat.data.objectdata.Credentials;
import de.minetrain.minechat.data.objectdata.Emotes;
import de.minetrain.minechat.data.objectdata.Messages;
import de.minetrain.minechat.twitch.obj.ChannelStatistics;

public class EclipseStoreRoot {

	private Map<String, ChannelStatistics> channelStatics; // Channel_id, data
	private Channels channels;
	private Emotes emotes;
	private Credentials credentials;
	private Messages messages;

	private void addChannelStatistics(String channelId, ChannelStatistics statistics) {
		getChannelStatics().put(channelId, statistics);
		EclipseStoreKeeper.storeManager().store(getChannelStatics());
	}

	public ChannelStatistics getChannelStatistics(String channelId) {
		if (!getChannelStatics().containsKey(channelId)) {
			addChannelStatistics(channelId, new ChannelStatistics(channelId));
		}

		return getChannelStatics().get(channelId);
	}

	public void saveAllChannelStatistics() {
		getChannelStatics().values().forEach(stats -> stats.saveChannelStatistics(EclipseStoreKeeper.storeManager()));
	}

	public Channels channels() {
		if (channels == null) {
			channels = new Channels();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return channels;
	}

	public Emotes emotes() {
		if (emotes == null) {
			emotes = new Emotes();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return emotes;
	}

	public Credentials credentials() {
		if (credentials == null) {
			credentials = new Credentials();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return credentials;
	}

	public Messages messages() {
		if (messages == null) {
			messages = new Messages();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return messages;
	}

	private Map<String, ChannelStatistics> getChannelStatics() {
		if (channelStatics == null) {
			channelStatics = new LazyHashMap<>();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return channelStatics;
	}
}
