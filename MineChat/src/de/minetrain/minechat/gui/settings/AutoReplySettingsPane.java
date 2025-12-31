package de.minetrain.minechat.gui.settings;

import java.util.regex.Pattern;

import de.minetrain.minechat.gui.viewmodel.AutoReplyViewModel;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AutoReplySettingsPane extends SettingsContentPane {

	public AutoReplySettingsPane() {
		setTitle("Auto Reply");

		TableView<AutoReplyViewModel> autoReplyTable = new TableView<>();
		TableColumn<AutoReplyViewModel, Boolean> enabledColumn = new TableColumn<>("Enabled");
		TableColumn<AutoReplyViewModel, String> channelColumn = new TableColumn<>("Channel");
		TableColumn<AutoReplyViewModel, Pattern> triggerRegexColumn = new TableColumn<>("Pattern");
		TableColumn<AutoReplyViewModel, String> actionsColumn = new TableColumn<>("Message");
		autoReplyTable.getColumns().add(enabledColumn);
		autoReplyTable.getColumns().add(channelColumn);
		autoReplyTable.getColumns().add(triggerRegexColumn);
		autoReplyTable.getColumns().add(actionsColumn);

		setCenter(autoReplyTable);

		CheckBox onlyObserveActiveChannel = new CheckBox("Only observe active channel");
		addAddtionalSettingsPane(onlyObserveActiveChannel);

		Button addAutoReplyButton = new Button("Add");
		Button editAutoReplyButton = new Button("Edit");
		Button deleteAutoReplyButton = new Button("Delete");
		addFunctionsBarItem(addAutoReplyButton);
		addFunctionsBarItem(editAutoReplyButton);
		addFunctionsBarItem(deleteAutoReplyButton);
	}
}
