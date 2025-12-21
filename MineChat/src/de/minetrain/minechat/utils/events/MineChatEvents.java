package de.minetrain.minechat.utils.events;

import de.minetrain.minechat.features.macros.MacroObject;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.OutboundChatMessage;


public interface MineChatEvents {
	public void onIncomingMessageEvent(TwitchMessage message);
	public void onSentMessageEvent(OutboundChatMessage message);
	public void onExecuteMacroEvent(MacroObject macro);


}
