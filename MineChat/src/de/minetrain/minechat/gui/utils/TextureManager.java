package de.minetrain.minechat.gui.utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fmsware.gif.GifDecoder;
import com.fmsware.gif.GifEncoder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.gui.obj.StatusBar;
import de.minetrain.minechat.gui.obj.TabButtonType;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.TwitchManager;
import kong.unirest.Unirest;

public class TextureManager {
	private static final Logger logger = LoggerFactory.getLogger(TextureManager.class);
	public static final String texturePath = "data/texture/";
	public static final String badgePath = texturePath + "badges/";
	public static final String profilePicPath = "data/texture/Icons/{ID}/profile_{SIZE}.png";
	
	private final ImageIcon mainFrame_TAB_1;
	private final ImageIcon mainFrame_TAB_2;
	private final ImageIcon mainFrame_TAB_3;
	private final ImageIcon onboarding;
	private final ImageIcon emoteBorder;
	private final ImageIcon programIcon;
	private final ImageIcon replyButton;
	private final ImageIcon markReadButton;
	private final ImageIcon cancelButton;
	private final ImageIcon confirmButton;
	private final ImageIcon enterButton;
	private final ImageIcon emoteButton;
	private final ImageIcon waveButton;
	private final ImageIcon loveButton;
	private final ImageIcon statusButton_1;
	private final ImageIcon statusButton_2;
	private final ImageIcon statusButton_3;
	private final ImageIcon rowArrowRight;
	private final ImageIcon rowArrowLeft;
	private final ImageIcon replyChainButton;
	
	public TextureManager() {
		logger.debug("Loading textures...");
		this.mainFrame_TAB_1 = new ImageIcon(texturePath + "MineChatTextur.png");
		this.mainFrame_TAB_2 =new ImageIcon(texturePath + "MineChatTextur2.png");
		this.mainFrame_TAB_3 = new ImageIcon(texturePath + "MineChatTextur3.png");
		this.onboarding = new ImageIcon(texturePath + "MineChatTexturOnboarding.png");
		this.emoteBorder = new ImageIcon(texturePath + "emoteBorder.png");
		this.programIcon = new ImageIcon(texturePath + "programIcon.png");
		this.replyButton = new ImageIcon(texturePath + "replyButton.png");
		this.markReadButton = new ImageIcon(texturePath + "markReadButton.png");
		this.cancelButton = new ImageIcon(texturePath + "cancelButton.png");
		this.confirmButton = new ImageIcon(texturePath + "confirmButton.png");
		this.enterButton = new ImageIcon(texturePath + "enterButton.png");
		this.emoteButton = new ImageIcon(texturePath + "emoteButton.png");
//		this.emoteButton = new ImageIcon("data/texture/Icons/99351845/jennyanPopcorn/jennyanPopcorn_1.gif");
		this.waveButton = new ImageIcon(texturePath + "waveButton.png");
		this.loveButton = new ImageIcon(texturePath + "loveButton.png");
		this.statusButton_1 = new ImageIcon(texturePath + "statusButton_1.png");
		this.statusButton_2 = new ImageIcon(texturePath + "statusButton_2.png");
		this.statusButton_3 = new ImageIcon(texturePath + "statusButton_3.png");
		this.rowArrowLeft = new ImageIcon(texturePath + "rowArrowLeft.png");
		this.rowArrowRight = new ImageIcon(texturePath + "rowArrowRight.png");
		this.replyChainButton = new ImageIcon(texturePath + "replyChainButton.png");
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
	
	public ImageIcon getEmoteBorder() {
		return emoteBorder;
	}
	
	public ImageIcon getProgramIcon() {
		return programIcon;
	}
	
	public ImageIcon getReplyButton() {
		return replyButton;
	}


	public ImageIcon getMarkReadButton() {
		return markReadButton;
	}
	
	public ImageIcon getCancelButton() {
		return cancelButton;
	}
	
	public ImageIcon getConfirmButton() {
		return confirmButton;
	}

	public ImageIcon getEnterButton() {
		return enterButton;
	}

	public ImageIcon getEmoteButton() {
		return emoteButton;
	}

	public ImageIcon getWaveButton() {
		return waveButton;
	}

	public ImageIcon getLoveButton() {
		return loveButton;
	}
	
	public ImageIcon getStatusButton_1() {
		return statusButton_1;
	}


	public ImageIcon getStatusButton_2() {
		return statusButton_2;
	}


	public ImageIcon getStatusButton_3() {
		return statusButton_3;
	}

	public ImageIcon getRowArrowRight() {
		return rowArrowRight;
	}


	public ImageIcon getRowArrowLeft() {
		return rowArrowLeft;
	}
	
	public ImageIcon getReplyChainButton() {
		return replyChainButton;
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
			logger.warn("Invalid boolean.", new IllegalArgumentException("Can�t fine a vaule for: "+tab));
			return null;
		}
	}
	
	
	public static void downloadProfileImage(String uri, String channelId) {
		try {
			downloadImage(uri, "Icons/"+channelId, "/profile.png");
			downloadImage(uri, "Icons/"+channelId, "/profile_18.png", new Dimension(18, 18));
			downloadImage(uri, "Icons/"+channelId, "/profile_25.png", new Dimension(25, 25));
			downloadImage(uri, "Icons/"+channelId, "/profile_75.png", new Dimension(75, 75));
		} catch (IOException ex) {
			logger.warn("Can�t download profile image. \n URL: " + uri, ex);
		}
	}
	
	public static void downloadImage(String uri, String fileLocation, String fileName) throws MalformedURLException, IOException, ProtocolException, FileNotFoundException {
		downloadImage(uri, fileLocation, fileName, null);
	}
	
	public static void downloadImage(String uri, String fileLocation, String fileName, Dimension dimension) throws MalformedURLException, IOException, ProtocolException, FileNotFoundException {
		try {
			System.out.println("New fileName --> "+fileName + " | "+fileLocation);
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
	         
	         if(dimension != null){
	        	 resizeImage(fileLocation, fileName, dimension);
	         }
	         
	         if(fileName.contains(".gif")){
	        	 reformatGif(texturePath + fileLocation + fileName, texturePath + fileLocation + fileName);
	         }
	         
	         System.out.println("Bild erfolgreich heruntergeladen.");
	      } catch (Exception ex) {
	    	  logger.error("Errer while downloading an emote", ex);
	      }
	}
	
	
	public static void resizeImage(String fileLocation, String fileName, Dimension dimension) throws IOException {
		fileLocation = texturePath + fileLocation;
        // Load the original image from the given file location and name
        BufferedImage originalImage = ImageIO.read(new File(fileLocation, fileName));

        // Calculate the scaled width and height based on the given dimension while preserving the aspect ratio
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int scaledWidth = dimension.width;
        int scaledHeight = (int) Math.round((double) originalHeight / originalWidth * scaledWidth);

        // Create a new BufferedImage with the scaled dimensions
        BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, originalImage.getType());

        // Scale the original image to the new dimensions using Graphics2D
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        graphics2D.dispose();

        // Save the scaled image to a new file with the same name in the same directory
        File output = new File(fileLocation, fileName);
        ImageIO.write(scaledImage, "png", output);
    }

	public static void mergeEmoteImages(String fileLocation, String fileName, String background){
		mergeEmoteImages(fileLocation, fileName, background, "png");
	}
	
	public static void mergeEmoteImages(String fileLocation, String fileName, String background, String format){
		format = format.replace(".", "");
		System.out.println("merge -- "+fileLocation+fileName+" ---> "+background+"  <---> "+format);
		try {
			File path = new File(texturePath + fileLocation); // base path of the images
			System.out.println(texturePath + fileLocation);
	
			// load source images
			BufferedImage image = ImageIO.read(new File(TextureManager.texturePath, background));
			BufferedImage overlay = ImageIO.read(new File(path, fileName));
	
			// create the new image, canvas size is the max. of both image sizes
			int w = Math.max(image.getWidth(), overlay.getWidth());
			int h = Math.max(image.getHeight(), overlay.getHeight());
			BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	
			// paint both images, preserving the alpha channels
			Graphics g = combined.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.drawImage(overlay, 4, 4, null);
	
			g.dispose();
	
			// Save as new image
			ImageIO.write(combined, format.toUpperCase(), new File(path, fileName.replace("."+format, "_BG.png")));
		} catch (IOException ex) {
			logger.error("Merging images whent wrong.", ex);
		}
	}
	
	private static void reformatGif(String origin, String destination) {
		try {
			GifDecoder decoder = new GifDecoder();
			decoder.read(origin);
			
			GifEncoder encoder = new GifEncoder();
			encoder.setRepeat(true);
			encoder.setTransparent();
			encoder.start(destination);

			encoder.setSize(decoder.getFrameSize());
			for (int i = 0; i < decoder.getFrameCount(); i++) {
				encoder.addFrame(decoder.getFrame(i), decoder.getDelay(i));
			}
	
			encoder.finish();
		} catch (Exception ex) {
			logger.warn("Unable to reformat gif! \nOrigin: "+origin+"\nDestination: "+destination, ex);
		}
	}
	
	
	public static void downloadChannelBadges(String userId){
		JsonObject fromJson = new Gson().fromJson(Unirest.get("https://api.twitch.tv/helix/chat/badges?broadcaster_id="+userId)
				.header("Authorization", "Bearer " + TwitchManager.getAccesToken())
				.header("Client-Id", TwitchManager.credentials.getClientID()).asString().getBody(),
				JsonObject.class);

		downloadBadge(fromJson, userId);
	}
	
	public static void downloadPublicData() {
		if (!Files.exists(Paths.get(badgePath + "vip/"))) {
			JsonObject fromJson = new Gson().fromJson(Unirest.get("https://api.twitch.tv/helix/chat/badges/global")
					.header("Authorization", "Bearer " + TwitchManager.getAccesToken())
					.header("Client-Id", TwitchManager.credentials.getClientID()).asString().getBody(),
					JsonObject.class);

			downloadBadge(fromJson, null);
		}


		if(!Files.exists(Paths.get(texturePath+"Icons/default"))){
			getDefaultEmotes();
		}
	}


	private static void downloadBadge(JsonObject fromJson, String channelId) {
		String badgePath = "badges/{SET_ID}"+(channelId != null ? "/Channel_"+channelId : "")+"/{ID}/";
		
		new Thread(() -> {
			fromJson.get("data").getAsJsonArray().forEach(respondsArray -> {
				JsonObject asJsonObject = respondsArray.getAsJsonObject();
				asJsonObject.get("versions").getAsJsonArray().forEach(badgeVersion -> {
					JsonObject version = badgeVersion.getAsJsonObject();
					String path = badgePath.replace("{SET_ID}", asJsonObject.get("set_id").getAsString()).replace("{ID}", version.get("id").getAsString());
					
					YamlManager config = new YamlManager(TextureManager.texturePath + path + "meta.yml");
					config.setString("Name", version.get("title").getAsString());
					config.setString("Description", version.get("description").getAsString());
					config.saveConfigToFile();

					try {
						Main.MAIN_FRAME.getTitleBar().getMainTab().getChatWindow().chatStatusPanel.setDownloadStatus("badge", version.get("title").getAsString()+".png", false);
				    	Main.MAIN_FRAME.getTitleBar().getSecondTab().getChatWindow().chatStatusPanel.setDownloadStatus("badge", version.get("title").getAsString()+".png", false);
				    	Main.MAIN_FRAME.getTitleBar().getThirdTab().getChatWindow().chatStatusPanel.setDownloadStatus("badge", version.get("title").getAsString()+".png", false);
						downloadImage(version.get("image_url_1x").getAsString(), path, "1.png");
						downloadImage(version.get("image_url_2x").getAsString(), path, "2.png");
						downloadImage(version.get("image_url_4x").getAsString(), path, "3.png");
					} catch (IOException ex) {
						logger.error("Error?", ex);
					}
				});
			});
			
			Main.MAIN_FRAME.getTitleBar().getMainTab().getChatWindow().chatStatusPanel.setDefault(false);
	    	Main.MAIN_FRAME.getTitleBar().getSecondTab().getChatWindow().chatStatusPanel.setDefault(false);
	    	Main.MAIN_FRAME.getTitleBar().getThirdTab().getChatWindow().chatStatusPanel.setDefault(false);
		}).start();
	}
	
	public static void downloadChannelEmotes(String channelId) {
		
		JsonObject fromJson = new Gson().fromJson(Unirest.get("https://api.twitch.tv/helix/chat/emotes?broadcaster_id="+channelId)// 'https://api.twitch.tv/helix/users?id=141981764&id=4845668'
				.header("Authorization", "Bearer "+TwitchManager.getAccesToken())
				.header("Client-Id", TwitchManager.credentials.getClientID())
				.asString()
				.getBody(), JsonObject.class);
		
		JsonArray jsonArray = fromJson.getAsJsonArray("data");
		String downloadURL = fromJson.get("template").getAsString();
		List<String> emoteList = new ArrayList<String>();
		List<String> emoteTier2List = new ArrayList<String>();
		List<String> emoteTier3List = new ArrayList<String>();
		List<String> emoteBitsList = new ArrayList<String>();
		List<String> emoteFollowList = new ArrayList<String>();
		
		for (int i=0; i < jsonArray.size(); i++) {
			JsonElement jsonElement = jsonArray.get(i);
			JsonObject entry = jsonElement.getAsJsonObject();
			
			String name = entry.get("name").getAsString();
			String format = (entry.get("format").toString().contains("animated") ? "animated" : "static");
			String fileFormat = ((format.length()>6) ? ".gif" : ".png");
			String emoteID = entry.get("id").getAsString();
			String fileLocation = "Icons/"+channelId+"/"+name+"/";
			
		    Main.MAIN_FRAME.getTitleBar().getMainTab().getChatWindow().chatStatusPanel.setDownloadStatus("emote", name+".png", false);
	    	Main.MAIN_FRAME.getTitleBar().getSecondTab().getChatWindow().chatStatusPanel.setDownloadStatus("emote", name+".png", false);
	    	Main.MAIN_FRAME.getTitleBar().getThirdTab().getChatWindow().chatStatusPanel.setDownloadStatus("emote", name+".png", false);
			YamlManager config = new YamlManager(TextureManager.texturePath+fileLocation+name+".yml");
			
			String borderImageTyp = "";
			String indexName = name+"%&%"+fileFormat;
			switch (entry.get("tier").getAsString()) {
				case "1000": emoteList.add(indexName); break;
				case "2000": emoteTier2List.add(indexName); borderImageTyp="2"; break;
				case "3000": emoteTier3List.add(indexName); borderImageTyp="3"; break;
				default:
					if(entry.get("emote_type").getAsString().equals("bitstier")){
						 emoteBitsList.add(indexName); borderImageTyp="Bits";
					}else{
						emoteFollowList.add(indexName); borderImageTyp="Follow";
					}
					break;
			}
			
			config.setString("Name", name);
			config.setString("ID", emoteID);
			config.setString("Tier", entry.get("tier").getAsString());
			config.setString("EmoteType", entry.get("emote_type").getAsString());
			config.setString("EmoteSet_Id",entry.get("emote_set_id").getAsString());
			config.setString("Tier",entry.get("tier").getAsString());
			config.setString("Format", format);
			config.setString("Theme", "dark");
			config.saveConfigToFile();
			
			try {
//				"https://static-cdn.jtvnw.net/emoticons/v2/{{id}}/{{format}}/{{theme_mode}}/{{scale}}"
//				downloadURL = downloadURL.replace("{{id}}", emoteID).replace("{{format}}", format).replace("{{theme_mode}}", "dark");
				System.out.println("Emote -> "+name+" --- "+downloadURL.replace("{{id}}", emoteID).replace("{{format}}", format).replace("{{theme_mode}}", "dark"));
				for (int index = 1; index < 4; index++) {
					TextureManager.downloadImage(downloadURL.replace("{{id}}", emoteID).replace("{{format}}", format).replace("{{theme_mode}}", "dark").replace("{{scale}}", index+".0"), fileLocation, name+"_"+index+fileFormat);
				}
				
				TextureManager.mergeEmoteImages(fileLocation, name+"_1"+fileFormat, "emoteBorder"+borderImageTyp+".png", fileFormat);
			} catch (IOException ex) {
				logger.error("Error?", ex);
			}
			
		}

	    Main.MAIN_FRAME.getTitleBar().getMainTab().getChatWindow().chatStatusPanel.setDownloadStatus("emote", "Saving data...", false);
    	Main.MAIN_FRAME.getTitleBar().getSecondTab().getChatWindow().chatStatusPanel.setDownloadStatus("emote", "Saving data...", false);
    	Main.MAIN_FRAME.getTitleBar().getThirdTab().getChatWindow().chatStatusPanel.setDownloadStatus("emote", "Saving data...", false);
		Collections.sort(emoteList);
		Collections.sort(emoteTier2List);
		Collections.sort(emoteTier2List);
		Collections.sort(emoteBitsList);
		Collections.sort(emoteFollowList);
		emoteList.addAll(emoteTier2List);
		emoteList.addAll(emoteTier3List);
		emoteList.addAll(emoteBitsList);
		emoteList.addAll(emoteFollowList);
		
		if(jsonArray.size()>0){
			List<String> indexList = Main.EMOTE_INDEX.getStringList("index");
			if(!indexList.contains("Channel_"+channelId)){
				indexList.add("Channel_"+channelId);
			}
			Main.EMOTE_INDEX.setStringList("index", indexList, false);
			Main.EMOTE_INDEX.setStringList("Channel_"+channelId, emoteList, true);
		}
		
		Main.MAIN_FRAME.getTitleBar().getMainTab().getChatWindow().chatStatusPanel.setDefault(false);
    	Main.MAIN_FRAME.getTitleBar().getSecondTab().getChatWindow().chatStatusPanel.setDefault(false);
    	Main.MAIN_FRAME.getTitleBar().getThirdTab().getChatWindow().chatStatusPanel.setDefault(false);
	}
	
	
	private static void getDefaultEmotes() {
		JFrame dialog = new JFrame("emote downloading...");
		try {
			dialog.setSize(300, 75);
			dialog.setLocation(Main.MAIN_FRAME.getLocation().x + 100, Main.MAIN_FRAME.getLocation().y + 400);
			dialog.setAlwaysOnTop(true);
			dialog.setUndecorated(true);
			dialog.setResizable(false);
			dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 25, 25));

			StatusBar statusBar = new StatusBar();
			dialog.add(statusBar);
			dialog.setVisible(true);

			statusBar.setProgress("Get the emote set", 80);
			JsonObject fromJson = new Gson().fromJson(Unirest.get("https://api.twitch.tv/helix/chat/emotes/global")
					.header("Authorization", "Bearer " + TwitchManager.getAccesToken())
					.header("Client-Id", TwitchManager.credentials.getClientID()).asString().getBody(),
					JsonObject.class);

			JsonArray jsonArray = fromJson.getAsJsonArray("data");
			List<String> emoteList = new ArrayList<String>();

			for (int i = 0; i < jsonArray.size(); i++) {
				JsonElement jsonElement = jsonArray.get(i);
				JsonObject entry = jsonElement.getAsJsonObject();

				String name = entry.get("name").getAsString();
				String formatName = name.replaceAll("[" + Pattern.quote("<>:\"/\\|?*()") + "]", "_");;
				System.out.println("emote --> "+name+" --> "+formatName);
				String fileLocation = "Icons/default/" + formatName + "/";

				statusBar.setProgress("Downloading: " + name, StatusBar.getPercentage(jsonArray.size(), i));
				YamlManager config = new YamlManager(TextureManager.texturePath + fileLocation + formatName + ".yml");
				config.setString("Name", name);
				config.setString("ID", entry.get("id").getAsString());
				config.setString("Format", "static");
				config.setString("Theme", "dark");
				config.saveConfigToFile();

				emoteList.add(formatName+"%&%.png");

				try {
					TextureManager.downloadImage(entry.getAsJsonObject("images").get("url_1x").getAsString().replace("light", "dark"), fileLocation, formatName + "_1.png");
					TextureManager.downloadImage(entry.getAsJsonObject("images").get("url_2x").getAsString().replace("light", "dark"), fileLocation, formatName + "_2.png");
					TextureManager.downloadImage(entry.getAsJsonObject("images").get("url_4x").getAsString().replace("light", "dark"), fileLocation, formatName + "_3.png");
					TextureManager.mergeEmoteImages(fileLocation, formatName + "_1.png", "emoteBorder.png");
				} catch (IOException ex) {
					logger.error("Error?", ex);
				}

			}

			statusBar.setProgress("Saving data...", 99);
			Collections.sort(emoteList);
			System.out.println("Emote list --> "+emoteList.toArray());

			if (jsonArray.size() > 0) {
				List<String> indexList = Main.EMOTE_INDEX.getStringList("index");
				if (!indexList.contains("Channel_default")) {
					indexList.add("Channel_default");
				}
				Main.EMOTE_INDEX.setStringList("index", indexList, false);
				Main.EMOTE_INDEX.setStringList("Channel_default", emoteList, true);
			}
			statusBar.setDone("Download completed!");
			dialog.dispose();
		} catch (Exception ex) {
			logger.error("Something went wrong while downloading an emote!", ex);
			dialog.dispose();
		}
	}

}
