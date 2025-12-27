package de.minetrain.minechat.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.Channel;
import de.minetrain.minechat.twitch.TwitchHelper;
import de.minetrain.minechat.twitch.obj.ChannelStatistics;
import de.minetrain.minechat.twitch.obj.GreetingsManager;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class ChannelActions {

	private static final Logger LOG = LoggerFactory.getLogger(ChannelActions.class);

	private final GreetingsManager greetingsManager;
	private final TwitchUserObj twitchUser;

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

//		macros.createMacro(new MacroObject(MacroType.TEXT, "emotesv2_6cc7fdb3cca74bdc80c49f4199b6d001", 00, "Test 1", "Macro-V2 | test_1".split("q")));
//		macros.createMacro(new MacroObject(MacroType.TEXT, "emotesv2_2f6e7f957a37440e92fc33c66be7c0c2", 10, "Test 2", "Macro-V2 | test_2".split("q")));
//		macros.createMacro(new MacroObject(MacroType.EMOTE, "emotesv2_392517b42c324d0f867b122a6bba1d9f", 20, "Test 3", "Macro-V2 | test_3".split("q")));
//
//		macros.createMacro(new MacroObject(MacroType.TEXT, "emotesv2_a164fa4a5298492f8850ae43bbda2bc4", 01, "Test 4", "Macro-V2 | test_4".split("q")));
//		macros.createMacro(new MacroObject(MacroType.TEXT, "612f819daf28e956864b54dd", 11, "Test 5", "Macro-V2 | test_5".split("q")));
//		macros.createMacro(new MacroObject(MacroType.EMOTE, "emotesv2_662fe5cfd480497f98bd3ec7b953817a", 21, "Test 6", "Macro-V2 | test_6".split("q")));

//		twitchUser.join(); // Zocki disabled...
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

	public boolean isModerator() {
		return chatRole.equalsIgnoreCase("moderator");
	}

	public String getChannelId() {
		return channel.getChannelId();
	}

	public Channel getChannel() {
		return channel;
	}
}
