package de.minetrain.minechat.main;

import java.util.Arrays;
import java.util.Objects;

import com.github.twitch4j.eventsub.events.ChannelChatMessageEvent;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.databases.OwnerCacheDatabase.UserChatData;
import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.Channel;
import de.minetrain.minechat.features.macros.ChannelMacros;
import de.minetrain.minechat.gui.emotes.ChannelEmotes;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.obj.messages.MessageComponent;
import de.minetrain.minechat.gui.obj.messages.MessageComponentContent;
import de.minetrain.minechat.twitch.TwitchHelper;
import de.minetrain.minechat.twitch.obj.ChannelStatistics;
import de.minetrain.minechat.twitch.obj.GreetingsManager;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import de.minetrain.minechat.utils.ChatMessage;
import de.minetrain.minechat.utils.HTMLColors;
import de.minetrain.minechat.utils.MessageHistory;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class ChannelActions {
	private final GreetingsManager greetingsManager;
	private final MessageHistory messageHistory;
	private final TwitchUserObj twitchUser;
	private final ChannelMacros macros;

	private final Channel channel;

	private String chatRole = "viwer";


	/**
	 * This needs to be replaced, and is just here, so i don�t lose track of it.
	 * <br> may move it to a channel message handler of some sort.
	 * <p> thats also the reason, this has no getter/setters and is public.
	 */
	public TwitchMessage replyMessage = null;

	public ChannelActions(Channel channel) {
		this.channel = channel;
		this.twitchUser = TwitchHelper.requestTwitchUser(TwitchApiCallType.ID, channel.getChannelId()).join();
		this.greetingsManager = new GreetingsManager();
		this.messageHistory = new MessageHistory();
		this.macros = new ChannelMacros(channel.getChannelId());

//		macros.createMacro(new MacroObject(MacroType.TEXT, "emotesv2_6cc7fdb3cca74bdc80c49f4199b6d001", 00, "Test 1", "Macro-V2 | test_1".split("q")));
//		macros.createMacro(new MacroObject(MacroType.TEXT, "emotesv2_2f6e7f957a37440e92fc33c66be7c0c2", 10, "Test 2", "Macro-V2 | test_2".split("q")));
//		macros.createMacro(new MacroObject(MacroType.EMOTE, "emotesv2_392517b42c324d0f867b122a6bba1d9f", 20, "Test 3", "Macro-V2 | test_3".split("q")));
//
//		macros.createMacro(new MacroObject(MacroType.TEXT, "emotesv2_a164fa4a5298492f8850ae43bbda2bc4", 01, "Test 4", "Macro-V2 | test_4".split("q")));
//		macros.createMacro(new MacroObject(MacroType.TEXT, "612f819daf28e956864b54dd", 11, "Test 5", "Macro-V2 | test_5".split("q")));
//		macros.createMacro(new MacroObject(MacroType.EMOTE, "emotesv2_662fe5cfd480497f98bd3ec7b953817a", 21, "Test 6", "Macro-V2 | test_6".split("q")));

//		twitchUser.join(); // Zocki disabled...
	}

	public void displayMessage(TwitchMessage message, ChannelChatMessageEvent event){
		MessageComponentContent messageComponentContent = new MessageComponentContent(
				null,
				message.getMessage(),
				null,
				message);

		EclipseStoreKeeper.root().addMessage(channel.getChannelId(), messageComponentContent);

		if(Objects.equals(getChannelId(), Main.getChannelManager().getActiveChanneldId())){
			addToViewPort(event);
		}

	}

	/// @deprecated Use displayMessage(TwitchMessage message, ChannelChatMessageEvent event) instead
	@Deprecated
	public void displayMessage(TwitchMessage message){
		MessageComponentContent messageComponentContent = new MessageComponentContent(
				null,
				message.getMessage(),
				null,
				message);

		EclipseStoreKeeper.root().addMessage(channel.getChannelId(), messageComponentContent);

		if(Objects.equals(getChannelId(), Main.getChannelManager().getActiveChanneldId())){
			addToViewPort(messageComponentContent);
		}

	}

	public void displayMessage(ChatMessage message){
		UserChatData ownerData = DatabaseManager.getOwnerCache().getById(channel.getChannelId());

		if(ownerData == null){
			ownerData = new UserChatData(channel.getChannelId(), HTMLColors.WHITE.getColorCode(), message.getSenderName(), "");
		}

		getStatistics().addMessage(message.getSenderName(), channel.getChannelId(), message.getMessage());
		getMessageHistory().addSendedMessages(message.getMessageRaw());

		Arrays.stream(message.getMessage().split(" ")).parallel().forEach(word -> {
			if(word.startsWith("@") && word.length() > 1){
				greetingsManager.setMentioned(word.replace("@", ""));
			}
		});

		MessageComponentContent messageComponentContent = new MessageComponentContent(
				ownerData,
				((replyMessage != null) ? "@" + replyMessage.getParentReplyUser() + " " : "")+ message.getMessage(),
				null,
				replyMessage);

		EclipseStoreKeeper.root().addMessage(channel.getChannelId(), messageComponentContent);

		addToViewPort(messageComponentContent);
	}

	private void addToViewPort(ChannelChatMessageEvent event){
		Platform.runLater(() -> {
			MessageComponent messageComponent = new MessageComponent();
			messageComponent.applyMessage(event);
			Main.messagePanel.getChildren().add(messageComponent);
		});
	}

	private void addToViewPort(MessageComponentContent messageContent){
		Platform.runLater(() -> {
			Main.messagePanel.getChildren().add(new MessageComponent(this, messageContent));
		});
	}

	public void loadViewPort(){
		new Thread(() -> {
			Platform.runLater(() -> {
				Main.macroPane.loadMacros(this);
				Main.messagePanel.getChildren().clear();
//				messageCache.forEach(messageContent -> Main.messagePanel.getChildren().add(new MessageComponent(messageContent)));
			});

			EclipseStoreKeeper.root().getMessages(this).forEach(messageContent -> {
				Platform.runLater(() -> Main.messagePanel.getChildren().add(new MessageComponent(this, messageContent)));
			});
		}).start();
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
		return EclipseStoreKeeper.root().getChannelStatistics(channel.getChannelId());
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
		return channel.getChannelId();
	}

	public Channel getChannel() {
		return channel;
	}

	public ChannelMacros getMacros() {
		return macros;
	}

	/**
	 * @return may be null, if no emotes are installed for the user.
	 */
	public ChannelEmotes getChannelEmotes(){
		return EmoteManager.getChannelEmotes(channel.getChannelId());
	}
}
