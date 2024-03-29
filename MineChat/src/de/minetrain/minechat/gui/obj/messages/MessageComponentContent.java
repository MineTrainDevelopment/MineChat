package de.minetrain.minechat.gui.obj.messages;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.databases.OwnerCacheDatabase.UserChatData;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import javafx.scene.paint.Color;

/**
 * 
 * @author MineTrain/Justin
 * @param userData - nullable if twitchMessage is not null - DisplayName, NameColor, BadgesSet
 * @param message - not null
 * @param timeStamp - nullable
 * @param twitchMessage - nullable
 */
public record MessageComponentContent(
		UserChatData userData, //DisplayName, NameColor, BadgesSet - nullable if twitchMessage is not null
		String message, // not null
		Long timeStamp, //nullable
		TwitchMessage twitchMessage //nullable
		){
	
	private static final Logger logger = LoggerFactory.getLogger(MessageComponentContent.class);
	
	
	public boolean isValid(){
//		return chatWindow != null && (twitchMessage == null ? userData != null : true) && message != null && !message.isBlank();
		return (twitchMessage == null ? userData != null : true) && message != null && !message.isBlank();
	}
	
//	public String getChannelId(){
//		return chatWindow.getChannelId();
//	}
	
	public String getUserName(){
		return userData != null ? userData.displa_name() : twitchMessage.getUserName();
	}
	
	public Color getUserColor(){
		return ColorManager.decode(userData != null ? userData.color_code() : twitchMessage.getUserColorCode(), ColorManager.encode(ColorManager.GUI_BACKGROUND));
	}
	
	/**
	 * Adjust colors based on background color.
	 * @return
	 */
	public String getUserColorCode(){
		return ColorManager.adjustHexcode(userData != null ? userData.color_code() : twitchMessage.getUserColorCode(), ColorManager.encode(ColorManager.GUI_BACKGROUND));
	}
	
	public String[] getBadges(){
		return userData != null ? userData.badges().split(", ") : twitchMessage.getBadgeTags();
	}
	
	public Long getEpochSec(){
		return twitchMessage != null ? twitchMessage.getEpochTime() : timeStamp;
	}
	
	public String getMessage(){
		return message;
	}
	
	public LocalDateTime getTimeStamp(){
		Long epochSec = getEpochSec();
		return epochSec != null
			? LocalDateTime.ofEpochSecond(epochSec / 1000, 0, ZoneId.systemDefault().getRules().getOffset(Instant.now()))
			: LocalDateTime.now();
	}
	
	/**
	 * @return All public emotes should twitchmessage be null.
	 */
	public Map<String, Emote> getEmoteSet(){
		if(twitchMessage == null){
			return EmoteManager.getPublicEmotes();
		}
		
		return twitchMessage.getEmoteSet();
	}
	
	public boolean isEmoteOnly(){
		return twitchMessage != null ? twitchMessage.isEmoteOnly() : false;
	}
	
	/**
	 * WARNING: This returns null, if the {@link TwitchMessage} is null.
	 */
	@Override
	public UserChatData userData(){
		return userData;
		
	}
	
}
