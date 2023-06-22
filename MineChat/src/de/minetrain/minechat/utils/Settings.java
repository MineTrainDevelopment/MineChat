package de.minetrain.minechat.utils;

import java.util.ArrayList;
import java.util.List;

public class Settings {
//	https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns
//	public static final String messageTimeFormat = "dd.MM.yyy | HH:mm:ss";
	public static final String messageTimeFormat = "HH:mm";
	public static final String timeFormat = "HH:mm";
	public static final String dateFormat = "dd.MM.yyy";
	public static final String dayFormat = "eeee";
	public static final List<String> highlightStrings = new ArrayList<String>();
	
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
	
	public Settings() {
		highlightStrings.add("minetrainlp");
		highlightStrings.add("minetrain");
		highlightStrings.add("mine");
	}
	
	public enum ReplyType{MESSAGE, THREAD_PARENT, USER_NAME} //message might be debricatet from twitch side.
}
