package de.minetrain.minechat.main;

import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.Channel;
import de.minetrain.minechat.data.objectdata.Channels;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.twitch.TwitchHelper;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import de.minetrain.minechat.utils.audio.AudioVolume;
import javafx.application.Platform;

public class ChannelManager {

	private static final Logger LOG = LoggerFactory.getLogger(ChannelManager.class);

	private Map<String, ChannelActions> channels = new HashMap<>();
	private String activeChannelId;

	public void init() {
		validateUsers().join();
		loadMissingChannelData();
		List<Channel> allChannels = getAllChannels();
		allChannels.forEach(channel -> TwitchHelper.joinChannel(channel.getChannelId()));

		if (allChannels.isEmpty()) {
			addChannel(TwitchHelper.getSelfUser().getUserId());
		}
	}

	public String getActiveChanneldId() {
		return activeChannelId;
	}

	public ChannelActions getActiveChannelActions() {
		if (activeChannelId == null) {
			return null;
		}
		return getChannelActions(activeChannelId);
	}

	/// Gets the ChannelActions for the given channel id.
	/// If the ChannelActions does not exist, it will be created.
	///
	/// @param channelId The channel id to get the ChannelActions for.
	/// @return The ChannelActions for the given channel id.
	public ChannelActions getChannelActions(String channelId) {
		return channels.computeIfAbsent(channelId, key -> new ChannelActions(getChannel(key)));
	}

	/// Sets the active channel.
	///
	/// @param channelViewModel The channel to set as active.
	/// @return true if the active channel was changed, false if it was already the active channel.
	public boolean setActiveChannel(ChannelViewModel channelViewModel) {
		if (channelViewModel.getChannelId().equals(activeChannelId)) {
			return false;
		}
		activeChannelId = channelViewModel.getChannelId();
		Platform.runLater(() -> {
			Main.titleBar.setSelectedChannel(channelViewModel);
			getActiveChannelActions().loadViewPort();
		});
		return true;
	}

	/// Sets the active channel by channel id.
	///
	/// @param channelId The channel id to set as active.
	public void setActiveChannel(String channelId) {
		findViewModel(channelId).ifPresent(this::setActiveChannel);
	}

	/// Adds a new channel to the ChannelManager.
	/// If the channel already exists, null is returned.
	///
	/// @param channelId The channel id to add.
	/// @return The newly created Channel, or null if the channel already exists.
	public Channel addChannel(String channelId) {
		return getChannel(channelId) == null ? createNewChannel(channelId) : null;
	}

	/// Gets the Channel object for the given channel id.
	///
	/// @param channelId The channel id to get the Channel object for.
	/// @return The Channel object for the given channel id, or null if it does not exist.
	public Channel getChannel(String channelId) {
		return getChannels().ofId(channelId);
	}

	/// Gets a list of all channels.
	///
	/// @return A list of all channels.
	public List<Channel> getAllChannels(){
		return getChannels().all();
	}

	public Collection<ChannelActions> getAllChannelActions(){
		return channels.values().stream().toList();
	}

	public void setChannelLiveStatus(String channelId, boolean isLive) {
		findViewModel(channelId).ifPresent(cvm -> cvm.setLive(isLive));
	}

	/// Finds the ChannelViewModel for the given channel id.
	///
	/// @param channelId The channel id to find the ChannelViewModel for.
	/// @return An Optional containing the ChannelViewModel if found, or empty if not found.
	private Optional<ChannelViewModel> findViewModel(String channelId) {
		return Main.titleBar.getChannels().stream()
			.filter(c -> c.getChannelId().equals(channelId))
			.findFirst();
	}

	/// Validates and updates the login names of all persisted channels.
	/// Therefore fetches the latest user information from Twitch and updates the login names accordingly.
	///
	/// @return A CompletableFuture that completes when the validation and update process is finished.
	private static CompletableFuture<Void> validateUsers(){
		Channels channels = getChannels();
		return TwitchHelper.requestTwitchUsers(TwitchApiCallType.ID, channels.compute(s -> s.map(Channel::getChannelId).toArray(String[]::new)))
			.thenApplyAsync(users -> users.stream()
				.filter(not(TwitchUserObj::isDummy))
				.map(user -> {
					Channel channel = channels.ofId(user.getUserId());
					if (channel == null || (Objects.equals(channel.getLoginName(), user.getLoginName()) && Objects.equals(channel.getDisplayName(), user.getDisplayName())) && Objects.equals(channel.getProfileImageUrl(), user.getProfileImageUrl())) {
						return null;
					}
					LOG.info("Updating channel info for {}: loginName='{}' -> '{}', displayName='{}' -> '{}'", user.getUserId(), channel.getLoginName(), user.getLoginName(), channel.getDisplayName(), user.getDisplayName());
					return channel.buildCopy().withLoginName(user.getLoginName()).withDisplayName(user.getDisplayName()).withProfileImageUrl(user.getProfileImageUrl()).build();
				}).filter(Objects::nonNull).toList())
			.thenAcceptAsync(channelUpdates -> {
				if (!channelUpdates.isEmpty()) {
					channels.addChannels(channelUpdates);
				}
			}).handle((_, e) -> {
				if (e != null) {
					LOG.error("Error validating user logins.", e);
				}
				return null;
			});
	}

	private void loadMissingChannelData() {
		CompletableFuture.runAsync(() -> getAllChannels().forEach(channel -> TextureManager.downloadMissingChannelData(channel.getChannelId()).join()));
	}

	private static Channels getChannels() {
		return EclipseStoreKeeper.root().channels();
	}

	private static Channel createChannelFromTwitchUser(TwitchUserObj twitchUser) {
		return new Channel(twitchUser.getUserId(), twitchUser.getLoginName(), twitchUser.getDisplayName(),
				"Viewer", null,"Hello {USER} HeyGuys\nWelcome {USER} HeyGuys", "By {USER}!\nHave a good one! {USER} <3",
				"Welcome back {USER} <3\nwb {USER} HeyGuys",
				null, AudioVolume.VOLUME_100, twitchUser.getProfileImageUrl());
	}

	private Channel createNewChannel(String channelId) {
		TwitchUserObj channel = TwitchHelper.requestTwitchUser(TwitchApiCallType.ID, channelId).join();
		if (channel.isDummy()) {
			return null;
		}

		Channel newChannel = createChannelFromTwitchUser(channel);
		getChannels().addChannel(newChannel);
		TwitchHelper.joinChannel(newChannel.getChannelId());

		TextureManager.downloadChannelEmotes(channelId, true);
		TextureManager.downloadBttvEmotes(channelId, true);
		TextureManager.downloadChannelBadges(channelId, true);
		Platform.runLater(() -> {
			ChannelViewModel channelViewModel = ChannelViewModel.of(newChannel);
			Main.titleBar.getChannels().add(channelViewModel);
			setActiveChannel(channelViewModel);
		});
		return newChannel;
	}
}