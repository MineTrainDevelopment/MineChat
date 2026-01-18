package de.minetrain.minechat.gui.viewmodel;

import java.util.EnumMap;
import java.util.Map;

import de.minetrain.minechat.features.messagehighlight.HighlightType;

public class SettingsViewModel {

	private Map<HighlightType, HighlightViewModel> highlightColorMap;

	public SettingsViewModel() {
		highlightColorMap = new EnumMap<>(HighlightType.class);
		for (HighlightType type : HighlightType.values()) {
			highlightColorMap.put(type, new HighlightViewModel(type));
		}
	}

	public HighlightViewModel getHighlightViewModel(HighlightType type) {
		return highlightColorMap.get(type);
	}
}
