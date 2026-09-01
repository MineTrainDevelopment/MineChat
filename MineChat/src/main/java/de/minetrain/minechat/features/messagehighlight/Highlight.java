package de.minetrain.minechat.features.messagehighlight;

import de.minetrain.minechat.gui.utils.ColorManager;
import javafx.scene.paint.Color;

public class Highlight {

	private final HighlightType type;
	private final int borderColor;
	private final boolean active;

	private transient Color cachedColor;

	public Highlight(HighlightType type, int borderColor, boolean active) {
		this.type = type;
		this.borderColor = borderColor;
		this.active = active;
	}

	public HighlightType getType() {
		return type;
	}

	public int getColor() {
		return borderColor;
	}

	public Color getColorAsColor() {
		if (cachedColor == null) {
			cachedColor = ColorManager.decodeFromInt(borderColor);
		}
		return cachedColor;
	}

	public boolean isActive() {
		return active;
	}

	public Builder buildCopy() {
		return new Builder()
			.withType(type)
			.withBorderColor(borderColor)
			.withActive(active);
	}

	public static class Builder {

		private HighlightType type;
		private int borderColor;
		private boolean active;

		public Builder withType(HighlightType type) {
			this.type = type;
			return this;
		}

		public Builder withBorderColor(int borderColor) {
			this.borderColor = borderColor;
			return this;
		}

		public Builder withActive(boolean active) {
			this.active = active;
			return this;
		}

		public Highlight build() {
			return new Highlight(type, borderColor, active);
		}
	}
}
