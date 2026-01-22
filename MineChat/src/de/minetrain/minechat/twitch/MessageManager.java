package de.minetrain.minechat.twitch;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.CountVariable;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.features.messagehighlight.HighlightType;
import de.minetrain.minechat.gui.frames.dialogs.CountVariableEditDialog;
import de.minetrain.minechat.gui.frames.dialogs.HighlightEditDialog;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.gui.viewmodel.HighlightViewModel;
import de.minetrain.minechat.gui.viewmodel.MacroViewModel;
import de.minetrain.minechat.gui.viewmodel.StreamInfoViewModel;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.AsyncMessageHandler;
import de.minetrain.minechat.utils.OutboundChatMessage;
import javafx.beans.property.IntegerProperty;

/// Manages the sending of chat messages to Twitch channels, including
/// handling message splitting for long messages.
public class MessageManager {

	private static final Logger LOG = LoggerFactory.getLogger(MessageManager.class);
	private static final int MAX_MESSAGE_LENGTH = 490;
	/// A regex pattern to identify count variable placeholders in messages.
	/// The pattern matches strings like {COUNT_X_VARIABLE}, {C_DISPLAY_VARIABLE}, etc.
	private static final Pattern COUNT_VARIABLE_PATTERN = Pattern.compile("\\{C(?:OUNT)?_(?:(\\d+|D(?:ISPLAY)?)_)?([A-Z]+)\\}");
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{(\\w+)\\}");

	private static MessageManager instance = new MessageManager();

	private AsyncMessageHandler messageHandler;
	private Map<String, IVariable> variables;
	private Runnable highlightChangeListener;

	/// Sends a message to the specified channel, splitting it into multiple
	/// messages if it exceeds the maximum length.
	///
	/// @param channelViewModel The channel view model associated with the channel.
	/// @param message The message to be sent.
	/// @see [MessageManager#sendMessage(OutboundChatMessage)]
	public static void sendMessage(ChannelViewModel channelViewModel, String message, String replyId) {
		String processedMessage = instance().processMessageString(message, channelViewModel);
		if (processedMessage.length() > MAX_MESSAGE_LENGTH) {
			splitString(processedMessage).forEach(newMessage -> sendMessage(channelViewModel, newMessage, replyId));
			return;
		}

		sendMessage(new OutboundChatMessage(channelViewModel, TwitchManager.ownerChannelName, processedMessage, replyId));
	}

	/// Queues an outbound chat message for sending.
	///
	/// @param chatMessage The outbound chat message to be sent.
	/// @see [AsyncMessageHandler#queueMessage(OutboundChatMessage)]
	protected static void sendMessage(OutboundChatMessage chatMessage) {
		instance().getMessageHandler().queueMessage(chatMessage);
	}

	/// Sends a message generated from a macro to the appropriate channel.
	///
	/// @param macro The macro view model containing the message to be sent.
	/// @see [MessageManager#sendMessage(ChannelActions, ChannelViewModel, String)]
	public static void sendMessage(MacroViewModel macro) {
		try {
			sendMessage(macro.getChannel(), macro.getRandomOutput(), null);
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

	/// Creates a new count variable through a dialog and adds it to the store.
	///
	/// @return An optional containing the created count variable, or empty if creation was cancelled.
	/// @see [CountVariableEditDialog]
	public static Optional<CountVariable> createCountVariable() {
		return new CountVariableEditDialog(CountVariable.builder()).showAndWait().map(editedCountVariable -> {
			EclipseStoreKeeper.root().countVariables().addCountVariable(editedCountVariable);
			return editedCountVariable;
		});
	}

	/// Edits an existing count variable through a dialog and updates it in the store.
	///
	/// @param countVariable The count variable to be edited.
	/// @return An optional containing the edited count variable, or empty if editing was cancelled.
	/// @see [CountVariableEditDialog]
	public static Optional<CountVariable> editCountVariable(CountVariable countVariable) {
		return new CountVariableEditDialog(countVariable.buildCopy()).showAndWait().map(editedCountVariable -> {
			if (!editedCountVariable.getName().equals(countVariable.getName())) {
				EclipseStoreKeeper.root().countVariables().removeCountVariable(countVariable.getName());
			}
			EclipseStoreKeeper.root().countVariables().addCountVariable(editedCountVariable);
			return editedCountVariable;
		});
	}

	/// Deletes a count variable from the store.
	///
	/// @param countVariable The count variable to be deleted.
	/// @return True if the count variable was successfully deleted, false otherwise.
	public static boolean deleteCountVariable(CountVariable countVariable) {
		return EclipseStoreKeeper.root().countVariables().removeCountVariable(countVariable.getName()) != null;
	}

	/// Retrieves all count variables from the store.
	///
	/// @return A collection of all count variables.
	public static Collection<CountVariable> getAllCountVariables() {
		return EclipseStoreKeeper.root().countVariables().getAllCountVariables();
	}

	/// Creates a new highlight string through a dialog and adds it to the store.
	///
	/// @return An optional containing the created highlight string, or empty if creation was cancelled.
	/// @see [HighlightEditDialog]
	public static Optional<HighlightString> createHighlightString() {
		Optional<HighlightString> result = new HighlightEditDialog(HighlightString.builder().withUuid(UUID.randomUUID())).showAndWait().map(editedHighlight -> {
			EclipseStoreKeeper.root().userSettings().addHighlightString(editedHighlight);
			return editedHighlight;
		});
		result.ifPresent(_ -> instance().notifyHighlightChangeListener());
		return result;
	}

	/// Edits an existing highlight string through a dialog and updates it in the store.
	///
	/// @param highlightString The highlight string to be edited.
	/// @return An optional containing the edited highlight string, or empty if editing was cancelled.
	/// @see [HighlightEditDialog]
	public static Optional<HighlightString> editHighlightString(HighlightString highlightString) {
		Optional<HighlightString> result = new HighlightEditDialog(highlightString.buildCopy()).showAndWait().map(editedHighlight -> {
			EclipseStoreKeeper.root().userSettings().addHighlightString(editedHighlight);
			return editedHighlight;
		});
		result.ifPresent(_ -> instance().notifyHighlightChangeListener());
		return result;
	}

	public static void updateHighlightString(HighlightString highlightString) {
		EclipseStoreKeeper.root().userSettings().addHighlightString(highlightString);
		instance().notifyHighlightChangeListener();
	}

	/// Deletes a highlight string from the store.
	///
	/// @param highlightString The highlight string to be deleted.
	/// @return True if the highlight string was successfully deleted, false otherwise.
	public static boolean deleteHighlightString(HighlightString highlightString) {
		boolean result = EclipseStoreKeeper.root().userSettings().removeHighlightString(highlightString.getUuid()) != null;
		instance().notifyHighlightChangeListener();
		return result;
	}

	/// Retrieves all highlight strings from the store.
	///
	/// @return A collection of all highlight strings.
	public static Collection<HighlightString> getAllHighlightStrings() {
		return EclipseStoreKeeper.root().userSettings().getAllHighlightStrings();
	}

	/// Updates a highlight setting in the store.
	///
	/// @param highlightViewModel The highlight view model containing the updated highlight settings.
	public static void updateHighlight(HighlightViewModel highlightViewModel) {
		EclipseStoreKeeper.root().userSettings().setHighlight(highlightViewModel.toHighlight());
		instance().notifyHighlightChangeListener();
	}

	/// Resets all highlights in the store to their default settings.
	public static void setDefaultHighlightSettings() {
		setHighlight(HighlightType.FIRST_MESSAGE, true, ColorManager.CHAT_MESSAGE_GREETING_HIGHLIGHT_DEFAULT);
		setHighlight(HighlightType.GOODBYE_MESSAGE, false, ColorManager.CHAT_MESSAGE_GREETING_HIGHLIGHT_DEFAULT);
		setHighlight(HighlightType.RETURN_MESSAGE, false, ColorManager.CHAT_MESSAGE_GREETING_HIGHLIGHT_DEFAULT);
		setHighlight(HighlightType.MODERATION, true,ColorManager.CHAT_MODERATION_DEFAULT);
		setHighlight(HighlightType.SUB, true, ColorManager.CHAT_UNIMPORTANT_DEFAULT);
		setHighlight(HighlightType.GIFT_SUB_SMALL, true, ColorManager.CHAT_SPENDING_SMALL_DEFAULT);
		setHighlight(HighlightType.GIFT_SUB_LARGE, true, ColorManager.CHAT_SPENDING_BIG_DEFAULT);
		setHighlight(HighlightType.INDIVIDUAL_GIFT_SUB, true, ColorManager.CHAT_UNIMPORTANT_DEFAULT);
		setHighlight(HighlightType.FOLLOW, true, ColorManager.CHAT_UNIMPORTANT_DEFAULT);
		setHighlight(HighlightType.CHEER, true, ColorManager.CHAT_SPENDING_SMALL_DEFAULT);
		setHighlight(HighlightType.MOD_ANNOUNCEMENT, true, ColorManager.CHAT_ANNOUNCEMENT_DEFAULT);
		setHighlight(HighlightType.USER_REWARD, true, ColorManager.CHAT_USER_REWARD_DEFAULT);
		setHighlight(HighlightType.HIGHLIGHT, true, ColorManager.CHAT_TWITCH_HIGHLIGHTED_DEFAULT);
		instance().notifyHighlightChangeListener();
	}

	private static void setHighlight(HighlightType type, boolean active, int color) {
		HighlightViewModel highlightViewModel = Main.getSettingsViewModel().getHighlightViewModel(type);
		highlightViewModel.setActive(active);
		highlightViewModel.setColor(color);
		EclipseStoreKeeper.root().userSettings().setHighlight(highlightViewModel.toHighlight());
	}

	/// Sets a listener to be called when highlight settings change.
	///
	/// @param listener The listener to be called on highlight changes.
	public static void setHighlightChangeListener(Runnable listener) {
		instance().highlightChangeListener = listener;
	}

	private void notifyHighlightChangeListener() {
		if (highlightChangeListener != null) {
			highlightChangeListener.run();
		}
	}

	private String processMessageString(String rawMessage, ChannelViewModel channelViewModel) {
		LocalDateTime now = LocalDateTime.now();
		Matcher matcher = COUNT_VARIABLE_PATTERN.matcher(rawMessage);
		StringBuilder processedMessage = new StringBuilder();
		while (matcher.find()) {
			String action = matcher.group(1);
			matcher.appendReplacement(processedMessage, Long.toString(processCountVariable(matcher.group(2), action != null ? action : "1")));
		}
		matcher.appendTail(processedMessage);
		String intermediateMessage = processedMessage.toString();

		matcher = VARIABLE_PATTERN.matcher(intermediateMessage);
		processedMessage = new StringBuilder();
		while (matcher.find()) {
			String name = matcher.group(1);
			IVariable variable = getVariable(name);
			if (variable != null) {
				matcher.appendReplacement(processedMessage, variable.retrieveValue(channelViewModel, now));
			}
		}
		matcher.appendTail(processedMessage);
		return processedMessage.toString();
	}

	private static long processCountVariable(String variable, String action) {
		CountVariable countVariable = action.startsWith("D")
			? EclipseStoreKeeper.root().countVariables().getOrCreateCountVariable(variable)
			: EclipseStoreKeeper.root().countVariables().getAndIncrementCountVariable(variable, Integer.parseInt(action));
		return countVariable.getValue();
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

		variables = new HashMap<>();
		registerVariable(new ClipboardVariable());
		registerVariable(new SimpleDateTimeVariable(dateTime -> dateTime.format(DateTimeFormatter.ofPattern(Settings.timeFormat)), "TIME"));
		registerVariable(new SimpleDateTimeVariable(dateTime -> dateTime.format(DateTimeFormatter.ofPattern(Settings.dateFormat)), "DATE"));
		registerVariable(new SimpleDateTimeVariable(dateTime -> dateTime.format(DateTimeFormatter.ofPattern(Settings.dayFormat)), "DAY"));
		registerVariable(new SimpleChannelVariable(cvm -> "@" + cvm.getChannelName(), "STREAMER", "CHANNEL"));
		registerVariable(new SimpleChannelVariable(_ -> "@" + TwitchHelper.getSelfUser().getDisplayName(), "MYSELF", "ME", "SELF"));
		registerVariable(new StreamInfoVariable(StreamInfoViewModel::getGameId, "GAME_ID"));
		registerVariable(new StreamInfoVariable(StreamInfoViewModel::getGameName, "GAME"));
		registerVariable(new StreamInfoVariable(StreamInfoViewModel::getTitle, "TITLE"));
		registerVariable(new StreamInfoVariable(streamInfo -> String.join(", ", streamInfo.getTags()), "TAGS"));
		registerVariable(new StreamInfoVariable(streamInfo -> Integer.toString(streamInfo.getViewerCount()), "VIEWER"));
		registerVariable(new StreamInfoVariable(streamInfo -> streamInfo.getStartedAt().toString(), "STARTED_AT")); // TODO nullsafe and formatting (as property?)
		registerVariable(new StreamInfoVariable(streamInfo -> Duration.between(streamInfo.getStartedAt(), Instant.now()).toString(), "UPTIME")); // TODO nullsafe and formatting (as property?)
	}

	private void registerVariable(IVariable variable) {
		for (String names : variable.getNames()) {
			variables.put(names, variable);
		}
	}

	private IVariable getVariable(String name) {
		return variables.get(name);
	}

	private AsyncMessageHandler getMessageHandler() {
		return messageHandler;
	}
}
