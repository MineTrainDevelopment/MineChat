package de.minetrain.minechat.gui.frames.emote_selector;

import java.util.List;

import org.fxmisc.flowless.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.objectdata.Emote;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;

//Some sort of emtoe select event to append to a frame.
//  - When i select an emote in the current instance of this frame, it triggers an event that
//  - for examle changes the emote inside a macro editor or appends an emote to the current input cursor.
//
//Option to dispose or close frame.
//  - A new select listner whould be requert to open the frame again??
public class EmoteBatchPane extends TitledPane implements Cell<EmoteBatchViewModel, EmoteBatchPane> {

	private static final Logger LOG = LoggerFactory.getLogger(EmoteBatchPane.class);

	private ObjectProperty<EmoteBatchViewModel> itemProperty;
	private IntegerProperty indexProperty;

	public EmoteBatchPane(EmoteBatchViewModel item) {
		this();
		updateItem(item);
	}

	public EmoteBatchPane() {
		setFocusTraversable(false);
		FlowPane flowPane = new FlowPane();
		setContent(flowPane);
//		setExpanded(false);
		flowPane.setHgap(3D);
		flowPane.setVgap(3D);

		textProperty().bind(itemProperty().flatMap(EmoteBatchViewModel::nameProperty));
		itemProperty().addListener((_, oldItem, newItem) -> {
			if (newItem != null) {
				loadEmotes(flowPane, newItem.getEmotes());
				expandedProperty().bindBidirectional(newItem.expandedProperty());
			} else {
				expandedProperty().unbindBidirectional(oldItem.expandedProperty());
				flowPane.getChildren().clear();
			}
		});
	}

	@Override
	public void updateItem(EmoteBatchViewModel item) {
		setItem(item);
	}

	@Override
	public void updateIndex(int index) {
		setIndex(index);
	}

	@Override
	public void reset() {
		setItem(null);
	}

	@Override
	public EmoteBatchPane getNode() {
		return this;
	}

	@Override
	public boolean isReusable() {
		return false;
	}

	public ObjectProperty<EmoteBatchViewModel> itemProperty() {
		if (itemProperty == null) {
			itemProperty = new SimpleObjectProperty<>(this, "item");
		}
		return itemProperty;
	}

	public IntegerProperty indexProperty() {
		if (indexProperty == null) {
			indexProperty = new SimpleIntegerProperty(this, "index");
		}
		return indexProperty;
	}

	public EmoteBatchViewModel getItem() {
		return itemProperty().get();
	}

	public void setItem(EmoteBatchViewModel item) {
		itemProperty().set(item);
	}

	public int getIndex() {
		return indexProperty().get();
	}

	public void setIndex(int index) {
		indexProperty().set(index);
	}

	private void loadEmotes(FlowPane flowPane, List<Emote> emotes) {
			List<EmoteSelectorButton> buttons = emotes.stream().map(this::createEmoteButton).toList();
			flowPane.getChildren().addAll(buttons);
					// TODO favorite action
	//			selectorButton.setOnAction(event -> emoteSelector.fireSelectEvent(emote)); // TODO selection action
	//			selectorButton.setOnMouseClicked(event -> { // TODO favorite action
	//				if (event.getButton().equals(MouseButton.SECONDARY) && emoteSelector.favoriteEmoteBatche != null) {
	//					emote.toggleFavorite();
	//					emoteSelector.favoriteEmoteBatche.grid.getChildren().clear();
	//					emoteSelector.favoriteEmoteBatche.loadEmotes();
	//				}
	//			});
		}

	private EmoteSelectorButton createEmoteButton(Emote emote) {
		EmoteSelectorButton button = new EmoteSelectorButton(emote);
		button.setOnAction(_ -> getItem().setSelectedEmote(emote));
		return button;
	}
}
