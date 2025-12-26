package de.minetrain.minechat.gui.input;

import org.fxmisc.richtext.model.NodeSegmentOpsBase;

import de.minetrain.minechat.gui.viewmodel.EmoteViewModel;
import de.minetrain.minechat.gui.viewmodel.IEmoteViewModel;

public class EmoteOps<S> extends NodeSegmentOpsBase<IEmoteViewModel, S> {

	public EmoteOps() {
		super(new EmoteViewModel("", "", false));
	}

	@Override
	public int length(IEmoteViewModel emote) {
		return emote.getName().isBlank() ? 0 : 1;
	}
}