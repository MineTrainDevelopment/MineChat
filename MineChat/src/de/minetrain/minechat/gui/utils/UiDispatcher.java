package de.minetrain.minechat.gui.utils;

import javafx.application.Platform;

public final class UiDispatcher {

	public static void runOnUiThread(Runnable runnable) {
		if (Platform.isFxApplicationThread()) {
			runnable.run();
		} else {
			Platform.runLater(runnable);
		}
	}

	private UiDispatcher() {
		// Private constructor to prevent instantiation
	}
}
