package de.minetrain.minechat.gui.frames.dialogs;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Pattern;

import de.minetrain.minechat.data.objectdata.AutoReply;
import de.minetrain.minechat.gui.input.ChatInputField;
import de.minetrain.minechat.gui.panes.ChannelCell;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.main.Main;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class AutoReplyEditDialog extends MineDialog<AutoReply> {

	private AutoReply.Builder autoReplyBuilder;

	private ComboBox<ChannelViewModel> channelSelector;
	private Spinner<Integer> messagesPerMinSpinner;
	private CheckBox replyCheckbox;
	private Spinner<Integer> delaySpinner;
	private TextField triggerPatternField;
	private ChatInputField replyMessageArea;

	public AutoReplyEditDialog(AutoReply.Builder autoReply) {
		setTitle("Edit auto reply");
		setWidth(450);

		autoReplyBuilder = autoReply;

		GridPane contentRoot = createRootGrid();

		channelSelector = new ComboBox<>();
		channelSelector.getItems().addAll(Main.getChannelManager().getChannelViewModels());
		channelSelector.getItems().sort(Comparator.comparing(ChannelViewModel::getChannelName, String.CASE_INSENSITIVE_ORDER));
		channelSelector.setMaxWidth(Double.MAX_VALUE);
		channelSelector.setCellFactory(_ -> new ChannelCell());
		channelSelector.setButtonCell(channelSelector.getCellFactory().call(null));
		channelSelector.getSelectionModel()
				.select(channelSelector.getItems().stream()
						.filter(item -> Objects.equals(item.getChannelId(), autoReply.getChannelId())).findFirst()
						.orElse(channelSelector.getItems().getFirst()));
		contentRoot.add(channelSelector, 0, 0);
		Label messagesPerMinLabel = new Label("Messages / min:");
		contentRoot.add(messagesPerMinLabel, 1, 0);
		messagesPerMinSpinner = new Spinner<>(1, 9999, 1);
		messagesPerMinSpinner.setEditable(true);
		messagesPerMinSpinner.getValueFactory().setValue(autoReply.getMessagesPerMinute());
		contentRoot.add(messagesPerMinSpinner, 2, 0);

		replyCheckbox = new CheckBox("Reply to trigger message");
		replyCheckbox.setSelected(autoReply.isReply());
		contentRoot.add(replyCheckbox, 0, 1);
		Label delayLabel = new Label("Reply delay (s):");
		contentRoot.add(delayLabel, 1, 1);
		delaySpinner = new Spinner<>(0, 9999, 0);
		delaySpinner.setEditable(true);
		delaySpinner.getValueFactory().setValue(autoReply.getDelay());
		contentRoot.add(delaySpinner, 2, 1);

		triggerPatternField = new TextField();
		triggerPatternField.setPromptText("Trigger pattern (Regex)");
		triggerPatternField.setText(autoReply.getPattern());
		triggerPatternField.focusedProperty().addListener((_, _, isNowFocused) -> {
			if (!isNowFocused.booleanValue()) {
				try {
					Pattern.compile(triggerPatternField.getText());
				} catch (Exception e) {
					triggerPatternField.setText(Pattern.quote(triggerPatternField.getText()));
				}
			}
		});
		contentRoot.add(triggerPatternField, 0, 2, 3, 1);

		replyMessageArea = new ChatInputField();
		if (autoReply.getOutput() != null) {
			replyMessageArea.replaceText(String.join("\n", autoReply.getOutput()));
		}
		contentRoot.add(replyMessageArea, 0, 3, 3, 1);

		getDialogPane().setContent(contentRoot);
	}

	@Override
	protected AutoReply yieldResultOnSuccess() {
		return autoReplyBuilder
			.withChannelId(channelSelector.getSelectionModel().getSelectedItem().getChannelId())
			.withMessagesPerMinute(messagesPerMinSpinner.getValue())
			.withDelay(delaySpinner.getValue())
			.withReply(replyCheckbox.isSelected())
			.withPattern(triggerPatternField.getText().trim())
			.withOutput(Arrays.asList(replyMessageArea.getPlainText().split("\n")))
			.build();
	}

	private GridPane createRootGrid() {
		GridPane contentRoot = new GridPane();
		contentRoot.setHgap(10);
		contentRoot.setVgap(10);

		ColumnConstraints col0 = new ColumnConstraints();
		col0.setPercentWidth(54.0);
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(27.0);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(19.0);
		contentRoot.getColumnConstraints().addAll(col0, col1, col2);

		double heightPercentage = 100.0 / 6.0;
		RowConstraints row0 = new RowConstraints();
		row0.setPercentHeight(heightPercentage);
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(heightPercentage);
		RowConstraints row2 = new RowConstraints();
		row2.setPercentHeight(heightPercentage);
		RowConstraints row3 = new RowConstraints();
		row3.setPercentHeight(50.0);
		contentRoot.getRowConstraints().addAll(row0, row1, row2, row3);
		return contentRoot;
	}
}
