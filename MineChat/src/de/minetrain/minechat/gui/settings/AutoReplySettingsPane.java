package de.minetrain.minechat.gui.settings;

import java.util.regex.Pattern;

import de.minetrain.minechat.gui.viewmodel.AutoReplyViewModel;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.main.Main;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AutoReplySettingsPane extends SettingsContentPane {

	public AutoReplySettingsPane() {
		setTitle("Auto Reply");

		TableView<AutoReplyViewModel> autoReplyTable = new TableView<>();
		TableColumn<AutoReplyViewModel, Boolean> enabledColumn = new TableColumn<>("Enabled");
		enabledColumn.setPrefWidth(100D);
		enabledColumn.setCellValueFactory(data -> data.getValue().enabledProperty());
		TableColumn<AutoReplyViewModel, String> channelColumn = new TableColumn<>("Channel");
		channelColumn.setPrefWidth(100D);
		channelColumn.setCellValueFactory(data -> data.getValue().channelProperty().flatMap(ChannelViewModel::channelNameProperty));
		TableColumn<AutoReplyViewModel, Pattern> patternColumn = new TableColumn<>("Pattern");
		patternColumn.setPrefWidth(100D);
		patternColumn.setCellValueFactory(data -> data.getValue().patternProperty());
		TableColumn<AutoReplyViewModel, String> messageColumn = new TableColumn<>("Message");
		messageColumn.setPrefWidth(400D);
		messageColumn.setCellValueFactory(data -> data.getValue().outputProperty().map(output -> String.join("; ", output)));
		autoReplyTable.getColumns().add(enabledColumn);
		autoReplyTable.getColumns().add(channelColumn);
		autoReplyTable.getColumns().add(patternColumn);
		autoReplyTable.getColumns().add(messageColumn);

		autoReplyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);

		setCenter(autoReplyTable);

		CheckBox onlyObserveActiveChannel = new CheckBox("Only observe active channel");
		addAddtionalSettingsPane(onlyObserveActiveChannel);

		Button addAutoReplyButton = new Button("Add");
		addAutoReplyButton.setOnAction(_ -> Main.getChannelManager().createAutoReply().ifPresent(newAutoReply -> {
			autoReplyTable.getItems().add(newAutoReply);
			autoReplyTable.getSelectionModel().select(newAutoReply);
			autoReplyTable.requestFocus();
		}));
		Button editAutoReplyButton = new Button("Edit");
		editAutoReplyButton.setOnAction(_ -> {
			Main.getChannelManager().editAutoReply(autoReplyTable.getSelectionModel().getSelectedItem());
			autoReplyTable.requestFocus();
		});
		editAutoReplyButton.disableProperty().bind(autoReplyTable.getSelectionModel().selectedItemProperty().isNull());
		Button deleteAutoReplyButton = new Button("Delete");
		// TODO confirmation dialog
		deleteAutoReplyButton.setOnAction(_ -> {
			AutoReplyViewModel autoReplyViewModel = autoReplyTable.getSelectionModel().getSelectedItem();
			if (Main.getChannelManager().deleteAutoReply(autoReplyViewModel)) {
				autoReplyTable.getItems().remove(autoReplyViewModel);
				autoReplyTable.getSelectionModel().clearSelection();
			}
			autoReplyTable.requestFocus();
		});
		deleteAutoReplyButton.disableProperty().bind(autoReplyTable.getSelectionModel().selectedItemProperty().isNull());
		addFunctionsBarItem(addAutoReplyButton);
		addFunctionsBarItem(editAutoReplyButton);
		addFunctionsBarItem(deleteAutoReplyButton);

		autoReplyTable.getItems().setAll(Main.getChannelManager().channelsProperty().get().stream()
				.flatMap(channel -> channel.getAutoReplies().stream())
				.toList());
	}
}
