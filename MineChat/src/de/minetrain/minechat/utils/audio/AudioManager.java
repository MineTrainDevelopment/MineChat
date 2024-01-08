package de.minetrain.minechat.utils.audio;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaException;

/**
 * @author MineTrain/Justin, ZockiRR
 */
public class AudioManager {
	private static final Logger logger = LoggerFactory.getLogger(AudioManager.class);
	public static final String RAW_AUDIO_PATH = "data/sounds/";
	public static final Path AUDIO_PATH = Path.of("data", "sounds");
	
	/**Path {@link URI#toString()} --> {@link AudioClip}*/
	private HashMap<String, AudioClip> audioCach = new HashMap<String, AudioClip>();
	
	public AudioManager() {
		Collection<Path> audioFiles = scrapeAudioFiles();
		logger.info("Scraped \""+audioFiles.size()+ "\" custom audio files.");
	}

	public static Collection<Path> scrapeAudioFiles() {
		try{
			return Files.walk(AUDIO_PATH)
				.filter(Files::isRegularFile)
				.collect(Collectors.toList());
		} catch (IOException ex) {
			logger.error("Can´t read all files from sound folder."+ex);
		}
		
		return new ArrayList<Path>();
	}
	
	
	/**
	 * 
	 * @param filePath path, to, your, file.mp3
	 * @return
	 */
	public static String createUri(String... filePath){
		Path audioPath = Path.of(AUDIO_PATH.toUri());
		for(String path : filePath){
			audioPath = audioPath.resolve(path);
		}
		return audioPath.toUri().toString();
	}
	
	/**
	 * Play a audio clip for a given path.
	 * @param AudioClip
	 */
	public AudioClip playAudioClip(AudioClip clip, AudioVolume volume){
		try {
			clip.setVolume(volume != null ? volume.getValue() : AudioVolume.VOLUME_100.getValue());
			clip.play();
			return clip;
		} catch (MediaException | NullPointerException ex) {
			logger.warn(ex.getMessage());
			return null;
		}
	}
	
	/**
	 * Play a audio clip for a given path.
	 * @param audioFile {@link DefaultAudioFiles}
	 */
	public AudioClip playAudioClip(DefaultAudioFiles audioFile, AudioVolume volume){
		return playAudioClip(getAudioClip(audioFile.getUri()), volume);
	}
	
	/**
	 * Play a audio clip for a given path.
	 * @param uri NOTE: This has to be a {@link URI#toString()}
	 */
	public AudioClip playAudioClip(String uri, AudioVolume volume){
		return playAudioClip(getAudioClip(uri), volume);
	}
	
	public static void stopAudioClip(AudioClip clip){
		if(clip != null && clip.isPlaying()){
			clip.stop();
		}
	}
	
	/**
	 * Get a audio clip for a given path.
	 * @param uri NOTE: This has to be a {@link URI#toString()}
	 * @return a cached or new {@link AudioClip}
	 */
	public AudioClip getAudioClip(String uri){
		try {
			return audioCach.computeIfAbsent(uri, AudioClip::new);
		} catch (MediaException ex) {
			logger.warn("Can´t find audio file -> "+uri+"\n"+ex.getMessage());
			return null;
		}
	}

	/**
	 * Get a audio clip for a file given path.
	 * @param audioFile {@link DefaultAudioFiles}
	 * @return a cached or new {@link AudioClip}
	 */
	public AudioClip getAudioClip(DefaultAudioFiles audioFile){
		return getAudioClip(audioFile.getUri());
	}
}
