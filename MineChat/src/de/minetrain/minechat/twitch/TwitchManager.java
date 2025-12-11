package de.minetrain.minechat.twitch;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.helix.domain.ChatBadgeSet;
import com.github.twitch4j.helix.domain.ChatBadgeSetList;
import com.github.twitch4j.helix.domain.ChatSettings;
import com.github.twitch4j.helix.domain.Emote;
import com.github.twitch4j.helix.domain.EmoteList;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;

import de.minetrain.minechat.gui.frames.GetCredentialsFrame;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.CredentialsManager;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import de.minetrain.minechat.utils.ChatMessage;
import de.minetrain.minechat.utils.events.MineChatEventType;
import io.github.bucket4j.Bandwidth;

/**
 * The TwitchManager class is responsible for creating and managing a Twitch client instance.
 * It provides the functionality to join a Twitch chat and send messages to it.
 *
 * @author MineTrain/Justin
 * @since 28.04.2023
 * @version 1.3
 */
public class TwitchManager {

	private static final Logger LOG = LoggerFactory.getLogger(TwitchManager.class);

	public record LiveMetaData(String title, String game, Instant startTime, int viewer, String[] tags){};
	public static String ownerChannelName = ">null<";

	private static TwitchManager instance;

	private ITwitchClient twitch;
	private CredentialsManager credentials;
	private final List<TwitchUserObj> twitchUsers;
	private TwitchUserObj ownerTwitchUser;


	/**
	 * Creates a new Twitch client instance using the provided TwitchCredentials.
	 * @param credentials The TwitchCredentials used to authenticate the Twitch client.
	 */
	public TwitchManager(CredentialsManager credentials) {
		this.credentials = credentials;
		this.twitchUsers = Collections.synchronizedList(new ArrayList<>());

//		Main.LOADINGBAR.setProgress("Conect to Twitch Helix", 25);

		//Configure the TwitchClientBuilder with the provided credentials.
		twitch = buildClient(credentials);

		if(twitch.getChat().getChannels().isEmpty()){
//			Asking for a new OAuth2 token.
//			Main.LOADINGBAR.setError("Requesting new OAuth2 Token.");
			LOG.warn("Requesting new OAuth2 Token.");

			JFrame tempFrame = new JFrame();
			tempFrame.setVisible(false);
			GetCredentialsFrame newCredentialsFrame = new GetCredentialsFrame(tempFrame);
			newCredentialsFrame.injectData(credentials.getClientID(), credentials.getClientSecret());
			newCredentialsFrame.startServer();
			LOG.warn("Start new HTTP server to get new OAuth2 key.");


			//If the new OAuth2 token also don´t work, let the user Reenter there API credentials
			try {
				this.credentials = new CredentialsManager();
				twitch = buildClient(this.credentials);
			} catch (Exception ex) {
				CredentialsManager.deleteCredentialsFile();
//				Main.LOADINGBAR.setError("Invalid Twitch Credentials!");
				LOG.error("Invalid twitch credentials!", ex);
			}

			ownerTwitchUser = new TwitchUserObj(TwitchApiCallType.LOGIN, ownerChannelName, true);
			return;
		}

//		Main.LOADINGBAR.setProgress("Join Twitch channels Helix", 60);
		twitch.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new TwitchListner()); //Register a listener for Twitch events.
		LOG.info("Connecting to channels: "+twitch.getChat().getChannels().toString()); //Print all the connected channels
		twitch.getChat().getChannels().forEach(s -> ownerChannelName = s);
		ownerTwitchUser = requestTwitchUser(TwitchApiCallType.LOGIN, ownerChannelName).join();
	}

	public static void init(CredentialsManager credentials) throws ExecutionException {
		if(instance != null) {
			LOG.warn("TwitchManager is already initialized and will be recreated!");
		}
		instance = new TwitchManager(credentials);
	}

	public static TwitchManager instance() {
		return instance;
	}

	public void joinChannel(String... names){
		if(names == null || names.length == 0 || String.join("", names).isBlank()){return;}
		for(String name : names) {
			if(!twitch.getChat().getChannels().contains(name)){
				LOG.info("Joining channel: {}", name);
				twitch.getChat().joinChannel(name);
				twitch.getClientHelper().enableFollowEventListener(name);
				twitch.getClientHelper().enableStreamEventListener(name);
			}
		}
	}

//	public void joinChannelById(String... channelIds){
//		getTwitchUsers(TwitchApiCallType.ID, channelIds).forEach(channel -> {
//			if(!channel.isDummy() && !twitch.getChat().getChannels().contains(channel.getLoginName())){
//				logger.info("Joining channel: "+channel.getLoginName());
//				twitch.getChat().joinChannel(channel.getLoginName());
//				twitch.getClientHelper().enableFollowEventListener(channel.getLoginName());
//			}
//		});
//	}

	public void leaveChannel(String... names) {
		if (names == null || names.length == 0 || String.join("", names).isBlank()) {
			return;
		}
		for (String name : names) {
			LOG.info("Leaving channel: {}", name);
			twitch.getChat().leaveChannel(name);
			twitch.getClientHelper().disableFollowEventListener(name);
			twitch.getClientHelper().disableStreamEventListener(name);
		}
	}

	public void leaveAllChannel(){
		twitch.getChat().getChannels().forEach(this::leaveChannel);
	}

	/**
	 * Sends a message to the specified Twitch chat.
	 *
	 * @param channel The name of the Twitch channel to send the message to.
	 * @param message The message to be sent to the Twitch chat channel.
	 */
	public void sendMessage(ChatMessage message) {
		message.displayMessage();

		if (message.getChannel().replyMessage != null) {
			replyMessage(message);
		} else {
			sendMessage(message.getChannel().getChannel().getLoginName(), message.getMessage());
		}

		// Fire the MineChatEvent.
		Main.eventManager.fireEvent(MineChatEventType.SENT_MESSAGE, message);
	}

	public CompletableFuture<List<ChatBadgeSet>> requestChannelBadges(String userId) {
		return CompletableFuture.supplyAsync(() -> {
			ChatBadgeSetList badges = twitch.getHelix().getChannelChatBadges(null, userId).execute();
			return badges != null ? badges.getBadgeSets() : List.of();
		});
	}

	public CompletableFuture<List<ChatBadgeSet>> requestGlobalBadges() {
		return CompletableFuture.supplyAsync(() -> {
			ChatBadgeSetList badges = twitch.getHelix().getGlobalChatBadges(null).execute();
			return badges != null ? badges.getBadgeSets() : List.of();
		});
	}

	public CompletableFuture<List<Emote>> requestChannelEmotes(String userId) {
		return CompletableFuture.supplyAsync(() -> {
			EmoteList emotes = twitch.getHelix().getChannelEmotes(null, userId).execute();
			return emotes != null ? emotes.getEmotes() : List.of();
		});
	}

	public CompletableFuture<List<Emote>> requestGlobalEmotes() {
		return CompletableFuture.supplyAsync(() -> {
			EmoteList emotes = twitch.getHelix().getGlobalEmotes(null).execute();
			return emotes != null ? emotes.getEmotes() : List.of();
		});
	}

	public CompletableFuture<ChatSettings> requestChatSettings(TwitchUserObj user) {
		if (user == null || user.isDummy()) {
			return CompletableFuture.completedFuture(null);
		}
		return CompletableFuture.supplyAsync(() -> {
			return twitch.getHelix().getChatSettings(null, user.getUserId(), null).execute().get();
		});
	}

	/**
	 * @return a list of channel IDs that are currently live.
	 */
	public CompletableFuture<List<String>> requestLiveStates(){
		return requestLiveUsers(TwitchApiCallType.ID, twitchUsers.stream()
				.filter(user -> !user.isDummy())
				.map(TwitchUserObj::getUserId)
				.toArray(String[]::new))
			.thenApply(users -> users.stream().map(TwitchUserObj::getUserId).toList());
	}

	public CompletableFuture<List<TwitchUserObj>> requestLiveUsers(TwitchApiCallType callType, String... channels) {
		if (channels == null || channels.length == 0) {
			return CompletableFuture.completedFuture(List.of());
		}
		return CompletableFuture.supplyAsync(() -> {
			StreamList streams = twitch.getHelix().getStreams(null, null, null, 100, null, null, callType == TwitchApiCallType.ID ? List.of(channels) : null, callType == TwitchApiCallType.LOGIN ? List.of(channels) : null).execute();
			List<String> idList = new ArrayList<>(streams.getStreams().size());
			HashMap<String, LiveMetaData> liveDataCache = new HashMap<>();// ChannelId, data
			for (Stream stream : streams.getStreams()) {
				idList.add(stream.getUserId());
				liveDataCache.put(stream.getUserId(), new LiveMetaData(
						stream.getTitle(),
						stream.getGameName(),
						stream.getStartedAtInstant(),
						stream.getViewerCount(),
						stream.getTags().toArray(String[]::new)));
			}
			return new ImmutablePair<>(idList, liveDataCache);
		}).thenCompose(pair -> requestTwitchUsers(TwitchApiCallType.ID, pair.getLeft().toArray(String[]::new))
			.thenApply(users -> {
				for (TwitchUserObj user : users) {
					LiveMetaData data = pair.getRight().get(user.getUserId());
					if (data != null) {
						user.setLiveData(data);
					}
				}
				return users;
			}));
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
	public CompletableFuture<TwitchUserObj> requestTwitchUser(TwitchApiCallType type, String channel) {
		return requestTwitchUsers(type, channel).thenApply(List::getFirst);
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
	public CompletableFuture<List<TwitchUserObj>> requestTwitchUsers(TwitchApiCallType callType, String... channels) {
		return CompletableFuture.supplyAsync(() -> {
			List<TwitchUserObj> users = new ArrayList<>();
			List<String> missingChannels = new ArrayList<>();
			List<String> lowerCaseChannels = Arrays.stream(channels).map(String::toLowerCase).toList();
			for (String channel : lowerCaseChannels) {
				twitchUsers.stream().filter(user -> user.getIdentifier(callType).equals(channel)).findFirst()
				.ifPresentOrElse(users::add, () -> missingChannels.add(channel));
			}

			if (!missingChannels.isEmpty()) {
				twitch.getHelix().getUsers(null, callType == TwitchApiCallType.ID ? missingChannels : null, callType == TwitchApiCallType.LOGIN ? missingChannels : null)
				.execute()
				.getUsers()
				.forEach(user -> {
					TwitchUserObj newTwitchUser = new TwitchUserObj(user);
					twitchUsers.add(newTwitchUser);
					users.add(newTwitchUser);
					missingChannels.remove(newTwitchUser.getIdentifier(callType));
				});
			}

			missingChannels.forEach(channel -> {
				TwitchUserObj twitchUser = new TwitchUserObj(callType, channel, true);
				twitchUsers.add(twitchUser);
				users.add(twitchUser);
			});
			return users;
		});
	}

	/**
	 * No url check request.
	 * <br> returns null or twitch user.
	 * @param url
	 * @return
	 */
	public CompletableFuture<TwitchUserObj> extracktUserLoginFromUrl(String url) {
		if (url.matches("\\b\\w+\\b")) {
			url = "twitch.tv/" + url;
		}

		Matcher matcher = Pattern.compile("(?<=twitch\\.tv/(?:popout/)?)(\\w+)").matcher(url);
		if (matcher.find()) {
			if (matcher.group().equals("popout") || matcher.group().equals("u")) {
				if (matcher.find()) {
					return requestTwitchUser(TwitchApiCallType.LOGIN, matcher.group());
				}
			}

			return requestTwitchUser(TwitchApiCallType.LOGIN, matcher.group());
		}
		return CompletableFuture.completedFuture(null);
	}

	public TwitchUserObj getSelfUser() {
		return ownerTwitchUser;
	}

	private TwitchClient buildClient(CredentialsManager credentials) {
		return TwitchClientBuilder.builder()
			.withClientId(credentials.getClientID())
			.withClientSecret(credentials.getClientSecret())
			.withEnableHelix(true)
			.withChatAccount(new OAuth2Credential("twitch", credentials.getOAuth2Token()))
			.withChatChannelMessageLimit(Bandwidth.builder().capacity(1L).refillGreedy(1, Duration.ofMillis(300)).id("per-channel-limit").build())
			.withEnableChat(true)
			.build();
	}

	/**
	 * Sends a message to the specified Twitch chat.
	 *
	 * @param channel The name of the Twitch channel to send the message to.
	 * @param message The message to be sent to the Twitch chat channel.
	 */
	private void sendMessage(String channel, String message) {
		LOG.debug("Sending message -> message"); // Log the sent message.
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin")); // Set the default time zone.

		// Send the message to the specified Twitch chat.
		twitch.getChat().sendMessage(channel, message);
	}

	/**
	 * Sends a message to the specified Twitch chat channel using the information from the provided {@link ChannelMessageEvent}.
	 *
	 * @param event The {@link ChannelMessageEvent} containing information about the chat channel and user.
	 * @param message The message to be sent to the Twitch chat channel.
	 */
	private void replyMessage(ChatMessage message) {
		TwitchMessage replyMessage = message.getChannel().replyMessage;
		twitch.getChat().sendMessage(message.getChannel().getChannel().getLoginName(), message.getMessage(), replyMessage.getClient_nonce(), replyMessage.getReplyId());
		message.getChannel().getGreetingsManager().setMentioned(replyMessage.getUserName().toLowerCase());
	}
}
