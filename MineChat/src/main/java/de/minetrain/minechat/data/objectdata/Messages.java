package de.minetrain.minechat.data.objectdata;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.serializer.collections.lazy.LazyArrayList;
import org.eclipse.serializer.concurrency.StripeLockScope;
import org.eclipse.serializer.persistence.types.PersistenceStoring;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;

public class Messages extends StripeLockScope {

	private final Map<String, List<ChatMessage>> channelIdToMessages = new ConcurrentHashMap<>();

	public int addMessage(ChatMessage message) {
		return addMessage(message, EclipseStoreKeeper.storeManager());
	}

	public int addMessage(ChatMessage message, PersistenceStoring persister) {
		return write(message.getChannelId(), () -> {
			List<ChatMessage> channelMessages = channelIdToMessages.get(message.getChannelId());
			if (channelMessages == null) {
				channelMessages = new LazyArrayList<>();
				channelMessages.add(message);
				channelIdToMessages.put(message.getChannelId(), channelMessages);
				write(this, () -> persister.store(channelIdToMessages));
				return 0;
			} else {
				channelMessages.add(message);
				persister.store(channelMessages);
				return channelMessages.size() - 1;
			}
		});
	}

	public ChatMessage ofId(String channelId, String messageId) {
		return read(channelId, () -> {
			List<ChatMessage> channelMessages = channelIdToMessages.get(channelId);
			if (channelMessages != null) {
				for (ChatMessage message : channelMessages.reversed()) {
					if (message.getMessageId().equals(messageId)) {
						return message;
					}
				}
			}
			return null;
		});
	}

	public List<ChatMessage> getMessagesByChannelId(String channelId) {
		List<ChatMessage> list = read(channelId, () -> channelIdToMessages.get(channelId));
		if (list != null) {
			return Collections.unmodifiableList(list);
		}
		list = write(channelId, () -> {
			List<ChatMessage> channelMessages = channelIdToMessages.get(channelId);
			if (channelMessages == null) {
				channelMessages = new LazyArrayList<>();
				channelIdToMessages.put(channelId, channelMessages);
				write(this, () -> EclipseStoreKeeper.storeManager().store(channelIdToMessages));
			}
			return channelMessages;
		});
		return Collections.unmodifiableList(list);
	}

	public <T> T computeByChannelId(String channelId, Function<Stream<ChatMessage>, T> function) {
		return read(channelId, () -> {
			List<ChatMessage> channelMessages = channelIdToMessages.get(channelId);
			return function.apply(channelMessages != null ? channelMessages.stream() : Stream.empty());
		});
	}

	public void clear(String channelId) {
		write(channelId, () -> channelIdToMessages.remove(channelId));
	}
}
