package de.minetrain.minechat.utils.events;

import de.minetrain.minechat.utils.message.Message;

public class EventTest extends EventListener {
	
	@Override
	public void onIncomingMessageEvent(Message message) {
		super.onIncomingMessageEvent(message);
		System.err.println("Messsage?");
	}
	
	@Override
	public void onMessageHighliteEvent(Message message) {
		super.onMessageHighliteEvent(message);
		System.err.println(message.getRawMessage());
	}

}
