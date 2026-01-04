package de.minetrain.minechat.utils.events;

import de.minetrain.minechat.gui.viewmodel.MacroViewModel;
import de.minetrain.minechat.utils.OutboundChatMessage;

abstract class EventListener implements MineChatEvents{

	@Override
	public void onSentMessageEvent(OutboundChatMessage message){}

	@Override
	public void onExecuteMacroEvent(MacroViewModel macro){}
}
