package de.minetrain.minechat.utils;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import de.minetrain.minechat.gui.obj.chat.userinput.textarea.UndoManager.UndoVariation;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.utils.obj.HighlightString;

public class Settings {
//	https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns
//	public static final String messageTimeFormat = "dd.MM.yyy | HH:mm:ss";
	public static final String messageTimeFormat = "HH:mm";
	public static final String timeFormat = "HH:mm";
	public static final String dateFormat = "dd.MM.yyy";
	public static final String dayFormat = "eeee";
	public static final List<HighlightString> highlightStrings = new ArrayList<HighlightString>();
	
	public static final boolean highlightChatMessages = true;
	public static final boolean dhighlightUserFirstMessages = true;
	public static final boolean highlightUserGoodbyeMessages = true;
	public static final boolean highlightKeywords = true;
	
	public static final boolean displayModActions = true;
	public static final boolean displaySubs_Follows = true;
	public static final boolean displayGiftedSubscriptions = true;
	public static final boolean displayBitsCheerd = true;
	public static final boolean displayAnnouncement = true;
	public static final boolean displayUserRewards = true;

	public static final long MAX_MESSAGE_DISPLAYING = 500;
	public static final ReplyType GREETING_TYPE = ReplyType.MESSAGE;
	public static final ReplyType REPLY_TYPE = ReplyType.THREAD_PARENT;

	public static final UndoVariation UNDO_VARIATION = UndoVariation.WORD;
	public static final long MAX_UNDO_LOG_SIZE = 100;
	
	public static final Font MESSAGE_FONT = new Font("Arial Unicode MS", Font.BOLD, 17);
	
	public static void reloadHighlights(){
		Main.CONFIG.getStringList("Highlights").forEach(highlight -> highlightStrings.add(new HighlightString(highlight)));
		if(highlightStrings.isEmpty()){
			HighlightString.saveNewWord(TwitchManager.ownerChannelName, ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT, ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT);
		}
	}
	
	public enum ReplyType{MESSAGE, THREAD_PARENT, USER_NAME} //message might be debricatet from twitch side.
}
