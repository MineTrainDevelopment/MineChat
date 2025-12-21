package de.minetrain.minechat.utils.events;

import de.minetrain.minechat.twitch.obj.TwitchMessage;

public class EventTest extends EventListener {

	@Override
	public void onIncomingMessageEvent(TwitchMessage message) {
		super.onIncomingMessageEvent(message);
		System.err.println("Messsage?");
	}

}
