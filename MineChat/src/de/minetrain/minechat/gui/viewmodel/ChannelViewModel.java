package de.minetrain.minechat.gui.viewmodel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.Channel;
import de.minetrain.minechat.data.objectdata.ChatMessage;
import de.minetrain.minechat.gui.utils.NotifiableObservableListWrapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class ChannelViewModel {

	public static ChannelViewModel of(String channelId, String channelName, int sortIndex, String profileImageUrl, String loginName) {
		return new ChannelViewModel(channelId, channelName, sortIndex, profileImageUrl, loginName);
	}

	public static ChannelViewModel of(Channel channel) {
		return of(channel.getChannelId(), channel.getDisplayName(), channel.getSortIndex(), channel.getProfileImageUrl(), channel.getLoginName());
	}

	private ReadOnlyStringWrapper channelIdProperty;
	private ReadOnlyStringWrapper channelNameProperty;
	private ReadOnlyStringWrapper profileImageUrlProperty;
	private ReadOnlyObjectWrapper<Image> profileImageSmallProperty;
	private ReadOnlyObjectWrapper<Image> profileImageLargeProperty;
	private ReadOnlyStringWrapper loginNameProperty;
	private IntegerProperty sortIndexProperty;
	/// Indicates whether the channel is currently live streaming
	private BooleanProperty liveProperty;
	/// Indicates whether the channel is currently selected in the UI
	private BooleanProperty selectedProperty;
	/// Indicates whether the channel is moderated by the active user
	private BooleanProperty moderatedProperty;
	/// Slow mode wait time in seconds
	private IntegerProperty slowModeWaitTimeProperty;
	private ObjectProperty<NotifiableObservableListWrapper<ChatMessage>> messagesProperty;
	private ObservableList<MacroViewModel> macros;
	private ObservableList<MacroViewModel> emoteMacros;
	private ObservableList<AutoReplyViewModel> autoReplies;

	private Set<String> participatedUserIds;

	protected ChannelViewModel(String channelId, String channelName, int sortIndex, String profileImageUrl, String loginName) {
		channelIdPropertyInternal().set(channelId);
		channelNamePropertyInternal().set(channelName);
		sortIndexProperty().set(sortIndex);
		profileImageUrlPropertyInternal().set(profileImageUrl);
		profileImageSmallPropertyInternal().bind(profileImageUrlProperty().map(url -> new Image(url, 24D, 24D, false, true, true)));
		profileImageLargePropertyInternal().bind(profileImageUrlProperty().map(url -> new Image(url, 75D, 75D, false, true, true)));
		loginNamePropertyInternal().set(loginName);

		macros = FXCollections.observableArrayList();
		emoteMacros = FXCollections.observableArrayList();
		autoReplies = FXCollections.observableArrayList();

		participatedUserIds = new HashSet<>();

		// TODO don't report initial value...
//		liveProperty().addListener((_, _, newValue) -> {
//			if (newValue.booleanValue()) {
//				Main.audioManager.playAudioClip(DefaultAudioFiles.LIVE_1, AudioVolume.VOLUME_100);
//				// TODO display live notification?
//				ChannelTab channelTab = getCurrentChannelTab(event.getChannel().getId());
//				if(channelTab != null){
//					channelTab.setLiveState(true);
//					liveNotification.setData(
//							channelTab,
//							event.getStream().getGameName(),
//							event.getStream().getTitle(),
//							event.getStream().getThumbnailUrl(80, 80));
//
//					Instant startedAtInstant = event.getStream().getStartedAtInstant();
//				}
//			}
//		});
	}

	public ReadOnlyStringProperty channelIdProperty() {
		return channelIdPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyStringWrapper channelIdPropertyInternal() {
		if (channelIdProperty == null) {
			channelIdProperty = new ReadOnlyStringWrapper(this, "channelId");
		}
		return channelIdProperty;
	}

	public ReadOnlyStringProperty channelNameProperty() {
		return channelNamePropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyStringWrapper channelNamePropertyInternal() {
		if (channelNameProperty == null) {
			channelNameProperty = new ReadOnlyStringWrapper(this, "channelName");
		}
		return channelNameProperty;
	}

	public ReadOnlyStringProperty profileImageUrlProperty() {
		return profileImageUrlPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyStringWrapper profileImageUrlPropertyInternal() {
		if (profileImageUrlProperty == null) {
			profileImageUrlProperty = new ReadOnlyStringWrapper(this, "profileImageUrl");
		}
		return profileImageUrlProperty;
	}

	public ReadOnlyObjectProperty<Image> profileImageSmallProperty() {
		return profileImageSmallPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyObjectWrapper<Image> profileImageSmallPropertyInternal() {
		if (profileImageSmallProperty == null) {
			profileImageSmallProperty = new ReadOnlyObjectWrapper<>(this, "profileImageSmall");
		}
		return profileImageSmallProperty;
	}

	public ReadOnlyObjectProperty<Image> profileImageLargeProperty() {
		return profileImageLargePropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyObjectWrapper<Image> profileImageLargePropertyInternal() {
		if (profileImageLargeProperty == null) {
			profileImageLargeProperty = new ReadOnlyObjectWrapper<>(this, "profileImageLarge");
		}
		return profileImageLargeProperty;
	}

	protected ReadOnlyStringWrapper loginNamePropertyInternal() {
		if (loginNameProperty == null) {
			loginNameProperty = new ReadOnlyStringWrapper(this, "loginName");
		}
		return loginNameProperty;
	}

	public ReadOnlyStringProperty loginNameProperty() {
		return loginNamePropertyInternal().getReadOnlyProperty();
	}

	public IntegerProperty sortIndexProperty() {
		if (sortIndexProperty == null) {
			sortIndexProperty = new SimpleIntegerProperty(this, "sortIndex", 0);
		}
		return sortIndexProperty;
	}

	public BooleanProperty liveProperty() {
		if (liveProperty == null) {
			liveProperty = new SimpleBooleanProperty(this, "live", false);
		}
		return liveProperty;
	}

	public BooleanProperty selectedProperty() {
		if (selectedProperty == null) {
			selectedProperty = new SimpleBooleanProperty(this, "selected", false);
		}
		return selectedProperty;
	}

	public BooleanProperty moderatedProperty() {
		if (moderatedProperty == null) {
			moderatedProperty = new SimpleBooleanProperty(this, "moderated", false);
		}
		return moderatedProperty;
	}

	public IntegerProperty slowModeWaitTimeProperty() {
		if (slowModeWaitTimeProperty == null) {
			slowModeWaitTimeProperty = new SimpleIntegerProperty(this, "slowModeWaitTime", 0);
		}
		return slowModeWaitTimeProperty;
	}

	public ObjectProperty<NotifiableObservableListWrapper<ChatMessage>> messagesProperty() {
		if (messagesProperty == null) {
			messagesProperty = new SimpleObjectProperty<>(this, "messages");
		}
		return messagesProperty;
	}

	public String getChannelId() {
		return channelIdProperty().get();
	}

	public String getChannelName() {
		return channelNameProperty().get();
	}

	public String getProfileImageUrl() {
		return profileImageUrlProperty().get();
	}

	public Image getProfileImageSmall() {
		return profileImageSmallProperty().get();
	}

	public Image getProfileImageLarge() {
		return profileImageLargeProperty().get();
	}

	public String getLoginName() {
		return loginNameProperty().get();
	}

	public int getSortIndex() {
		return sortIndexProperty().get();
	}

	public void setSortIndex(int index) {
		sortIndexProperty().set(index);
	}

	public boolean isLive() {
		return liveProperty().get();
	}

	public void setLive(boolean isLive) {
		liveProperty().set(isLive);
	}

	public boolean isSelected() {
		return selectedProperty().get();
	}

	public void setSelected(boolean isSelected) {
		selectedProperty().set(isSelected);
	}

	public boolean isModerated() {
		return moderatedProperty().get();
	}

	public void setModerated(boolean isModerated) {
		moderatedProperty().set(isModerated);
	}

	public int getSlowModeWaitTime() {
		return slowModeWaitTimeProperty().get();
	}

	public void setSlowModeWaitTime(int waitTimeInSeconds) {
		slowModeWaitTimeProperty().set(waitTimeInSeconds);
	}

	public void setMessages(NotifiableObservableListWrapper<ChatMessage> messages) {
		messagesProperty().set(messages);
	}

	public NotifiableObservableListWrapper<ChatMessage> getMessages() {
		return messagesProperty().get();
	}

	public void initMessages() {
		setMessages(new NotifiableObservableListWrapper<>(EclipseStoreKeeper.root().messages().getMessagesByChannelId(getChannelId())));
	}

	public ObservableList<MacroViewModel> getMacros() {
		return macros;
	}

	public ObservableList<MacroViewModel> getEmoteMacros() {
		return emoteMacros;
	}

	public ObservableList<AutoReplyViewModel> getAutoReplies() {
		return autoReplies;
	}

	public Set<String> getParticipatedUserIds(){
		return participatedUserIds;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getChannelId());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ChannelViewModel other = (ChannelViewModel) obj;
		return Objects.equals(getChannelId(), other.getChannelId());
	}
}
