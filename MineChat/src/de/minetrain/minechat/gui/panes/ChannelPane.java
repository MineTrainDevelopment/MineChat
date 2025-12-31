package de.minetrain.minechat.gui.panes;

import java.util.ArrayList;

import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.flowless.VirtualizedScrollPane;

import de.minetrain.minechat.data.objectdata.ChatMessage;
import de.minetrain.minechat.gui.utils.NotifiableObservableListWrapper;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ChannelPane extends VBox {

	private final MacroPanelPane marcoPanelPane;
	private final InputFieldPane inputFieldPane;
	private VirtualFlow<ChatMessage, MessageCell> messageVirtualFlow;
	private ListChangeListener<ChatMessage> listChangeListener;

	private ObjectProperty<ChannelViewModel> channelProperty;

	public ChannelPane() {
		getStyleClass().add("channel-pane");
		marcoPanelPane = new MacroPanelPane();
		marcoPanelPane.channelProperty().bind(channelProperty());
		messageVirtualFlow = VirtualFlow.createVertical(new NotifiableObservableListWrapper<>(new ArrayList<>()), MessageCell::new);
		channelProperty().flatMap(ChannelViewModel::messagesProperty).addListener(this::handleChangedMessageSet);
		inputFieldPane = new InputFieldPane();

		getChildren().addAll(marcoPanelPane, messageVirtualFlow, inputFieldPane);
		VBox.setVgrow(messageVirtualFlow, Priority.ALWAYS);
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

	public VirtualFlow<ChatMessage, MessageCell> getMessageVirtualFlow() {
		return messageVirtualFlow;
	}

	public InputFieldPane getInputFieldPane() {
		return inputFieldPane;
	}

	public MacroPanelPane getMacroPanelPane() {
		return marcoPanelPane;
	}

	private void handleChangedMessageSet(ObservableValue<?> obs, NotifiableObservableListWrapper<ChatMessage> oldMessages, NotifiableObservableListWrapper<ChatMessage> newMessages) {
		if (listChangeListener != null && oldMessages != null) {
			oldMessages.removeListener(listChangeListener);
		}
		messageVirtualFlow = VirtualFlow.createVertical(newMessages, MessageCell::new);
		messageVirtualFlow.showAsLast(newMessages.size() - 1);
		listChangeListener = change -> {
			while (change.next()) {
				if (change.wasAdded() && change.getFrom() - 1 == messageVirtualFlow.getLastVisibleIndex()) {
					messageVirtualFlow.showAsLast(change.getTo() - 1);
				}
			}
		};
		newMessages.addListener(listChangeListener);
		VirtualizedScrollPane<VirtualFlow<ChatMessage, MessageCell>> virtualizedScrollPane = new VirtualizedScrollPane<>(messageVirtualFlow);
		VBox.setVgrow(virtualizedScrollPane, Priority.ALWAYS);
		getChildren().set(1, virtualizedScrollPane);
	}
}
