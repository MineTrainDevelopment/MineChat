package de.minetrain.minechat.utils.message.tokens;

import de.minetrain.minechat.utils.MineTextFlow;
import de.minetrain.minechat.utils.message.MessageToken;

public class WordToken extends MessageToken{

	public WordToken(String word) {
		super(word);
	}

	@Override
	public void appendNode(MineTextFlow textFlow) {
		textFlow.appendString(getRawText());
	}

	

}
