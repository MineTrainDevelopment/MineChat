package de.minetrain.minechat.gui.viewmodel;

import java.util.Objects;

import de.minetrain.minechat.data.objectdata.Channel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;

public class ChannelViewModel {

	public static ChannelViewModel of(String channelId, String channelName, String profileImageUrl, String loginName) {
		return new ChannelViewModel(channelId, channelName, profileImageUrl, loginName);
	}

	public static ChannelViewModel of(Channel channel) {
		return of(channel.getChannelId(), channel.getDisplayName(), channel.getProfileImageUrl(), channel.getLoginName());
	}

	private ReadOnlyStringWrapper channelIdProperty;
	private ReadOnlyStringWrapper channelNameProperty;
	private ReadOnlyStringWrapper profileImageUrlProperty;
	private ReadOnlyStringWrapper loginNameProperty;
	private BooleanProperty liveProperty;
	private BooleanProperty selectedProperty;

	protected ChannelViewModel(String channelId, String channelName, String profileImageUrl, String loginName) {
		channelIdPropertyInternal().set(channelId);
		channelNamePropertyInternal().set(channelName);
		profileImageUrlPropertyInternal().set(profileImageUrl);
		loginNamePropertyInternal().set(loginName);
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

	protected ReadOnlyStringWrapper loginNamePropertyInternal() {
		if (loginNameProperty == null) {
			loginNameProperty = new ReadOnlyStringWrapper(this, "loginName");
		}
		return loginNameProperty;
	}

	public ReadOnlyStringProperty loginNameProperty() {
		return loginNamePropertyInternal().getReadOnlyProperty();
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

	public String getChannelId() {
		return channelIdProperty().get();
	}

	public String getChannelName() {
		return channelNameProperty().get();
	}

	public String getProfileImageUrl() {
		return profileImageUrlProperty().get();
	}

	public String getLoginName() {
		return loginNameProperty().get();
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
