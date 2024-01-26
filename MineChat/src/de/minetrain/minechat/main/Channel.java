package de.minetrain.minechat.main;

import java.util.Arrays;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.databases.OwnerCacheDatabase.UserChatData;
import de.minetrain.minechat.data.objectdata.ChannelData;
import de.minetrain.minechat.features.macros.ChannelMacros;
import de.minetrain.minechat.features.macros.MacroObject;
import de.minetrain.minechat.features.macros.MacroType;
import de.minetrain.minechat.gui.emotes.ChannelEmotes;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.obj.messages.MessageComponentContent;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.ChannelStatistics;
import de.minetrain.minechat.twitch.obj.GreetingsManager;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import de.minetrain.minechat.utils.ChatMessage;
import de.minetrain.minechat.utils.HTMLColors;
import de.minetrain.minechat.utils.MessageHistory;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Channel {
	private final GreetingsManager greetingsManager;
	private final MessageHistory messageHistory;
	private final ChannelStatistics statistics;
	private final TwitchUserObj twitchUser;
	private final ChannelData channelData;
	private final ChannelMacros macros;

	private final String channelId;
	
	private String chatRole = "viwer";
	
	/**
	 * This needs to be replaced, and is just here, so i don´t lose track of it.
	 * <br> may move it to a channel message handler of some sort.
	 * <p> thats also the reason, this has no getter/setters and is public.
	 */
	public TwitchMessage replyMessage = null;
		
	public Channel(String channelId) {
		this.channelId = channelId;
		this.twitchUser = TwitchManager.getTwitchUser(TwitchApiCallType.ID, channelId);
		this.statistics = new ChannelStatistics(channelId);
		this.greetingsManager = new GreetingsManager();
		this.messageHistory = new MessageHistory();
		this.macros = new ChannelMacros(channelId);

//		macros.createMacro(new MacroObject(MacroType.TEXT, "emotesv2_6cc7fdb3cca74bdc80c49f4199b6d001", 00, "Test 1", "Macro-V2 | test_1".split("q")));
//		macros.createMacro(new MacroObject(MacroType.TEXT, "emotesv2_2f6e7f957a37440e92fc33c66be7c0c2", 10, "Test 2", "Macro-V2 | test_2".split("q")));
//		macros.createMacro(new MacroObject(MacroType.EMOTE, "emotesv2_392517b42c324d0f867b122a6bba1d9f", 20, "Test 3", "Macro-V2 | test_3".split("q")));
//
//		macros.createMacro(new MacroObject(MacroType.TEXT, "emotesv2_a164fa4a5298492f8850ae43bbda2bc4", 01, "Test 4", "Macro-V2 | test_4".split("q")));
//		macros.createMacro(new MacroObject(MacroType.TEXT, "612f819daf28e956864b54dd", 11, "Test 5", "Macro-V2 | test_5".split("q")));
//		macros.createMacro(new MacroObject(MacroType.EMOTE, "emotesv2_662fe5cfd480497f98bd3ec7b953817a", 21, "Test 6", "Macro-V2 | test_6".split("q")));
		
		channelData = DatabaseManager.getChannel().getChannelById(channelId);
	}
	

	public void displayMessage(TwitchMessage message){
		new MessageComponentContent(
				null,
				message.getMessage(), 
				null,
				message);
	}
	
	public void displayMessage(ChatMessage message){
		UserChatData ownerData = DatabaseManager.getOwnerCache().getById(channelId);
		
		if(ownerData == null){
			ownerData = new UserChatData(channelId, HTMLColors.WHITE.getColorCode(), message.getSenderName(), "");
		}
		
		getStatistics().addMessage(message.getSenderName(), channelId, message.getMessage());
		getMessageHistory().addSendedMessages(message.getMessageRaw());
		
		Arrays.stream(message.getMessage().split(" ")).parallel().forEach(word -> {
			if(word.startsWith("@") && word.length() > 1){
				greetingsManager.setMentioned(word.replace("@", ""));
			}
		});
		
		new MessageComponentContent(
				ownerData,
				((replyMessage != null) ? "@" + replyMessage.getParentReplyUser() + " " : "")+ message.getMessage(),
				null,
				replyMessage);
	}
	
	public Rectangle getProfilePic(int size) {
		Rectangle profilePic = new Rectangle(0, 0, size, size);
//        profilePic.setId("tab-profile-image");
        profilePic.setId("macro-key-image");

        ImagePattern pattern = new ImagePattern(getProfileImage(size));

        profilePic.setFill(pattern);
		return profilePic;
	}


	public Image getProfileImage(int size) {
		return new Image(twitchUser.getProfileImageUrl(), size, size, false, false);
	}
	
	
	public ChannelStatistics getStatistics() {
		return statistics;
	}
	
	public GreetingsManager getGreetingsManager() {
		return greetingsManager;
	}
	
	public MessageHistory getMessageHistory() {
		return messageHistory;
	}
	
	public boolean isModerator() {
		return chatRole.equalsIgnoreCase("moderator");
	}
	
	public String getChannelId() {
		return channelId;
	}
	
	public ChannelData getChannelData() {
		return channelData;
	}
	
	public ChannelMacros getMacros() {
		return macros;
	}

	/**
	 * @return may be null, if no emotes are installed for the user.
	 */
	public ChannelEmotes getChannelEmotes(){
		return EmoteManager.getChannelEmotes(channelId);
	}
}
