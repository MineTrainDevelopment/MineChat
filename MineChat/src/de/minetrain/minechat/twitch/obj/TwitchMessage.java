package de.minetrain.minechat.twitch.obj;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.emotes.WebEmote;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.ChannelManager;
import de.minetrain.minechat.twitch.TwitchManager;

public class TwitchMessage {
	private static final Logger logger = LoggerFactory.getLogger(TwitchMessage.class);
	private static final transient Map<String, String> colorCache = new HashMap<String, String>();
	private static final transient ConcurrentHashMap<String, WebEmote> webEmoteCache = new ConcurrentHashMap<String, WebEmote>();//emote_id, emote
	
	private final String message;
	private final String messageId;
	private final String channelId;
	private final String client_nonce;

	private String replyId;
	private String replyUser;

	private final String userId;
	private final String userName;
	private final String userColorCode;
	private final String[] badgeTags;
	private final List<String> webEmotesPaths;
	
	private final Long epochTime;
	private final boolean emoteOnly;
	private final boolean highlighted;
	private final boolean firstMessages;
	private final boolean firstMessageOfInstance;

	public TwitchMessage(IRCMessageEvent ircMessage, String message) {
		this.message = message;
		this.messageId = ircMessage.getTagValue("id").orElse(">null<");
		this.channelId = ircMessage.getTagValue("room-id").orElse(">null<");
		this.userId = ircMessage.getTagValue("user-id").orElse("0");
		this.userName = ircMessage.getTagValue("display-name").orElse(">null<");
		this.client_nonce = ircMessage.getTagValue("client_nonce").orElse(">null<");
		this.epochTime = Long.parseLong(ircMessage.getTagValue("tmi-sent-ts").orElse("0"));
		this.userColorCode = ircMessage.getTagValue("color").orElse("#ffffff");
		this.replyId = ircMessage.getTagValue("reply-parent-msg-id").orElse(null);
		this.replyUser = ircMessage.getTagValue("reply-parent-display-name").orElse(null);
		this.emoteOnly = !Boolean.parseBoolean(ircMessage.getTagValue("emote-only").orElse("true"));
		this.highlighted = ircMessage.getTagValue("msg-id").orElse("false").equals("false") ? false : true;
		this.firstMessages = ircMessage.getTagValue("first-msg").orElse("0").equals("1") ? true : false;
		this.firstMessageOfInstance = ChannelManager.getChannel(channelId).getGreetingsManager().add(userName);
		
		String emotes = ircMessage.getTagValue("emotes").orElse(null);
		
		webEmotesPaths = (emotes != null ? Arrays.asList(emotes.split("/")) : List.of());
    	badgeTags = ircMessage.getTagValue("badges").orElse("").split(",");
    	
    	//TODO: Implement this in a nother location
//    	if(getUserName().equalsIgnoreCase(TwitchManager.ownerChannelName)){
//			GreetingsManager greetingsManager = ChannelManager.getChannel(channelId).getGreetingsManager();
//			mentionedUserNames.forEach(name -> greetingsManager.setMentioned(name.toLowerCase()));
//		}
	}

	private HashMap<String, WebEmote> getWebEmotes() {
//		emotesv2_5d1cdac68be9419486d3be49d78ae402:0-6,8-14,16-22,24-30,32-38
		HashMap<String, WebEmote> emoteSet = new HashMap<String, WebEmote>();
		if(webEmotesPaths != null){
			webEmotesPaths.forEach(emote -> {
				String[] emoteSplit = emote.split(":");
				String emoteId = emoteSplit[0];
				String[] emoteLocations = emoteSplit[1].split(",");
				
				if(emoteLocations != null && emoteLocations.length != 0){
					String[] emoteLocation = emoteLocations[0].split("-");
					String emoteName = message.substring(Integer.parseInt(emoteLocation[0]), Integer.parseInt(emoteLocation[1])+1);
					
					emoteSet.put(emoteName, webEmoteCache.computeIfAbsent(emote, key -> {
						try {
							return new WebEmote(emoteName, emoteId);
						} catch (MalformedURLException ex) {
							logger.debug("Can´t load web emote for -> "+emoteName, ex);
						}
						return null;
					}));
				}
			});
		}
		return emoteSet;
	}
	
	private final HashMap<String, Emote> getInstalltEmotes() {
		HashMap<String, Emote> emoteSet = new HashMap<String, Emote>();//Name, emote
		
    	Arrays.stream(message.split(" ")).parallel().forEach(word -> {
    		
			Emote emoteByName = EmoteManager.getChannelEmoteByName(channelId, word);
			if(emoteByName != null) {
				emoteSet.put(emoteByName.getName(), emoteByName);
			}else if(Settings.emoteBlendinOnDisplaying){
				emoteByName = EmoteManager.getEmoteByName(word);
				if(emoteByName != null){
					emoteSet.put(emoteByName.getName(), emoteByName);
				}
			}
			
		});
    	
    	return emoteSet;
    }

	public ArrayList<Path> getBadges() {
		ArrayList<Path> paths = new ArrayList<>();
		Arrays.asList(badgeTags).forEach(badge -> {
			Path path = Path.of(TextureManager.badgePath+badge+"/1.png");
			
			if(badge.startsWith("subscriber") || badge.startsWith("bits")){
				String channelBadgePath = TextureManager.badgePath+badge.substring(0, badge.indexOf("/"))+"/Channel_"+channelId+"";

				if(Files.exists(Paths.get(channelBadgePath))){
					path = Path.of(channelBadgePath+badge.substring(badge.indexOf("/"))+"/1.png");
				}
			}
			
			if (Files.exists(path)) {
				paths.add(path);
			}
		});
		
		return paths;
	}
	
	public boolean isOlderThanHours(int hours){
        return Duration.between(Instant.ofEpochSecond(epochTime), Instant.now()).toHours() > hours;
	}
	
	public boolean isOlderThanDays(int days){
        return Duration.between(Instant.ofEpochSecond(epochTime), Instant.now()).toDays() > days;
	}

	public String getMessage() {
		return message;
	}

	public String getMessageId() {
		return messageId;
	}

	public String getReplyId() {
		return (replyId == null) ? messageId : replyId;
	}
	
	public String getParentReplyUser() {
		return (replyUser == null) ? userName : replyUser;
	}
	
	public String getParentReplyColor() {
		return colorCache.get(getParentReplyUser().toLowerCase());
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserColorCode() {
		return userColorCode;
	}

	public Long getEpochTime() {
		return epochTime;
	}

	public String getClient_nonce() {
		return client_nonce;
	}
	
	public String getChannelId() {
		return channelId;
	}
	
	public boolean isReply() {
		return replyId != null;
	}
	
	public boolean isEmoteOnly() {
		return emoteOnly;
	}
	
	public boolean isHighlighted() {
		return highlighted;
	}
	
	public boolean isFirstMessage(){
		return firstMessages;
	}
	
	public boolean isFirstMessageOfInstance() {
		return userId.equals(TwitchManager.ownerTwitchUser.getUserId()) ? false : firstMessageOfInstance;
	}

	public boolean isParentReply() {
		return ((replyId != null && replyUser != null) && replyId != messageId) ? true : false;
	}
	
	public TwitchMessage setReply(String messageId, String userName) {
		this.replyId = messageId;
		this.replyUser = userName;
		return this;
	}
	
	public static final String getCachedColorCode(String userLogin) {
		return colorCache.get(userLogin);
	}
	
	public String[] getBadgeTags() {
		return badgeTags;
	}
	
	public String getRawBadgeTags() {
		return String.join(", ", getBadgeTags());
	}

	public HashMap<String, Emote> getEmoteSet() {
		HashMap<String, Emote> emoteSet = getInstalltEmotes();
		HashMap<String, WebEmote> webEmotes = getWebEmotes();
		webEmotes.keySet().removeAll(emoteSet.keySet());
		emoteSet.putAll(webEmotes);
		return emoteSet;
	}
	
	
	@Override
	public String toString() {
		return userName+": "+message;
	}
}
