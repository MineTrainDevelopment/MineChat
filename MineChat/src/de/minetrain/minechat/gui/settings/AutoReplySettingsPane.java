package de.minetrain.minechat.gui.settings;

import java.util.Comparator;
import java.util.regex.Pattern;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.config.enums.AutoReplyState;
import de.minetrain.minechat.gui.panes.TooltipTableCell;
import de.minetrain.minechat.gui.viewmodel.AutoReplyViewModel;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.main.Main;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

public class AutoReplySettingsPane extends SettingsContentPane {

	public AutoReplySettingsPane() {
		setTitle("Auto Reply");

		TableView<AutoReplyViewModel> autoReplyTable = createAutoReplyTable();

		setCenter(autoReplyTable);

		CheckBox onlyObserveActiveChannel = new CheckBox("Only observe active channel");
		onlyObserveActiveChannel.setSelected(Settings.autoReplyState == AutoReplyState.CURRENT_TAB);
		onlyObserveActiveChannel.selectedProperty().addListener((_, _, newVal) -> Settings.setAutoReplyState(newVal.booleanValue() ? AutoReplyState.CURRENT_TAB : AutoReplyState.ALL));
		addAddtionalSettingsPane(onlyObserveActiveChannel);

		Button addAutoReplyButton = new Button("Add");
		addAutoReplyButton.setOnAction(_ -> {
			Main.getChannelManager().createAutoReply().ifPresent(newAutoReply -> {
				autoReplyTable.getItems().add(newAutoReply);
				autoReplyTable.getSelectionModel().select(newAutoReply);
				autoReplyTable.sort();
			});
			autoReplyTable.requestFocus();
		});
		Button editAutoReplyButton = new Button("Edit");
		editAutoReplyButton.setOnAction(_ -> {
			Main.getChannelManager().editAutoReply(autoReplyTable.getSelectionModel().getSelectedItem());
			autoReplyTable.sort();
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
		});
		deleteAutoReplyButton.disableProperty().bind(autoReplyTable.getSelectionModel().selectedItemProperty().isNull());
		addFunctionsBarItem(addAutoReplyButton);
		addFunctionsBarItem(editAutoReplyButton);
		addFunctionsBarItem(deleteAutoReplyButton);

		autoReplyTable.getItems().setAll(Main.getChannelManager().getChannelViewModels().stream()
			.sorted(Comparator.comparing(ChannelViewModel::getChannelName, String.CASE_INSENSITIVE_ORDER))
			.flatMap(channel -> channel.getAutoReplies().stream().sorted(Comparator.comparing(AutoReplyViewModel::getUuid)))
			.toList());
	}

	private TableView<AutoReplyViewModel> createAutoReplyTable() {
		TableView<AutoReplyViewModel> autoReplyTable = new TableView<>();
		autoReplyTable.setEditable(true);
		TableColumn<AutoReplyViewModel, Boolean> enabledColumn = new TableColumn<>("Enabled");
		enabledColumn.setPrefWidth(100D);
		enabledColumn.setCellValueFactory(data -> data.getValue().enabledProperty());
		enabledColumn.setCellFactory(_ -> createEnabledCell());
		TableColumn<AutoReplyViewModel, String> channelColumn = new TableColumn<>("Channel");
		channelColumn.setPrefWidth(200D);
		channelColumn.setCellValueFactory(data -> data.getValue().channelProperty().flatMap(ChannelViewModel::channelNameProperty));
		channelColumn.setCellFactory(_ -> createChannelCell());
		channelColumn.setComparator(String.CASE_INSENSITIVE_ORDER);
		TableColumn<AutoReplyViewModel, Pattern> patternColumn = new TableColumn<>("Pattern");
		patternColumn.setPrefWidth(100D);
		patternColumn.setCellValueFactory(data -> data.getValue().patternProperty());
		patternColumn.setComparator(Comparator.comparing(Pattern::pattern, String.CASE_INSENSITIVE_ORDER));
		patternColumn.setCellFactory(TooltipTableCell.forTableColumn());
		TableColumn<AutoReplyViewModel, String> messageColumn = new TableColumn<>("Message");
		messageColumn.setPrefWidth(300D);
		messageColumn.setCellValueFactory(data -> data.getValue().outputProperty().map(output -> String.join("; ", output)));
		messageColumn.setComparator(String.CASE_INSENSITIVE_ORDER);
		messageColumn.setCellFactory(TooltipTableCell.forTableColumn());
		autoReplyTable.getColumns().add(enabledColumn);
		autoReplyTable.getColumns().add(channelColumn);
		autoReplyTable.getColumns().add(patternColumn);
		autoReplyTable.getColumns().add(messageColumn);

		autoReplyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
		return autoReplyTable;
	}

	private TableCell<AutoReplyViewModel, String> createChannelCell() {
		return new TableCell<>() {
			private final ImageView imageView = new ImageView();
			{
				imageView.setPreserveRatio(true);
			}

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
					setTooltip(null);
				} else {
					setText(item);
					setTooltip(new Tooltip(item));
					AutoReplyViewModel rowItem = getTableRow().getItem();
					if (rowItem != null && rowItem.getChannel() != null) {
						imageView.setImage(rowItem.getChannel().getProfileImageSmall());
						setGraphic(imageView);
					} else {
						setGraphic(null);
					}
				}
			}
		};
	}

	private TableCell<AutoReplyViewModel, Boolean> createEnabledCell() {
		return new TableCell<>() {

			private final CheckBox checkBox = new CheckBox();
			{
				getStyleClass().add("check-box-table-cell");
				checkBox.setAlignment(Pos.CENTER);
			}

			@Override
			protected void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					setGraphic(checkBox);
					checkBox.setSelected(item);
					checkBox.disableProperty().bind(Bindings.not(getTableView().editableProperty()
						.and(getTableColumn().editableProperty())
						.and(editableProperty())));
					checkBox.setOnAction(_ -> {
						AutoReplyViewModel autoReply = getTableRow().getItem();
						autoReply.setEnabled(checkBox.isSelected());
						Main.getChannelManager().updateAutoReply(autoReply);
					});
				}
			}
		};
	}
}
