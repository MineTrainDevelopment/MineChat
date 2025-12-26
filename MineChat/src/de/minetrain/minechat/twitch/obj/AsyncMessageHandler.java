package de.minetrain.minechat.twitch.obj;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.twitch.TwitchHelper;
import de.minetrain.minechat.utils.OutboundChatMessage;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/// Handles asynchronous sending of chat messages to Twitch channels, respecting rate limits and maintaining message history.
public class AsyncMessageHandler {

	private static final Logger LOG = LoggerFactory.getLogger(AsyncMessageHandler.class);

	private static record QueuedMessage(OutboundChatMessage message, Future<?> future) {}

	private final ReentrantReadWriteLock historyLock;
	private final ReentrantLock queueLock;
	private final ObservableList<QueuedMessage> queuedSends;
	private final IntegerProperty queueSizeProperty;
	private final Map<String, ExecutorService> channelExecutors;
	private final Deque<OutboundChatMessage> messageHistory;

	/// Constructs a new AsyncMessageHandler instance.
	public AsyncMessageHandler() {
		historyLock = new ReentrantReadWriteLock();
		queueLock = new ReentrantLock();
		queuedSends = FXCollections.observableList(new LinkedList<>());
		queueSizeProperty = new SimpleIntegerProperty(this, "queueSize", 0);
		channelExecutors = new HashMap<>();
		messageHistory = new LinkedList<>();
	}

	/// Queues a message to be sent asynchronously.
	///
	/// @param message The out bound chat message to be sent.
	public void queueMessage(OutboundChatMessage message) {
		writeQueue(() -> queuedSends.add(new QueuedMessage(message, getChannelExecutor(message.getChannel().getChannelId()).submit(() -> sendMessage(message)))));
	}

	/// An observable property representing the size of the message queue.
	///
	/// @return The observable integer value representing the queue size.
	public IntegerProperty queueSizeProperty() {
		return queueSizeProperty;
	}

	/// Retrieves the current size of the message queue.
	///
	/// @return The current queue size.
	public int getQueueSize() {
		return queueSizeProperty().get();
	}

	/// Clears all queued messages that have not yet been sent.
	public void clearQueue() {
		writeQueue(() -> {
			queuedSends.forEach(queuedMessage -> queuedMessage.future().cancel(true));
			queuedSends.clear();
		});
	}

	/// Sends a message to the Twitch API, respecting rate limits and updating the message history.
	///
	/// @param chatMessage The message to be sent.
	private void sendMessage(OutboundChatMessage chatMessage) {
		writeHistory(() -> messageHistory.removeIf(message -> System.currentTimeMillis() - message.getSendTime() > 30000L));
		long delay = computeDelay(chatMessage.getChannelViewModel());
		if (delay > 0) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				LOG.error("Message sending was interrupted: {}", chatMessage.getMessage(), e);
				Thread.currentThread().interrupt();
				return;
			}
		}
		writeQueue(() -> queuedSends.removeIf(queuedMessage -> queuedMessage.message() == chatMessage));
		LOG.info("Sending message: {}", chatMessage.getMessage());
		TwitchMessage replyMessage = chatMessage.getChannel().replyMessage;
		try {
			TwitchHelper.sendMessage(chatMessage.getChannel().getChannelId(), chatMessage.getMessage(),
				replyMessage != null ? replyMessage.getMessageId() : null).thenAccept(sentMessage -> {
					if (sentMessage.getDropReason() != null) {
						LOG.warn("Message was dropped: {} Reason: {}", chatMessage.getMessage(), sentMessage.getDropReason().getMessage());
					} else {
						LOG.info("Message sent successfully: {}", chatMessage.getMessage());
					}
				}).get();
			chatMessage.setSendTime(System.currentTimeMillis());
			writeHistory(() -> messageHistory.offerLast(chatMessage));
		} catch (InterruptedException e) {
			LOG.error("Message sending was interrupted: {}", chatMessage.getMessage(), e);
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			LOG.error("Failed to send message: {}", chatMessage.getMessage(), e.getCause());
		}
	}

	/// Computes the delay required before sending a message to a specific channel, based on the following table:
	/// | Limit | Description |
	/// | :-| :-|
	/// | 20 Messages per 30 seconds | If the user is not the channel’s broadcaster, a moderator, or a VIP, the bot may send a maximum of 20 messages per 30 seconds. |
	/// | 100 Messages per 30 seconds | If the user is the channel’s broadcaster, a moderator, or a VIP, the bot may send a maximum of 100 messages per 30 seconds. |
	/// | | NOTE: Messages by a user who is a broadcaster, moderator, or VIP will still add to the 20 messages per 30 seconds rate limit, but when that is breached messages from these users can still be sent until the larger rate limit bucket is filled. |
	/// | 1 message per second per channel | If the user is not the channel’s broadcaster, a moderator, or a VIP, the bot may send a maximum of 1 message per second per channel. |
	///
	/// @see [https://dev.twitch.tv/docs/chat/#rate-limits](https://dev.twitch.tv/docs/chat/#rate-limits)
	/// @param channel The channel to compute the delay for.
	/// @return The computed delay in milliseconds.
	private long computeDelay(ChannelViewModel channel) {
		if (channel.isModerated()) {
			if (readHistory(messageHistory::size) < 100) {
				return 0L;
			}
			OutboundChatMessage outboundChatMessage = readHistory(() -> messageHistory.stream().skip(messageHistory.size() - 100L).findFirst().orElse(null));
			if (outboundChatMessage != null) {
				return outboundChatMessage.getSendTime() + 30000L - System.currentTimeMillis();
			}
		}
		long overallDelay = 0L;
		if (readHistory(messageHistory::size) >= 20) {
			OutboundChatMessage outboundChatMessage =  readHistory(() -> messageHistory.stream().skip(messageHistory.size() - 20L).findFirst().orElse(null));
			if (outboundChatMessage != null) {
				overallDelay = outboundChatMessage.getSendTime() + 30000L - System.currentTimeMillis();
			}
		}
		OutboundChatMessage outboundChatMessage =  readHistory(() -> messageHistory.reversed().stream().filter(message -> Objects.equals(channel, message.getChannelViewModel())).findFirst().orElse(null));
		if (outboundChatMessage == null) {
			return overallDelay;
		}
		long channelDelay = outboundChatMessage.getSendTime() + Math.max(1000L, channel.getSlowModeWaitTime()) - System.currentTimeMillis();
		return Math.max(overallDelay, channelDelay);
	}

	/// Retrieves the executor service for a specific channel, creating a new one if it doesn't exist.
	/// Note: This method is not thread-safe and should be called within a locked context.
	///
	/// @param channelId The ID of the channel.
	/// @return The executor service for the specified channel.
	private ExecutorService getChannelExecutor(String channelId) {
		return channelExecutors.computeIfAbsent(channelId, _ -> Executors.newSingleThreadExecutor(Thread.ofVirtual().factory()));
	}

	/// Executes a given action within a locked context to ensure thread safety when accessing the message queue.
	///
	/// @param action The action to be executed.
	private void writeQueue(Runnable action) {
		try {
			queueLock.lock();
			action.run();
			Platform.runLater(() -> queueSizeProperty.set(queuedSends.size()));
		} finally {
			queueLock.unlock();
		}
	}

	/// Executes a given action within a locked context to ensure thread safety when accessing the message history.
	///
	/// @param action The action to be executed.
	private void writeHistory(Runnable action) {
		try {
			historyLock.writeLock().lock();
			action.run();
		} finally {
			historyLock.writeLock().unlock();
		}
	}

	/// Executes a given action within a read-locked context to ensure thread safety when accessing the message history.
	///
	/// @param action The action to be executed.
	/// @return The result of the action.
	private <T> T readHistory(Supplier<T> action) {
		try {
			historyLock.readLock().lock();
			return action.get();
		} finally {
			historyLock.readLock().unlock();
		}
	}
}
