package de.minetrain.minechat.twitch;

import java.util.function.Function;

import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.gui.viewmodel.StreamInfoViewModel;

public class StreamInfoVariable extends SimpleChannelVariable {

	public StreamInfoVariable(Function<StreamInfoViewModel, String> valueFunction, String... names) {
		super(valueFunction.compose(ChannelViewModel::getStreamInfo), names);
	}
}
