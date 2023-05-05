package de.minetrain.minechat.Twitch;

/**
 * This class represents Twitch API credentials for a specific user.
 * the user's client ID and client secret, which are required to make API calls.
 * 
 * <p>Note: This class is meant to be used as an example and should not be used in
 * production code, as it stores the credentials in plain text.
 * 
 * @author MineTrain/Justin
 * @version 1.0
 * @since 05.05.2023
 */
public class TwitchCredentials {
	private final String ClientID = ">null<"; //The unique identifier assigned to the user by Twitch.
	private final String CLientSecret = ">null<"; //The secret token assigned to the user by Twitch, used to authenticate API calls.
	
	/**
	 * @return The user's Twitch API client ID.
	 */
	public String getClientID() {
		return ClientID;
	}
	
	/**
	 * @return The user's Twitch API client secret.
	 */
	public String getCLientSecret() {
		return CLientSecret;
	}
}
