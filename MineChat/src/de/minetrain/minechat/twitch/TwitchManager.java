package de.minetrain.minechat.twitch;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import com.github.twitch4j.helix.domain.ChatBadgeSet;
import com.github.twitch4j.helix.domain.ChatBadgeSetList;
import com.github.twitch4j.helix.domain.ChatSettings;
import com.github.twitch4j.helix.domain.Emote;
import com.github.twitch4j.helix.domain.EmoteList;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import de.minetrain.minechat.utils.OutboundChatMessage;
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

	private final ITwitchClient twitch;
	private final List<TwitchUserObj> twitchUsers;
	private final TwitchUserObj ownerTwitchUser;


	/**
	 * Creates a new Twitch client instance using the provided TwitchCredentials.
	 * @param credentials The TwitchCredentials used to authenticate the Twitch client.
	 */
	public TwitchManager(String oAuth2Token) {
		this.twitchUsers = Collections.synchronizedList(new ArrayList<>());

//		Main.LOADINGBAR.setProgress("Conect to Twitch Helix", 25);

		//Configure the TwitchClientBuilder with the provided credentials.
		twitch = buildClient(oAuth2Token);

//		Main.LOADINGBAR.setProgress("Join Twitch channels Helix", 60);
		twitch.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new TwitchListener());
		ownerTwitchUser = new TwitchUserObj(twitch.getHelix().getUsers(null, null, null).execute().getUsers().getFirst());
		ownerChannelName = ownerTwitchUser.getLoginName();
	}

	public static void init(String oAuth2Token) {
		if(instance != null) {
			LOG.warn("TwitchManager is already initialized and will be recreated!");
		}
		instance = new TwitchManager(oAuth2Token);
	}

	public static TwitchManager instance() {
		return instance;
	}

	public void joinChannel(String channeldId) {
		LOG.info("Joining channel: {}", channeldId);
		twitch.getEventSocket().register(SubscriptionTypes.CHANNEL_CHAT_MESSAGE.prepareSubscription(builder -> builder.broadcasterUserId(channeldId).userId(getSelfUser().getUserId()).build(), null));
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
	public void sendMessage(OutboundChatMessage message) {
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

	public static CompletableFuture<String> requestOAuthToken(String clientId) {
		RandomStringUtils secure = RandomStringUtils.secure();
		String state = secure.nextAlphanumeric(32);
		String requestUrl = "https://id.twitch.tv/oauth2/authorize?response_type=token&client_id=" + clientId + "&redirect_uri=http://localhost:8000/oauth_callback&scope=user:read:chat&state=" + state;
		Pattern pattern = Pattern.compile("access_token=([^&]+).*&state=([^&]+)");
		CompletableFuture<String> futureToken = new CompletableFuture<>();
		String html = """
			<!doctype html><html><head><meta charset='utf-8'><title>OAuth Callback</title></head>
			<body>
			<script>
			  const params = new URLSearchParams(window.location.hash.substring(1));
			  const accessToken = params.get('access_token') || '';
			  const scope = params.get('scope') || '';
			  const state = params.get('state') || '';
			  fetch('/token', {
			    method: 'POST',
			    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			    body: new URLSearchParams({access_token: accessToken, scope: scope, state: state})
			  }).then(() => {
			    document.body.innerText = 'Authentication complete. You can close this window.';
			  }).catch(() => {
			    document.body.innerText = 'Failed to deliver token.';
			  });
			</script>
			</body></html>
			""";
		HttpServer httpServer = null;
		try {
			httpServer = HttpServer.create(new InetSocketAddress("localhost", 8000), 0);
			httpServer.createContext("/oauth_callback", exchange -> handleOAuthCallback(html, exchange));
			httpServer.createContext("/token", exchange -> handleTokenReceive(state, pattern, futureToken, exchange));
			httpServer.start();
		} catch (IOException e) {
			LOG.error("Failed to start OAuth HTTP server!", e);
			futureToken.completeExceptionally(e);
			return futureToken;
		}

		try {
			Desktop.getDesktop().browse(URI.create(requestUrl));
		} catch (IOException e) {
			futureToken.completeExceptionally(e);
			httpServer.stop(0);
			LOG.error("Failed to request OAuth token!", e);
		}
		return futureToken;
	}

	private static void handleTokenReceive(String state, Pattern pattern, CompletableFuture<String> futureToken, HttpExchange exchange) throws IOException {
		if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
			exchange.sendResponseHeaders(405, -1);
			return;
		}
		String query = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
		LOG.debug("OAuth2 token received with query: {}", query);
		byte[] ok = "OK".getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
		exchange.sendResponseHeaders(200, ok.length);
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(ok);
		}
		if (StringUtils.isBlank(query)) {
			LOG.error("No query parameters found in OAuth2 token receive!");
			exchange.getHttpContext().getServer().stop(0);
			futureToken.completeExceptionally(new RuntimeException("No query parameters found in OAuth2 token receive."));
			return;
		}

		Matcher matcher = pattern.matcher(query);
		if (matcher.find()) {
			if (!state.equals(matcher.group(2))) {
				LOG.warn("State mismatch! Potential CSRF attack.");
				return;
			}
			String oAuth2Token = matcher.group(1);
			LOG.info("Received new OAuth2 token.");
			exchange.getHttpContext().getServer().stop(0);
			futureToken.complete(oAuth2Token);
			return;
		}
		LOG.error("Failed to retrieve OAuth2 token!");
		exchange.getHttpContext().getServer().stop(0);
		futureToken.completeExceptionally(new RuntimeException("Failed to retrieve OAuth2 token."));
	}

	private static void handleOAuthCallback(String html, HttpExchange exchange) throws IOException {
		byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
		exchange.sendResponseHeaders(200, bytes.length);
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(bytes);
		}
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

	private TwitchClient buildClient(String oAuth2Token) {
		return TwitchClientBuilder.builder()
			.withEnableEventSocket(true)
			.withDefaultAuthToken(new OAuth2Credential("twitch", oAuth2Token))
			.withEnableHelix(true)
			.withChatAccount(new OAuth2Credential("twitch", oAuth2Token))
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
	private void replyMessage(OutboundChatMessage message) {
		TwitchMessage replyMessage = message.getChannel().replyMessage;
		twitch.getChat().sendMessage(message.getChannel().getChannel().getLoginName(), message.getMessage(), replyMessage.getClient_nonce(), replyMessage.getReplyId());
		message.getChannel().getGreetingsManager().setMentioned(replyMessage.getUserName().toLowerCase());
	}
}
