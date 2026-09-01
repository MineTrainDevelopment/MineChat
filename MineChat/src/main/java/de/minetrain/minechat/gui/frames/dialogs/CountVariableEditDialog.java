package de.minetrain.minechat.gui.frames.dialogs;

import java.util.regex.Pattern;

import de.minetrain.minechat.data.objectdata.CountVariable;
import de.minetrain.minechat.gui.utils.LongSpinnerValueFactory;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class CountVariableEditDialog extends MineDialog<CountVariable> {

	private CountVariable.Builder countVariableBuilder;

	private TextField nameField;
	private Spinner<Long> valueSpinner;

	public CountVariableEditDialog(CountVariable.Builder countVariable) {
		setTitle("Edit count variable");
		setWidth(450);

		countVariableBuilder = countVariable;

		GridPane contentRoot = createRootGrid();

		nameField = new TextField();
		nameField.setPromptText("Variable name");
		nameField.setText(countVariable.getName());
		Pattern uppercasePattern = Pattern.compile("[A-Z]*");
		nameField.setTextFormatter(new TextFormatter<>(change -> {
			change.setText(change.getText().toUpperCase());
			if (uppercasePattern.matcher(change.getControlNewText()).matches()) {
				return change;
			}
			return null;
		}));
		contentRoot.add(nameField, 0, 0, 2, 1);

		Label valueLabel = new Label("Value:");
		contentRoot.add(valueLabel, 0, 1);
		valueSpinner = new Spinner<>(new LongSpinnerValueFactory(Long.MIN_VALUE, Long.MAX_VALUE, 0L));
		valueSpinner.setEditable(true);
		valueSpinner.getValueFactory().setValue(countVariable.getValue());
		valueSpinner.setMaxWidth(Double.MAX_VALUE);
		contentRoot.add(valueSpinner, 1, 1);

		getDialogPane().setContent(contentRoot);
		getDialogPane().lookupButton(ButtonType.OK).disableProperty()
			.bind(nameField.textProperty().isEmpty());
	}

	@Override
	protected CountVariable yieldResultOnSuccess() {
		return countVariableBuilder
			.withName(nameField.getText().trim())
			.withValue(valueSpinner.getValue())
			.build();
	}

	private GridPane createRootGrid() {
		GridPane contentRoot = new GridPane();
		contentRoot.setHgap(10);
		contentRoot.setVgap(10);

		ColumnConstraints col0 = new ColumnConstraints();
		col0.setPercentWidth(25.0);
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(75.0);
		contentRoot.getColumnConstraints().addAll(col0, col1);

		RowConstraints row0 = new RowConstraints();
		row0.setPercentHeight(50.0);
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(50.0);
		contentRoot.getRowConstraints().addAll(row0, row1);
		return contentRoot;
	}
}
