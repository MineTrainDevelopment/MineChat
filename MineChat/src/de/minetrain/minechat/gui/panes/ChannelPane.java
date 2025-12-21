package de.minetrain.minechat.gui.panes;

import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ChannelPane extends VBox {

	private final MacroPanelPane marcoPanelPane;
	private final MessageListView messageListView;
	private final InputFieldPane inputFieldPane;

	private ObjectProperty<ChannelViewModel> channelProperty;

	public ChannelPane() {
		marcoPanelPane = new MacroPanelPane();
		marcoPanelPane.channelProperty().bind(channelProperty());
		messageListView = new MessageListView();
		messageListView.messagesProperty().bind(channelProperty().flatMap(ChannelViewModel::messagesProperty));
		inputFieldPane = new InputFieldPane();

		getChildren().addAll(marcoPanelPane, messageListView, inputFieldPane);
		VBox.setVgrow(messageListView, Priority.ALWAYS);
	}

	public ObjectProperty<ChannelViewModel> channelProperty() {
		if (channelProperty == null) {
			channelProperty = new SimpleObjectProperty<>(this, "channel");
		}
		return channelProperty;
	}

	public void setChannel(ChannelViewModel channel) {
		channelProperty().set(channel);
	}

	public ChannelViewModel getChannel() {
		return channelProperty().get();
	}

	public MessageListView getMessageListView() {
		return messageListView;
	}

	public InputFieldPane getInputFieldPane() {
		return inputFieldPane;
	}

	public MacroPanelPane getMacroPanelPane() {
		return marcoPanelPane;
	}
}
