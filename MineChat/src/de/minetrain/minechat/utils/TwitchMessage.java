package de.minetrain.minechat.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.obj.TwitchEmote;
import de.minetrain.minechat.gui.obj.ChannelTab;

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
	
	private final String userName;
	private final String userColorCode;
	private final Map<String, String> emoteSet = new HashMap<String, String>();
	
	private final Long epochTime;
	private final ChannelTab parentTab;
	private final boolean dummy;

	public TwitchMessage(ChannelTab parentTab, Map<String, String> data, String message) {
		this.parentTab = parentTab;
		this.message = message;
		this.messageId = data.get("id");
		this.channelId = data.get("room-id");
		this.userName = data.get("display-name");
		this.client_nonce = data.get("client_nonce");
		this.epochTime = Long.parseLong(data.get("tmi-sent-ts"));
		this.userColorCode = (data.get("color") != null ? data.get("color") : "#ffffff");
		this.replyId = (data.containsKey("reply-parent-msg-id") ? data.get("reply-parent-msg-id") : null);
		this.replyUser = (data.containsKey("reply-parent-display-name") ? data.get("reply-parent-display-name") : null);
		this.dummy = false;
		
		emoteSet.putAll(TwitchEmote.getEmotesByName());
		colorCache.put(userName.toLowerCase(), userColorCode);
		List<String> emotesPaths = (data.get("emotes") != null ? Arrays.asList(data.get("emotes").split("/")) : null);

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

					if(TwitchEmote.getEmotesByName().containsKey(emoteName)){
						emoteSet.put(emoteName, TwitchEmote.getEmotesByName().get(emoteName));
					}else if(TwitchEmote.CACHED_WEB_EMOTES.containsKey(emoteName)){
						emoteSet.put(emoteName, TwitchEmote.CACHED_WEB_EMOTES.get(emoteName));
					}else{
						TwitchEmote.CACHED_WEB_EMOTES.put(emoteName, emoteUrl);
						emoteSet.put(emoteName, emoteUrl);
					}
				});
			});
		}
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
	
	@Override
	public String toString() {
		return userName+": "+message;
	}
}
