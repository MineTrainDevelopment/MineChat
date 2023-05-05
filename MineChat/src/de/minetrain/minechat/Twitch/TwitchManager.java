package de.minetrain.minechat.Twitch;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import de.minetrain.minechat.main.Main;

/**
 * The TwitchManager class is responsible for creating and managing a Twitch client instance. 
 * It provides the functionality to join a Twitch chat and send messages to it.
 * 
 * @author MineTrain/Justin
 * @since 28.04.2023
 * @version 1.3
 */
public class TwitchManager {
	private static final Logger logger = LoggerFactory.getLogger(TwitchManager.class);
	public static TwitchClient twitch; //The static TwitchClient instance for managing Twitch interactions.
	
	/**
	 * Creates a new Twitch client instance using the provided TwitchCredentials.
	 * @param credentials The TwitchCredentials used to authenticate the Twitch client.
	 */
	public TwitchManager(TwitchCredentials credentials) {
		TwitchClientBuilder twitchBuilder = TwitchClientBuilder.builder();
		Main.LOADINGBAR.setProgress("Conect to Twitch Helix", 20);	
		
		//Configure the TwitchClientBuilder with the provided credentials.
		twitch=twitchBuilder
			.withClientId(credentials.getClientID())
			.withClientSecret(credentials.getClientSecret())
			.withEnableHelix(true)
			.withChatAccount(new OAuth2Credential("twitch", credentials.getTesting_oauth2()))
	        .withEnableChat(true)
			.build();
		
		Main.LOADINGBAR.setProgress("Join Twitch channels Helix", 60);
		twitch.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new TwitchListner()); //Register a listener for Twitch events.
		logger.info("Connecting to channels: "+twitch.getChat().getChannels().toString()); //Print all the connected channels
		Main.openMainFrame();
	}
	
	
	public void joinChannel(String... names){
		for(String name : names) {
			twitch.getChat().joinChannel(name);
		}
	}
	
	
	/**
	 * Sends a message to the specified Twitch chat.
	 * 
	 * @param channel The name of the Twitch channel to send the message to.
	 * @param user The name of the user sending the message.
	 * @param message The message to be sent to the Twitch chat channel.
	 */
	public static void sendMessage(String channel, String user, String message) {
		logger.debug("Sending message -> message"); //Log the sent message.
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin")); //Set the default time zone.
		
		//Send the message to the specified Twitch chat.
		twitch.getChat().sendMessage(channel, message 
			.replace("{USER}", (user == null) ? "" : "@"+user)
			.replace("{STREAMER}", (channel == null) ? "" : "@"+channel)
			.replace("{TIME}", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm", Locale.GERMAN))));
	}
	
	
	/**
	 * Sends a message to the specified Twitch chat channel using the information from the provided {@link ChannelMessageEvent}.
	 *
	 * @param event The {@link ChannelMessageEvent} containing information about the chat channel and user.
	 * @param message The message to be sent to the Twitch chat channel.
	 */
	public static void sendMessage(ChannelMessageEvent event, String message) {
		TwitchManager.sendMessage(event.getChannel().getName(), event.getUser().getName(), message);
	}

}
