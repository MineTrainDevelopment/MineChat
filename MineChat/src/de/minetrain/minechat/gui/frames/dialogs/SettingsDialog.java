package de.minetrain.minechat.gui.frames.dialogs;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.panes.TabButton;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SettingsDialog extends MineDialog<Settings> {

	private ObjectProperty<TabButton> selectedTabProperty;

	public SettingsDialog() {
		setTitle("Settings");
		setWidth(800);
		setHeight(600);

		BorderPane dialogRoot = new BorderPane();
		getDialogPane().setContent(dialogRoot);

		VBox tabs = new VBox();
		tabs.setPadding(new Insets(30D, -5D, 0, 0));
		TabButton chatTabButton = createTabButton("Chat");
		TabButton appearanceTabButton = createTabButton("Appearance");
		TabButton highlightsTabButton = createTabButton("Highlights");
		TabButton autoReplyTabButton = createTabButton("Auto Reply");
		TabButton countVariablesTabButton = createTabButton("Count Variables");
		tabs.getChildren().addAll(chatTabButton, appearanceTabButton, highlightsTabButton, autoReplyTabButton, countVariablesTabButton);

		dialogRoot.setLeft(tabs);

		StackPane contentPane = new StackPane();
		contentPane.setId("dialog-content");
		contentPane.setPrefWidth(1);
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

	private TabButton createTabButton(String title) {
		TabButton button = new TabButton();
		button.setText(title);
		button.setId("tab-button");
		button.setPrefWidth(150);
		button.selectedProperty().bind(selectedTabProperty().isEqualTo(button));
		button.setOnAction(_ -> selectedTabProperty().set(button));
		return button;
	}
}
