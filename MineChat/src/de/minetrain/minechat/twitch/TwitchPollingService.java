package de.minetrain.minechat.twitch;

import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.twitch4j.helix.domain.Stream;

import de.minetrain.minechat.data.objectdata.Channel;
import de.minetrain.minechat.main.Main;

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
			Set<String> liveChannelIds = TwitchHelper.requestStreamInfo(Main.getChannelManager().getAllChannels().stream().map(Channel::getChannelId).toArray(String[]::new)).get().stream()
				.map(Stream::getUserId)
				.collect(toUnmodifiableSet());
			Main.getChannelManager().channelsProperty().get().forEach(channelViewModel -> channelViewModel.setLive(liveChannelIds.contains(channelViewModel.getChannelId())));
		} catch (ExecutionException e) {
			LOG.error("Error fetching stream info for channels.", e.getCause());
		} catch (InterruptedException e) {
			LOG.error("Stream info polling was interrupted.", e);
			Thread.currentThread().interrupt();
		}
	}
}
