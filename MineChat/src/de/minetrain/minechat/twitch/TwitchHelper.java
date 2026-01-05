package de.minetrain.minechat.twitch;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import com.github.twitch4j.helix.domain.ChatBadgeSet;
import com.github.twitch4j.helix.domain.ChatSettings;
import com.github.twitch4j.helix.domain.Emote;
import com.github.twitch4j.helix.domain.ModeratedChannel;
import com.github.twitch4j.helix.domain.SentChatMessage;
import com.github.twitch4j.helix.domain.Stream;

import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;

/**
 * A helper class for interacting with Twitch.
 * This class provides static methods to perform various Twitch-related operations
 * such as joining/leaving channels, sending messages, and requesting user information.
 */
public final class TwitchHelper {

	public static final String CHANNEL_ID_PUBLIC = "public";

	private TwitchHelper() {
		// Private constructor to prevent instantiation
	}

	public static void joinChannel(String channelId){
		TwitchManager.instance().joinChannel(channelId);
	}

	public static void shutdown() {
		TwitchManager.instance().shutdown();
	}

	public static CompletableFuture<SentChatMessage> sendMessage(String channelId, String message, String replyMessageId) {
		return TwitchManager.instance().sendMessage(channelId, message, replyMessageId);
	}

	public static CompletableFuture<ChatSettings> getChatSettings(TwitchUserObj user) {
		return TwitchManager.instance().requestChatSettings(user);
	}

	public static CompletableFuture<List<ChatBadgeSet>> requestChannelBadges(String channelId) {
		return TwitchManager.instance().requestChannelBadges(channelId);
	}

	public static CompletableFuture<List<ChatBadgeSet>> requestGlobalBadges() {
		return TwitchManager.instance().requestGlobalBadges();
	}

	public static CompletableFuture<List<Emote>> requestChannelEmotes(String channelId) {
		return TwitchManager.instance().requestChannelEmotes(channelId);
	}

	public static CompletableFuture<List<Emote>> requestGlobalEmotes() {
		return TwitchManager.instance().requestGlobalEmotes();
	}

	public static CompletableFuture<List<Stream>> requestStreamInfo(String... channelIds){
		return TwitchManager.instance().requestStreamInfo(channelIds);
	}

	public static CompletableFuture<List<TwitchUserObj>> requestLiveUsers(TwitchApiCallType callType, String... channels){
		return TwitchManager.instance().requestLiveUsers(callType, channels);
	}

	public static CompletableFuture<List<ModeratedChannel>> requestModeratedChannel() {
		return TwitchManager.instance().requestModeratedChannel();
	}

	public static CompletableFuture<ChatSettings> requestChannelChatSettings(String channelId) {
		return TwitchManager.instance().requestChannelChatSettings(channelId);
	}

	public static CompletableFuture<List<Emote>> requestAvailableUserEmotes(String channelId) {
		return TwitchManager.instance().requestAvailableUserEmotes(channelId);
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

	public static String generateNameRegex(String twitchName) {
		// Split username into parts at boundaries between letters/digits/special chars
		String[] parts = twitchName.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)|(?<=\\D)(?=[_-])|(?<=[_-])(?=\\D)");

		if (parts.length == 1) {
			return twitchName; // Single part, return as-is
		}

		// Find first part containing letters (the actual username base)
		int baseNameIndex = findBaseName(parts);

		StringBuilder pattern = new StringBuilder();
		pattern.append("^");
		if (baseNameIndex > 0) {
			pattern.append("(?:");
			for (int i = 0; i < baseNameIndex; i++) {
				pattern.append(Pattern.quote(parts[i]));
			}
			pattern.append(")?");
		}
		pattern.append(parts[baseNameIndex]);
		if (baseNameIndex < parts.length - 1) {
			pattern.append("(?:");
			for (int i = baseNameIndex + 1; i < parts.length; i++) {
				pattern.append(Pattern.quote(parts[i]));
			}
			pattern.append(")?");
		}
		pattern.append("$");
		return pattern.toString();
	}

	private static int findBaseName(String[] parts) {
		Pattern pattern = Pattern.compile(".*[a-zA-Z].*");
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			if (pattern.matcher(part).matches()) { // Contains at least one letter
				return i;
			}
		}
		return 0; // Fallback to first part
	}
}
