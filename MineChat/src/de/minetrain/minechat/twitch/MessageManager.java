package de.minetrain.minechat.twitch;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.features.macros.MacroViewModel;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.main.ChannelActions;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.AsyncMessageHandler;
import de.minetrain.minechat.utils.OutboundChatMessage;
import javafx.beans.property.IntegerProperty;

/// Manages the sending of chat messages to Twitch channels, including
/// handling message splitting for long messages.
public class MessageManager {

	private static final Logger LOG = LoggerFactory.getLogger(MessageManager.class);
	private static final int MAX_MESSAGE_LENGTH = 490;

	private static MessageManager instance = new MessageManager();

	private AsyncMessageHandler messageHandler;

	/// Sends a message to the specified channel, splitting it into multiple
	/// messages if it exceeds the maximum length.
	///
	/// @param channel The channel actions to send the message to.
	/// @param channelViewModel The channel view model associated with the channel.
	/// @param message The message to be sent.
	/// @see [MessageManager#sendMessage(OutboundChatMessage)]
	public static void sendMessage(ChannelActions channel, ChannelViewModel channelViewModel, String message) {
		if (message.length() > MAX_MESSAGE_LENGTH) {
			splitString(message).forEach(newMessage -> sendMessage(channel, channelViewModel, newMessage));
			return;
		}

		sendMessage(new OutboundChatMessage(channel, channelViewModel, TwitchManager.ownerChannelName, message));
	}

	/// Queues an outbound chat message for sending.
	///
	/// @param chatMessage The outbound chat message to be sent.
	/// @see [AsyncMessageHandler#queueMessage(OutboundChatMessage)]
	public static void sendMessage(OutboundChatMessage chatMessage) {
		instance().getMessageHandler().queueMessage(chatMessage);
	}

	/// Sends a message generated from a macro to the appropriate channel.
	///
	/// @param macro The macro view model containing the message to be sent.
	/// @see [MessageManager#sendMessage(ChannelActions, ChannelViewModel, String)]
	public static void sendMessage(MacroViewModel macro) {
		try {
			sendMessage(Main.getChannelManager().getChannelActions(macro.getChannel().getChannelId()), macro.getChannel(), macro.getRandomOutput());
		} catch (Exception ex) {
			LOG.error("Unable to send macro message: " + ex.getMessage(), ex);
		}
	}

	/// An observable property representing the size of the message queue.
	///
	/// @return The observable integer value representing the queue size.
	public static IntegerProperty queueSizeProperty() {
		return instance().getMessageHandler().queueSizeProperty();
	}

	/// Retrieves the current size of the message queue.
	///
	/// @return The current queue size.
	public static int getQueueSize() {
		return queueSizeProperty().get();
	}

	/// Splits a long message into smaller chunks that fit within the maximum
	/// message length, attempting to split at word or sentence boundaries.
	///
	/// @param input The input string to be split.
	/// @return A list of message chunks.
	private static List<String> splitString(String input) {
		List<String> chunks = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		int chunkSize = MAX_MESSAGE_LENGTH - 10;

		int wordBoundary = -1; // Index of the last space character within the chunk limit
		int sentenceBoundary = -1; // Index of the last sentence-ending character within the last 50 characters

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			builder.append(c);

			if (c == ' ') {
				wordBoundary = builder.length() - 1;
			}

//            if (builder.length() >= chunkSize - 100 && (c == '.')) {
			if (builder.length() >= chunkSize - 100 && (c == '.' || c == '!' || c == '?')) {
				sentenceBoundary = builder.length() - 1;
			}

			if (builder.length() == chunkSize) {
				if (sentenceBoundary != -1) {
					chunks.add(builder.substring(0, sentenceBoundary + 1));
					builder.delete(0, sentenceBoundary + 1);
					wordBoundary = -1;
					sentenceBoundary = -1;
				} else if (wordBoundary != -1) {
					chunks.add(builder.substring(0, wordBoundary));
					builder.delete(0, wordBoundary + 1);
					wordBoundary = -1;
					sentenceBoundary = -1;
				} else {
					chunks.add(builder.toString());
					builder.setLength(0);
					sentenceBoundary = -1;
				}
			}
		}

		// Add the remaining characters as the last chunk
		if (builder.length() > 0) {
			chunks.add(builder.toString());
		}

		for (int i = 0; i < chunks.size(); i++) {
			chunks.set(i, "(" + (i + 1) + ") " + chunks.get(i));
		}

		return chunks;
	}

	private static MessageManager instance() {
		return instance;
	}

	private MessageManager() {
		messageHandler = new AsyncMessageHandler();
	}

	private AsyncMessageHandler getMessageHandler() {
		return messageHandler;
	}
}
