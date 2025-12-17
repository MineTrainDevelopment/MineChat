package de.minetrain.minechat.twitch;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.github.twitch4j.helix.domain.ChatBadgeSet;
import com.github.twitch4j.helix.domain.ChatSettings;
import com.github.twitch4j.helix.domain.Emote;

import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import de.minetrain.minechat.utils.OutboundChatMessage;

/**
 * A helper class for interacting with Twitch.
 * This class provides static methods to perform various Twitch-related operations
 * such as joining/leaving channels, sending messages, and requesting user information.
 */
public final class TwitchHelper {

	private TwitchHelper() {
		// Private constructor to prevent instantiation
	}

	public static void joinChannel(String channelId){
		TwitchManager.instance().joinChannel(channelId);
	}

	public static void leaveChannel(String... names){
		TwitchManager.instance().leaveChannel(names);
	}


	public static void leaveAllChannel(){
		TwitchManager.instance().leaveAllChannel();
	}

	public static void sendMessage(OutboundChatMessage message) {
		TwitchManager.instance().sendMessage(message);
	}

	public static CompletableFuture<ChatSettings> getChatSettings(TwitchUserObj user) {
		return TwitchManager.instance().requestChatSettings(user);
	}

	public static CompletableFuture<List<ChatBadgeSet>> requestChannelBadges(String userId) {
		return TwitchManager.instance().requestChannelBadges(userId);
	}

	public static CompletableFuture<List<ChatBadgeSet>> requestGlobalBadges() {
		return TwitchManager.instance().requestGlobalBadges();
	}

	public static CompletableFuture<List<Emote>> requestChannelEmotes(String userId) {
		return TwitchManager.instance().requestChannelEmotes(userId);
	}

	public static CompletableFuture<List<Emote>> requestGlobalEmotes() {
		return TwitchManager.instance().requestGlobalEmotes();
	}

	/**
	 * @return a list of channel IDs that are currently live.
	 */
	public static CompletableFuture<List<String>> requestLiveStates(){
		return TwitchManager.instance().requestLiveStates();
	}

	public static CompletableFuture<List<TwitchUserObj>> requestLiveUsers(TwitchApiCallType callType, String... channels){
		return TwitchManager.instance().requestLiveUsers(callType, channels);
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
	public static CompletableFuture<TwitchUserObj> requestTwitchUser(TwitchApiCallType type, String channel) {
		return TwitchManager.instance().requestTwitchUser(type, channel);
	}

	/**
	 * A method to retrieve a Twitch user based on their username or user ID.
	 *
	 * <br> The method checks if the desired user is in the cache. If the user is not in the cache,
	 * an API call is made and a new user is created.
	 *
	 * @param callType The type of API call to be made (username or user ID)
	 * @param channels The username(s) or user ID(s) of the desired user(s)
	 * @return A list of {@link TwitchUserObj} objects representing the desired user(s)
	 */
	public static CompletableFuture<List<TwitchUserObj>> requestTwitchUsers(TwitchApiCallType callType, String... channels) {
		return TwitchManager.instance().requestTwitchUsers(callType, channels);
	}

	/**
	 * No url check requert. <br>
	 * returns null or twitch user.
	 *
	 * @param url
	 * @return
	 */
	public static CompletableFuture<TwitchUserObj> extracktUserLoginFromUrl(String url) {
		return TwitchManager.instance().extracktUserLoginFromUrl(url);
	}

	public static TwitchUserObj getSelfUser() {
		return TwitchManager.instance().getSelfUser();
	}
}
