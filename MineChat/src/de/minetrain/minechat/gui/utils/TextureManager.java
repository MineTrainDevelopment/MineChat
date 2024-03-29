package de.minetrain.minechat.gui.utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fmsware.gif.GifDecoder;
import com.fmsware.gif.GifEncoder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.EmoteManager;
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
	private final ImageIcon mainFrame_Blank;
	private final ImageIcon onboarding;
	private final ImageIcon emoteBorder;
	private final ImageIcon programIcon;
	private final ImageIcon replyButton;
	private final ImageIcon markReadButton;
	private final ImageIcon cancelButton;
	private final ImageIcon confirmButton;
	private final ImageIcon editButton;
	private final ImageIcon infoButton;
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
	private final ImageIcon programClose;
	private final ImageIcon programMinimize;
	private final ImageIcon programSettings;
	private final ImageIcon macroKeyPressed;
	private final ImageIcon macroKeyHover;
	private final ImageIcon macroKey;
	private final ImageIcon macroEmoteKeyPressed;
	private final ImageIcon macroEmoteKeyHover;
	private final ImageIcon macroEmoteKey;
	private final ImageIcon liveIcon;
	private final ImageIcon notificationButton;
	private final ImageIcon notificationButtonHover;
	private final ImageIcon profilePicLoading;
	private final ImageIcon copyButton;
	
	public TextureManager() {
		logger.debug("Loading textures...");
		this.mainFrame_TAB_1 = new ImageIcon(texturePath + "program/MineChatTextur.png");
		this.mainFrame_TAB_2 =new ImageIcon(texturePath + "program/MineChatTextur2.png");
		this.mainFrame_TAB_3 = new ImageIcon(texturePath + "program/MineChatTextur3.png");
		this.mainFrame_Blank = new ImageIcon(texturePath + "program/MineChatTextur_Blank.png");
		this.onboarding = new ImageIcon(texturePath + "program/MineChatTexturOnboarding.png");
		this.emoteBorder = new ImageIcon(texturePath + "emoteBorder/emoteBorder.png");
		this.programIcon = new ImageIcon(texturePath + "program/programIcon.png");
		this.replyButton = new ImageIcon(texturePath + "chatInput/replyButton.png");
		this.markReadButton = new ImageIcon(texturePath + "chatInput/markReadButton.png");
		this.cancelButton = new ImageIcon(texturePath + "utilIcon/cancelButton.png");
		this.confirmButton = new ImageIcon(texturePath + "utilIcon/confirmButton.png");
		this.editButton = new ImageIcon(texturePath + "utilIcon/editButton.png");
		this.infoButton = new ImageIcon(texturePath + "utilIcon/infoButton.png");
		this.enterButton = new ImageIcon(texturePath + "chatInput/enterButton.png");
		this.emoteButton = new ImageIcon(texturePath + "chatInput/emoteButton.png");
		this.waveButton = new ImageIcon(texturePath + "chatInput/waveButton.png");
		this.loveButton = new ImageIcon(texturePath + "chatInput/loveButton.png");
		this.statusButton_1 = new ImageIcon(texturePath + "program/statusButton_1.png");
		this.statusButton_2 = new ImageIcon(texturePath + "program/statusButton_2.png");
		this.statusButton_3 = new ImageIcon(texturePath + "program/statusButton_3.png");
		this.rowArrowLeft = new ImageIcon(texturePath + "program/rowArrowLeft.png");
		this.rowArrowRight = new ImageIcon(texturePath + "program/rowArrowRight.png");
		this.replyChainButton = new ImageIcon(texturePath + "chatInput/replyChainButton.png");
		this.programClose = new ImageIcon(texturePath + "program/programClose.png");
		this.programMinimize = new ImageIcon(texturePath + "program/programMinimize.png");
		this.programSettings = new ImageIcon(texturePath + "program/programSettings.png");
		this.macroKeyPressed = new ImageIcon(texturePath + "macroBorder/macroKeyPressed.gif");
		this.macroKeyHover = new ImageIcon(texturePath + "macroBorder/macroKeyHover.png");
		this.macroKey = new ImageIcon(texturePath + "macroBorder/macroKey.png");
		this.macroEmoteKeyPressed = new ImageIcon(texturePath + "macroBorder/macroEmoteKeyPressed.gif");
		this.macroEmoteKeyHover = new ImageIcon(texturePath + "macroBorder/macroEmoteKeyHover.png");
		this.macroEmoteKey = new ImageIcon(texturePath + "macroBorder/macroEmoteKey.png");
		this.liveIcon = new ImageIcon(texturePath + "liveNotification/LiveIcon.png");
		this.notificationButton = new ImageIcon(texturePath + "liveNotification/NotificationButton.png");
		this.notificationButtonHover = new ImageIcon(texturePath + "liveNotification/NotificationButtonHover.png");
		this.profilePicLoading = new ImageIcon(texturePath + "settingsMenu/profilePicLoading.gif");
		this.copyButton = new ImageIcon(texturePath + "utilIcon/copyButton.png");
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

	public ImageIcon getMainFrame_Blank() {
		return mainFrame_Blank;
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
	
	public ImageIcon getEditButton() {
		return editButton;
	}
	
	public ImageIcon getInfoButton() {
		return infoButton;
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
	
	public ImageIcon getProgramClose() {
		return programClose;
	}
	
	public ImageIcon getProgramMinimize() {
		return programMinimize;
	}
	
	public ImageIcon getProgramSettings() {
		return programSettings;
	}
	
	public ImageIcon getMacroKeyPressed() {
		return macroKeyPressed;
	}
	
	public ImageIcon getMacroKeyHover() {
		return macroKeyHover;
	}
	
	public ImageIcon getMacroKey() {
		return macroKey;
	}
	
	public ImageIcon getMacroEmoteKeyPressed() {
		return macroEmoteKeyPressed;
	}
	
	public ImageIcon getMacroEmoteKeyHover() {
		return macroEmoteKeyHover;
	}
	
	public ImageIcon getMacroEmoteKey() {
		return macroEmoteKey;
	}
	
	public ImageIcon getLiveIcon() {
		return liveIcon;
	}
	
	
	public ImageIcon getNotificationButton() {
		return notificationButton;
	}


	public ImageIcon getNotificationButtonHover() {
		return notificationButtonHover;
	}
	

	public ImageIcon getProfilePicLoading() {
		return profilePicLoading;
	}

	public ImageIcon getCopyButton() {
		return copyButton;
	}

	
	
	public static void downloadProfileImage(String uri, String channelId) {
		try {
			downloadImage(uri, "Icons/"+channelId, "/profile.png");
			downloadImage(uri, "Icons/"+channelId, "/profile_18.png", new Dimension(18, 18));
			downloadImage(uri, "Icons/"+channelId, "/profile_25.png", new Dimension(25, 25));
			downloadImage(uri, "Icons/"+channelId, "/profile_75.png", new Dimension(75, 75));
			downloadImage(uri, "Icons/"+channelId, "/profile_80.png", new Dimension(80, 80));
		} catch (IOException ex) {
			logger.warn("Can�t download profile image. \n URL: " + uri, ex);
		}
	}
	
	public static void downloadImage(String uri, String fileLocation, String fileName) throws MalformedURLException, IOException, ProtocolException, FileNotFoundException {
		downloadImage(uri, fileLocation, fileName, null);
	}
	
	public static void downloadImage(String uri, String fileLocation, String fileName, Dimension dimension) throws MalformedURLException, IOException, ProtocolException, FileNotFoundException {
		try {
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
			getDefaultBadges();
		}


		if(!DatabaseManager.getEmote().isPublicEmotesInstald()){
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
						//TODO: Update GUI info 
						downloadImage(version.get("image_url_1x").getAsString(), path, "1.png");
						downloadImage(version.get("image_url_2x").getAsString(), path, "2.png");
						downloadImage(version.get("image_url_4x").getAsString(), path, "3.png");
					} catch (IOException ex) {
						logger.error("Error?", ex);
					}
				});
			});
			
		}).start();
	}
	
	public static void downloadChannelEmotes(String channelId) {
		new Thread(() -> {
			JsonObject fromJson = new Gson().fromJson(Unirest.get("https://api.twitch.tv/helix/chat/emotes?broadcaster_id="+channelId)// 'https://api.twitch.tv/helix/users?id=141981764&id=4845668'
					.header("Authorization", "Bearer "+TwitchManager.getAccesToken())
					.header("Client-Id", TwitchManager.credentials.getClientID())
					.asString()
					.getBody(), JsonObject.class);
			
			JsonArray jsonArray = fromJson.getAsJsonArray("data");
			String downloadURL = fromJson.get("template").getAsString();
	
			ArrayList<String> tier1 = new ArrayList<String>();
			ArrayList<String> tier2 = new ArrayList<String>();
			ArrayList<String> tier3 = new ArrayList<String>();
			ArrayList<String> follower = new ArrayList<String>();
			ArrayList<String> bits = new ArrayList<String>();
			
			for (int i=0; i < jsonArray.size(); i++) {
				JsonElement jsonElement = jsonArray.get(i);
				JsonObject entry = jsonElement.getAsJsonObject();
				
				String emoteID = entry.get("id").getAsString();
				String format = (entry.get("format").toString().contains("animated") ? "animated" : "static");
				String fileFormat = ((format.length()>6) ? ".gif" : ".png");
				String fileLocation = "Icons/"+channelId+"/"+emoteID+"/";
				String name = entry.get("name").getAsString();
				String tier = entry.get("tier").getAsString();
				String emote_type = entry.get("emote_type").getAsString();
				
		    	switch (tier) {
				case "1000": tier1.add(emoteID); break;
				case "2000": tier2.add(emoteID); break;
				case "3000": tier3.add(emoteID); break;
	
				default:
					if(emote_type.startsWith("bitstier")){
						bits.add(emoteID);
					}
					
					if(emote_type.startsWith("follower")){
						follower.add(emoteID);
					}
					break;
				}
		    	
		    	boolean isFavorite = false;
		    	Emote emoteByName = EmoteManager.getEmoteByName(name);
		    	if(emoteByName != null){
		    		isFavorite = emoteByName.isFavorite();
		    	}
				
	//	    	DatabaseManager.getEmote().insert(channelId, emoteID, name, false, isFavorite, entry.get("emote_type").getAsString(), tier, format, !format.equals("static"), fileLocation);
				DatabaseManager.getEmote().insert(
		    			emoteID, 
		    			name, 
		    			false, 
		    			isFavorite,
		    			emote_type, 
		    			tier, 
		    			format.contains("animated") ? "gif" : "png", 
		    			!format.equals("static"), 
		    			texturePath+fileLocation+emoteID+"_1"+fileFormat);
				
				
				try {
	//				"https://static-cdn.jtvnw.net/emoticons/v2/{{id}}/{{format}}/{{theme_mode}}/{{scale}}"
	//				downloadURL = downloadURL.replace("{{id}}", emoteID).replace("{{format}}", format).replace("{{theme_mode}}", "dark");
					System.out.println("Emote -> "+name+" --- "+downloadURL.replace("{{id}}", emoteID).replace("{{format}}", format).replace("{{theme_mode}}", "dark"));
					for (int index = 1; index < 4; index++) {
						TextureManager.downloadImage(downloadURL.replace("{{id}}", emoteID).replace("{{format}}", format).replace("{{theme_mode}}", "dark").replace("{{scale}}", index+".0"), fileLocation, emoteID+"_"+index+fileFormat);
					}
					
	//				TextureManager.mergeEmoteImages(fileLocation, emoteID+"_1"+fileFormat, "emoteBorder"+borderImageTyp+".png", fileFormat);
				} catch (IOException ex) {
					logger.error("Can�t download the Twitch emote '"+name+"'.", ex);
				}
				
			}
			
			String tierlevel = EmoteManager.getChannelEmotes().containsKey(channelId) ? EmoteManager.getChannelEmotes(channelId).getSubLevel() : "tier0";
			DatabaseManager.getEmote().insertChannel(channelId, tierlevel, tier1, tier2, tier3, bits, follower);
			DatabaseManager.commit();
			DatabaseManager.getEmote().getAll();
			DatabaseManager.getEmote().getAllChannels();
			
		}).start();
	}
	
	
	public static void downloadBttvEmotes(String channelId){
		new Thread(() -> {
			JsonObject fromJson = new Gson().fromJson(Unirest.get("https://api.betterttv.net/3/cached/users/twitch/"+channelId)
	//				.header("Accept", "*/*")
					.header("User-Agent", "MineChat Client")
					.asString()
					.getBody(), JsonObject.class);
			
			if(fromJson.toString().equals("{\"message\":\"user not found\"}")){
				return;
			}
			
			JsonArray channelArray = fromJson.getAsJsonArray("channelEmotes");
			JsonArray sharedArray = fromJson.getAsJsonArray("sharedEmotes");
			channelArray.addAll(sharedArray);
			
			ArrayList<String> emoteIDs = new ArrayList<String>();
			
			for (int i=0; i < channelArray.size(); i++) {
				JsonObject entry = channelArray.get(i).getAsJsonObject();
	
				String emoteID = entry.get("id").getAsString();
				String name = entry.get("code").getAsString();
				String imageType = entry.get("imageType").getAsString();
				String fileLocation = "Icons/bttv/"+emoteID+"/";
				
				emoteIDs.add(emoteID);
				
				boolean isFavorite = false;
		    	Emote emoteByName = EmoteManager.getEmoteByName(name);
		    	if(emoteByName != null){
		    		isFavorite = emoteByName.isFavorite();
		    	}
		    	
		    	DatabaseManager.getEmote().insert(
		    			emoteID, 
		    			name, 
		    			false, 
		    			isFavorite,
		    			"bttv", 
		    			null, 
		    			imageType, 
		    			entry.get("animated").getAsBoolean(), 
		    			texturePath+fileLocation+emoteID+"_1."+imageType);
		    	
				try {
					String downloadURL = "https://cdn.betterttv.net/emote/{{id}}/{{scale}}";
					for (int index = 1; index < 4; index++) {
						TextureManager.downloadImage(downloadURL.replace("{{id}}", emoteID).replace("{{scale}}", index+"x"), fileLocation, emoteID+"_"+index+"."+imageType);
					}
					
	//				TextureManager.mergeEmoteImages(fileLocation, emoteID+"_1."+imageType, "emoteBorder.png", imageType);
				} catch (IOException ex) {
					logger.error("Can�t download the Twitch emote '"+name+"'.", ex);
				}
				
			}
			
	
			DatabaseManager.getEmote().insertChannelBttv(channelId, emoteIDs);
			DatabaseManager.commit();
			DatabaseManager.getEmote().getAll();
			DatabaseManager.getEmote().getAllChannels();
		}).start();
	}
	
	
	public static void getDefaultEmotes() {
		new Thread(() -> {
			try {
				JsonObject fromJson = new Gson().fromJson(Unirest.get("https://api.twitch.tv/helix/chat/emotes/global")
						.header("Authorization", "Bearer " + TwitchManager.getAccesToken())
						.header("Client-Id", TwitchManager.credentials.getClientID()).asString().getBody(),
						JsonObject.class);
	
				JsonArray jsonArray = fromJson.getAsJsonArray("data");
	
				for (int i = 0; i < jsonArray.size(); i++) {
					JsonElement jsonElement = jsonArray.get(i);
					JsonObject entry = jsonElement.getAsJsonObject();
	
					String name = entry.get("name").getAsString();
					String emoteId = entry.get("id").getAsString();
					String fileLocation = "Icons/default/"+emoteId+"/";
					String format = (entry.get("format").toString().contains("animated") ? "animated" : "static");
					String fileFormat = ((format.length()>6) ? ".gif" : ".png");
					String downloadURL = fromJson.get("template").getAsString();
	
					boolean isFavorite = false;
			    	Emote emoteByName = EmoteManager.getEmoteByName(name);
			    	if(emoteByName != null){
			    		isFavorite = emoteByName.isFavorite();
			    	}
					
					DatabaseManager.getEmote().insert(
			    			emoteId, 
			    			name, 
			    			true, 
			    			isFavorite,
			    			"default", 
			    			null,
			    			format.contains("animated") ? "gif" : "png", 
			    			!format.equals("static"), 
			    			texturePath+fileLocation+emoteId+"_1"+fileFormat);
	
					try {
						for (int index = 1; index < 4; index++) {
							TextureManager.downloadImage(downloadURL.replace("{{id}}", emoteId).replace("{{format}}", format).replace("{{theme_mode}}", "dark").replace("{{scale}}", index+".0"), fileLocation, emoteId+"_"+index+fileFormat);
						}
					} catch (IOException ex) {
						logger.error("Can�t download the Twitch emote '"+name+"'.", ex);
					}
	
				}
	
				DatabaseManager.commit();
				DatabaseManager.getEmote().getAll();
			} catch (Exception ex) {
				logger.error("Something went wrong while downloading an emote!", ex);
			}
		}).start();
	}
	
	public static void getDefaultBadges(){
		JsonObject fromJson = new Gson().fromJson(Unirest.get("https://api.twitch.tv/helix/chat/badges/global")
				.header("Authorization", "Bearer " + TwitchManager.getAccesToken())
				.header("Client-Id", TwitchManager.credentials.getClientID()).asString().getBody(),
				JsonObject.class);

		downloadBadge(fromJson, null);
	}

}
