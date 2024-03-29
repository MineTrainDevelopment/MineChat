package de.minetrain.minechat.utils.events;

import de.minetrain.minechat.gui.obj.messages.MessageComponentContent;
import de.minetrain.minechat.twitch.obj.TwitchMessage;

public class EventTest extends EventListener {
	
	@Override
	public void onIncomingMessageEvent(TwitchMessage message) {
		super.onIncomingMessageEvent(message);
		System.err.println("Messsage?");
	}
	
	@Override
	public void onMessageHighliteEvent(MessageComponentContent content) {
		super.onMessageHighliteEvent(content);
		System.err.println(content.message());
	}

}
