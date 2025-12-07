package de.minetrain.minechat.utils.message.tokens;

import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.utils.MineTextFlow;
import de.minetrain.minechat.utils.message.Message;
import de.minetrain.minechat.utils.message.MessageToken;

public class EmoteToken extends MessageToken {
	private final String emoteId;
	
	public EmoteToken(String emoteId, String currentEmoteName) {
		super(currentEmoteName);
		this.emoteId = emoteId;
	}
	
	public EmoteToken(Emote emote) {
		this(emote.getEmoteId(), emote.getName());
	}

	public String getEmoteId() {
		return emoteId;
	}
	
	/**
	 * NOTE: This name should only be used, incase on emote can be found for the specified emoteId.
	 */
	public String getEmoteName() {
		return getRawText();
	}

	@Override
	public void appendNode(MineTextFlow textFlow) {
		Emote emote = Message.webEmoteCache.get(getEmoteId());
		
		if(emote == null){
			emote = EmoteManager.getEmoteById(getEmoteId());
		}
		
		if(emote != null){
			textFlow.appendEmote(emote);
			return;
		}
		
		textFlow.appendString(getEmoteName());
	}

}
