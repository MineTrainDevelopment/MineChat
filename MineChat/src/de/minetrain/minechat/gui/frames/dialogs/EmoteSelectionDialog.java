package de.minetrain.minechat.gui.frames.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.fxmisc.flowless.Cell;
import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.gui.frames.emote_selector.EmoteBatchPane;
import de.minetrain.minechat.gui.frames.emote_selector.EmoteBatchViewModel;
import de.minetrain.minechat.gui.frames.emote_selector.EmoteSelectorChannelButton;
import de.minetrain.minechat.main.Main;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class EmoteSelectionDialog extends MineDialog<Emote> {

	private static final Logger LOG = LoggerFactory.getLogger(EmoteSelectionDialog.class);

	private boolean closeOnSelect = true;
	private ObjectProperty<Emote> selectedEmoteProperty;

	public EmoteSelectionDialog() {
		setTitle("Emote Selector");
		setWidth(400);
		setHeight(400);

		BorderPane layout = new BorderPane();

		VBox channelButtons = new VBox(5);
		channelButtons.setAlignment(Pos.CENTER_LEFT);

		ObservableList<EmoteBatchViewModel> emoteBatches = FXCollections.observableArrayList();
		VirtualFlow<EmoteBatchViewModel, Cell<EmoteBatchViewModel, EmoteBatchPane>> virtualFlow = VirtualFlow.createVertical(emoteBatches, EmoteBatchPane::new);

		emoteBatches.add(new EmoteBatchViewModel("Favorite", List.of()));

		CompletableFuture.runAsync(() -> Main.getChannelManager().channelsProperty().get().forEach(channel -> {
			Set<Emote> twitchEmotes = EclipseStoreKeeper.root().emotes().getEmotesByChannelId(channel.getChannelId());
			Set<Emote> bttvEmotes = EclipseStoreKeeper.root().emotes().getBttvEmotesByChannelId(channel.getChannelId());
			if (!twitchEmotes.isEmpty() || !bttvEmotes.isEmpty()) {
				EmoteSelectorChannelButton channelButton = new EmoteSelectorChannelButton(channel);

				List<Emote> allEmotes = new ArrayList<>(twitchEmotes.size() + bttvEmotes.size());
				allEmotes.addAll(twitchEmotes);
				allEmotes.addAll(bttvEmotes);
				Platform.runLater(() -> {
					channelButtons.getChildren().add(channelButton);
					int index = emoteBatches.size();
					emoteBatches.add(createEmoteBatchViewModel(channel.getChannelName(), allEmotes));
					channelButton.selectedProperty().bind(Bindings.createBooleanBinding(() -> index == virtualFlow.visibleCells().stream().map(Cell::getNode).mapToInt(EmoteBatchPane::getIndex).min().orElse(-1), virtualFlow.visibleCells()));
					channelButton.setOnAction(_ -> virtualFlow.showAsFirst(index));
				});
			}
		})).thenAccept(_ -> Platform.runLater(() -> emoteBatches.add(new EmoteBatchViewModel("Default", List.of()))));

		ScrollPane tabPane = new ScrollPane(channelButtons);
//		tabPane.setStyle("-fx-border-width: 10; -fx-border-color: transparent;");
		tabPane.setFocusTraversable(false);
		tabPane.setFitToHeight(true);
		tabPane.setFitToWidth(true);
		tabPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		tabPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		virtualFlow.show(0);
		layout.setRight(tabPane);
		layout.setCenter(new VirtualizedScrollPane<>(virtualFlow));
		getDialogPane().setContent(layout);

		if (closeOnSelect) {
			selectedEmoteProperty().addListener((_, _, newEmote) -> {
				if (newEmote != null) {
					setResult(newEmote);
					close();
				}
			});
		}
	}

	public ObjectProperty<Emote> selectedEmoteProperty() {
		if (selectedEmoteProperty == null) {
			selectedEmoteProperty = new SimpleObjectProperty<>(this, "selectedEmote");
		}
		return selectedEmoteProperty;
	}

	public void setSelectedEmote(Emote emote) {
		selectedEmoteProperty().set(emote);
	}

	public Emote getSelectedEmote() {
		return selectedEmoteProperty().get();
	}

	private EmoteBatchViewModel createEmoteBatchViewModel(String name, List<Emote> allEmotes) {
		return new EmoteBatchViewModel(name, allEmotes, selectedEmoteProperty());
	}

	public void setCloseOnSelect(boolean closeOnSelect) {
		this.closeOnSelect = closeOnSelect;
	}

	@Override
	protected Emote yieldResultOnSuccess() {
		return selectedEmoteProperty().get();
	}
}
