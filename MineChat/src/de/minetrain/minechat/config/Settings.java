package de.minetrain.minechat.config;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.enums.AutoReplyState;
import de.minetrain.minechat.config.enums.ReplyType;
import de.minetrain.minechat.config.enums.UndoVariation;
import de.minetrain.minechat.gui.utils.ColorManager;

public class Settings{
	private static final Logger logger = LoggerFactory.getLogger(Settings.class);
//	https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns
//	public static String messageTimeFormat = "dd.MM.yyy | HH:mm:ss";
	public static YamlManager settings;

	public static String messageTimeFormat; //
	public static String timeFormat; //
	public static String dateFormat; //
	public static String dayFormat; //
	/**keyWord, obj*/

	public static long highlightUserReturnThreshold;
	public static boolean highlightKeywords;
	public static boolean displayEmoteOnly;

	public static int MAX_MESSAGE_DISPLAYING;
	public static ReplyType GREETING_TYPE; //
	public static ReplyType REPLY_TYPE; //

	public static UndoVariation UNDO_VARIATION;
	public static int MAX_UNDO_LOG_SIZE;

	public static AutoReplyState autoReplyState;

	public static boolean holdToSendMessages;
	public static boolean emoteBlendinOnDisplaying;


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

		highlightUserReturnThreshold = settings.getLong("Highlights.MessageHighlights.ReturnMessage.ThresholdSeconds", 3600);
		highlightKeywords = settings.getBoolean("Highlights.MessageHighlights.KeyWods.Active");

		MAX_MESSAGE_DISPLAYING = settings.getInt("Chatting.MaxMessageDisplaying", 500);
		GREETING_TYPE = ReplyType.get(settings.getString("Chatting.GreetingType", "MESSAGE"));
		REPLY_TYPE = ReplyType.get(settings.getString("Chatting.ReplyType", "ReplyType"));
		UNDO_VARIATION = UndoVariation.get(settings.getString("Chatting.UndoMode", "WORD"));
		MAX_UNDO_LOG_SIZE = settings.getInt("Chatting.UndoCacheSize", 100);
		autoReplyState = AutoReplyState.get(settings.getString("Chatting.AutoReplyState", "ALL"));
		holdToSendMessages = settings.getBoolean("Chatting.HoltToSendMessages", true);
		emoteBlendinOnDisplaying = settings.getBoolean("Chatting.emoteBlendinOnDisplaying", false);
		displayEmoteOnly = settings.getBoolean("Chatting.DisplayEmoteOnly", true);

		MESSAGE_FONT = new Font(
				settings.getString("Font.Name", "Arial Unicode MS"),
				settings.getInt("Font.Style", 1),
				settings.getInt("Font.Size", 17));

		new ColorManager(settings);
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

	public static void setAutoReplyState(AutoReplyState state){
		settings.setString("Chatting.AutoReplyState", state.name(), true);
		autoReplyState = state;
	}

	public static void setHoltToSendMessages(boolean state){
		settings.setBoolean("Chatting.HoltToSendMessages",  state, true);
		holdToSendMessages = state;
	}

	public static void setEmoteBlendinOnDisplaying(boolean state){
		settings.setBoolean("Chatting.emoteBlendinOnDisplaying",  state, true);
		emoteBlendinOnDisplaying = state;
//		MessageComponent.clearDocumentCache();
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
		settings.setStringList("Highlights.MessageHighlights.KeyWods.List", new ArrayList<>(), false);

		settings.setString("Variables.MessageTime", "HH:mm");
		settings.setString("Variables.TimeFormat", "HH:mm");
		settings.setString("Variables.DateFormat", "dd.MM.yyyy");
		settings.setString("Variables.DayFormat", "eeee");

		settings.setNumber("Chatting.MaxMessageDisplaying", 500);
		settings.setString("Chatting.GreetingType", ReplyType.MESSAGE.name());
		settings.setString("Chatting.ReplyType", ReplyType.THREAD.name());
		settings.setString("Chatting.UndoMode", UndoVariation.WORD.name());
		settings.setNumber("Chatting.UndoCacheSize", 100);
		settings.setString("Chatting.AutoReplyState", AutoReplyState.ALL.name());
		settings.setBoolean("Chatting.HoltToSendMessages", true);
		settings.setBoolean("Chatting.emoteBlendinOnDisplaying", false);
		settings.setBoolean("Chatting.DisplayEmoteOnly", true);

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
