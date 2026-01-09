package de.minetrain.minechat.gui.settings;

import java.util.Comparator;

import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.twitch.MessageManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;

public class HighlightSettingsPane extends SettingsContentPane {

	public HighlightSettingsPane() {
		setTitle("Highlights");

		TableView<HighlightString> highlightsTable = createHighlightTable();

		setCenter(highlightsTable);

		Button addHighlightButton = new Button("Add");
		addHighlightButton.setOnAction(_ -> {
			MessageManager.createHighlightString().ifPresent(newHighlight -> {
				highlightsTable.getItems().add(newHighlight);
				highlightsTable.getSelectionModel().select(newHighlight);
				highlightsTable.sort();
			});
			highlightsTable.requestFocus();
		});
		Button editHighlightButton = new Button("Edit");
		editHighlightButton.setOnAction(_ -> {
			MessageManager.editHighlightString(highlightsTable.getSelectionModel().getSelectedItem()).ifPresent(editedHighlight -> {
				int selectedIndex = highlightsTable.getSelectionModel().getSelectedIndex();
				highlightsTable.getItems().set(selectedIndex, editedHighlight);
				highlightsTable.getSelectionModel().select(editedHighlight);
				highlightsTable.sort();
			});
			highlightsTable.requestFocus();
		});
		editHighlightButton.disableProperty().bind(highlightsTable.getSelectionModel().selectedItemProperty().isNull());
		Button deleteHighlightButton = new Button("Delete");
		// TODO confirmation dialog
		deleteHighlightButton.setOnAction(_ -> {
			HighlightString highlight = highlightsTable.getSelectionModel().getSelectedItem();
			if (MessageManager.deleteHighlightString(highlight)) {
				highlightsTable.getItems().remove(highlight);
				highlightsTable.getSelectionModel().clearSelection();
			}
		});
		deleteHighlightButton.disableProperty().bind(highlightsTable.getSelectionModel().selectedItemProperty().isNull());
		addFunctionsBarItem(addHighlightButton);
		addFunctionsBarItem(editHighlightButton);
		addFunctionsBarItem(deleteHighlightButton);

		highlightsTable.getItems().setAll(MessageManager.getAllHighlightStrings().stream().sorted(Comparator.comparing(HighlightString::getPattern, String.CASE_INSENSITIVE_ORDER)).toList());
	}

	private TableView<HighlightString> createHighlightTable() {
		TableView<HighlightString> hightlightTable = new TableView<>();
		hightlightTable.setEditable(true);
		TableColumn<HighlightString, Boolean> enabledColumn = new TableColumn<>("Enabled");
		enabledColumn.setPrefWidth(100D);
		enabledColumn.setCellValueFactory(data -> new ReadOnlyBooleanWrapper(data.getValue().isEnabled()));
		enabledColumn.setCellFactory(_ -> createEnabledCell());
		TableColumn<HighlightString, String> patternColumn = new TableColumn<>("Regex pattern");
		patternColumn.setPrefWidth(600D);
		patternColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPattern()));
		patternColumn.setCellFactory(_ -> createPatternCell());
		patternColumn.setComparator(String.CASE_INSENSITIVE_ORDER);
		// TODO?
//		TableColumn<HighlightString, Pattern> borderColorColumn = new TableColumn<>("Pattern");
//		borderColorColumn.setPrefWidth(100D);
//		borderColorColumn.setCellValueFactory(data -> data.getValue().patternProperty());
//		borderColorColumn.setComparator(Comparator.comparing(Pattern::pattern, String.CASE_INSENSITIVE_ORDER));
//		borderColorColumn.setCellFactory(TooltipTableCell.forTableColumn());
		hightlightTable.getColumns().add(enabledColumn);
		hightlightTable.getColumns().add(patternColumn);
//		hightlightTable.getColumns().add(borderColorColumn);

		hightlightTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
		return hightlightTable;
	}

	private TableCell<HighlightString, String> createPatternCell() {
		return new TableCell<>() {

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
					setTooltip(null);
				} else {
					HighlightString rowItem = getTableRow().getItem();
					setText(item);
					Color color = rowItem.getWordColorAsColor();
					setTextFill(color);
					StringBuilder tooltip = new StringBuilder(item).append(' ').append('[');
					ColorManager.encode(color, tooltip);
					setTooltip(new Tooltip(tooltip.append(']').toString()));
				}
			}
		};
	}

	private TableCell<HighlightString, Boolean> createEnabledCell() {
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
						HighlightString highlight = getTableRow().getItem().buildCopy().withEnabled(checkBox.isSelected()).build();
						MessageManager.updateHighlightString(highlight);
						getTableRow().getTableView().getItems().set(getIndex(), highlight);
						getTableRow().getTableView().getSelectionModel().select(highlight);
						getTableRow().getTableView().sort();
					});
				}
			}
		};
	}
}
