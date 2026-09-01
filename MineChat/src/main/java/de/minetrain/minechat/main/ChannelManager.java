package de.minetrain.minechat.main;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.AutoReplies;
import de.minetrain.minechat.data.objectdata.AutoReply;
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
import de.minetrain.minechat.gui.utils.UiDispatcher;
import de.minetrain.minechat.gui.viewmodel.AutoReplyViewModel;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.gui.viewmodel.MacroViewModel;
import de.minetrain.minechat.twitch.TwitchHelper;
import de.minetrain.minechat.twitch.TwitchPollingService;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import de.minetrain.minechat.utils.audio.AudioVolume;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class ChannelManager {

	private static final Logger LOG = LoggerFactory.getLogger(ChannelManager.class);
	private static final int MACROS_PER_CHANNEL = 12;
	private static final int EMOTE_MACROS_PER_CHANNEL = 18;

	private TwitchPollingService twitchPollingService;

	private ObjectProperty<ChannelViewModel> activeChannelProperty;
	private ObservableMap<String, ChannelViewModel> channels;

	public ChannelManager(TwitchPollingService twitchPollingService) {
		channels = FXCollections.observableHashMap();

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

		Map<String, ChannelViewModel> channelMap = allChannels.stream().map(this::createNewChannelViewModel).collect(toMap(ChannelViewModel::getChannelId, Function.identity()));
		channels.putAll(channelMap);

		if (allChannels.isEmpty()) {
			addChannel(TwitchHelper.getSelfUser().getUserId(), 0);
		}

		CompletableFuture.delayedExecutor(300L, TimeUnit.MILLISECONDS).execute(() -> {
			if (!getChannelViewModels().isEmpty()) {
				getChannelViewModels().stream()
					.min(Comparator.comparingInt(ChannelViewModel::getSortIndex))
					.ifPresent(this::setActiveChannel);
			}
		});
	}

	public String getActiveChanneldId() {
		ChannelViewModel activeChannel = getActiveChannel();
		return activeChannel != null ? activeChannel.getChannelId() : null;
	}

	/// Adds a new channel to the ChannelManager.
	/// If the channel already exists, null is returned.
	///
	/// @param channelId The channel id to add.
	/// @param insertIndex The index to insert the channel at.
	/// @return The newly created Channel, or null if the channel already exists.
	public Channel addChannel(String channelId, int insertIndex) {
		return getChannel(channelId) == null ? createNewChannel(channelId, insertIndex) : null;
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

	public Collection<ChannelViewModel> getChannelViewModels() {
		return channels.values();
	}

	public ChannelViewModel getChannelViewModel(String channelId) {
		return channels.get(channelId);
	}

	public ObservableMap<String, ChannelViewModel> getChannelViewModelMap() {
		return channels;
	}

	public ObjectProperty<ChannelViewModel> activeChannelProperty() {
		if (activeChannelProperty == null) {
			activeChannelProperty = new SimpleObjectProperty<>(this, "activeChannel");
		}
		return activeChannelProperty;
	}

	public void setActiveChannel(ChannelViewModel channel) {
		UiDispatcher.runOnUiThread(() -> activeChannelProperty().set(channel));
	}

	public ChannelViewModel getActiveChannel() {
		return activeChannelProperty().get();
	}

	public void moveChannel(ChannelViewModel moveChannel, ChannelViewModel targetChannel) {
		List<ChannelViewModel> channelList = new ArrayList<>(getChannelViewModels());
		channelList.sort(Comparator.comparingInt(ChannelViewModel::getSortIndex));
		int moveIndex = channelList.indexOf(moveChannel);
		int targetIndex = channelList.indexOf(targetChannel);
		if (moveIndex < targetIndex) {
			Collections.rotate(channelList.subList(moveIndex, targetIndex + 1), -1);
		} else {
			Collections.rotate(channelList.subList(targetIndex, moveIndex + 1), 1);
		}
		for (int i = 0; i < channelList.size(); i++) {
			channelList.get(i).setSortIndex(i);
		}
		List<Channel> updatedChannels = getChannels().all().stream()
			.map(c -> {
				String channelId = c.getChannelId();
				ChannelViewModel channelViewModel = getChannelViewModel(channelId);
				return channelViewModel.getSortIndex() != c.getSortIndex() ?
					c.buildCopy().withSortIndex(channelViewModel.getSortIndex()).build()
					: null;
			}).filter(Objects::nonNull).toList();
		getChannels().addChannels(updatedChannels);
	}

	public void editMacro(MacroViewModel macro) {
		Macro.Builder builder =  macro.toMacro().buildCopy();
		new MacroEditorDialog(builder).showAndWait().ifPresent(editedMacro -> {
			macro.apply(editedMacro);
			getMacros().addMacro(editedMacro);
		});
	}

	public Optional<AutoReplyViewModel> createAutoReply() {
		Builder builder = AutoReply.builder()
			.withUuid(UUID.randomUUID())
			.withDelay(0)
			.withMessagesPerMinute(1);
		return new AutoReplyEditDialog(builder).showAndWait().map(editedAutoReply -> {
			getAutoReplies().addAutoReply(editedAutoReply);
			ChannelViewModel cvm = getChannelViewModel(editedAutoReply.getChannelId());
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

	public void updateAutoReply(AutoReplyViewModel autoReply) {
		getAutoReplies().addAutoReply(autoReply.toAutoReply());
	}

	public boolean deleteAutoReply(AutoReplyViewModel autoReply) {
		autoReply.getChannel().getAutoReplies().remove(autoReply);
		return getAutoReplies().removeAutoReply(autoReply.getUuid());
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

	private static Channel createChannelFromTwitchUser(TwitchUserObj twitchUser, int insertIndex) {
		return new Channel(twitchUser.getUserId(), twitchUser.getLoginName(), twitchUser.getDisplayName(), insertIndex,
				"Viewer", null,"Hello {USER} HeyGuys\nWelcome {USER} HeyGuys", "By {USER}!\nHave a good one! {USER} <3",
				"Welcome back {USER} <3\nwb {USER} HeyGuys",
				null, AudioVolume.VOLUME_100, twitchUser.getProfileImageUrl());
	}

	private Channel createNewChannel(String channelId, int insertIndex) {
		TwitchUserObj channel = TwitchHelper.requestTwitchUser(TwitchApiCallType.ID, channelId).join();
		if (channel.isDummy()) {
			return null;
		}

		ArrayList<Channel> updateChannels = getChannels().all().stream()
			.filter(c -> c.getSortIndex() >= insertIndex)
			.map(c -> c.buildCopy().withSortIndex(c.getSortIndex() + 1).build())
			.collect(toCollection(ArrayList::new));
		Channel newChannel = createChannelFromTwitchUser(channel, insertIndex);
		updateChannels.add(newChannel);
		getChannels().addChannels(updateChannels);
		TwitchHelper.joinChannel(newChannel.getChannelId());

		TextureManager.downloadChannelEmotes(channelId, true);
		TextureManager.downloadBttvEmotes(channelId, true);
		TextureManager.downloadChannelBadges(channelId, true);
		ChannelViewModel cvm = createNewChannelViewModel(newChannel);
		UiDispatcher.runOnUiThread(() -> {
			channels.forEach((_, eachCvm) -> {
				if (eachCvm.getSortIndex() >= insertIndex) {
					eachCvm.setSortIndex(eachCvm.getSortIndex() + 1);
				}
			});
			channels.put(newChannel.getChannelId(), cvm);
			CompletableFuture.delayedExecutor(300L, TimeUnit.MILLISECONDS).execute(() -> setActiveChannel(cvm));
		});
		return newChannel;
	}

	private ChannelViewModel createNewChannelViewModel(Channel channel) {
		ChannelViewModel cvm = ChannelViewModel.of(channel);
		Map<MacroType, List<Macro>> macrosByType = getMacros().computeByChannelId(channel.getChannelId(), macros -> macros.sorted(Comparator.comparing(Macro::getIndex)).collect(groupingBy(Macro::getMacroType)));
		cvm.getMacros().addAll(createMacroViewModels(cvm, macrosByType, MacroType.TEXT, MACROS_PER_CHANNEL));
		cvm.getEmoteMacros().addAll(createMacroViewModels(cvm, macrosByType, MacroType.EMOTE, EMOTE_MACROS_PER_CHANNEL));
		List<AutoReplyViewModel> autoRepliesViewModels = getAutoReplies().computeByChannelId(channel.getChannelId(), autoReplies -> autoReplies.map(autoReply -> AutoReplyViewModel.of(autoReply, cvm)).toList());
		cvm.getAutoReplies().addAll(autoRepliesViewModels);

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