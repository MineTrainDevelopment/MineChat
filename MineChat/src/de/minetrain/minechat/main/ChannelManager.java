package de.minetrain.minechat.main;

import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.Channel;
import de.minetrain.minechat.data.objectdata.Channels;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.twitch.TwitchHelper;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import de.minetrain.minechat.utils.audio.AudioVolume;

public class ChannelManager {

	private static final Logger LOG = LoggerFactory.getLogger(ChannelManager.class);

	private Map<String, ChannelActions> channels = new HashMap<>();
	private String activeChannelId;

	public void init() {
		validateUserLogins().join();
		getAllChannels().forEach(channel -> TwitchHelper.joinChannel(channel.getChannelId()));

		if (getAllChannels().isEmpty()) {
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
	/// @param channelId The channel id to set as active.
	/// @return true if the active channel was changed, false if it was already the active channel.
	public boolean setActiveChannel(String channelId){
		if(channelId.equals(activeChannelId)){
			return false;
		}
		activeChannelId = channelId;
		return true;
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

	/// Validates and updates the login names of all persisted channels.
	/// Therefore fetches the latest user information from Twitch and updates the login names accordingly.
	///
	/// @return A CompletableFuture that completes when the validation and update process is finished.
	private static CompletableFuture<Void> validateUserLogins(){
		Channels channels = getChannels();
		return TwitchHelper.requestTwitchUsers(TwitchApiCallType.ID, channels.compute(s -> s.map(Channel::getChannelId).toArray(String[]::new)))
			.thenApplyAsync(users -> users.stream()
				.filter(not(TwitchUserObj::isDummy))
				.map(user -> {
					Channel channel = channels.ofId(user.getUserId());
					if (channel == null || channel.getLoginName().equals(user.getLoginName())) {
						return null;
					}
					return channel.buildCopy().withLoginName(user.getLoginName()).build();
				}).filter(Objects::nonNull).toList())
			.thenAcceptAsync(channelUpdates -> {
				if (channelUpdates.isEmpty()) {
					channels.addChannels(channelUpdates);
				}
			}).handle((_, e) -> {
				if (e != null) {
					LOG.error("Error validating user logins.", e);
				}
				return null;
			});
	}

	private static Channels getChannels() {
		return EclipseStoreKeeper.root().channels();
	}

	private static Channel createChannelFromTwitchUser(TwitchUserObj twitchUser) {
		return new Channel(twitchUser.getUserId(), twitchUser.getLoginName(), twitchUser.getDisplayName(),
				"Viewer", null,"Hello {USER} HeyGuys\nWelcome {USER} HeyGuys", "By {USER}!\nHave a good one! {USER} <3",
				"Welcome back {USER} <3\nwb {USER} HeyGuys",
				null, AudioVolume.VOLUME_100);
	}

	private static Channel createNewChannel(String channelId) {
		TwitchUserObj channel = TwitchHelper.requestTwitchUser(TwitchApiCallType.ID, channelId).join();
		if (channel.isDummy()) {
			return null;
		}

		Channel newChannel = createChannelFromTwitchUser(channel);
		getChannels().addChannel(newChannel);
		TwitchHelper.joinChannel(newChannel.getChannelId());

		TextureManager.downloadChannelEmotes(channelId);
		TextureManager.downloadBttvEmotes(channelId);
		TextureManager.downloadChannelBadges(channelId);
		return newChannel;
	}
}