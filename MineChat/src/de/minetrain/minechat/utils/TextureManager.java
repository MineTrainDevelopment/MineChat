package de.minetrain.minechat.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.minetrain.minechat.config.ConfigManager;
import de.minetrain.minechat.gui.frames.EmoteDownlodFrame;
import de.minetrain.minechat.gui.frames.EmoteDownlodFrame.EmotePlatform;
import de.minetrain.minechat.gui.obj.StatusBar;
import de.minetrain.minechat.gui.obj.TabButtonType;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.TwitchCredentials;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import kong.unirest.Unirest;

public class TextureManager {
	private static final Logger logger = LoggerFactory.getLogger(TextureManager.class);
	public static final String texturePath = "data/texture/";
	private final ImageIcon mainFrame_TAB_1;
	private final ImageIcon mainFrame_TAB_2;
	private final ImageIcon mainFrame_TAB_3;
	private final ImageIcon onboarding;
	
	public TextureManager() {
		logger.debug("Loading textures...");
		this.mainFrame_TAB_1 = new ImageIcon(texturePath + "MineChatTextur.png");
		this.mainFrame_TAB_2 =new ImageIcon(texturePath + "MineChatTextur2.png");
		this.mainFrame_TAB_3 = new ImageIcon(texturePath + "MineChatTextur3.png");
		this.onboarding = new ImageIcon(texturePath + "MineChatTexturOnboarding.png");
		logger.debug("Loading textures done.");
	}


	public ImageIcon getMainFrame_TAB_1() {
		return mainFrame_TAB_1;
	}

	public ImageIcon getMainFrame_TAB_2() {
		return mainFrame_TAB_2;
	}

	public ImageIcon getMainFrame_TAB_3() {
		return mainFrame_TAB_3;
	}

	public ImageIcon getOnboarding() {
		return onboarding;
	}
	
	public ImageIcon getByTabButton(TabButtonType tab){
		switch (tab) {
		case TAB_MAIN:
			return mainFrame_TAB_1;
			
		case TAB_SECOND:
			return mainFrame_TAB_2;
			
		case TAB_THIRD:
			return mainFrame_TAB_3;

		default:
			logger.warn("Invalid boolean.", new IllegalArgumentException("Can´t fine a vaule for: "+tab));
			return null;
		}
	}
	
	
	public static void downloadProfileImage(String uri, Long channelId) {
		try {
			logger.info("Downloading image...");
			downloadImmage(uri, "Icons/"+channelId, "/profile.png");
			logger.info("Image downloaded successfully.");
		} catch (IOException ex) {
			logger.warn("Can´t download profile image. \n URL: " + uri, ex);
		}
	}
	
	public static void downloadImmage(String uri, String fileLocation, String fileName) throws MalformedURLException, IOException, ProtocolException, FileNotFoundException {
		URL url = new URL(uri);
		HttpURLConnection httpVerbindung = (HttpURLConnection) url.openConnection();
		httpVerbindung.setRequestMethod("GET");

		InputStream inputStream = httpVerbindung.getInputStream();
		byte[] buffer = new byte[1024];
		int bytesRead;
		
		Files.createDirectories(Paths.get(texturePath + fileLocation));
		OutputStream outputStream = new FileOutputStream(texturePath + fileLocation + fileName);

		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}

		outputStream.close();
		inputStream.close();
	}
	
}
