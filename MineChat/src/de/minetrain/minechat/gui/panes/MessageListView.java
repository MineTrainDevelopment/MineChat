package de.minetrain.minechat.gui.panes;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.objectdata.ChatMessage;
import de.minetrain.minechat.gui.utils.NotifiableObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;

public class MessageListView extends ListView<ChatMessage> {

	private static final Logger LOG = LoggerFactory.getLogger(MessageListView.class);

	private NotifiableObjectProperty<List<ChatMessage>> messagesProperty;

	public MessageListView() {
		setCellFactory(_ -> new MessageListCell());
		setSelectionModel(null);
		itemsProperty().bind(messagesProperty().map(FXCollections::observableList));
		itemsProperty().addListener((_, _, newItems) -> {
			LOG.info("Messages updated, new size: {}", newItems != null ? newItems.size() : "null");
			if (newItems != null) {
				scrollTo(newItems.size() - 1);
			}
		});
	}

	public NotifiableObjectProperty<List<ChatMessage>> messagesProperty() {
		if (messagesProperty == null) {
			messagesProperty = new NotifiableObjectProperty<>(this, "messages");
		}
		return messagesProperty;
	}

	public void setMessages(List<ChatMessage> messages) {
		messagesProperty().set(messages);
	}

	public List<ChatMessage> getMessages() {
		return messagesProperty().get();
	}
}
