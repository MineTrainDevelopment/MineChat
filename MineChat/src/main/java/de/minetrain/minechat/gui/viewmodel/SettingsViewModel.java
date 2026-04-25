package de.minetrain.minechat.gui.viewmodel;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Map;

import de.minetrain.minechat.config.enums.ReplyType;
import de.minetrain.minechat.features.messagehighlight.HighlightType;
import de.minetrain.minechat.gui.utils.ColorManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;

public class SettingsViewModel {

	private Map<HighlightType, HighlightViewModel> highlightColorMap;
	private BooleanProperty autoReplyOnlyInActiveTabProperty;
	private IntegerProperty undoStepsProperty;
	private BooleanProperty undoLetterModeProperty;
	private ObjectProperty<ReplyType> replyTypeProperty;
	private ObjectProperty<ReplyType> greetingTypeProperty;
	private BooleanProperty showEmoteOnlyMessagesProperty;
	private ObjectProperty<Duration> userAwayThresholdProperty;
	private StringProperty messageTimeProperty;
	private StringProperty timeFormatProperty;
	private StringProperty dateFormatProperty;
	private StringProperty dayFormatProperty;
	private ObservableValue<DateTimeFormatter> messageTimeFormatterProperty;
	private ObservableValue<DateTimeFormatter> timeFormatterProperty;
	private ObservableValue<DateTimeFormatter> dateFormatterProperty;
	private ObservableValue<DateTimeFormatter> dayFormatterProperty;
	private ObservableValue<DateTimeFormatter> dayTimeFormatterProperty;
	private ObservableValue<DateTimeFormatter> dateTimeFormatterProperty;
	private IntegerProperty baseColorProperty;
	private IntegerProperty accentColorProperty;
	private IntegerProperty borderColorProperty;
	private ObservableStringValue rootStyleProperty;

	public SettingsViewModel() {
		highlightColorMap = new EnumMap<>(HighlightType.class);
		for (HighlightType type : HighlightType.values()) {
			highlightColorMap.put(type, new HighlightViewModel(type));
		}
	}

	public HighlightViewModel getHighlightViewModel(HighlightType type) {
		return highlightColorMap.get(type);
	}

	public BooleanProperty autoReplyOnlyInActiveTabProperty() {
		if (autoReplyOnlyInActiveTabProperty == null) {
			autoReplyOnlyInActiveTabProperty = new SimpleBooleanProperty(this, "autoReplyOnlyInActiveTab");
		}
		return autoReplyOnlyInActiveTabProperty;
	}

	public void setAutoReplyOnlyInActiveTab(boolean value) {
		autoReplyOnlyInActiveTabProperty().set(value);
	}

	public boolean isAutoReplyOnlyInActiveTab() {
		return autoReplyOnlyInActiveTabProperty().get();
	}

	public IntegerProperty undoStepsProperty() {
		if (undoStepsProperty == null) {
			undoStepsProperty = new SimpleIntegerProperty(this, "undoSteps");
		}
		return undoStepsProperty;
	}

	public void setUndoSteps(int value) {
		undoStepsProperty().set(value);
	}

	public int getUndoSteps() {
		return undoStepsProperty().get();
	}

	public BooleanProperty undoLetterModeProperty() {
		if (undoLetterModeProperty == null) {
			undoLetterModeProperty = new SimpleBooleanProperty(this, "undoLetterMode");
		}
		return undoLetterModeProperty;
	}

	public void setUndoLetterMode(boolean value) {
		undoLetterModeProperty().set(value);
	}

	public boolean isUndoLetterMode() {
		return undoLetterModeProperty().get();
	}

	public ObjectProperty<ReplyType> replyTypeProperty() {
		if (replyTypeProperty == null) {
			replyTypeProperty = new SimpleObjectProperty<>(this, "replyType");
		}
		return replyTypeProperty;
	}

	public void setReplyType(ReplyType value) {
		replyTypeProperty().set(value);
	}

	public ReplyType getReplyType() {
		return replyTypeProperty().get();
	}

	public ObjectProperty<ReplyType> greetingTypeProperty() {
		if (greetingTypeProperty == null) {
			greetingTypeProperty = new SimpleObjectProperty<>(this, "greetingType");
		}
		return greetingTypeProperty;
	}

	public void setGreetingType(ReplyType value) {
		greetingTypeProperty().set(value);
	}

	public ReplyType getGreetingType() {
		return greetingTypeProperty().get();
	}

	public BooleanProperty showEmoteOnlyMessagesProperty() {
		if (showEmoteOnlyMessagesProperty == null) {
			showEmoteOnlyMessagesProperty = new SimpleBooleanProperty(this, "showEmoteOnlyMessages");
		}
		return showEmoteOnlyMessagesProperty;
	}

	public void setShowEmoteOnlyMessages(boolean value) {
		showEmoteOnlyMessagesProperty().set(value);
	}

	public boolean isShowEmoteOnlyMessages() {
		return showEmoteOnlyMessagesProperty().get();
	}

	public ObjectProperty<Duration> userAwayThresholdProperty() {
		if (userAwayThresholdProperty == null) {
			userAwayThresholdProperty = new SimpleObjectProperty<>(this, "userAwayThreshold");
		}
		return userAwayThresholdProperty;
	}

	public void setUserAwayThreshold(Duration value) {
		userAwayThresholdProperty().set(value);
	}

	public Duration getUserAwayThreshold() {
		return userAwayThresholdProperty().get();
	}

	public StringProperty messageTimeProperty() {
		if (messageTimeProperty == null) {
			messageTimeProperty = new SimpleStringProperty(this, "messageTimeFormat");
		}
		return messageTimeProperty;
	}

	public void setMessageTimeFormat(String value) {
		messageTimeProperty().set(value);
	}

	public String getMessageTimeFormat() {
		return messageTimeProperty().get();
	}

	public StringProperty timeFormatProperty() {
		if (timeFormatProperty == null) {
			timeFormatProperty = new SimpleStringProperty(this, "timeFormat");
		}
		return timeFormatProperty;
	}

	public void setTimeFormat(String value) {
		timeFormatProperty().set(value);
	}

	public String getTimeFormat() {
		return timeFormatProperty().get();
	}

	public StringProperty dateFormatProperty() {
		if (dateFormatProperty == null) {
			dateFormatProperty = new SimpleStringProperty(this, "dateFormat");
		}
		return dateFormatProperty;
	}

	public void setDateFormat(String value) {
		dateFormatProperty().set(value);
	}

	public String getDateFormat() {
		return dateFormatProperty().get();
	}

	public StringProperty dayFormatProperty() {
		if (dayFormatProperty == null) {
			dayFormatProperty = new SimpleStringProperty(this, "dayFormat");
		}
		return dayFormatProperty;
	}

	public void setDayFormat(String value) {
		dayFormatProperty().set(value);
	}

	public String getDayFormat() {
		return dayFormatProperty().get();
	}

	public ObservableValue<DateTimeFormatter> messageTimeFormatterProperty() {
		if (messageTimeFormatterProperty == null) {
			messageTimeFormatterProperty = messageTimeProperty().map(DateTimeFormatter::ofPattern);
		}
		return messageTimeFormatterProperty;
	}

	public DateTimeFormatter getMessageTimeFormatter() {
		return messageTimeFormatterProperty().getValue();
	}

	public ObservableValue<DateTimeFormatter> timeFormatterProperty() {
		if (timeFormatterProperty == null) {
			timeFormatterProperty = timeFormatProperty().map(DateTimeFormatter::ofPattern);
		}
		return timeFormatterProperty;
	}

	public DateTimeFormatter getTimeFormatter() {
		return timeFormatterProperty().getValue();
	}

	public ObservableValue<DateTimeFormatter> dateFormatterProperty() {
		if (dateFormatterProperty == null) {
			dateFormatterProperty = dateFormatProperty().map(DateTimeFormatter::ofPattern);
		}
		return dateFormatterProperty;
	}

	public DateTimeFormatter getDateFormatter() {
		return dateFormatterProperty().getValue();
	}

	public ObservableValue<DateTimeFormatter> dayFormatterProperty() {
		if (dayFormatterProperty == null) {
			dayFormatterProperty = dayFormatProperty().map(DateTimeFormatter::ofPattern);
		}
		return dayFormatterProperty;
	}

	public DateTimeFormatter getDayFormatter() {
		return dayFormatterProperty().getValue();
	}

	public ObservableValue<DateTimeFormatter> dayTimeFormatterProperty() {
		if (dayTimeFormatterProperty == null) {
			dayTimeFormatterProperty = Bindings.createObjectBinding(() -> DateTimeFormatter.ofPattern(getDayFormat() + " | " + getMessageTimeFormat()), dayFormatProperty(), messageTimeProperty());
		}
		return dayTimeFormatterProperty;
	}

	public DateTimeFormatter getDayTimeFormatter() {
		return dayTimeFormatterProperty().getValue();
	}

	public ObservableValue<DateTimeFormatter> dateTimeFormatterProperty() {
		if (dateTimeFormatterProperty == null) {
			dateTimeFormatterProperty = Bindings.createObjectBinding(() -> DateTimeFormatter.ofPattern(getDateFormat() + " | " + getMessageTimeFormat()), dateFormatProperty(), messageTimeProperty());
		}
		return dateTimeFormatterProperty;
	}

	public DateTimeFormatter getDateTimeFormatter() {
		return dateTimeFormatterProperty().getValue();
	}

	public IntegerProperty baseColorProperty() {
		if (baseColorProperty == null) {
			baseColorProperty = new SimpleIntegerProperty(this, "baseColor");
		}
		return baseColorProperty;
	}

	public void setBaseColor(int value) {
		baseColorProperty().set(value);
	}

	public int getBaseColor() {
		return baseColorProperty().get();
	}

	public IntegerProperty accentColorProperty() {
		if (accentColorProperty == null) {
			accentColorProperty = new SimpleIntegerProperty(this, "accentColor");
		}
		return accentColorProperty;
	}

	public void setAccentColor(int value) {
		accentColorProperty().set(value);
	}

	public int getAccentColor() {
		return accentColorProperty().get();
	}

	public IntegerProperty borderColorProperty() {
		if (borderColorProperty == null) {
			borderColorProperty = new SimpleIntegerProperty(this, "borderColor");
		}
		return borderColorProperty;
	}

	public void setBorderColor(int value) {
		borderColorProperty().set(value);
	}

	public int getBorderColor() {
		return borderColorProperty().get();
	}

	public ObservableStringValue rootStyleProperty() {
		if (rootStyleProperty == null) {
			rootStyleProperty = Bindings.createStringBinding(() -> {
				StringBuilder styleBuilder = new StringBuilder();
				styleBuilder.append("base-color: ");
				ColorManager.encode(getBaseColor(), styleBuilder);
				styleBuilder.append("; accent-color: ");
				ColorManager.encode(getAccentColor(), styleBuilder);
				styleBuilder.append("; border-color: ");
				ColorManager.encode(getBorderColor(), styleBuilder);
				styleBuilder.append(";");
				return styleBuilder.toString();
			}, baseColorProperty(), accentColorProperty(), borderColorProperty());
		}
		return rootStyleProperty;
	}

	public String getRootStyle() {
		return rootStyleProperty().get();
	}
}
