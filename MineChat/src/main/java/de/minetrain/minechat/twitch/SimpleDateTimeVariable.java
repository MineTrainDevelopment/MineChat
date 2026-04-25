package de.minetrain.minechat.twitch;

import java.time.LocalDateTime;
import java.util.function.Function;

import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;

public class SimpleDateTimeVariable implements IVariable {

	private final String[] names;
	private final Function<LocalDateTime, String> valueFunction;

	public SimpleDateTimeVariable(Function<LocalDateTime, String> valueFunction, String... names) {
		this.names = names;
		this.valueFunction = valueFunction;
	}

	@Override
	public String[] getNames() {
		return names;
	}

	@Override
	public String retrieveValue(ChannelViewModel channelViewModel, LocalDateTime localDateTime) {
		return valueFunction.apply(localDateTime);
	}
}
