package de.minetrain.minechat.gui.frames.emote_selector;

import de.minetrain.minechat.gui.panes.TabButton;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class EmoteSelectorChannelButton extends TabButton {

	public EmoteSelectorChannelButton(ChannelViewModel channel) {
		setFocusTraversable(false);
		getStyleClass().add("channel-button");

		StackPane wrapper = new StackPane();
		wrapper.getStyleClass().add("image-wrapper");

		ImageView imageView = new ImageView();
		imageView.imageProperty().bind(channel.profileImageSmallProperty());
		imageView.fitHeightProperty().bind(wrapper.minHeightProperty());
		imageView.fitWidthProperty().bind(wrapper.minWidthProperty());
		imageView.setPreserveRatio(true);
		wrapper.getChildren().add(imageView);

		setGraphic(wrapper);
	}
}
