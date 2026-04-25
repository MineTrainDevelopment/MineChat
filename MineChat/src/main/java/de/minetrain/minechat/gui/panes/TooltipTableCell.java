package de.minetrain.minechat.gui.panes;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

public class TooltipTableCell<S, T> extends TableCell<S, T> {

	public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn() {
		return _ -> new TooltipTableCell<>();
	}

	@Override
	protected void updateItem(T item, boolean empty) {
		if (item == getItem()) {
			return;
		}

		super.updateItem(item, empty);

		if (item == null) {
			setTooltip(null);
			setText(null);
			setGraphic(null);
		} else if (item instanceof Node node) {
			setTooltip(null);
			setText(null);
			setGraphic(node);
		} else {
			String string = item.toString();
			setText(string);
			setTooltip(StringUtils.isNotBlank(string) ? new Tooltip(getText()) : null);
			setGraphic(null);
		}
	}
}
