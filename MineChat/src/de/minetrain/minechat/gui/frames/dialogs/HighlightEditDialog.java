package de.minetrain.minechat.gui.frames.dialogs;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.gui.utils.ColorManager;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;

public class HighlightEditDialog extends MineDialog<HighlightString> {

	private HighlightString.Builder highlightBuilder;

	private TextField patternField;
	private ColorPicker wordColorPicker;
	private ColorPicker borderColorPicker;

	public HighlightEditDialog(HighlightString.Builder highlight) {
		setTitle("Edit highlight");
		setWidth(450);

		highlightBuilder = highlight;

		GridPane contentRoot = createRootGrid();

		patternField = new TextField();
		patternField.setPromptText("Regex pattern");
		patternField.setText(highlight.getPattern());
		contentRoot.add(patternField, 0, 0, 4, 1);

		Label wordColorLabel = new Label("Fill:");
		contentRoot.add(wordColorLabel, 0, 1);
		wordColorPicker = new ColorPicker();
		Color wordColor = highlight.getWordColorCode() != null ? Color.web(highlight.getWordColorCode()) : null;
		wordColorPicker.setValue(wordColor);
		contentRoot.add(wordColorPicker, 1, 1);

		Label borderColorLabel = new Label("Border:");
		contentRoot.add(borderColorLabel, 2, 1);
		borderColorPicker = new ColorPicker();
		Color borderColor = highlight.getBorderColorCode() != null ? Color.web(highlight.getBorderColorCode()) : null;
		borderColorPicker.setValue(borderColor);
		contentRoot.add(borderColorPicker, 3, 1);

		getDialogPane().setContent(contentRoot);
		getDialogPane().lookupButton(ButtonType.OK).disableProperty()
			.bind(patternField.textProperty().isEmpty().or(Bindings.createBooleanBinding(() -> {
				try {
					Pattern.compile(patternField.getText());
					return false;
				} catch (PatternSyntaxException _) {
					return true;
				}
			}, patternField.textProperty())));
	}

	@Override
	protected HighlightString yieldResultOnSuccess() {
		Color wordColor = wordColorPicker.getValue();
		Color borderColor = borderColorPicker.getValue();
		return highlightBuilder
			.withPattern(patternField.getText())
			.withWordColorCode(wordColor != null ? ColorManager.encode(wordColor) : null)
			.withBorderColorCode(borderColor != null ? ColorManager.encode(borderColor) : null)
			.build();
	}

	private GridPane createRootGrid() {
		GridPane contentRoot = new GridPane();
		contentRoot.setHgap(10);
		contentRoot.setVgap(10);

		ColumnConstraints col0 = new ColumnConstraints();
		col0.setPercentWidth(15.0);
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(35.0);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(15.0);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(35.0);
		contentRoot.getColumnConstraints().addAll(col0, col1, col2, col3);

		double  percentHeight = 100.0 / 2.0;
		RowConstraints row0 = new RowConstraints();
		row0.setPercentHeight(percentHeight);
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(percentHeight);
		contentRoot.getRowConstraints().addAll(row0, row1);
		return contentRoot;
	}
}
