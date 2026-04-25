package de.minetrain.minechat.twitch;

import java.time.LocalDateTime;

import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;

public interface IVariable {

	String[] getNames();

	String retrieveValue(ChannelViewModel channelViewModel, LocalDateTime localDateTime);
}
