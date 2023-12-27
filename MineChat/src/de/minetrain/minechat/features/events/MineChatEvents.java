package de.minetrain.minechat.features.events;

import de.minetrain.minechat.config.obj.MacroObject;
import de.minetrain.minechat.gui.obj.messages.MessageComponentContent;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.ChatMessage;


public interface MineChatEvents {
	public void onIncomingMessageEvent(TwitchMessage message);
	public void onMessageHighliteEvent(MessageComponentContent content);
	public void onSentMessageEvent(ChatMessage message);
	public void onExecuteMacroEvent(MacroObject macro);
	

}
