package de.minetrain.minechat.gui.frames.emote_selector;

import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class EmoteSelectorChannelButton extends Button {
	private static final PseudoClass SELECTED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("selected");
	private static final double PROFILE_IMAGE_SIZE = 28D;

	private BooleanProperty selectedProperty;

	public EmoteSelectorChannelButton(ChannelViewModel channel) {
		setFocusTraversable(false);
		setId("channel-button");

		ImageView imageView = new ImageView();
		imageView.imageProperty().bind(channel.profileImageSmallProperty());
		imageView.setFitHeight(PROFILE_IMAGE_SIZE);
		imageView.setFitWidth(PROFILE_IMAGE_SIZE);
		imageView.setPreserveRatio(true);
		StackPane wrapper = new StackPane();
		wrapper.setMinSize(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE);
		wrapper.getChildren().add(imageView);
		setGraphic(wrapper);
	}

	public BooleanProperty selectedProperty() {
		if (selectedProperty == null) {
			selectedProperty = new SimpleBooleanProperty(this, "selected", false) {
				@Override
				protected void invalidated() {
					pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, get());
				}
			};
		}
		return selectedProperty;
	}
}
