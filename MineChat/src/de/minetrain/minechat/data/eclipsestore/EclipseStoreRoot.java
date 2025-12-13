package de.minetrain.minechat.data.eclipsestore;

import java.util.List;
import java.util.Map;

import org.eclipse.serializer.collections.lazy.LazyArrayList;
import org.eclipse.serializer.collections.lazy.LazyHashMap;

import de.minetrain.minechat.data.objectdata.Channels;
import de.minetrain.minechat.data.objectdata.Emotes;
import de.minetrain.minechat.gui.obj.messages.MessageComponentContent;
import de.minetrain.minechat.main.ChannelActions;
import de.minetrain.minechat.twitch.obj.ChannelStatistics;

public class EclipseStoreRoot {
	private static final int maxMessagesSize = 10_000;

	private Map<String, List<MessageComponentContent>> twitchMessages;// Channel_id, data
	private Map<String, ChannelStatistics> channelStatics; // Channel_id, data
	private Channels channels;
	private Emotes emotes;

	public void addMessage(String channelId, MessageComponentContent message) {
		Map<String, List<MessageComponentContent>> twitchMessages = getTwitchMessages();
		if (!twitchMessages.containsKey(channelId)) {
			twitchMessages.put(channelId, new LazyArrayList<>());
			EclipseStoreKeeper.storeManager().store(twitchMessages);
		}

//		TODO: Zocki will testen... Mach wieder an :P
		List<MessageComponentContent> messages = twitchMessages.get(channelId);
//		if(messages.size() > maxMessagesSize){
//			messages.subList(0, 1000).clear();
//		}

		messages.add(message);
		EclipseStoreKeeper.storeManager().store(messages);
	}

	public List<MessageComponentContent> getMessages(ChannelActions channel) {
		return getTwitchMessages().getOrDefault(channel.getChannelId(), List.of());
	}

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

	// Lazy loading.
	private Map<String, List<MessageComponentContent>> getTwitchMessages() {
		if (twitchMessages == null) {
			twitchMessages = new LazyHashMap<>();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return twitchMessages;
	}

	private Map<String, ChannelStatistics> getChannelStatics() {
		if (channelStatics == null) {
			channelStatics = new LazyHashMap<>();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return channelStatics;
	}
}
