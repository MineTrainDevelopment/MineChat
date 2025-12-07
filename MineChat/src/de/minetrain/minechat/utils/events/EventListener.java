package de.minetrain.minechat.utils.events;

import de.minetrain.minechat.features.macros.MacroObject;
import de.minetrain.minechat.utils.ChatMessage;
import de.minetrain.minechat.utils.message.Message;

abstract class EventListener implements MineChatEvents{

	@Override
	public void onIncomingMessageEvent(Message message){}

	@Override
	public void onMessageHighliteEvent(Message content){}
	
	@Override
	public void onSentMessageEvent(ChatMessage message){}
	
	@Override
	public void onExecuteMacroEvent(MacroObject macro){}
	

}
