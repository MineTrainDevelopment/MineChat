package de.minetrain.minechat.twitch.obj;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.config.enums.ReplyType;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.utils.TextureManager;

public class TwitchMessage {
	private static final String TWITCH_EMOTE_URL = "https://static-cdn.jtvnw.net/emoticons/v2/{ID}/static/dark/1.0";
	private static final Map<String, String> colorCache = new HashMap<String, String>();
	private static final Logger logger = LoggerFactory.getLogger(TwitchMessage.class);
	private final String message;
	private final String messageId;
	private final String channelId;
	private final String client_nonce;

	private String replyId;
	private String replyUser;
	private ReplyType replyType = Settings.REPLY_TYPE;
	
	private final String userName;
	private final String userColorCode;
	private final Map<String, String> emoteSet = new HashMap<String, String>();
	private final List<String> badges = new ArrayList<>();
	
	private final Long epochTime;
	private final ChannelTab parentTab;
	private final boolean dummy;

	public TwitchMessage(ChannelTab parentTab, IRCMessageEvent ircMessage, String message) {
		this.parentTab = parentTab;
		this.message = message;
		this.messageId = ircMessage.getTagValue("id").orElse(">null<");
		this.channelId = ircMessage.getTagValue("room-id").orElse(">null<");
		this.userName = ircMessage.getTagValue("display-name").orElse(">null<");
		this.client_nonce = ircMessage.getTagValue("client_nonce").orElse(">null<");
		this.epochTime = Long.parseLong(ircMessage.getTagValue("tmi-sent-ts").orElse("0"));
		this.userColorCode = ircMessage.getTagValue("color").orElse("#ffffff");
		this.replyId = ircMessage.getTagValue("reply-parent-msg-id").orElse(null);
		this.replyUser = ircMessage.getTagValue("reply-parent-display-name").orElse(null);
		this.dummy = false;

		if(EmoteManager.getChannelEmotes().containsKey(channelId)){
			emoteSet.putAll(EmoteManager.getChannelEmotes(channelId).values().stream().collect(Collectors.toMap(Emote::getName, Emote::getFilePath)));
		}
		
		emoteSet.putAll(EmoteManager.getGlobalEmotes().values().stream().collect(Collectors.toMap(Emote::getName, Emote::getFilePath)));
		colorCache.put(userName.toLowerCase(), userColorCode);
		
		String emotes = ircMessage.getTagValue("emotes").orElse(null);
		List<String> emotesPaths = (emotes != null ? Arrays.asList(emotes.split("/")) : null);

//		emotesv2_5d1cdac68be9419486d3be49d78ae402:0-6,8-14,16-22,24-30,32-38
		if(emotesPaths != null){
			emotesPaths.forEach(emote -> {
				String[] emoteSplit = emote.split(":");
				String emoteId = emoteSplit[0];
				String[] emoteLocations = emoteSplit[1].split(",");
				
				Arrays.asList(emoteLocations).forEach(s -> {
					String[] emoteLocation = s.split("-");
					String emoteName = message.substring(Integer.parseInt(emoteLocation[0]), Integer.parseInt(emoteLocation[1])+1);
					String emoteUrl = "URL%"+TWITCH_EMOTE_URL.replace("{ID}", emoteId);
					
					//Put all emotes in there, incase its not posible to get non Static twitch emotes.
					if(!emoteSet.containsKey(emoteName)){
						if(EmoteManager.CACHED_WEB_EMOTES.containsKey(emoteId)){
							emoteSet.put(emoteName, EmoteManager.CACHED_WEB_EMOTES.get(emoteId));
						}else{
							EmoteManager.CACHED_WEB_EMOTES.put(emoteId, emoteUrl);
							emoteSet.put(emoteName, emoteUrl);
						}
					}
				});
			});
		}
		
    	String[] badgeTags = ircMessage.getTagValue("badges").orElse("").split(",");
		Arrays.asList(badgeTags).forEach(badge -> {
			String path = TextureManager.badgePath+badge+"/1.png";
			
			if(badge.startsWith("subscriber") || badge.startsWith("bits")){
				String channelBadgePath = TextureManager.badgePath+badge.substring(0, badge.indexOf("/"))+"/Channel_"+ircMessage.getChannel().getId()+"";

				if(Files.exists(Paths.get(channelBadgePath))){
					path = channelBadgePath+badge.substring(badge.indexOf("/"))+"/1.png";
				}
			}
			
			if (Files.exists(Paths.get(path))) {
				getBadges().add(path);
			}
		});
	}
	
	public TwitchMessage(ChannelTab parentTab, String userName, String message) {
		this.parentTab = parentTab;
		this.message = message;
		this.messageId = null;
		this.channelId = null;
		this.userName = userName;
		this.client_nonce = null;
		this.epochTime = 0l;
		this.userColorCode = "#ffffff";
		this.replyId = null;
		this.replyUser = null;
		this.dummy = true;
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

	public String getUserName() {
		return userName;
	}

	public String getUserColorCode() {
		return userColorCode;
	}

	public Map<String, String> getEmotes() {
		return emoteSet;
	}

	public Long getEpochTime() {
		return epochTime;
	}

	public ChannelTab getParentTab() {
		return parentTab;
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
	
	public List<String> getBadges() {
		return badges;
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
