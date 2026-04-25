package de.minetrain.minechat.gui.panes;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.util.Logging;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;

public class TabButton extends Button {

	private static final PseudoClass SELECTED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("selected");

	private BooleanProperty selectedProperty;

	public TabButton() {
		super();
		getStyleClass().add("tab-button");
	}

	public BooleanProperty selectedProperty() {
		if (selectedProperty == null) {
			selectedProperty = new SimpleBooleanProperty(this, "selected") {
				@Override
				protected void invalidated() {
					PlatformLogger logger = Logging.getInputLogger();
					if (logger.isLoggable(Level.FINER)) {
						logger.finer(this + " selected=" + get());
					}
					pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, get());
				}
			};
		}
		return selectedProperty;
	}
}
