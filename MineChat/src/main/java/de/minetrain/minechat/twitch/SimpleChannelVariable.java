package de.minetrain.minechat.twitch;

import java.time.LocalDateTime;
import java.util.function.Function;

import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;

public class SimpleChannelVariable implements IVariable {

	private final String[] names;
	private final Function<ChannelViewModel, String> valueFunction;

	public SimpleChannelVariable(Function<ChannelViewModel, String> valueFunction, String... names) {
		this.names = names;
		this.valueFunction = valueFunction;
	}

	@Override
	public String[] getNames() {
		return names;
	}

	@Override
	public String retrieveValue(ChannelViewModel channelViewModel, LocalDateTime localDateTime) {
		return valueFunction.apply(channelViewModel);
	}
}
