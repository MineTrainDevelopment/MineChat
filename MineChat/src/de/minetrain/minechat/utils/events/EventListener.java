package de.minetrain.minechat.utils.events;

import de.minetrain.minechat.features.macros.MacroObject;
import de.minetrain.minechat.gui.obj.messages.MessageComponentContent;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.ChatMessage;

abstract class EventListener implements MineChatEvents{

	@Override
	public void onIncomingMessageEvent(TwitchMessage message){}

	@Override
	public void onMessageHighliteEvent(MessageComponentContent content){}
	
	@Override
	public void onSentMessageEvent(ChatMessage message){}
	
	@Override
	public void onExecuteMacroEvent(MacroObject macro){}
	

}
