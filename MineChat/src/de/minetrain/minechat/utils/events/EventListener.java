package de.minetrain.minechat.utils.events;

import de.minetrain.minechat.features.macros.MacroViewModel;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.OutboundChatMessage;

abstract class EventListener implements MineChatEvents{

	@Override
	public void onIncomingMessageEvent(TwitchMessage message){}

	@Override
	public void onSentMessageEvent(OutboundChatMessage message){}

	@Override
	public void onExecuteMacroEvent(MacroViewModel macro){}
}
