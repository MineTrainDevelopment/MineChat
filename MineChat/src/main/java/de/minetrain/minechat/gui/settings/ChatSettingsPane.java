package de.minetrain.minechat.gui.settings;

import de.minetrain.minechat.features.messagehighlight.HighlightType;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.viewmodel.HighlightViewModel;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.MessageManager;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class ChatSettingsPane extends SettingsContentPane {

	public ChatSettingsPane() {
		setTitle("Chatting");

		GridPane contentRoot = new GridPane();
		contentRoot.setHgap(5);
		contentRoot.setVgap(5);
		setCenter(contentRoot);

		for (int i = 0; i < HighlightType.values().length; i++) {
			HighlightViewModel highlight = Main.getSettingsViewModel().getHighlightViewModel(HighlightType.values()[i]);
			createHighlightSetting(highlight, contentRoot, i);
		}
	}

	private void createHighlightSetting(HighlightViewModel highlight, GridPane gridPane, int row) {
		gridPane.add(new Label(highlight.getType().getDisplayName()), 0, row);
		ColorPicker colorPicker = new ColorPicker(ColorManager.decodeFromInt(highlight.getColor()));
		colorPicker.setOnAction(_ -> {
			highlight.setColor(ColorManager.encodeToInt(colorPicker.getValue()));
			MessageManager.updateHighlight(highlight);
		});
		gridPane.add(colorPicker, 1, row);
		CheckBox enabledCheckBox = new CheckBox();
		enabledCheckBox.setSelected(highlight.isActive());
		enabledCheckBox.setOnAction(_ -> {
			highlight.setActive(enabledCheckBox.isSelected());
			MessageManager.updateHighlight(highlight);
		});
		gridPane.add(enabledCheckBox, 2, row);
	}
}
