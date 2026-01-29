package de.minetrain.minechat.data.objectdata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.serializer.concurrency.LockScope;
import org.eclipse.serializer.persistence.types.PersistenceStoring;

import de.minetrain.minechat.config.enums.ReplyType;
import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.features.messagehighlight.Highlight;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.features.messagehighlight.HighlightType;
import de.minetrain.minechat.gui.utils.FontStyle;

public class UserSettings extends LockScope {

	private final Map<UUID, HighlightString> highlightStrings = new HashMap<>();
	private final Map<HighlightType, Highlight> highlights = new HashMap<>(); // NOSONAR: EnumMap is currently not supported by Eclipse Serializer
	private boolean isInitialized = false;

	private boolean autoReplyOnlyInActiveTab = false;
	private int undoSteps = 100;
	private boolean undoLetterMode = false;
	private ReplyType replyType = ReplyType.THREAD;
	private ReplyType greetingType = ReplyType.MESSAGE;
	private boolean showEmoteOnlyMessages = true;
	private long userAwayThreshold = 3600000L; // 1 hour
	private String messageTimeFormat = "HH:mm";
	private String timeFormat = "HH:mm";
	private String dateFormat = "dd:MM:yyyy";
	private String dayFormat = "eeee";

	private String fontFamily = "Arial";
	private int fontSize = 17;
	private FontStyle fontStyle = FontStyle.BOLD;

	private int baseColor = 0x191919ff;
	private int accentColor = 0x9146ffff;
	private int borderColor = 0x000000ff;

	public void addHighlightString(HighlightString highlightString) {
		addHighlightString(highlightString, EclipseStoreKeeper.storeManager());
	}

	public void addHighlightString(HighlightString highlightString, PersistenceStoring persister) {
		write(() -> {
			highlightStrings.put(highlightString.getUuid(), highlightString);
			persister.store(highlightStrings);
		});
	}

	public HighlightString removeHighlightString(UUID uuid) {
		return removeHighlightString(uuid, EclipseStoreKeeper.storeManager());
	}

	public HighlightString removeHighlightString(UUID uuid, PersistenceStoring persister) {
		return write(() -> {
			HighlightString removedHighlightString = highlightStrings.remove(uuid);
			persister.store(highlightStrings);
			return removedHighlightString;
		});
	}

	public void setHighlight(Highlight highlight) {
		setHighlight(highlight, EclipseStoreKeeper.storeManager());
	}

	public void setHighlight(Highlight highlight, PersistenceStoring persister) {
		write(() -> {
			highlights.put(highlight.getType(), highlight);
			persister.store(highlights);
		});
	}

	public boolean isInitialized() {
		return read(() -> !isInitialized);
	}

	public void setInitialized() {
		write(() -> {
			isInitialized = true;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public <T> T computeHighlightStrings(Function<Stream<HighlightString>, T> function) {
		return read(() -> function.apply(highlightStrings.values().stream()));
	}

	public Collection<HighlightString> getAllHighlightStrings() {
		return read(() -> Collections.unmodifiableCollection(highlightStrings.values()));
	}

	public Highlight getHighlight(HighlightType type) {
		return read(() -> highlights.get(type));
	}

	public Map<HighlightType, Highlight> getAllHighlights() {
		return read(() -> Collections.unmodifiableMap(highlights));
	}

	public boolean isAutoReplyOnlyInActiveTab() {
		return read(() -> autoReplyOnlyInActiveTab);
	}

	public void setAutoReplyOnlyInActiveTab(boolean autoReplyOnlyInActiveTab) {
		write(() -> {
			this.autoReplyOnlyInActiveTab = autoReplyOnlyInActiveTab;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public int getUndoSteps() {
		return read(() -> undoSteps);
	}

	public void setUndoSteps(int undoSteps) {
		write(() -> {
			this.undoSteps = undoSteps;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public boolean isUndoLetterMode() {
		return read(() -> undoLetterMode);
	}

	public void setUndoLetterMode(boolean undoLetterMode) {
		write(() -> {
			this.undoLetterMode = undoLetterMode;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public ReplyType getReplyType() {
		return read(() -> replyType);
	}

	public void setReplyType(ReplyType replyType) {
		write(() -> {
			this.replyType = replyType;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public ReplyType getGreetingType() {
		return read(() -> greetingType);
	}

	public void setGreetingType(ReplyType greetingType) {
		write(() -> {
			this.greetingType = greetingType;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public boolean isShowEmoteOnlyMessages() {
		return read(() -> showEmoteOnlyMessages);
	}

	public void setShowEmoteOnlyMessages(boolean showEmoteOnlyMessages) {
		write(() -> {
			this.showEmoteOnlyMessages = showEmoteOnlyMessages;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public long getUserAwayThreshold() {
		return read(() -> userAwayThreshold);
	}

	public void setUserAwayThreshold(long userAwayThreshold) {
		write(() -> {
			this.userAwayThreshold = userAwayThreshold;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public String getMessageTimeFormat() {
		return read(() -> messageTimeFormat);
	}

	public void setMessageTimeFormat(String messageTimeFormat) {
		write(() -> {
			this.messageTimeFormat = messageTimeFormat;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public String getTimeFormat() {
		return read(() -> timeFormat);
	}

	public void setTimeFormat(String timeFormat) {
		write(() -> {
			this.timeFormat = timeFormat;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public String getDateFormat() {
		return read(() -> dateFormat);
	}

	public void setDateFormat(String dateFormat) {
		write(() -> {
			this.dateFormat = dateFormat;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public String getDayFormat() {
		return read(() -> dayFormat);
	}

	public void setDayFormat(String dayFormat) {
		write(() -> {
			this.dayFormat = dayFormat;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public String getFontFamily() {
		return read(() -> fontFamily);
	}

	public void setFontFamily(String fontFamily) {
		write(() -> {
			this.fontFamily = fontFamily;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public int getFontSize() {
		return read(() -> fontSize);
	}

	public void setFontSize(int fontSize) {
		write(() -> {
			this.fontSize = fontSize;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public FontStyle getFontStyle() {
		return read(() -> fontStyle);
	}

	public void setFontStyle(FontStyle fontStyle) {
		write(() -> {
			this.fontStyle = fontStyle;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public int getBaseColor() {
		return read(() -> baseColor);
	}

	public void setBaseColor(int baseColor) {
		write(() -> {
			this.baseColor = baseColor;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public int getAccentColor() {
		return read(() -> accentColor);
	}

	public void setAccentColor(int accentColor) {
		write(() -> {
			this.accentColor = accentColor;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}

	public int getBorderColor() {
		return read(() -> borderColor);
	}

	public void setBorderColor(int borderColor) {
		write(() -> {
			this.borderColor = borderColor;
			EclipseStoreKeeper.storeManager().store(this);
		});
	}
}
