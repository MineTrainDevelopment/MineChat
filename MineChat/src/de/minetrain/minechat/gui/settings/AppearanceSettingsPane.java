package de.minetrain.minechat.gui.settings;

import java.util.function.IntConsumer;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.UserSettings;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.viewmodel.SettingsViewModel;
import de.minetrain.minechat.main.Main;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class AppearanceSettingsPane extends SettingsContentPane {

	public AppearanceSettingsPane() {
		setTitle("Appearance");

		GridPane contentRoot = new GridPane();
		contentRoot.setHgap(5);
		contentRoot.setVgap(5);
		setCenter(contentRoot);

		SettingsViewModel settingsViewModel = Main.getSettingsViewModel();
		UserSettings userSettings = EclipseStoreKeeper.root().userSettings();
		createColorSetting("Base color", settingsViewModel.getBaseColor(), contentRoot, 0, newColor -> {
			userSettings.setBaseColor(newColor);
			settingsViewModel.setBaseColor(newColor);
		});
		createColorSetting("Accent color", settingsViewModel.getAccentColor(), contentRoot, 1, newColor -> {
			userSettings.setAccentColor(newColor);
			settingsViewModel.setAccentColor(newColor);
		});
		createColorSetting("Border color", settingsViewModel.getBorderColor(), contentRoot, 2, newColor -> {
			userSettings.setBorderColor(newColor);
			settingsViewModel.setBorderColor(newColor);
		});
	}

	private void createColorSetting(String name, int color, GridPane gridPane, int row, IntConsumer onChange) {
		gridPane.add(new Label(name), 0, row);
		ColorPicker colorPicker = new ColorPicker(ColorManager.decodeFromInt(color));
		colorPicker.setOnAction(_ -> onChange.accept(ColorManager.encodeToInt(colorPicker.getValue())));
		gridPane.add(colorPicker, 1, row);
	}
}
