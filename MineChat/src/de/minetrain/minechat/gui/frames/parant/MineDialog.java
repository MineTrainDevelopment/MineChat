package de.minetrain.minechat.gui.frames.parant;

import de.minetrain.minechat.main.Main;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.paint.Color;

public abstract class MineDialog<R> extends Dialog<R> {

	protected MineDialog() {
		initStyle(Main.primaryStage.getStyle());
		getDialogPane().getScene().setFill(Color.TRANSPARENT);
		getDialogPane().getStylesheets().addAll(Main.primaryStage.getScene().getStylesheets());
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		setResultConverter(dialogButton -> switch (dialogButton.getButtonData()) {
			case OK_DONE -> yieldResultOnSuccess();
			default -> null;
		});
	}

	protected abstract R yieldResultOnSuccess();
}
