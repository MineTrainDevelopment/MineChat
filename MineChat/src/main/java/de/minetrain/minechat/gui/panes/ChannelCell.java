package de.minetrain.minechat.gui.panes;

import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

public class ChannelCell extends ListCell<ChannelViewModel> {

	private ImageView imageView;

	public ChannelCell() {
		imageView = new ImageView();
		imageView.setPreserveRatio(true);
	}

	@Override
	protected void updateItem(ChannelViewModel item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setText(null);
			setGraphic(null);
			imageView.setImage(null);
		} else {
			imageView.setImage(item.getProfileImageSmall());
			setText(item.getChannelName());
			setGraphic(imageView);
		}
	}
}
