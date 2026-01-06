package de.minetrain.minechat.twitch;

import java.time.LocalDateTime;

import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import javafx.scene.input.Clipboard;

public class ClipboardVariable implements IVariable {

	@Override
	public String[] getNames() {
		return new String[] { "CLIP", "CLIPBOARD", "CLIP_BOARD" };
	}

	@Override
	public String retrieveValue(ChannelViewModel channelViewModel, LocalDateTime localDateTime) {
		String string = Clipboard.getSystemClipboard().getString();
		return string != null ? string : "";
	}
}
