package de.minetrain.minechat.utils.audio;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaException;

/// Manages audio clips for the application, providing caching and error handling. Audio clips can be played with specified volume levels and will be loaded from the resources or from custom URIs.
///
/// @author MineTrain
public class AudioManager {

	private static final Logger LOG = LoggerFactory.getLogger(AudioManager.class);

	private HashMap<String, AudioClip> audioCache = new HashMap<>();

	/// Plays the audio clip with the given volume. If the volume is null, it will
	/// be played with 100% volume.
	public void playAudioClip(AudioClip clip, AudioVolume volume) {
		clip.setVolume(volume != null ? volume.getValue() : AudioVolume.VOLUME_100.getValue());
		clip.play();
	}

	/// Plays the audio file with the given volume. If the volume is null, it will
	/// be played with 100% volume.
	public AudioClip playAudioClip(DefaultAudioFiles audioFile, AudioVolume volume) {
		AudioClip clip = getAudioClip(audioFile);
		playAudioClip(clip, volume);
		return clip;
	}

	/// Plays the audio file with the given volume. If the volume is null, it will
	/// be played with 100% volume.
	public AudioClip playAudioClip(String uri, AudioVolume volume) {
		AudioClip clip = getAudioClip(uri);
		playAudioClip(clip, volume);
		return clip;
	}

	/// Stops the audio clip if it is currently playing.
	public static void stopAudioClip(AudioClip clip) {
		if (clip.isPlaying()) {
			clip.stop();
		}
	}

	/// Gets the audio clip for the given Uri. The clip will be cached, so
	/// subsequent calls with the same Uri will return the same clip instance. If the
	/// audio file cannot be loaded, null will be returned.
	public AudioClip getAudioClip(String uri) {
		try {
			return audioCache.computeIfAbsent(uri, AudioClip::new);
		} catch (MediaException e) {
			LOG.error("Audio file could not be loaded from uri '{}':", uri, e);
			return null;
		}
	}

	/// Gets the audio clip for the given DefaultAudioFiles. The clip will be
	/// cached, so subsequent calls with the same DefaultAudioFiles will return the
	/// same clip instance. If the audio file cannot be loaded, null will
	/// be returned.
	public AudioClip getAudioClip(DefaultAudioFiles audioFile) {
		return getAudioClip(getResourceUri(audioFile.getResourceName()));
	}

	private static String getResourceUri(String resourceName) {
		try {
			return AudioManager.class.getResource("/sounds/" + resourceName).toString();
		} catch (NullPointerException _) {
			LOG.warn("Audio resource not found: {}", resourceName);
			return null;
		}
	}
}
