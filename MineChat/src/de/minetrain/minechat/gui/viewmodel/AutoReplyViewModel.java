package de.minetrain.minechat.gui.viewmodel;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import de.minetrain.minechat.data.objectdata.AutoReply;
import de.minetrain.minechat.main.Main;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AutoReplyViewModel {

	private ObjectProperty<UUID> uuidProperty;
	private ObjectProperty<ChannelViewModel> channelProperty;
	private BooleanProperty enabledProperty;
	private IntegerProperty messagesPerMinuteProperty;
	/// Delay in seconds before the auto-reply is sent
	private IntegerProperty delayProperty;
	private ObjectProperty<Pattern> patternProperty;
	private BooleanProperty replyProperty;
	private ObjectProperty<String[]> outputProperty;

	public static AutoReplyViewModel of(AutoReply autoReply, ChannelViewModel channel) {
		AutoReplyViewModel autoReplyViewModel = new AutoReplyViewModel();
		autoReplyViewModel.setUuid(autoReply.getUuid());
		autoReplyViewModel.setChannel(channel);
		autoReplyViewModel.setEnabled(autoReply.isEnabled());
		autoReplyViewModel.setMessagesPerMinute(autoReply.getMessagesPerMinute());
		autoReplyViewModel.setDelay(autoReply.getDelay());
		autoReplyViewModel.setPattern(autoReply.getPattern());
		autoReplyViewModel.setReply(autoReply.isReply());
		autoReplyViewModel.setOutput(autoReply.getOutput());
		return autoReplyViewModel;
	}

	public ObjectProperty<UUID> uuidProperty() {
		if (uuidProperty == null) {
			uuidProperty = new SimpleObjectProperty<>(this, "uuid");
		}
		return uuidProperty;
	}

	public UUID getUuid() {
		return uuidProperty().get();
	}

	public void setUuid(UUID uuid) {
		uuidProperty().set(uuid);
	}

	public ObjectProperty<ChannelViewModel> channelProperty() {
		if (channelProperty == null) {
			channelProperty = new SimpleObjectProperty<>(this, "channel");
		}
		return channelProperty;
	}

	public ChannelViewModel getChannel() {
		return channelProperty().get();
	}

	public void setChannel(ChannelViewModel channel) {
		channelProperty().set(channel);
	}

	public BooleanProperty enabledProperty() {
		if (enabledProperty == null) {
			enabledProperty = new SimpleBooleanProperty(this, "enabled", true);
		}
		return enabledProperty;
	}

	public boolean isEnabled() {
		return enabledProperty().get();
	}

	public void setEnabled(boolean enabled) {
		enabledProperty().set(enabled);
	}

	public IntegerProperty messagesPerMinuteProperty() {
		if (messagesPerMinuteProperty == null) {
			messagesPerMinuteProperty = new SimpleIntegerProperty(this, "messagesPerMinute", 1);
		}
		return messagesPerMinuteProperty;
	}

	public Integer getMessagesPerMinute() {
		return messagesPerMinuteProperty().get();
	}

	public void setMessagesPerMinute(Integer messagesPerMinute) {
		messagesPerMinuteProperty().set(messagesPerMinute);
	}

	public IntegerProperty delayProperty() {
		if (delayProperty == null) {
			delayProperty = new SimpleIntegerProperty(this, "delay", 1);
		}
		return delayProperty;
	}

	public Integer getDelay() {
		return delayProperty().get();
	}

	public void setDelay(Integer delay) {
		delayProperty().set(delay);
	}

	public ObjectProperty<Pattern> patternProperty() {
		if (patternProperty == null) {
			patternProperty = new SimpleObjectProperty<>(this, "pattern");
		}
		return patternProperty;
	}

	public Pattern getPattern() {
		return patternProperty().get();
	}

	public void setPattern(Pattern pattern) {
		patternProperty().set(pattern);
	}

	public void setPattern(String regex) {
		patternProperty().set(Pattern.compile(regex));
	}

	public BooleanProperty replyProperty() {
		if (replyProperty == null) {
			replyProperty = new SimpleBooleanProperty(this, "reply", false);
		}
		return replyProperty;
	}

	public boolean isReply() {
		return replyProperty().get();
	}

	public void setReply(boolean replyToTrigger) {
		replyProperty().set(replyToTrigger);
	}

	public ObjectProperty<String[]> outputProperty() {
		if (outputProperty == null) {
			outputProperty = new SimpleObjectProperty<>(this, "output");
		}
		return outputProperty;
	}

	public String[] getOutput() {
		return outputProperty().get();
	}

	public void setOutput(String... output) {
		outputProperty().set(output);
	}

	public void apply(AutoReply autoReply) {
		setChannel(Main.getChannelManager().channelsProperty().get().stream().filter(c -> c.getChannelId().equals(autoReply.getChannelId())).findFirst().orElse(null));
		setEnabled(autoReply.isEnabled());
		setMessagesPerMinute(autoReply.getMessagesPerMinute());
		setDelay(autoReply.getDelay());
		setPattern(autoReply.getPattern());
		setReply(autoReply.isReply());
		setOutput(autoReply.getOutput());
	}

	public AutoReply toAutoReply() {
		return new AutoReply(getUuid(), getChannel() != null ? getChannel().getChannelId() : null, isEnabled(), getPattern().pattern(), getOutput(), getMessagesPerMinute(), getDelay(), isReply());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getUuid());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AutoReplyViewModel other = (AutoReplyViewModel) obj;
		return Objects.equals(getUuid(), other.getUuid());
	}
}
