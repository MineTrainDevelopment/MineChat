package de.minetrain.minechat.gui.emotes;

import java.net.MalformedURLException;

import de.minetrain.minechat.main.Main;

public class WebEmote extends Emote{
	private static final String TWITCH_EMOTE_URL = "https://static-cdn.jtvnw.net/emoticons/v2/{ID}/{FORMAT}/dark/1.0"; //static, animated

	public WebEmote(String name, String emoteId) throws MalformedURLException {
		super(name, emoteId, Main.isValidImageURL(getTwitchEmoteUrl(emoteId, true)) ? "gif" : "png");
	}

	public static String getTwitchEmoteUrl(String emoteId, boolean animated) {
		return TWITCH_EMOTE_URL.replace("{ID}", emoteId).replace("{FORMAT}", animated ? "animated" : "static");
	}

}
