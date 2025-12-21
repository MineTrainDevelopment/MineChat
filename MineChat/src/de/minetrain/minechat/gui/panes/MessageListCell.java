package de.minetrain.minechat.gui.panes;

import de.minetrain.minechat.data.objectdata.ChatMessage;
import de.minetrain.minechat.gui.obj.messages.MessageComponent;
import javafx.scene.control.ListCell;

public class MessageListCell extends ListCell<ChatMessage> {

	private final MessageComponent messageComponent;

	public MessageListCell() {
		messageComponent = new MessageComponent();
		// Prevent horizontal scroll
		messageComponent.setMinWidth(0);
		messageComponent.setPrefWidth(1);
	}

	@Override
	protected void updateItem(ChatMessage item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setGraphic(null);
			messageComponent.clearMessage();
		} else {
			messageComponent.applyMessage(item);
			setGraphic(messageComponent);
		}
	}
}
