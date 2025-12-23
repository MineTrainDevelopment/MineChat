package de.minetrain.minechat.gui.frames;

import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.data.objectdata.Macro;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.frames.emote_selector.EmoteSelectionDialog;
import de.minetrain.minechat.gui.frames.emote_selector.EmoteSelectorButton;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MacroEditorDialog extends MineDialog<Macro> {

	private Macro.Builder macroBuilder;
	private TextField titleInputField;
	private TextArea outputInputField;

	public MacroEditorDialog(Macro.Builder macro) {
		setTitle("Edit this macro:");
		setWidth(420);
		setHeight(300);

		macroBuilder = macro;

		titleInputField = new TextField(macro.getTitle());

		EmoteSelectorButton emoteButton = new EmoteSelectorButton();
		if (macro.getEmoteId() != null) {
			Emote emote = EmoteManager.getEmoteById(macro.getEmoteId());
			emoteButton.setEmote(emote);
		}
		emoteButton.setOnAction(_ -> {
			new EmoteSelectionDialog().showAndWait().ifPresent(newEmote -> {
				macroBuilder.withEmoteId(newEmote.getEmoteId());
				emoteButton.setEmote(newEmote);
			});
		});

		titleInputField.setId("message-input-field");
//		titleInputField.setPromptText(selectedEmote.getName());
		titleInputField.setFocusTraversable(false);
		titleInputField.minHeightProperty().bind(emoteButton.heightProperty());

		outputInputField = new TextArea();
		if (macro.getOutput() != null) {
			outputInputField.setText(String.join("\n", macro.getOutput()));
		}
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
	protected Macro yieldResultOnSuccess() {
		return macroBuilder
				.withTitle(titleInputField.getText().trim())
				.withOutput(outputInputField.getText().lines().toList())
				.build();
	}
}
