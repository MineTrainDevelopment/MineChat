package de.minetrain.minechat.gui.obj.messages;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.databases.OwnerCacheDatabase.UserChatData;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.MineTextFlow;
import javafx.scene.paint.Color;

/**
 * 
 * @author MineTrain/Justin
 * @param userData - nullable if twitchMessage is not null - DisplayName, NameColor, BadgesSet
 * @param message - not null
 * @param timeStamp - nullable
 * @param twitchMessage - nullable
 */
public class MessageComponentContent {
	private static final Logger logger = LoggerFactory.getLogger(MessageComponentContent.class);
	private final UserChatData userData; //DisplayName, NameColor, BadgesSet - nullable if twitchMessage is not null
	private final String message; // not null
	private final Long timeStamp; //nullable
	private final TwitchMessage twitchMessage; //nullable
	
	private transient MineTextFlow messageFlow;
	private transient boolean emoteOnly = true;

	public MessageComponentContent(UserChatData userData, String message, Long timeStamp, TwitchMessage twitchMessage){
		this.userData = userData;
		this.message = message;
		this.timeStamp = timeStamp;
		this.twitchMessage = twitchMessage;
	}
	
	public MessageComponentContent(String message, TwitchMessage twitchMessage){
		this.userData = null;
		this.message = message;
		this.timeStamp = null;
		this.twitchMessage = twitchMessage;
	}
	
	public boolean isValid(){
//		return chatWindow != null && (twitchMessage == null ? userData != null : true) && message != null && !message.isBlank();
		return (twitchMessage == null ? userData != null : true) && message != null && !message.isBlank();
	}
	
	
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
	
	
	public MineTextFlow formatText(){
		if(messageFlow != null){
			System.out.println("x");
			return messageFlow;
		}

		System.out.println("NEW");
		MineTextFlow textFlow = new MineTextFlow(16d);
		messageFlow = textFlow;
		textFlow.appendString("["+getTimeStamp(this)+"] ");
		
		//Cache to prevent unnecessary CPU cycles.
		Map<String, Emote> emoteSet = getEmoteSet(); //Store to prevent the twitch message from calculating all emotes over and over.
		List<HighlightString> highlights = Settings.highlightStrings.values().stream().filter(highlight -> highlight.isAktiv()).toList();
		
		for(String word : getMessage().split(" ")){
//			if(word.contains(".") && !word.endsWith(".") && Main.isValidImageURL(word)){
//				textFlow.appendHyperLink(word);
//				continue;
//			}
//			
//			if(emoteSet.containsKey(word)){
//				textFlow.appendEmote(emoteSet.get(word));
//				textFlow.appendSpace();
//				continue;
//			}
//			
			int elements = textFlow.getChildren().size();
//
//			//This may takes to mutch time.
//			highlights.forEach(highlight -> {
//	    		Pattern pattern = Pattern.compile("\\b" + highlight.getWord() + "\\b", Pattern.CASE_INSENSITIVE);
//	            Matcher matcher = pattern.matcher(word);
//	            if (matcher.find()) {
//	    			textFlow.appendString(word+" ", highlight.getWordColor());
//	    			if(!textFlow.hasHighlight()){
//	    				textFlow.setHighlight(highlight);
//	    			}
//	    		}
//	    	});

			if(elements == textFlow.getChildren().size()){
				emoteOnly = false;
				textFlow.appendString(word+" ");
			}
		}
		
		textFlow.setStyle("-fx-padding: 0 5 0 5;");
		return textFlow;
	}
	
	public static String getTimeStamp(MessageComponentContent messageContent) {
		String pattern = Settings.messageTimeFormat;
		
		TwitchMessage message = messageContent.getTwitchMessage();
		if(message != null && message.isOlderThanHours(24)){
			String dateFormat = message.isOlderThanDays(7) ? Settings.dateFormat : Settings.dayFormat;
		    pattern = dateFormat + " | " + pattern;
		}
		
		return LocalDateTime.ofEpochSecond(messageContent.getEpochSec()/1000, 0, ZoneId.systemDefault().getRules().getOffset(Instant.now()))
			.format(DateTimeFormatter.ofPattern(pattern, new Locale(System.getProperty("user.language"), System.getProperty("user.country"))));
	}

	/**
	 * WARNING: This returns null, if the {@link TwitchMessage} is null.
	 */
	public UserChatData getUserData() {
		return userData;
	}

	public TwitchMessage getTwitchMessage() {
		return twitchMessage;
	}
	
	public String getMessage(){
		return message;
	}
	
	public boolean isEmoteOnly(){
		return emoteOnly || (twitchMessage != null ? twitchMessage.isEmoteOnly() : false);
	}
	
	
	
	
}
