package de.minetrain.minechat.gui.frames;

import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.data.objectdata.MacroData;
import de.minetrain.minechat.features.macros.MacroObject;
import de.minetrain.minechat.features.macros.MacroType;
import de.minetrain.minechat.gui.frames.emote_selector.EmoteSelector;
import de.minetrain.minechat.gui.frames.emote_selector.EmoteSelectorButton;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MacroEditorFrame extends MineDialog<MacroData> {

	private Emote selectedEmote;
	private EmoteSelector emoteSelector;

	public MacroEditorFrame(MacroObject macro, MacroType macroType, int button_id) {
		setTitle("Edit this macro:");
		setWidth(420);
		setHeight(300);

		String title = macro != null ? macro.getTitle() : "";
		String output = "";

		TextField titleInputField = new TextField(title);

		EmoteSelectorButton emoteButton = new EmoteSelectorButton();
		emoteButton.setOnAction(_ -> {
			new EmoteSelector().showAndWait().ifPresent(newEmote -> {
				selectedEmote = newEmote;
				emoteButton.setEmote(newEmote);
			});
		});
//		emoteButton.setOnMouseClicked(event -> {
//			if (emoteSelector == null) {
//				emoteSelector = new EmoteSelector(newEmote -> {
//					selectedEmote = newEmote;
//					emoteButton.changeImage(newEmote);
//					titleInputField.setPromptText(newEmote.getName());
//				});
//			} else {
//				emoteSelector.openStage(false);
//			}
//		});

		titleInputField.setId("message-input-field");
//		titleInputField.setPromptText(selectedEmote.getName());
		titleInputField.setFocusTraversable(false);
		titleInputField.minHeightProperty().bind(emoteButton.heightProperty());

		TextArea outputInputField = new TextArea(output);
		outputInputField.setId("message-input-field");
		outputInputField.setPromptText("""
			Output text
			Line split (ENTER key) == Random output text list

			You can use all message variables.
			  - Like {TIME}, {TOTAL_MESSAGES}...
			""");
		outputInputField.setFocusTraversable(false);

		HBox metaBox = new HBox(10, emoteButton, titleInputField);
		HBox.setHgrow(titleInputField, Priority.ALWAYS);
		VBox layout = new VBox(10, metaBox, outputInputField);

		getDialogPane().setContent(layout);
	}

	@Override
	protected MacroData yieldResultOnSuccess() {
		return null; // TODO: Implement MacroData creation based on user input
	}
}
