package de.minetrain.minechat.gui.viewmodel;

public class EmoteViewModel implements IEmoteViewModel {

	private final String emoteId;
	private final String name;
	private final boolean animated;

	public EmoteViewModel(String emoteId, String name, boolean animated) {
		this.emoteId = emoteId;
		this.name = name;
		this.animated = animated;
	}

	@Override
	public String getEmoteId() {
		return emoteId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isAnimated() {
		return animated;
	}
}
