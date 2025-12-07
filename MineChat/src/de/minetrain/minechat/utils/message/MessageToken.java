package de.minetrain.minechat.utils.message;

import de.minetrain.minechat.utils.MineTextFlow;
import de.minetrain.minechat.utils.message.tokens.EmoteToken;
import de.minetrain.minechat.utils.message.tokens.HyperLinkToken;
import de.minetrain.minechat.utils.message.tokens.WordToken;

public abstract class MessageToken {
	private final String text;
	
	public abstract void appendNode(MineTextFlow textFlow);
	
	public MessageToken(String text) {
		this.text = text;
	}
	
	public String getRawText(){
		return text;
	}
	
	@Override
	public String toString() {
		return getRawText();
	}

	public boolean isEmote() {
		return this instanceof EmoteToken;
	}
	
	public boolean isHyperLink() {
		return this instanceof HyperLinkToken;
	}
	
	public boolean isWord() {
		return this instanceof WordToken;
	}
	
	public boolean isUserName() {
		return false;
	}
	

}
