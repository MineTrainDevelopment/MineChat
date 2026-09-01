package de.minetrain.minechat.twitch;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.twitch4j.helix.domain.ModeratedChannel;
import com.github.twitch4j.helix.domain.Stream;

import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.gui.viewmodel.EmoteViewModel;
import de.minetrain.minechat.gui.viewmodel.StreamInfoViewModel;
import de.minetrain.minechat.main.Main;
import javafx.application.Platform;

/// A service that polls Twitch information at regular intervals.
///
/// This is needed because the Twitch EventSub has very restrictive subscriptions limits for information not directly related to the authenticated user.
public class TwitchPollingService {

	private static final Logger LOG = LoggerFactory.getLogger(TwitchPollingService.class);

	private final ScheduledExecutorService executorService;
	private final List<ScheduledFuture<?>> scheduledTasks;

	private boolean polling;

	public TwitchPollingService() {
		executorService = Executors.newSingleThreadScheduledExecutor();
		scheduledTasks = new ArrayList<>();
	}

	public void start() {
		if (isPolling()) {
			throw new IllegalStateException("Polling service is already running.");
		}
		polling = true;
		scheduledTasks.add(executorService.scheduleAtFixedRate(this::pollStreamInfo, 0, 5L, TimeUnit.SECONDS));
	}

	public void queueAvailableEmotesRefresh(String channelId) {
		if (!isPolling()) {
			throw new IllegalStateException("Polling service is not running.");
		}
		scheduledTasks.add(executorService.schedule(() -> pollAvailableEmotes(channelId), 0, TimeUnit.NANOSECONDS));
	}

	public void queueModeratedChannelsRefresh() {
		if (!isPolling()) {
			throw new IllegalStateException("Polling service is not running.");
		}
		scheduledTasks.add(executorService.schedule(this::pollModeratedChannels, 0, TimeUnit.NANOSECONDS));
	}

	public void queueChannelChatSettingsRefresh() {
		if (!isPolling()) {
			throw new IllegalStateException("Polling service is not running.");
		}
		scheduledTasks.add(executorService.schedule(this::pollChannelChatSettings, 0, TimeUnit.NANOSECONDS));
	}

	public void stop() {
		if (!isPolling()) {
			throw new IllegalStateException("Polling service is not running.");
		}
		polling = false;
		scheduledTasks.forEach(task -> task.cancel(false));
		scheduledTasks.clear();
	}

	public void shutdown() {
		polling = false;
		scheduledTasks.clear();
		executorService.shutdown();
	}

	protected boolean isPolling() {
		return polling;
	}

	protected ScheduledExecutorService getExecutorService() {
		return executorService;
	}

	private void pollStreamInfo() {
		try {
			Map<String, Stream> liveChannels = TwitchHelper.requestStreamInfo(Main.getChannelManager().getChannelViewModels().stream().map(ChannelViewModel::getChannelId).toArray(String[]::new)).get().stream()
				.collect(toMap(Stream::getUserId, Function.identity()));
			Platform.runLater(() -> {
				Main.getChannelManager().getChannelViewModels().forEach(channel -> {
					Stream stream = liveChannels.get(channel.getChannelId());
					StreamInfoViewModel streamInfo = channel.getStreamInfo();
					if (stream != null) {
						channel.setLive(true);
						streamInfo.setGameId(stream.getGameId());
						streamInfo.setGameName(stream.getGameName());
						streamInfo.setTitle(stream.getTitle());
						streamInfo.getTags().setAll(stream.getTags() != null ? stream.getTags() : List.of());
						streamInfo.setViewerCount(stream.getViewerCount());
						streamInfo.setStartedAt(stream.getStartedAtInstant());
						streamInfo.setMature(stream.isMature() != null && stream.isMature());
						streamInfo.setLanguage(stream.getLanguage());
						streamInfo.setThumbnailUrlTemplate(stream.getThumbnailUrlTemplate());
					} else {
						channel.setLive(false);
						streamInfo.setGameId("");
						streamInfo.setGameName("");
						streamInfo.setTitle("");
						streamInfo.getTags().clear();
						streamInfo.setViewerCount(0);
						streamInfo.setStartedAt(null);
						streamInfo.setMature(false);
						streamInfo.setLanguage("");
						streamInfo.setThumbnailUrlTemplate(null);
					}
				});
			});
		} catch (ExecutionException e) {
			LOG.error("Error fetching stream info for channels.", e.getCause());
		} catch (InterruptedException e) {
			LOG.error("Stream info polling was interrupted.", e);
			Thread.currentThread().interrupt();
		}
	}

	private void pollAvailableEmotes(String channelId) {
		try {
			Map<String, EmoteViewModel> emotes = TwitchHelper.requestAvailableUserEmotes(channelId).get().stream()
				.map(emote -> new EmoteViewModel(emote.getId(), emote.getName()))
				.collect(toMap(EmoteViewModel::getName, Function.identity(), (e1, _) -> e1));

			Main.getEmoteManager().cacheAvailableEmotesByName(channelId, emotes);
		} catch (ExecutionException e) {
			LOG.error("Error fetching available emotes for channel id: {}", channelId, e.getCause());
		} catch (InterruptedException e) {
			LOG.error("Emote polling was interrupted for channel id: {}", channelId, e);
			Thread.currentThread().interrupt();
		}
	}

	private void pollModeratedChannels() {
		try {
			Set<String> moderatedChannelIds = TwitchHelper.requestModeratedChannel().get().stream().map(ModeratedChannel::getBroadcasterId).collect(toUnmodifiableSet());
			Platform.runLater(() -> Main.getChannelManager().getChannelViewModels().forEach(channel -> channel.setModerated(moderatedChannelIds.contains(channel.getChannelId()))));
		} catch (ExecutionException e) {
			LOG.error("Error fetching moderated channels.", e.getCause());
		} catch (InterruptedException e) {
			LOG.error("Moderated channel polling was interrupted.", e);
			Thread.currentThread().interrupt();
		}
	}

	private void pollChannelChatSettings() {
		Main.getChannelManager().getChannelViewModels().forEach(channel -> {
			try {
				TwitchHelper.requestChannelChatSettings(channel.getChannelId()).thenAccept(chatSettings -> Platform.runLater(() -> channel.setSlowModeWaitTime(chatSettings.isSlowMode().booleanValue() ? chatSettings.getSlowModeWaitTime() : 0))).get();
			} catch (InterruptedException e) {
				LOG.error("Chat settings polling was interrupted for channel id: {}", channel.getChannelId(), e);
				Thread.currentThread().interrupt();
			} catch (ExecutionException e) {
				LOG.error("Error fetching chat settings for channel id: {}", channel.getChannelId(), e.getCause());
			}
		});
	}
}
