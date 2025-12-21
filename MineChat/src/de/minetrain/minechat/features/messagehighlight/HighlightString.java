package de.minetrain.minechat.features.messagehighlight;

import java.util.UUID;
import java.util.regex.Pattern;

import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.utils.audio.AudioManager;
import de.minetrain.minechat.utils.audio.AudioVolume;
import javafx.scene.paint.Color;

public class HighlightString {

	private final UUID uuid;
	private final String pattern;
	private final String wordColorCode;
	private final String borderColorCode;
	private final String soundPath;
	private final AudioVolume soundVolume;
	private final boolean enabled;

	private transient Pattern compiledPattern;

	public HighlightString(String pattern, String wordColorCode, String borderColorCode) {
		this.uuid = UUID.randomUUID();
		this.pattern = pattern;
		this.wordColorCode = wordColorCode;
		this.borderColorCode = borderColorCode;
		this.soundPath = null;
		this.soundVolume = null;
		this.enabled = true;
	}

	public HighlightString(UUID id, String pattern, String wordColorCode, String borderColorCode, String soundPath, AudioVolume soundVolume, boolean enabled) {
		this.uuid = id;
		this.pattern = pattern;
		this.wordColorCode = wordColorCode;
		this.borderColorCode = borderColorCode;
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

	public String getWordColorCode() {
		return wordColorCode;
	}

	public String getBorderColorCode() {
		return borderColorCode;
	}

	public Color getWordColor() {
		return Color.web(wordColorCode);
	}

	public Color getBorder() {
		return Color.web(borderColorCode);
	}

	public boolean isPlaySound() {
		return soundPath != null;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Builder buildCopy() {
		return new Builder().withUuid(uuid).withPattern(pattern).withWordColorCode(wordColorCode)
				.withBorderColorCode(borderColorCode).withSoundPath(soundPath).withSoundVolume(soundVolume)
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
		private String wordColorCode;
		private String borderColorCode;
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

		public Builder withWordColorCode(String wordColorCode) {
			this.wordColorCode = wordColorCode;
			return this;
		}

		public Builder withBorderColorCode(String borderColorCode) {
			this.borderColorCode = borderColorCode;
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

		public HighlightString build() {
			return new HighlightString(uuid, pattern, wordColorCode, borderColorCode, soundPath, soundVolume, enabled);
		}
	}
}
