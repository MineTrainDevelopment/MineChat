package de.minetrain.minechat.utils.events;

import de.minetrain.minechat.gui.viewmodel.MacroViewModel;
import de.minetrain.minechat.utils.OutboundChatMessage;

public interface MineChatEvents {
	public void onSentMessageEvent(OutboundChatMessage message);
	public void onExecuteMacroEvent(MacroViewModel macro);
}
