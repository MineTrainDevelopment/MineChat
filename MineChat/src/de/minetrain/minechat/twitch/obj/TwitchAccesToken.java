package de.minetrain.minechat.twitch.obj;

import com.google.gson.JsonObject;

/**
 * This class represents a Twitch access token, which is used to authorize requests to the Twitch API.
 * @author MineTrain/Justin
 * @since 29.01.2023
 * @version 1.0
 */
public class TwitchAccesToken {
	private final int expiresIn; //The number of milliseconds until the access token expires.
	private final String accessToken; //The actual access token value.
	private final String tokenType; //The token type, which should always "bearer" for Twitch access tokens.

	/**
	* Constructs a new TwitchAccessToken object from a JSON object.
	* @param json The JSON object containing the access token data.
	*/
	public TwitchAccesToken(JsonObject json) {
		this.expiresIn = Integer.parseInt(String.valueOf(json.get("expires_in")).replace("\"", ""));
		this.accessToken = String.valueOf(json.get("access_token")).replace("\"", "");
		this.tokenType = String.valueOf(json.get("token_type")).replace("\"", "");
	}

	
	/**
	 * Check if the token is expired
	 * @return true if expiresIn < 5 else false
	 */
	public boolean isExpired(){
		return (expiresIn>5) ? false:true;
	}

	/**
	* @return The number of seconds until the access token expires.
	*/
	public int getExpiresIn() {
		return expiresIn;
	}

	/**
	* @return The actual access token value.
	*/
	public String getAccessToken() {
		return accessToken;
	}

	/**
	* @return The token type, which is always "bearer" for Twitch access tokens.
	*/
	public String getTokenType() {
		return tokenType;
	}
	
	@Override
	public String toString() {
		return accessToken;
	}
	
}
