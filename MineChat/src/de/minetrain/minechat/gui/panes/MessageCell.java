package de.minetrain.minechat.gui.panes;

import org.fxmisc.flowless.Cell;

import de.minetrain.minechat.data.objectdata.ChatMessage;
import de.minetrain.minechat.gui.obj.messages.MessageComponent;

public class MessageCell implements Cell<ChatMessage, MessageComponent> {

	private final MessageComponent messageComponent;
	private ChatMessage currentItem;

	public MessageCell(ChatMessage initialItem) {
		messageComponent = new MessageComponent();
		// Prevent horizontal scroll
		messageComponent.setMinWidth(0);
		messageComponent.setPrefWidth(1);

		updateItem(initialItem);
	}

	@Override
	public void updateItem(ChatMessage item) {
		currentItem = item;
		if (item == null) {
			messageComponent.clearMessage();
		} else {
			messageComponent.applyMessage(item);
		}
	}

	public void refreshItem() {
		if (currentItem != null) {
			messageComponent.applyMessage(currentItem);
		}
	}

	@Override
	public MessageComponent getNode() {
		return messageComponent;
	}

	@Override
	public boolean isReusable() {
		return true;
	}
}
