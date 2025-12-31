package de.minetrain.minechat.gui.frames.dialogs;

import java.util.function.Supplier;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.panes.TabButton;
import de.minetrain.minechat.gui.settings.AppearanceSettingsPane;
import de.minetrain.minechat.gui.settings.AutoReplySettingsPane;
import de.minetrain.minechat.gui.settings.ChatSettingsPane;
import de.minetrain.minechat.gui.settings.CountVariablesSettingsPane;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SettingsDialog extends MineDialog<Settings> {

	private ObjectProperty<TabButton> selectedTabProperty;
	private StackPane contentPane;

	public SettingsDialog() {
		setTitle("Settings");
		setWidth(800);
		setHeight(600);

		BorderPane dialogRoot = new BorderPane();
		dialogRoot.getStyleClass().add("settings-dialog");
		getDialogPane().setContent(dialogRoot);

		VBox tabs = new VBox();
		tabs.setPadding(new Insets(30D, -5D, 0, 0));
		TabButton chatTabButton = createTabButton("Chat", ChatSettingsPane::new);
		TabButton appearanceTabButton = createTabButton("Appearance", AppearanceSettingsPane::new);
		TabButton highlightsTabButton = createTabButton("Highlights", ChatSettingsPane::new);
		TabButton autoReplyTabButton = createTabButton("Auto Reply", AutoReplySettingsPane::new);
		TabButton countVariablesTabButton = createTabButton("Count Variables", CountVariablesSettingsPane::new);
		tabs.getChildren().addAll(chatTabButton, appearanceTabButton, highlightsTabButton, autoReplyTabButton, countVariablesTabButton);

		dialogRoot.setLeft(tabs);

		contentPane = new StackPane();
		contentPane.getStyleClass().add("content");
		dialogRoot.setCenter(contentPane);

		selectedTabProperty().set(chatTabButton);
	}

	protected ObjectProperty<TabButton> selectedTabProperty() {
		if (selectedTabProperty == null) {
			selectedTabProperty = new SimpleObjectProperty<>(this, "selectedTab");
		}
		return selectedTabProperty;
	}

	@Override
	protected Settings yieldResultOnSuccess() {
		return null;
	}

	private TabButton createTabButton(String title, Supplier<Node> contentSupplier) {
		TabButton button = new TabButton();
		button.setText(title);
		button.selectedProperty().bind(selectedTabProperty().isEqualTo(button));
		button.setOnAction(_ -> {
			selectedTabProperty().set(button);
			contentPane.getChildren().setAll(contentSupplier.get());
		});
		return button;
	}
}
