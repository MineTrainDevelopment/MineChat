package de.minetrain.minechat.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.utils.audio.AudioManager;
import de.minetrain.minechat.utils.events.EventManager;
import de.minetrain.minechat.utils.plugins.PluginManager;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	public static final String VERSION = "V0.9";
	public static AudioManager audioManager = new AudioManager();;
	public static EventManager eventManager = new EventManager();
	public static PluginManager pluginManager = new PluginManager(); //TODO: Load this in in main with GUI feedback.
	
	public static void main(String[] args) throws Exception {
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	DatabaseManager.getChannelStatistics().saveAllChannelStatistics();
		    	TwitchManager.leaveAllChannel();
		    }
		});
	}
		

	public static AudioManager getAudioManager(){
		return audioManager;
	}
	
	public static EventManager getEventManager(){
		return eventManager;
	}
	
	/**
	 * Extracts the domain from a given URL.
	 *
	 * @param input The input URL to extract the domain from.
	 * @return The extracted domain or the original input if the domain can´t be extracted.
	 */
	public static String extractDomain(String input) {
        try {
        	String url = input.replace("https://", "");
	        String host = url.substring(0, url.contains("/") ? url.indexOf("/") : url.length());
	        String[] split = host.split("\\.");
	        String domain = split[split.length - 2] + "." + split[split.length - 1];
	        return domain;
		} catch (Exception ex) {
			logger.warn("Can´t extract the domain from -> "+input);
			return input;
		}
	}
}
