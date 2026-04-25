package de.minetrain.minechat.gui.viewmodel;

import java.time.Instant;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StreamInfoViewModel {

	private StringProperty gameIdProperty;
	private StringProperty gameNameProperty;
	private StringProperty titleProperty;
	private ReadOnlyListWrapper<String> tagsProperty;
	private IntegerProperty viewerCountProperty;
	private ObjectProperty<Instant> startedAtProperty;
	private BooleanProperty matureProperty;
	private StringProperty languageProperty;
	private StringProperty thumbnailUrlTemplateProperty;

	public StringProperty gameIdProperty() {
		if (gameIdProperty == null) {
			gameIdProperty = new SimpleStringProperty(this, "gameId", "");
		}
		return gameIdProperty;
	}

	public String getGameId() {
		return gameIdProperty().get();
	}

	public void setGameId(String gameId) {
		gameIdProperty().set(gameId);
	}

	public StringProperty gameNameProperty() {
		if (gameNameProperty == null) {
			gameNameProperty = new SimpleStringProperty(this, "gameName", "");
		}
		return gameNameProperty;
	}

	public String getGameName() {
		return gameNameProperty().get();
	}

	public void setGameName(String gameName) {
		gameNameProperty().set(gameName);
	}

	public StringProperty titleProperty() {
		if (titleProperty == null) {
			titleProperty = new SimpleStringProperty(this, "title", "");
		}
		return titleProperty;
	}

	public String getTitle() {
		return titleProperty().get();
	}

	public void setTitle(String title) {
		titleProperty().set(title);
	}

	protected ReadOnlyListWrapper<String> tagsPropertyInternal() {
		if (tagsProperty == null) {
			tagsProperty = new ReadOnlyListWrapper<>(this, "tags", FXCollections.observableArrayList());
		}
		return tagsProperty;
	}

	public ReadOnlyListProperty<String> tagsProperty() {
		return tagsPropertyInternal().getReadOnlyProperty();
	}

	public ObservableList<String> getTags() {
		return tagsProperty().get();
	}

	public IntegerProperty viewerCountProperty() {
		if (viewerCountProperty == null) {
			viewerCountProperty = new SimpleIntegerProperty(this, "viewerCount", 0);
		}
		return viewerCountProperty;
	}

	public int getViewerCount() {
		return viewerCountProperty().get();
	}

	public void setViewerCount(int count) {
		viewerCountProperty().set(count);
	}

	public ObjectProperty<Instant> startedAtProperty() {
		if (startedAtProperty == null) {
			startedAtProperty = new SimpleObjectProperty<>(this, "startedAt");
		}
		return startedAtProperty;
	}

	public Instant getStartedAt() {
		return startedAtProperty().get();
	}

	public void setStartedAt(Instant startedAt) {
		startedAtProperty().set(startedAt);
	}

	public BooleanProperty matureProperty() {
		if (matureProperty == null) {
			matureProperty = new SimpleBooleanProperty(this, "mature", false);
		}
		return matureProperty;
	}

	public boolean isMature() {
		return matureProperty().get();
	}

	public void setMature(boolean mature) {
		matureProperty().set(mature);
	}

	public StringProperty languageProperty() {
		if (languageProperty == null) {
			languageProperty = new SimpleStringProperty(this, "language", "");
		}
		return languageProperty;
	}

	public String getLanguage() {
		return languageProperty().get();
	}

	public void setLanguage(String language) {
		languageProperty().set(language);
	}

	public StringProperty thumbnailUrlTemplateProperty() {
		if (thumbnailUrlTemplateProperty == null) {
			thumbnailUrlTemplateProperty = new SimpleStringProperty(this, "thumbnailUrlTemplate");
		}
		return thumbnailUrlTemplateProperty;
	}

	public String getThumbnailUrlTemplate() {
		return thumbnailUrlTemplateProperty().get();
	}

	public void setThumbnailUrlTemplate(String urlTemplate) {
		thumbnailUrlTemplateProperty().set(urlTemplate);
	}
}
