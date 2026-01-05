package de.minetrain.minechat.gui.settings;

import java.util.Comparator;
import java.util.Map;

import org.slf4j.helpers.MessageFormatter;

import de.minetrain.minechat.data.objectdata.CountVariable;
import de.minetrain.minechat.gui.panes.TooltipTableCell;
import de.minetrain.minechat.twitch.MessageManager;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

public class CountVariablesSettingsPane extends SettingsContentPane {

	public CountVariablesSettingsPane() {
		setTitle("Count Variables");

		TableView<CountVariable> countVariableTable = createCountVariableTable();

		setCenter(countVariableTable);

		Button addCountVariableButton = new Button("Add");
		addCountVariableButton.setOnAction(_ -> {
			MessageManager.createCountVariable().ifPresent(newCountVariable -> {
				countVariableTable.getItems().add(newCountVariable);
				countVariableTable.getSelectionModel().select(newCountVariable);
				countVariableTable.sort();
			});
			countVariableTable.requestFocus();
		});
		Button editCountVariableButton = new Button("Edit");
		editCountVariableButton.setOnAction(_ -> {
			MessageManager.editCountVariable(countVariableTable.getSelectionModel().getSelectedItem()).ifPresent(editedCountVariable -> {
				int selectedIndex = countVariableTable.getSelectionModel().getSelectedIndex();
				countVariableTable.getItems().set(selectedIndex, editedCountVariable);
				countVariableTable.getSelectionModel().select(editedCountVariable);
				countVariableTable.sort();
			});
			countVariableTable.requestFocus();
		});
		editCountVariableButton.disableProperty().bind(countVariableTable.getSelectionModel().selectedItemProperty().isNull());
		Button deleteCountVariableButton = new Button("Delete");
		// TODO confirmation dialog
		deleteCountVariableButton.setOnAction(_ -> {
			CountVariable countVariable = countVariableTable.getSelectionModel().getSelectedItem();
			if (MessageManager.deleteCountVariable(countVariable)) {
				countVariableTable.getItems().remove(countVariable);
				countVariableTable.getSelectionModel().clearSelection();
			}
		});
		deleteCountVariableButton.disableProperty().bind(countVariableTable.getSelectionModel().selectedItemProperty().isNull());
		Button copyInfoButton = new Button("Copy Info");
		copyInfoButton.setOnAction(_ -> {
			Clipboard clipboard = Clipboard.getSystemClipboard();
			String info = """
				{C_D_{}} - Display current value
				{C_2_{}} - Increase by 2 and display
				{C_{}"} - Increase by 1 and display
				""";
			String name = countVariableTable.getSelectionModel().getSelectedItem().getName();
			clipboard.setContent(Map.of(DataFormat.PLAIN_TEXT,
					MessageFormatter.basicArrayFormat(info, new Object[] { name, name, name })));
		});
		copyInfoButton.disableProperty().bind(countVariableTable.getSelectionModel().selectedItemProperty().isNull());
		addFunctionsBarItem(addCountVariableButton);
		addFunctionsBarItem(editCountVariableButton);
		addFunctionsBarItem(deleteCountVariableButton);
		addFunctionsBarItem(copyInfoButton);

		countVariableTable.getItems().setAll(MessageManager.getAllCountVariables().stream().sorted(Comparator.comparing(CountVariable::getName)).toList());
	}

	private TableView<CountVariable> createCountVariableTable() {
		TableView<CountVariable> countVariableTable = new TableView<>();
		countVariableTable.setEditable(true);
		TableColumn<CountVariable, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setPrefWidth(350D);
		nameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
		nameColumn.setCellFactory(TooltipTableCell.forTableColumn());
		TableColumn<CountVariable, Number> valueColumn = new TableColumn<>("Value");
		valueColumn.setPrefWidth(350D);
		valueColumn.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getValue()));
		valueColumn.setCellFactory(TooltipTableCell.forTableColumn());
		countVariableTable.getColumns().add(nameColumn);
		countVariableTable.getColumns().add(valueColumn);

		countVariableTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
		return countVariableTable;
	}
}
