package de.minetrain.minechat.gui.settings;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public abstract class SettingsContentPane extends BorderPane {

	private StringProperty titleProperty;
	private VBox functionsBar;
	private VBox additionalSettingsPane;

	protected SettingsContentPane() {
		getStyleClass().add("settings-content");
		Label titleText = new Label();
		titleText.getStyleClass().add("title");
		titleProperty = titleText.textProperty();
		setTop(titleText);

		functionsBar = new VBox() {

			@Override
			protected void layoutChildren() {
				// Make all children the same width
				double prefWidth = getChildren().stream().mapToDouble(b -> b.prefWidth(-1)).max().orElse(0D);
				for (var child : getChildren()) {
					if (child instanceof Region region) {
						region.setPrefWidth(prefWidth);
					}
				}
				super.layoutChildren();
			}
		};
		functionsBar.getStyleClass().add("functions-bar");
		setRight(functionsBar);

		additionalSettingsPane = new VBox();
		additionalSettingsPane.getStyleClass().add("additional-settings");
		setBottom(additionalSettingsPane);
	}

	public StringProperty titleProperty() {
		return titleProperty;
	}

	public void setTitle(String title) {
		titleProperty.set(title);
	}

	public void addFunctionsBarItem(Region item) {
		functionsBar.getChildren().add(item);
	}

	public void addAddtionalSettingsPane(Region pane) {
		additionalSettingsPane.getChildren().add(pane);
	}
}
