package de.minetrain.minechat.gui.viewmodel;

import de.minetrain.minechat.features.messagehighlight.Highlight;
import de.minetrain.minechat.features.messagehighlight.HighlightType;
import de.minetrain.minechat.gui.utils.ColorManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableStringValue;

public class HighlightViewModel {

	private HighlightType type;
	private BooleanProperty activeProperty;
	private IntegerProperty colorProperty;
	private ObservableStringValue backgroundStyleProperty;
	private ObservableStringValue borderStyleProperty;

	public HighlightViewModel(HighlightType type) {
		this.type = type;
	}

	public HighlightType getType() {
		return type;
	}

	public BooleanProperty activeProperty() {
		if (activeProperty == null) {
			activeProperty = new SimpleBooleanProperty(this, "active", false);
		}
		return activeProperty;
	}

	public IntegerProperty colorProperty() {
		if (colorProperty == null) {
			colorProperty = new SimpleIntegerProperty(this, "color", 0xFFFFFFFF);
		}
		return colorProperty;
	}

	public int getColor() {
		return colorProperty().get();
	}

	public void setColor(int color) {
		colorProperty().set(color);
	}

	public boolean isActive() {
		return activeProperty().get();
	}

	public void setActive(boolean active) {
		activeProperty().set(active);
	}

	public String getBackgroundStyle() {
		return backgroundStyleProperty().get();
	}

	public String getBorderStyle() {
		return borderStyleProperty().get();
	}

	public void apply(Highlight highlight) {
		setActive(highlight.isActive());
		setColor(highlight.getColor());
	}

	public ObservableStringValue backgroundStyleProperty() {
		if (backgroundStyleProperty == null) {
			backgroundStyleProperty = Bindings.createStringBinding(() -> ColorManager.encode(colorProperty().get(), "-fx-background-color: ", ";"), colorProperty());
		}
		return backgroundStyleProperty;
	}

	public ObservableStringValue borderStyleProperty() {
		if (borderStyleProperty == null) {
			borderStyleProperty = Bindings.createStringBinding(() -> ColorManager.encode(colorProperty().get(), "-fx-border-color: ", ";"), colorProperty());
		}
		return borderStyleProperty;
	}

	public Highlight toHighlight() {
		return new Highlight(getType(), getColor(), isActive());
	}
}
