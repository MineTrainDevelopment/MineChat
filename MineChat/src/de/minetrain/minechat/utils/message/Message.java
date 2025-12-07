package de.minetrain.minechat.utils.message;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import de.minetrain.minechat.data.databases.OwnerCacheDatabase.UserChatData;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.emotes.WebEmote;
import de.minetrain.minechat.main.Channel;
import de.minetrain.minechat.main.ChannelManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.UserColorCache;
import de.minetrain.minechat.utils.message.tokens.EmoteToken;
import de.minetrain.minechat.utils.message.tokens.HyperLinkToken;
import de.minetrain.minechat.utils.message.tokens.UserNameToken;
import de.minetrain.minechat.utils.message.tokens.WordToken;

public class Message {
	public static final transient ConcurrentHashMap<String, WebEmote> webEmoteCache = new ConcurrentHashMap<String, WebEmote>();//emote_id, emote
//	public static final ConcurrentHashMap<String, String> nameColorCache = new ConcurrentHashMap<String, String>();//userName, UserColorHex
	public static final UserColorCache nameColorCache = new UserColorCache();
	private static final transient Logger logger = LoggerFactory.getLogger(Message.class);
	private UserChatData user;
	private MessageToken[] messageTokens;
	private final Instant timeStamp;
	private final boolean emoteOnly;
	private final boolean urlOnly;
	
	
	//Twitch only
	private final String messageId;
	private final String channelId;
	private final String client_nonce;

	private final String replyMessageId;
	private final String replyUserName;
	private final String channelPointActionId;

	private final boolean channelPointHighlight;
	private final boolean firstChannelMessages;

	public Message(UserChatData user, String rawMessage, String channelId) {
		this.user = user;
		this.messageTokens = parseMessage(rawMessage);
		this.timeStamp = Instant.now();
		this.emoteOnly = Arrays.stream(messageTokens).allMatch(MessageToken::isEmote);
		this.urlOnly = Arrays.stream(messageTokens).allMatch(MessageToken::isHyperLink);
		this.channelId = channelId;
		
		
		this.messageId = null;
		this.client_nonce = null;
		this.replyMessageId = null;
		this.replyUserName = null;
		this.channelPointActionId = null;
		this.channelPointHighlight = false;
		this.firstChannelMessages = false;
	}
	
	public Message(IRCMessageEvent event) {
		String rawMessage = event.getMessage().orElse(event.getRawMessage());
		this.user = new UserChatData(
				event.getUserId(),
				nameColorCache.getColorCode(event.getUserName()),
				event.getUserDisplayName().orElse(event.getUserName()),
				String.join(", ", event.getTagValue("badges").orElse("").split(",")));
		
		HashMap<String, WebEmote> webEmoteSet = new HashMap<String, WebEmote>();
		event.getTagValue("emotes").ifPresent(emotes -> {
			Arrays.stream(emotes.split("/")).parallel().forEach(emote -> {
				String[] emoteSplit = emote.split(":");
				String emoteId = emoteSplit[0];
				String[] emoteLocations = emoteSplit[1].split(",");
				
				if(emoteLocations != null && emoteLocations.length != 0){
					String[] emoteLocation = emoteLocations[0].split("-");
					String emoteName = rawMessage.substring(Integer.parseInt(emoteLocation[0]), Integer.parseInt(emoteLocation[1])+1);
					
					webEmoteSet.put(emoteName, webEmoteCache.computeIfAbsent(emoteId, key -> {
						try {
							return new WebEmote(emoteName, emoteId);
						} catch (MalformedURLException ex) {
							logger.debug("Can´t load web emote for -> "+emoteName, ex);
						}
						return null;
					}));
				}
			});
		});
		
		this.messageTokens = parseMessage(rawMessage, webEmoteSet);
		this.emoteOnly = Arrays.stream(messageTokens).allMatch(MessageToken::isEmote);
		this.urlOnly = Arrays.stream(messageTokens).allMatch(MessageToken::isHyperLink);
		this.timeStamp = Instant.ofEpochSecond(Long.parseLong(event.getTagValue("tmi-sent-ts").orElse("0")));
		
		this.channelId = event.getChannelId();
		this.client_nonce = event.getNonce().orElse(null);
		this.messageId = event.getMessageId().orElse(null);
		this.replyMessageId = event.getTagValue("reply-parent-msg-id").orElse(null);
		this.replyUserName = event.getTagValue("reply-parent-display-name").orElse(null);
		this.channelPointActionId = event.getTagValue("custom-reward-id").orElse(null);
		
		this.channelPointHighlight = event.getTagValue("msg-id").orElse("").equals("highlighted-message");
		this.firstChannelMessages = event.getTagValue("first-msg").orElse("0").equals("1");
	}
	

	private final MessageToken[] parseMessage(String rawMessage){
		return parseMessage(rawMessage, null);
	}
	
	private final MessageToken[] parseMessage(String rawMessage, HashMap<String, WebEmote> webEmoteSet){
		String[] words = rawMessage.split(" ");
		ArrayList<MessageToken> tokens = new ArrayList<MessageToken>(words.length);
		Channel channel = ChannelManager.getChannel(channelId);
		
		for(String word : words){
			if(!word.endsWith(".") && word.contains(".") && Main.isValidURL(word)){
				tokens.add(new HyperLinkToken(word));
				continue;
			}
			
			if(webEmoteSet != null){
				Optional<Emote> emote = getWebEmote(word, webEmoteSet);
				if(emote.isPresent()){
					tokens.add(new EmoteToken(emote.get()));
					continue;
				}
			}
			
			Optional<Emote> emote = getEmote(word);
			if(emote.isPresent()){
				tokens.add(new EmoteToken(emote.get()));
				continue;
			}

//			if(word.startsWith("@") && word.length() > 1 && channel != null && channel.getGreetingsManager().contains(word.substring(1))){
			//May add a user validation.
			if(word.startsWith("@") && word.length() > 1){
				tokens.add(new UserNameToken(word));
				continue;
			}
			
			tokens.add(new WordToken(word));
		}
		return tokens.toArray(MessageToken[]::new);
	}
	

	private static final Optional<Emote> getEmote(String word){
		return Optional.ofNullable(EmoteManager.getEmoteByName(word));
	}
	
	private static final Optional<Emote> getWebEmote(String word, HashMap<String, WebEmote> webEmoteSet){
		if(webEmoteSet != null && !webEmoteSet.isEmpty()){
			if(webEmoteSet.containsKey(word)){
				return Optional.of(webEmoteSet.get(word));
			}
		}
		return Optional.empty();
	}
	
	public MessageToken[] getMessageTokens() {
		return messageTokens;
	}
	
	public String getRawMessage(){
		return Arrays.stream(messageTokens).map(MessageToken::getRawText).collect(Collectors.joining(" "));
	}
	
	public UserChatData getUser() {
		return user;
	}
	
	public Instant getTimeStamp() {
		return timeStamp;
	}

	public boolean isEmoteOnly() {
		return emoteOnly;
	}

	public boolean isUrlOnly() {
		return urlOnly;
	}

	public String getMessageId() {
		return messageId;
	}

	public String getChannelId() {
		return channelId;
	}

	public String getClient_nonce() {
		return client_nonce;
	}

	public String getReplyMessageId() {
		return replyMessageId;
	}

	public String getReplyUserName() {
		return replyUserName;
	}

	public String getChannelPointActionId() {
		return channelPointActionId;
	}
	
	public boolean isChannelPointHighlight() {
		return channelPointHighlight;
	}

	public boolean isFirstChannelMessages() {
		return firstChannelMessages;
	}
	
	
	
	
	

}
