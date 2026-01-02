package de.minetrain.minechat.main;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.AutoReplies;
import de.minetrain.minechat.data.objectdata.AutoReply.Builder;
import de.minetrain.minechat.data.objectdata.Channel;
import de.minetrain.minechat.data.objectdata.Channels;
import de.minetrain.minechat.data.objectdata.Macro;
import de.minetrain.minechat.data.objectdata.Macros;
import de.minetrain.minechat.features.macros.MacroType;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.frames.dialogs.AutoReplyEditDialog;
import de.minetrain.minechat.gui.frames.dialogs.MacroEditorDialog;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.gui.viewmodel.AutoReplyViewModel;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.gui.viewmodel.MacroViewModel;
import de.minetrain.minechat.twitch.TwitchHelper;
import de.minetrain.minechat.twitch.TwitchPollingService;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import de.minetrain.minechat.utils.audio.AudioVolume;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChannelManager {

	private static final Logger LOG = LoggerFactory.getLogger(ChannelManager.class);
	private static final int MACROS_PER_CHANNEL = 12;
	private static final int EMOTE_MACROS_PER_CHANNEL = 18;

	private Map<String, ChannelActions> channels = new HashMap<>();
	private TwitchPollingService twitchPollingService;

	private ObjectProperty<ChannelViewModel> activeChannelProperty;
	private ReadOnlyObjectWrapper<ObservableList<ChannelViewModel>> channelsProperty;

	public ChannelManager(TwitchPollingService twitchPollingService) {
		this.twitchPollingService = twitchPollingService;
		activeChannelProperty().addListener((_, _, newChannel) -> {
			if (newChannel != null) {
				this.twitchPollingService.queueAvailableEmotesRefresh(newChannel.getChannelId());
				this.twitchPollingService.queueModeratedChannelsRefresh();
			}
		});
	}

	public void init() {
		validateUsers().join();
		loadMissingChannelData();
		List<Channel> allChannels = getAllChannels();
		allChannels.forEach(channel -> TwitchHelper.joinChannel(channel.getChannelId()));

		List<ChannelViewModel> channelList = allChannels.stream().map(this::createNewChannelViewModel).toList();
		channelsPropertyInternal().set(FXCollections.observableArrayList(channelList));

		if (allChannels.isEmpty()) {
			addChannel(TwitchHelper.getSelfUser().getUserId());
		}

		CompletableFuture.delayedExecutor(300L, TimeUnit.MILLISECONDS).execute(() -> {
			if (!channelsProperty().get().isEmpty()) {
				setActiveChannel(channelsProperty().get().getFirst());
			}
		});
	}

	public String getActiveChanneldId() {
		ChannelViewModel activeChannel = getActiveChannel();
		return activeChannel != null ? activeChannel.getChannelId() : null;
	}

	public ChannelActions getActiveChannelActions() {
		ChannelViewModel activeChannel = getActiveChannel();
		return activeChannel != null ? getChannelActions(activeChannel.getChannelId()) : null;
	}

	/// Gets the ChannelActions for the given channel id.
	/// If the ChannelActions does not exist, it will be created.
	///
	/// @param channelId The channel id to get the ChannelActions for.
	/// @return The ChannelActions for the given channel id.
	public ChannelActions getChannelActions(String channelId) {
		return channels.computeIfAbsent(channelId, key -> new ChannelActions(getChannel(key)));
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

	public ObjectProperty<ChannelViewModel> activeChannelProperty() {
		if (activeChannelProperty == null) {
			activeChannelProperty = new SimpleObjectProperty<>(this, "activeChannel");
		}
		return activeChannelProperty;
	}

	public void setActiveChannel(ChannelViewModel channel) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> setActiveChannel(channel));
			return;
		}
		activeChannelProperty().set(channel);
	}

	public ChannelViewModel getActiveChannel() {
		return activeChannelProperty().get();
	}

	protected ReadOnlyObjectWrapper<ObservableList<ChannelViewModel>> channelsPropertyInternal() {
		if (channelsProperty == null) {
			channelsProperty = new ReadOnlyObjectWrapper<>(this, "channels");
		}
		return channelsProperty;
	}

	public ReadOnlyObjectProperty<ObservableList<ChannelViewModel>> channelsProperty() {
		return channelsPropertyInternal().getReadOnlyProperty();
	}

	public void editMacro(MacroViewModel macro) {
		Macro.Builder builder =  macro.toMacro().buildCopy();
		new MacroEditorDialog(builder).showAndWait().ifPresent(editedMacro -> {
			macro.apply(editedMacro);
			getMacros().addMacro(editedMacro);
		});
	}

	public Optional<AutoReplyViewModel> createAutoReply() {
		Builder builder = de.minetrain.minechat.data.objectdata.AutoReply.builder()
			.withUuid(UUID.randomUUID())
			.withDelay(0)
			.withMessagesPerMinute(1);
		return new AutoReplyEditDialog(builder).showAndWait().map(editedAutoReply -> {
			getAutoReplies().addAutoReply(editedAutoReply);
			ChannelViewModel cvm = Main.getChannelManager().channelsProperty().get().stream()
				.filter(channel -> Objects.equals(channel.getChannelId(), editedAutoReply.getChannelId()))
				.findFirst()
				.orElse(null);
			AutoReplyViewModel autoReplyViewModel = AutoReplyViewModel.of(editedAutoReply, cvm);
			cvm.getAutoReplies().add(autoReplyViewModel);
			return autoReplyViewModel;
		});
	}

	public void editAutoReply(AutoReplyViewModel autoReply) {
		Builder builder = autoReply.toAutoReply().buildCopy();
		new AutoReplyEditDialog(builder).showAndWait().ifPresent(editedAutoReply -> {
			autoReply.apply(editedAutoReply);
			getAutoReplies().addAutoReply(editedAutoReply);
		});
	}

	public boolean deleteAutoReply(AutoReplyViewModel autoReply) {
		autoReply.getChannel().getAutoReplies().remove(autoReply);
		return getAutoReplies().removeAutoReply(autoReply.getUuid());
	}

	/// Finds the ChannelViewModel for the given channel id.
	///
	/// @param channelId The channel id to find the ChannelViewModel for.
	/// @return An Optional containing the ChannelViewModel if found, or empty if not found.
	private Optional<ChannelViewModel> findViewModel(String channelId) {
		return channelsProperty().get().stream()
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

	private static Macros getMacros() {
		return EclipseStoreKeeper.root().macros();
	}

	private static AutoReplies getAutoReplies() {
		return EclipseStoreKeeper.root().autoReplies();
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
		ChannelViewModel cvm = createNewChannelViewModel(newChannel);
		channelsProperty().get().add(cvm);
		setActiveChannel(cvm);
		return newChannel;
	}

	private ChannelViewModel createNewChannelViewModel(Channel channel) {
		ChannelViewModel cvm = ChannelViewModel.of(channel);
		Map<MacroType, List<Macro>> macrosByType = getMacros().computeByChannelId(channel.getChannelId(), macros -> macros.sorted(Comparator.comparing(Macro::getIndex)).collect(groupingBy(Macro::getMacroType)));
		cvm.getMacros().addAll(createMacroViewModels(cvm, macrosByType, MacroType.TEXT, MACROS_PER_CHANNEL));
		cvm.getEmoteMacros().addAll(createMacroViewModels(cvm, macrosByType, MacroType.EMOTE, EMOTE_MACROS_PER_CHANNEL));
		cvm.getAutoReplies().addAll(getAutoReplies().computeByChannelId(channel.getChannelId(), autoReplies -> autoReplies.map(autoReply -> AutoReplyViewModel.of(autoReply, cvm)).toList()));

		cvm.initMessages();
		cvm.selectedProperty().bind(activeChannelProperty().isEqualTo(cvm));
		return cvm;
	}

	private MacroViewModel[] createMacroViewModels(ChannelViewModel cvm, Map<MacroType, List<Macro>> macros, MacroType type, int count) {
		MacroViewModel[] macroViewModels = new MacroViewModel[count];
		macros.getOrDefault(type, List.of()).forEach(m -> macroViewModels[m.getIndex()] = MacroViewModel.of(m, cvm, EmoteManager.getEmoteById(m.getEmoteId())));
		for (int i = 0; i < macroViewModels.length; i++) {
			if (macroViewModels[i] == null) {
				macroViewModels[i] = new MacroViewModel(cvm, i, type);
			}
		}
		return macroViewModels;
	}
}