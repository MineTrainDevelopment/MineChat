package de.minetrain.minechat.gui.emotes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import de.minetrain.minechat.main.Main;

public class WebEmote extends Emote{
	private static final String TWITCH_EMOTE_URL = "https://static-cdn.jtvnw.net/emoticons/v2/{ID}/{FORMAT}/dark/1.0"; //static, animated
	private final URL url;

	public WebEmote(String name, String emoteId) throws MalformedURLException {
		super(name, emoteId, Main.isValidImageURL(getTwitchEmoteUrl(emoteId, true)) ? "gif" : "png");
		this.url = new URL(getTwitchEmoteUrl(emoteId, getFileFormat().contains("gif")));
	}

	public static String getTwitchEmoteUrl(String emoteId, boolean animated) {
		return TWITCH_EMOTE_URL.replace("{ID}", emoteId).replace("{FORMAT}", animated ? "animated" : "static");
	}
	
	@Override
	public String getFilePath() {
		return url.getPath();
	}
	
	@Override
	public ImageIcon getImageIcon() {
		try {
			return new ImageIcon(ImageIO.read(url));
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * NOTE: {@link WebEmote}s can´t use {@link EmoteSize}.
	 */
	@Override
	public ImageIcon getImageIcon(EmoteSize size) {
		return getImageIcon();
	}
	
	public String getUrl() {
		return url.getPath();
	}

}
