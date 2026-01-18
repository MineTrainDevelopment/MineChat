package de.minetrain.minechat.features.messagehighlight;

import java.util.UUID;
import java.util.regex.Pattern;

import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.utils.audio.AudioManager;
import de.minetrain.minechat.utils.audio.AudioVolume;
import javafx.scene.paint.Color;

public class HighlightString {

	private final UUID uuid;
	private final String pattern;
	private final int wordColor;
	private final int borderColor;
	private final String soundPath;
	private final AudioVolume soundVolume;
	private final boolean enabled;

	private transient Pattern compiledPattern;
	private transient Color cachedWordColor;
	private transient Color cachedBorderColor;
	private transient String cachedBorderStyle;

	public static Builder builder() {
		return new Builder();
	}

	public HighlightString(String pattern, int wordColor, int borderColor) {
		this.uuid = UUID.randomUUID();
		this.pattern = pattern;
		this.wordColor = wordColor;
		this.borderColor = borderColor;
		this.soundPath = null;
		this.soundVolume = null;
		this.enabled = true;
	}

	public HighlightString(UUID id, String pattern, int wordColor, int borderColor, String soundPath, AudioVolume soundVolume, boolean enabled) {
		this.uuid = id;
		this.pattern = pattern;
		this.wordColor = wordColor;
		this.borderColor = borderColor;
		this.soundPath = soundPath;
		this.soundVolume = soundVolume;
		this.enabled = enabled;
	}

	public void playSound() {
		if (isPlaySound()) {
			Main.getAudioManager().playAudioClip(getSoundUri(), soundVolume);
		}
	}

	public AudioVolume getSoundVolume() {
		return soundVolume;
	}

	public String getSoundPath() {
		return AudioManager.RAW_AUDIO_PATH.replace("/", "\\") + soundPath;
	}

	public String getSoundUri() {
		return AudioManager.createUri(soundPath);
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getPattern() {
		return pattern;
	}

	public int getWordColor() {
		return wordColor;
	}

	public Color getWordColorAsColor() {
		if (cachedWordColor == null) {
			cachedWordColor = ColorManager.decodeFromInt(getWordColor());
		}
		return cachedWordColor;
	}

	public int getBorderColor() {
		return borderColor;
	}

	public Color getBorderColorAsColor() {
		if (cachedBorderColor == null) {
			cachedBorderColor = ColorManager.decodeFromInt(getBorderColor());
		}
		return cachedBorderColor;
	}

	public String getBorderStyle() {
		if (cachedBorderStyle == null) {
			cachedBorderStyle = ColorManager.encode(getBorderColor(), "-fx-border-color: ", ";");
		}
		return cachedBorderStyle;
	}

	public boolean isPlaySound() {
		return soundPath != null;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Builder buildCopy() {
		return new Builder().withUuid(uuid).withPattern(pattern).withWordColor(wordColor)
				.withBorderColor(borderColor).withSoundPath(soundPath).withSoundVolume(soundVolume)
				.withEnabled(enabled);
	}

	public Pattern getCompiledPattern() {
		if (compiledPattern == null) {
			compiledPattern = Pattern.compile("^" + pattern + "$", Pattern.CASE_INSENSITIVE);
		}
		return compiledPattern;
	}

	public static class Builder {

		private UUID uuid;
		private String pattern;
		private int wordColor;
		private int borderColor;
		private String soundPath;
		private AudioVolume soundVolume;
		private boolean enabled;

		public Builder withUuid(UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder withPattern(String pattern) {
			this.pattern = pattern;
			return this;
		}

		public Builder withWordColor(int wordColor) {
			this.wordColor = wordColor;
			return this;
		}

		public Builder withBorderColor(int borderColor) {
			this.borderColor = borderColor;
			return this;
		}

		public Builder withSoundPath(String soundPath) {
			this.soundPath = soundPath;
			return this;
		}

		public Builder withSoundVolume(AudioVolume soundVolume) {
			this.soundVolume = soundVolume;
			return this;
		}

		public Builder withEnabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		public UUID getUuid() {
			return uuid;
		}

		public String getPattern() {
			return pattern;
		}

		public int getWordColor() {
			return wordColor;
		}

		public int getBorderColor() {
			return borderColor;
		}

		public String getSoundPath() {
			return soundPath;
		}

		public AudioVolume getSoundVolume() {
			return soundVolume;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public HighlightString build() {
			return new HighlightString(uuid, pattern, wordColor, borderColor, soundPath, soundVolume, enabled);
		}
	}
}
