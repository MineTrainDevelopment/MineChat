package de.minetrain.minechat.twitch.obj;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.config.enums.ReplyType;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.emotes.WebEmote;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.ChannelManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.TwitchManager;

public class TwitchMessage {
	private static final Map<String, String> colorCache = new HashMap<String, String>();
	private static final Logger logger = LoggerFactory.getLogger(TwitchMessage.class);
	private final String message;
	private final String messageId;
	private final String channelId;
	private final String client_nonce;

	private String replyId;
	private String replyUser;
	private ReplyType replyType = Settings.REPLY_TYPE;

	private final String userId;
	private final String userName;
	private final String userColorCode;
	private final List<Emote> emoteSet = new ArrayList<Emote>();
	private final List<Path> badges = new ArrayList<>();
	private final List<String> mentionedUserNames = new ArrayList<String>();
	private final ConcurrentHashMap<String, String> hyperLinkTranslations = new ConcurrentHashMap<String, String>(); //URL, domain
	private final ConcurrentHashMap<String, HighlightString> highlightStrings = new ConcurrentHashMap<String, HighlightString>();//word, highlite
	private final ConcurrentHashMap<String, WebEmote> webEmoteCache = new ConcurrentHashMap<String, WebEmote>();//emote_id, emote
	private final String[] badgeTags;
	
	private final Long epochTime;
	private final boolean emoteOnly;
	private final boolean highlighted;
	private final boolean dummy;
	private final boolean firstMessages;

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
		this.dummy = false;
		
		String emotes = ircMessage.getTagValue("emotes").orElse(null);
		List<String> emotesPaths = (emotes != null ? Arrays.asList(emotes.split("/")) : List.of());
		emoteSet.addAll(collectEmotes(message, emotesPaths));
		
    	badgeTags = ircMessage.getTagValue("badges").orElse("").split(",");
		badges.addAll(collectBadges(ircMessage.getChannel().getId(), badgeTags));
		
		collectMessageComponents(message);
		
		if(ircMessage.getUserName().equals(TwitchManager.ownerChannelName)){
			GreetingsManager greetingsManager = ChannelManager.getChannel(channelId).getGreetingsManager();
			mentionedUserNames.forEach(name -> greetingsManager.setMentioned(name.toLowerCase()));
		}
	}

	private List<WebEmote> collectEmotes(String message, List<String> emotesPaths) {
//		emotesv2_5d1cdac68be9419486d3be49d78ae402:0-6,8-14,16-22,24-30,32-38
		List<WebEmote> emoteSet = new ArrayList<WebEmote>();
		if(emotesPaths != null){
			emotesPaths.forEach(emote -> {
				String[] emoteSplit = emote.split(":");
				String emoteId = emoteSplit[0];
				String[] emoteLocations = emoteSplit[1].split(",");
				
				Arrays.asList(emoteLocations).forEach(s -> {
					String[] emoteLocation = s.split("-");
					String emoteName = message.substring(Integer.parseInt(emoteLocation[0]), Integer.parseInt(emoteLocation[1])+1);
					
					emoteSet.add(webEmoteCache.computeIfAbsent(emote, key -> {
						try {
							return new WebEmote(emoteName, emoteId);
						} catch (MalformedURLException ex) {
							logger.debug("Can´t load web emote for -> "+emoteName, ex);
						}
						return null;
					}));
				});
			});
		}
		return emoteSet;
	}

	public static ArrayList<Path> collectBadges(String channel_id, String[] badgeTags) {
		ArrayList<Path> paths = new ArrayList<>();
		Arrays.asList(badgeTags).forEach(badge -> {
			Path path = Path.of(TextureManager.badgePath+badge+"/1.png");
			
			if(badge.startsWith("subscriber") || badge.startsWith("bits")){
				String channelBadgePath = TextureManager.badgePath+badge.substring(0, badge.indexOf("/"))+"/Channel_"+channel_id+"";

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
	
	private final void collectMessageComponents(String message) {
    	Arrays.stream(message.split(" ")).parallel().forEach(word -> {
    		
    		if(Settings.highlightKeywords){
    			Settings.highlightStrings.values().stream().filter(highlight -> highlight.isAktiv()).forEach(highlight -> {
    				Pattern pattern = Pattern.compile("\\b" + highlight.getWord() + "\\b", Pattern.CASE_INSENSITIVE);
    				Matcher matcher = pattern.matcher(word);
    				if (matcher.find()) {
    					highlightStrings.put(word, highlight);
    				}
    			});
    		}
			
			Emote emoteByName = EmoteManager.getChannelEmoteByName(channelId, word);
			if(emoteByName != null) {
				emoteSet.add(emoteByName);
			}else if(Settings.emoteBlendinOnDisplaying){
				emoteByName = EmoteManager.getEmoteByName(word);
				if(emoteByName != null){
					emoteSet.add(emoteByName);
				}
			}
			
			if(word.contains(".") && Main.isValidURL(word)){
				hyperLinkTranslations.put(word, Main.extractDomain(word));
			}
			
			if(word.startsWith("@") && word.length() > 1){
				mentionedUserNames.add(word.replace("@", ""));
			}
		});
    }
	
	public TwitchMessage(String userName, String message) {
		this.message = message;
		this.messageId = null;
		this.channelId = null;
		this.userId = "0";
		this.userName = userName;
		this.client_nonce = null;
		this.epochTime = 0l;
		this.userColorCode = "#ffffff";
		this.replyId = null;
		this.replyUser = null;
		this.dummy = true;
		this.highlighted = false;
		this.emoteOnly = false;
		this.firstMessages = false;
		this.badgeTags = "".split("");
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

	public List<Emote> getEmotes() {
		return emoteSet;
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
	
	public boolean isNull() {
		return dummy;
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
	
	public List<Path> getBadges() {
		return badges;
	}
	
	public String[] getBadgeTags() {
		return badgeTags;
	}
	
	public String getRawBadgeTags() {
		return String.join(", ", getBadgeTags());
	}

	public ReplyType getReplyType() {
		return replyType;
	}

	public void setReplyType(ReplyType replyType) {
		this.replyType = replyType;
	}
	
	@Override
	public String toString() {
		return userName+": "+message;
	}
}
