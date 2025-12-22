package de.minetrain.minechat.gui.frames.emote_selector;

import java.util.List;

import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.gui.emotes.EmoteSelectorButton;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;

//Some sort of emtoe select event to append to a frame.
//  - When i select an emote in the current instance of this frame, it triggers an event that
//  - for examle changes the emote inside a macro editor or appends an emote to the current input cursor.
//
//Option to dispose or close frame.
//  - A new select listner whould be requert to open the frame again??
public class EmoteSelectorBatche extends TitledPane {

	public EmoteSelectorBatche(String name, List<Emote> emotes) {
		setText(name);
		setBorder(null);
		FlowPane flowPane = new FlowPane();
		setContent(flowPane);
//		setExpanded(false);
		flowPane.setHgap(5);
		flowPane.setVgap(5);
		loadEmotes(flowPane, emotes);
	}

	private void loadEmotes(FlowPane flowPane, List<Emote> emotes) {
		List<EmoteSelectorButton> buttons = emotes.stream().map(EmoteSelectorButton::new).toList();
		flowPane.getChildren().addAll(buttons);
//			selectorButton.setOnAction(event -> emoteSelector.fireSelectEvent(emote)); // TODO selection action
//			selectorButton.setOnMouseClicked(event -> { // TODO favorite action
//				if (event.getButton().equals(MouseButton.SECONDARY) && emoteSelector.favoriteEmoteBatche != null) {
//					emote.toggleFavorite();
//					emoteSelector.favoriteEmoteBatche.grid.getChildren().clear();
//					emoteSelector.favoriteEmoteBatche.loadEmotes();
//				}
//			});
	}
}
