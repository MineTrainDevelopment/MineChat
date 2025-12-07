package de.minetrain.minechat.utils.message.tokens;

import de.minetrain.minechat.utils.MineTextFlow;
import de.minetrain.minechat.utils.message.MessageToken;

public class HyperLinkToken extends MessageToken {
	//NOTE: Du siehst dies hier zwar eh nicht, aber fals doch, am anfang des jahres 2024 hat der zocki gesagt, wir sollen überprüfen ob es nicht eine bessere praxis wäre, eine URI anstat einer URL zu speichern.
	//NOTE: Wir haben uns soeben auf einen String geeinigt.
	
	public HyperLinkToken(String url) {
		super(url);
	}
	
	@Override
	public void appendNode(MineTextFlow textFlow) {
		textFlow.appendHyperLink(getRawText());
	}

}
