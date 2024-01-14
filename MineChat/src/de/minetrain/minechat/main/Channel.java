package de.minetrain.minechat.main;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import de.minetrain.minechat.config.obj.ChannelMacros;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.databases.OwnerCacheDatabase.UserChatData;
import de.minetrain.minechat.data.objectdata.ChannelData;
import de.minetrain.minechat.gui.obj.messages.MessageComponentContent;
import de.minetrain.minechat.gui.utils.TextureManager;
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
	private final ChannelData channelData;
	private final ChannelMacros macros;

	private final String channelId;
	
	private final List<String> greetingTexts;
	private final List<String> goodByTexts;
	private final List<String> returnTexts;
	private String chatRole = "viwer";
	
	/**
	 * This needs to be replaced, and is just here, so i don´t lose track of it.
	 * <br> may move it to a channel message handler of some sort.
	 * <p> thats also the reason, this has no getter/setters and is public.
	 */
	public TwitchMessage replyMessage = null;
		
	public Channel(String channelId) {
		this.channelId = channelId;
		this.statistics = new ChannelStatistics(channelId);
		this.greetingsManager = new GreetingsManager();
		this.messageHistory = new MessageHistory();
		this.macros = null;
		this.greetingTexts = null;
		this.goodByTexts = null;
		this.returnTexts = null;
		
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
        profilePic.setId("rounded-image");

        ImagePattern pattern = new ImagePattern(getProfileImage(size));

        profilePic.setFill(pattern);
		return profilePic;
	}


	public Image getProfileImage(int size) {
		return new Image("file:"+TextureManager.profilePicPath.replace("{ID}", getChannelId()).replace("_{SIZE}", ""), size, size, false, false);
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
}
