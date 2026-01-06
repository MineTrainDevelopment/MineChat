package de.minetrain.minechat.twitch;

import java.util.function.Function;

import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;

public class SimpleVariable implements IVariable {

	private final String id;
	private final String[] names;
	private final Function<ChannelViewModel, String> valueFunction;

	public SimpleVariable(String id, Function<ChannelViewModel, String> valueFunction, String... names) {
		this.id = id;
		this.names = names;
		this.valueFunction = valueFunction;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String[] getNames() {
		return names;
	}

	@Override
	public String retrieveValue(ChannelViewModel channelViewModel) {
		return valueFunction.apply(channelViewModel);
	}
}
