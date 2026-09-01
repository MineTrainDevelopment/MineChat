package de.minetrain.minechat.gui.viewmodel;

public class EmoteViewModel implements IEmoteViewModel {

	private final String emoteId;
	private final String name;

	public EmoteViewModel(String emoteId, String name) {
		this.emoteId = emoteId;
		this.name = name;
	}

	@Override
	public String getEmoteId() {
		return emoteId;
	}

	@Override
	public String getName() {
		return name;
	}
}
