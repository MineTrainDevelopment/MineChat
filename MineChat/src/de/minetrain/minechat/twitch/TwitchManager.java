package de.minetrain.minechat.twitch;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.JFrame;

import org.apache.commons.lang.IncompleteArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import de.minetrain.minechat.gui.frames.GetCredentialsFrame;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.CredentialsManager;
import de.minetrain.minechat.twitch.obj.TwitchAccesToken;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import de.minetrain.minechat.utils.ChatMessage;
import de.minetrain.minechat.utils.events.MineChatEventType;
import io.github.bucket4j.Bandwidth;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

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
	public record LiveMetaData(String title, String game, Instant startTime, int viewer, String[] tags){};
	public static final List<TwitchUserObj> twitchUsers = new ArrayList<>();
	public static String ownerChannelName = ">null<";
	public static TwitchUserObj ownerTwitchUser;
	public static CredentialsManager credentials;
	protected static TwitchAccesToken accesToken;
	private static int reconnectCount = 0;
	
	/**
	 * Creates a new Twitch client instance using the provided TwitchCredentials.
	 * @param credentials The TwitchCredentials used to authenticate the Twitch client.
	 */
	public TwitchManager(CredentialsManager credentials) {
		TwitchManager.credentials = credentials;
		TwitchClientBuilder twitchBuilder = TwitchClientBuilder.builder();

//		Main.LOADINGBAR.setProgress("Requesting new API AccesToken", 20);
		accesToken = getAccesToken();
//		Main.LOADINGBAR.setProgress("Conect to Twitch Helix", 25);
		
		//Configure the TwitchClientBuilder with the provided credentials.
		twitch=twitchBuilder
			.withClientId(credentials.getClientID())
			.withClientSecret(credentials.getClientSecret())
			.withEnableHelix(true)
			.withChatAccount(new OAuth2Credential("twitch", credentials.getOAuth2Token()))
			.withChatChannelMessageLimit(Bandwidth.simple(1, Duration.ofMillis(300)).withId("per-channel-limit"))
	        .withEnableChat(true)
			.build();
		
		//TODO Try to reconnect befor promtint to re enter credenials
		
		if(twitch.getChat().getChannels().isEmpty()){
			
			//Try to reconnect 3 times, befor asking for new credentials.
			if(reconnectCount<3){
				reconnectCount++;
				new TwitchManager(credentials);
				return;
			}
			
//			Asking for a new OAuth2 token.
//			Main.LOADINGBAR.setError("Requesting new OAuth2 Token.");
			logger.error("Requesting new OAuth2 Token.");
			
			JFrame tempFrame = new JFrame();
			tempFrame.setVisible(false);
			GetCredentialsFrame newCredentialsFrame = new GetCredentialsFrame(tempFrame);
			newCredentialsFrame.injectData(credentials.getClientID(), credentials.getClientSecret());
			newCredentialsFrame.startServer();
			logger.warn("Start new HTTP server to get new OAuth2 key.");
			
			
			//If the new OAuth2 token also don�t work, let the user Reenter there API credentials
			try {
				new TwitchManager(new CredentialsManager());
			} catch (Exception ex) {
				CredentialsManager.deleteCredentialsFile();
//				Main.LOADINGBAR.setError("Invalid Twitch Credentials!");
				logger.error("Invalid twitch credentials!", ex);
			}
			
			ownerTwitchUser = new TwitchUserObj(TwitchApiCallType.LOGIN, ownerChannelName, true);
			return;
		}
		
		
//		Main.LOADINGBAR.setProgress("Join Twitch channels Helix", 60);
		twitch.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new TwitchListner()); //Register a listener for Twitch events.
		logger.info("Connecting to channels: "+twitch.getChat().getChannels().toString()); //Print all the connected channels
		twitch.getChat().getChannels().forEach(s -> ownerChannelName = s);
		ownerTwitchUser = getTwitchUser(TwitchApiCallType.LOGIN, ownerChannelName);
	}
	
	
	public static void joinChannel(String... names){
		if(names == null || names.length == 0 || String.join("", names).isBlank()){return;}
		for(String name : names) {
			if(!twitch.getChat().getChannels().contains(name)){
				logger.info("Joining channel: "+name);
				twitch.getChat().joinChannel(name);
				twitch.getClientHelper().enableFollowEventListener(name);
				twitch.getClientHelper().enableStreamEventListener(name);
			}
		}
	}
	
	
//	public static void joinChannelById(String... channelIds){
//		getTwitchUsers(TwitchApiCallType.ID, channelIds).forEach(channel -> {
//			if(!channel.isDummy() && !twitch.getChat().getChannels().contains(channel.getLoginName())){
//				logger.info("Joining channel: "+channel.getLoginName());
//				twitch.getChat().joinChannel(channel.getLoginName());
//				twitch.getClientHelper().enableFollowEventListener(channel.getLoginName());
//			}
//		});
//	}
	
	
	public static void leaveChannel(String... names){
		if(names == null || names.length == 0 || String.join("", names).isBlank()){return;}
		for(String name : names) {
			logger.info("Leaving channel: "+name);
			twitch.getChat().leaveChannel(name);
			twitch.getClientHelper().disableFollowEventListener(name);
			twitch.getClientHelper().disableStreamEventListener(name);
		}
	}
	
	
	public static void leaveAllChannel(){
		twitch.getChat().getChannels().forEach(name -> leaveChannel(name));
	}
	



	/**
	 * Sends a message to the specified Twitch chat.
	 * 
	 * @param channel The name of the Twitch channel to send the message to.
	 * @param message The message to be sent to the Twitch chat channel.
	 */
	public static void sendMessage(ChatMessage message) {
		message.displayMessage();
				
		if(message.getChannel().replyMessage != null){
			replyMessage(message);
		}else{
			sendMessage(message.getChannel().getChannelData().getLoginName(), message.getMessage());
		}
		
		//Fire the MineChatEvent.
		Main.eventManager.fireEvent(MineChatEventType.SENT_MESSAGE, message);
	}
	
	
	/**
	 * Sends a message to the specified Twitch chat.
	 * 
	 * @param channel The name of the Twitch channel to send the message to.
	 * @param message The message to be sent to the Twitch chat channel.
	 */
	private static void sendMessage(String channel, String message) {
		logger.debug("Sending message -> message"); //Log the sent message.
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin")); //Set the default time zone.
		
		//Send the message to the specified Twitch chat.
		twitch.getChat().sendMessage(channel, message);
	}
	
	/**
	 * Sends a message to the specified Twitch chat channel using the information from the provided {@link ChannelMessageEvent}.
	 *
	 * @param event The {@link ChannelMessageEvent} containing information about the chat channel and user.
	 * @param message The message to be sent to the Twitch chat channel.
	 */
	private static void replyMessage(ChatMessage message) {
		TwitchMessage replyMessage = message.getChannel().replyMessage;
		twitch.getChat().sendMessage(message.getChannel().getChannelData().getLoginName(), message.getMessage(), replyMessage.getClient_nonce(), replyMessage.getReplyId());
		message.getChannel().getGreetingsManager().setMentioned(replyMessage.getUserName().toLowerCase());
	}
	
	
	public static TwitchAccesToken getAccesToken() {
		if(accesToken!=null && !accesToken.isExpired()){
			logger.debug("Twitch-acces-token | Is valid");
			return accesToken;
		}
		
		logger.warn("Twitch-acces-token | Generating a new token!");
		JsonObject json;
		try {
			json = new Gson().fromJson(requestAccesToken(credentials.getClientID(), credentials.getClientSecret()), JsonObject.class);
			return new TwitchAccesToken(json);
		} catch (JsonSyntaxException | IllegalArgumentException | NullPointerException ex) {
			logger.error("Error while trying to get a new AccesToken", ex);
			return null;
		}
	}
	
	public static String requestAccesToken(String clientID, String clientSecret) throws IllegalArgumentException{
		HttpResponse<String> response = Unirest.post("https://id.twitch.tv/oauth2/token?")
				.header("content-type", "application/x-www-form-urlencoded")
				.body("client_id="+clientID+"&client_secret="+clientSecret+"&grant_type=client_credentials")
				.asString();

		if(response.getBody().contains("\"status\":400")){
			throw new IllegalArgumentException("Invaild client ID");
		}
		
		if(response.getBody().contains("\"status\":403")){
			throw new IllegalArgumentException("Invaild client secret");
		}
		
		return response.getBody();
	}
	
	public static List<Object> getChatSettings(TwitchUserObj user) {
		if(user == null || user.isDummy()){return List.of();}
		
		JsonArray asJsonArray = newApiCall("https://api.twitch.tv/helix/chat/settings?broadcaster_id="+user.getUserId()).getAsJsonArray("data");
		System.err.println(asJsonArray);
		asJsonArray.forEach(data -> System.err.println(data));
		return null;
	}

	/**
	 * @return a list of channel IDs that are currently live.
	 */
	public static List<String> getLiveStates(){
		return getLiveUseres(TwitchApiCallType.ID, twitchUsers.stream()
				.filter(user -> !user.isDummy())
				.map(TwitchUserObj::getUserId)
				.collect(Collectors.toList()).toArray(String[]::new))
				.stream().map(TwitchUserObj::getUserId).toList();
	}
	
	public static List<TwitchUserObj> getLiveUseres(TwitchApiCallType callType, String... channels){
		if(channels == null || channels.length == 0){return List.of();}
		List<String> idList = new ArrayList<String>();
		
		channels = Arrays.copyOfRange(channels, 0, Math.min(channels.length, 100));
		String url = "https://api.twitch.tv/helix/streams?"+callType.getUrl()+"="+String.join("&"+callType.getUrl()+"=", channels)+"&type=live&first=100";
		HashMap<String, LiveMetaData> liveDataCache = new HashMap<String, LiveMetaData>();//ChannelId, data
		
		newApiCall(url).getAsJsonArray("data").forEach(data -> {
			idList.add(data.getAsJsonObject().get("user_id").getAsString());
			
			liveDataCache.put(data.getAsJsonObject().get("user_id").getAsString(), new LiveMetaData(
					data.getAsJsonObject().get("title").getAsString(),
					data.getAsJsonObject().get("game_name").getAsString(),
					Instant.parse(data.getAsJsonObject().get("started_at").getAsString()),
					data.getAsJsonObject().get("viewer_count").getAsInt(),
					data.getAsJsonObject().get("tags").getAsJsonArray().toString().replace("\"", "").split(",")));
		});
		
		return getTwitchUsers(TwitchApiCallType.ID, idList.toArray(String[]::new)).stream()
				.filter(user -> !user.isDummy() && user.setLiveData(liveDataCache.get(user.getUserId()))).toList();
	}
	
	/**
	 * Makes an API call to Twitch API and returns the resulting JSON object.
	 * <br> NOTE: You can also make one string containing "name1, name2" 
	 * 
	 * @param callType	The {@link TwitchApiCallType}
	 * @param channels	The twitch channel names/ids.
	 * @return The JSON object returned by the API call.
	 * @see <a href="https://dev.twitch.tv/docs/api/reference/#get-users">Twitch API Dokumentation</a>
	 */
	public static JsonObject newApiCall(TwitchApiCallType callType, String... channels) {
		if(channels != null && channels.length > 100){
			logger.error("To manny channels for one API call! Limiting to 100 items.", new IncompleteArgumentException("To many channels!", channels));
		}
		return newApiCall(
				"https://api.twitch.tv/helix/users?"+callType.getShortUrl()+"="+String.join("&"+callType.getShortUrl()+"=", 
				Arrays.copyOfRange(channels, 0, Math.min(channels.length, 100))));
	}

	private static JsonObject newApiCall(String url) {
		return new Gson().fromJson(Unirest.get(url)
			.header("Authorization", "Bearer "+TwitchManager.getAccesToken())
			.header("Client-Id", credentials.getClientID())
			.header("User-Agent", "MineChat Client")
			.asString()
			.getBody(), JsonObject.class);
	}
	
	
	/**
	 * A method to retrieve a Twitch user based on their username or user ID.
	 *
	 * <br> The method checks if the desired user is in the cache. If the user is not in the cache,
	 * an API call is made and a new user is created.
	 *
	 * @param callType The type of API call to be made (username or user ID)
	 * @param channel The username or user ID of the desired user
	 * @return A {@link TwitchUserObj} representing the desired user
	 */
	public static TwitchUserObj getTwitchUser(TwitchApiCallType type, String channel) {
		return getTwitchUsers(type, channel).get(0);
	}
	
	/**
	 * A method to retrieve a Twitch user based on their username or user ID.
	 *
	 * <br> The method checks if the desired user is in the cache. If the user is not in the cache,
	 * an API call is made and a new user is created.
	 * 
	 * <p> NOTE: You can also make one string containing "name1, name2" 
	 *
	 * @param callType The type of API call to be made (username or user ID)
	 * @param channels The username(s) or user ID(s) of the desired user(s)
	 * @return A list of {@link TwitchUserObj} objects representing the desired user(s)
	 */
	public static List<TwitchUserObj> getTwitchUsers(TwitchApiCallType callType, String... channels) {
		// Create a list to store the retrieved Twitch users
		List<TwitchUserObj> users = new ArrayList<TwitchUserObj>();

		// Concatenate the user names/IDs into a single comma-separated string
		String channelsAsString = "";
		for (String channel : channels) {
			channelsAsString += channel.toLowerCase() + ",";
		}
		
		// Remove the trailing comma from the string, if present and Split the string into an array of individual user names/IDs
		channelsAsString = (channelsAsString.endsWith(",")) ? channelsAsString.substring(0, channelsAsString.length() - 1) : channelsAsString;
		channels = channelsAsString.replace(" ", "").split(",");

		// Create a list of channels that need to be retrieved via API call
		List<String> callMe = new ArrayList<String>();
		List<String> notCalledChannels = new ArrayList<String>();
		callMe.addAll(Arrays.asList(channels));

		// Check if the desired users are in the cache
		for (String channel : channels) {
			for (TwitchUserObj user : twitchUsers) {
				if (user.getIdentifier(callType).equals(channel)) {
					users.add(user);
					callMe.remove(channel);
				}
			}
		}

		// Add the channels that were not found in the cache to the list of channels to retrieve via API call
		notCalledChannels.addAll(callMe);
		
		// If there are channels to retrieve via API call, retrieve the data
		if (callMe.size() > 0) {
			JsonObject apiCall = newApiCall(callType, callMe.toArray(new String[0]));
			if(apiCall != null && apiCall.getAsJsonArray("data") != null){
				apiCall.getAsJsonArray("data").forEach(obj -> {
					TwitchUserObj newTwitchUser = new TwitchUserObj(obj.getAsJsonObject());
					twitchUsers.add(newTwitchUser);
					users.add(newTwitchUser);
					notCalledChannels.remove(newTwitchUser.getIdentifier(callType));
				});
			}
		}

		// For each remaining channel that was not retrieved via API call, create a new "placeholder" TwitchUserObj
		notCalledChannels.forEach(channel -> {
			TwitchUserObj twitchUser = new TwitchUserObj(callType, channel, true);
			twitchUsers.add(twitchUser);
			users.add(twitchUser);
		});

		return users;
	}
	
	
	/**
	 * No url check requert.
	 * <br> returns null or twitch user.
	 * @param url
	 * @return
	 */
	public static TwitchUserObj extracktUserLoginFromUrl(String url){
		if(url.matches("\\b\\w+\\b")){url = "twitch.tv/"+url;}
		
    	try {
    		Matcher matcher = Pattern.compile("(?<=twitch\\.tv/(?:popout/)?)(\\w+)").matcher(url);
        	if(matcher.find()){
        		if(matcher.group().equals("popout") || matcher.group().equals("u")) {
                    if(matcher.find()){
                        return TwitchManager.getTwitchUser(TwitchApiCallType.LOGIN, matcher.group());
                    }
        		}
        		
                return TwitchManager.getTwitchUser(TwitchApiCallType.LOGIN, matcher.group());
        	}
		} catch (Exception e) {}
    	return null;
    }

}
