package de.minetrain.minechat.gui.viewmodel;

import java.util.regex.Pattern;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AutoReplyViewModel {

	private ObjectProperty<ChannelViewModel> channelProperty;
	private IntegerProperty messagesPerMinuteProperty;
	/// Delay in seconds before the auto-reply is sent
	private IntegerProperty triggerDelayProperty;
	private ObjectProperty<Pattern> triggerRegexProperty;
	private BooleanProperty replyToTriggerProperty;
	private ObjectProperty<String[]> outputProperty;


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

	public IntegerProperty triggerDelayProperty() {
		if (triggerDelayProperty == null) {
			triggerDelayProperty = new SimpleIntegerProperty(this, "triggerDelay", 1);
		}
		return triggerDelayProperty;
	}

	public Integer getTriggerDelay() {
		return triggerDelayProperty().get();
	}

	public void setTriggerDelay(Integer triggerDelay) {
		triggerDelayProperty().set(triggerDelay);
	}

	public ObjectProperty<Pattern> triggerRegexProperty() {
		if (triggerRegexProperty == null) {
			triggerRegexProperty = new SimpleObjectProperty<>(this, "triggerRegex");
		}
		return triggerRegexProperty;
	}

	public Pattern getTriggerRegex() {
		return triggerRegexProperty().get();
	}

	public void setTriggerRegex(Pattern triggerRegex) {
		triggerRegexProperty().set(triggerRegex);
	}

	public void setTriggerRegex(String regex) {
		triggerRegexProperty().set(Pattern.compile(regex));
	}

	public BooleanProperty replyToTriggerProperty() {
		if (replyToTriggerProperty == null) {
			replyToTriggerProperty = new SimpleBooleanProperty(this, "replyToTrigger", false);
		}
		return replyToTriggerProperty;
	}

	public Boolean getReplyToTrigger() {
		return replyToTriggerProperty().get();
	}

	public void setReplyToTrigger(Boolean replyToTrigger) {
		replyToTriggerProperty().set(replyToTrigger);
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
}
