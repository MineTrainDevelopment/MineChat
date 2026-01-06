package de.minetrain.minechat.twitch;

import java.util.function.Function;

import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.gui.viewmodel.StreamInfoViewModel;

public class StreamInfoVariable extends SimpleVariable {

	public StreamInfoVariable(String id, Function<StreamInfoViewModel, String> valueFunction, String... names) {
		super(id, valueFunction.compose(ChannelViewModel::getStreamInfo), names);
	}
}
