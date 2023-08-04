package de.minetrain.minechat.config;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.enums.ReplyType;
import de.minetrain.minechat.config.enums.UndoVariation;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.utils.obj.HighlightDefault;
import de.minetrain.minechat.utils.obj.HighlightGiftSubs;
import de.minetrain.minechat.utils.obj.HighlightString;

public class Settings{
	private static final Logger logger = LoggerFactory.getLogger(Settings.class);
//	https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns
//	public static String messageTimeFormat = "dd.MM.yyy | HH:mm:ss";
	public static YamlManager settings;
	
	public static String messageTimeFormat; //
	public static String timeFormat; //
	public static String dateFormat; //
	public static String dayFormat; //
	public static List<HighlightString> highlightStrings = new ArrayList<HighlightString>();
	
	public static HighlightDefault highlightUserFirstMessages; //
	public static HighlightDefault highlightUserGoodbyeMessages; 
	public static boolean highlightKeywords;
	
	public static HighlightDefault displayModActions; //
	public static HighlightDefault displayFollows; //
	public static HighlightDefault displaySubs; //
	public static HighlightGiftSubs displayGiftedSubs; //
	public static HighlightDefault displayIndividualGiftedSubs; //
	public static HighlightDefault displayBitsCheerd; //
	public static HighlightDefault displayAnnouncement; //
	public static HighlightDefault displayUserRewards; //

	public static long MAX_MESSAGE_DISPLAYING;
	public static ReplyType GREETING_TYPE; //
	public static ReplyType REPLY_TYPE; //

	public static UndoVariation UNDO_VARIATION;
	public static long MAX_UNDO_LOG_SIZE;

	public static Font MESSAGE_FONT;
	
	public Settings() {
		loadSettings();
	}
	
	public static void loadSettings() {
		File file = new File("data/Settings.yml");
		if(file.isFile()) { 
			settings = new YamlManager("data/Settings.yml");
		}else{
			settings = createNewConfig("data/Settings.yml");
		}
		
		messageTimeFormat = settings.getString("Variables.MessageTime", "HH:mm");
		timeFormat = settings.getString("Variables.TimeFormat", "HH:mm");
		dateFormat = settings.getString("Variables.DateFormat", "dd:MM:yyyy");
		dayFormat = settings.getString("Variables.DayFormat", "eeee");
		highlightStrings = new ArrayList<HighlightString>();
		
		highlightUserFirstMessages = new HighlightDefault(settings, "Highlights.MessageHighlights.FirstMessage");
		highlightUserGoodbyeMessages = new HighlightDefault(settings, "Highlights.MessageHighlights.GoodByeMessage");
		highlightKeywords = settings.getBoolean("Highlights.MessageHighlights.KeyWods.Active");
		
		displayModActions = new HighlightDefault(settings, "Highlights.EventHighlights.Moderaion");
		displaySubs = new HighlightDefault(settings, "Highlights.EventHighlights.Subs");
		displayFollows = new HighlightDefault(settings, "Highlights.EventHighlights.Follows");
		displayGiftedSubs = new HighlightGiftSubs(settings, "Highlights.EventHighlights.GiftSubs");
		displayIndividualGiftedSubs = new HighlightDefault(settings, "Highlights.EventHighlights.IndividualGiftSubs");
		displayBitsCheerd = new HighlightDefault(settings, "Highlights.EventHighlights.BitsCheerd");
		displayAnnouncement = new HighlightDefault(settings, "Highlights.EventHighlights.ModAnnouncement");
		displayUserRewards = new HighlightDefault(settings, "Highlights.EventHighlights.UserRewards");

		MAX_MESSAGE_DISPLAYING = settings.getInt("Chatting.MaxMessageDisplaying", 500);
		GREETING_TYPE = ReplyType.get(settings.getString("Chatting.GreetingType", "MESSAGE"));
		REPLY_TYPE = ReplyType.get(settings.getString("Chatting.ReplyType", "ReplyType"));

		UNDO_VARIATION = UndoVariation.get(settings.getString("Chatting.UndoMode", "WORD"));
		MAX_UNDO_LOG_SIZE = settings.getInt("Chatting.UndoCacheSize", 100);
		
		MESSAGE_FONT = new Font(
				settings.getString("Font.Name", "Arial Unicode MS"), 
				settings.getInt("Font.Style", 1), 
				settings.getInt("Font.Size", 17));
		
		reloadHighlights();
		new ColorManager(settings);
	}
	
	public static void reloadHighlights(){
		settings.getStringList("Highlights.MessageHighlights.KeyWods.List").forEach(highlight -> highlightStrings.add(new HighlightString(highlight)));
		if(highlightStrings.isEmpty()){
			HighlightString.saveNewWord(TwitchManager.ownerChannelName, ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT, ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT);
		}
	}

	
	
	public static void setMessageTimeFormat(String newMessageTimeFormat) {
		settings.setString("Variables.MessageTime", newMessageTimeFormat, true);
		messageTimeFormat = newMessageTimeFormat;
	}

	public static void setTimeFormat(String newTimeFormat) {
		settings.setString("Variables.TimeFormat", newTimeFormat, true);
		timeFormat = newTimeFormat;
	}

	public static void setDateFormat(String newDateFormat) {
		//set to config
		settings.setString("Variables.DateFormat", newDateFormat, true);
		dateFormat = newDateFormat;
	}

	public static void setDayFormat(String newDayFormat) {
		settings.setString("Variables.DayFormat", newDayFormat, true);
		dayFormat = newDayFormat;
	}
	
	
	private static YamlManager createNewConfig(String path){
		logger.warn("Create new Settings file!");
		YamlManager settings = new YamlManager(path);
		settings.setString("Colors.GUI.Font", ColorManager.encode(ColorManager.FONT_DEFAULT));
		settings.setString("Colors.GUI.Background", ColorManager.encode(ColorManager.GUI_BACKGROUND_DEFAULT));
		settings.setString("Colors.GUI.BackgroundLight", ColorManager.encode(ColorManager.GUI_BACKGROUND_LIGHT_DEFAULT));
		settings.setString("Colors.GUI.Border", ColorManager.encode(ColorManager.GUI_BORDER_DEFAULT));
		settings.setString("Colors.GUI.ButtonBackground", ColorManager.encode(ColorManager.GUI_BUTTON_BACKGROUND_DEFAULT));
		settings.setString("Colors.GUI.DefaultKeyHighlight", ColorManager.encode(ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT_DEFAULT));
		

		settings.setBoolean("Highlights.MessageHighlights.KeyWods.Active", true);
		settings.setStringList("Highlights.MessageHighlights.KeyWods.List", new ArrayList<String>(), false);
		
		settings.setBoolean("Highlights.MessageHighlights.FirstMessage.Active", true);
		settings.setString("Highlights.MessageHighlights.FirstMessage.Color", ColorManager.encode(ColorManager.CHAT_MESSAGE_GREETING_HIGHLIGHT_DEFAULT));
		
		settings.setBoolean("Highlights.MessageHighlights.GoodByeMessage.Active", false);
		settings.setString("Highlights.MessageHighlights.GoodByeMessage.Color", ColorManager.encode(ColorManager.CHAT_MESSAGE_GREETING_HIGHLIGHT_DEFAULT));
		
		settings.setBoolean("Highlights.EventHighlights.Moderaion.Active", true);
		settings.setString("Highlights.EventHighlights.Moderaion.Color", ColorManager.encode(ColorManager.CHAT_MODERATION_DEFAULT));

		settings.setBoolean("Highlights.EventHighlights.Subs.Active", true);
		settings.setString("Highlights.EventHighlights.Subs.Color", ColorManager.encode(ColorManager.CHAT_UNIMPORTANT_DEFAULT));

		settings.setBoolean("Highlights.EventHighlights.GiftSubs.Active", true);
		settings.setString("Highlights.EventHighlights.GiftSubs.ColorSmall", ColorManager.encode(ColorManager.CHAT_SPENDING_SMALL_DEFAULT));
		settings.setString("Highlights.EventHighlights.GiftSubs.ColorBig", ColorManager.encode(ColorManager.CHAT_SPENDING_BIG_DEFAULT));

		settings.setBoolean("Highlights.EventHighlights.IndividualGiftSubs.Active", true);
		settings.setString("Highlights.EventHighlights.IndividualGiftSubs.Color", ColorManager.encode(ColorManager.CHAT_UNIMPORTANT_DEFAULT));

		settings.setBoolean("Highlights.EventHighlights.Follows.Active", true);
		settings.setString("Highlights.EventHighlights.Follows.Color", ColorManager.encode(ColorManager.CHAT_UNIMPORTANT_DEFAULT));

		settings.setBoolean("Highlights.EventHighlights.BitsCheerd.Active", true);
		settings.setString("Highlights.EventHighlights.BitsCheerd.Color", ColorManager.encode(ColorManager.CHAT_SPENDING_SMALL_DEFAULT));

		settings.setBoolean("Highlights.EventHighlights.ModAnnouncement.Active", true);
		settings.setString("Highlights.EventHighlights.ModAnnouncement.Color", ColorManager.encode(ColorManager.CHAT_ANNOUNCEMENT_DEFAULT));

		settings.setBoolean("Highlights.EventHighlights.UserRewards.Active", true);
		settings.setString("Highlights.EventHighlights.UserRewards.Color", ColorManager.encode(ColorManager.CHAT_USER_REWARD_DEFAULT));
		


		settings.setString("Variables.MessageTime", "HH:mm");
		settings.setString("Variables.TimeFormat", "HH:mm");
		settings.setString("Variables.DateFormat", "dd.MM.yyyy");
		settings.setString("Variables.DayFormat", "eeee");
		
		

		settings.setNumber("Chatting.MaxMessageDisplaying", 500);
		settings.setString("Chatting.GreetingType", ReplyType.MESSAGE.name());
		settings.setString("Chatting.ReplyType", ReplyType.THREAD.name());
		settings.setString("Chatting.UndoMode", UndoVariation.WORD.name());
		settings.setNumber("Chatting.UndoCacheSize", 100);
		
		

		settings.setString("Font.Name", "Arial Unicode MS");
		settings.setNumber("Font.Style", 1);
		settings.setNumber("Font.Size", 17);
		
		settings.saveConfigToFile();
		return settings;
	}

	
//	CustomiseTimeFormatFrame test = new CustomiseTimeFormatFrame(onboardingFrame, "Title", "HH:ss");
//  System.out.println(test.getCurentInput());
	
//  new AddWordHighlightFrame();
}
