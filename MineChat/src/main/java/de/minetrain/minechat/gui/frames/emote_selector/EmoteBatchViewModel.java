package de.minetrain.minechat.gui.frames.emote_selector;

import java.util.List;

import de.minetrain.minechat.data.objectdata.Emote;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EmoteBatchViewModel {

	private StringProperty nameProperty;
	private ObjectProperty<ObservableList<Emote>> emotesProperty;
	private BooleanProperty expandedProperty;
	private ObjectProperty<Emote> selectedEmoteProperty;

	public EmoteBatchViewModel(String name, List<Emote> emotes) {
		setName(name);
		setEmotes(emotes instanceof ObservableList<Emote> oList ? oList : FXCollections.observableList(emotes));
	}

	public EmoteBatchViewModel(String name, List<Emote> emotes, ObjectProperty<Emote> selectedEmoteProperty) {
		this(name, emotes);
		this.selectedEmoteProperty = selectedEmoteProperty;
	}

	public StringProperty nameProperty() {
		if (nameProperty == null) {
			nameProperty = new SimpleStringProperty(this, "name");
		}
		return nameProperty;
	}

	public ObjectProperty<ObservableList<Emote>> emotesProperty() {
		if (emotesProperty == null) {
			emotesProperty = new SimpleObjectProperty<>(this, "emotes");
		}
		return emotesProperty;
	}

	public BooleanProperty expandedProperty() {
		if (expandedProperty == null) {
			expandedProperty = new SimpleBooleanProperty(this, "expanded", true);
		}
		return expandedProperty;
	}

	public void setName(String name) {
		nameProperty().set(name);
	}

	public String getName() {
		return nameProperty().get();
	}

	public void setEmotes(ObservableList<Emote> emotes) {
		emotesProperty().set(emotes);
	}

	public ObservableList<Emote> getEmotes() {
		return emotesProperty().get();
	}

	public void setExpanded(boolean collapsed) {
		expandedProperty().set(collapsed);
	}

	public boolean isExpanded() {
		return expandedProperty().get();
	}

	public ObjectProperty<Emote> selectedEmoteProperty() {
		if (selectedEmoteProperty == null) {
			selectedEmoteProperty = new SimpleObjectProperty<>(this, "selectedEmote");
		}
		return selectedEmoteProperty;
	}

	public Emote getSelectedEmote() {
		return selectedEmoteProperty().get();
	}

	public void setSelectedEmote(Emote emote) {
		selectedEmoteProperty().set(emote);
	}
}
