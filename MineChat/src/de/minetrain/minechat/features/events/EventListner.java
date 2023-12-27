package de.minetrain.minechat.features.events;

import de.minetrain.minechat.config.obj.MacroObject;
import de.minetrain.minechat.gui.obj.messages.MessageComponentContent;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.ChatMessage;

abstract class EventListner implements MineChatEvents{

	@Override
	public void onIncomingMessageEvent(TwitchMessage message){}

	@Override
	public void onMessageHighliteEvent(MessageComponentContent content){}
	
	@Override
	public void onSentMessageEvent(ChatMessage message){}
	
	@Override
	public void onExecuteMacroEvent(MacroObject macro){}
	

}
