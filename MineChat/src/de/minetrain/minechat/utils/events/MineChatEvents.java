package de.minetrain.minechat.utils.events;

import de.minetrain.minechat.features.macros.MacroObject;
import de.minetrain.minechat.utils.ChatMessage;
import de.minetrain.minechat.utils.message.Message;


public interface MineChatEvents {
	public void onIncomingMessageEvent(Message message);
	public void onMessageHighliteEvent(Message content);
	public void onSentMessageEvent(ChatMessage message);
	public void onExecuteMacroEvent(MacroObject macro);
	

}
