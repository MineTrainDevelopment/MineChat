package de.minetrain.minechat.twitch;

import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;

public interface IVariable {

	String getId();

	String[] getNames();

	String retrieveValue(ChannelViewModel channelViewModel);
}
