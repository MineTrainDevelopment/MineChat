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

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.gui.obj.TabButtonType;

public class TextureManager {
	private static final Logger logger = LoggerFactory.getLogger(TextureManager.class);
	public static final String texturePath = "data/texture/";
	private final ImageIcon mainFrame_TAB_1;
	private final ImageIcon mainFrame_TAB_2;
	private final ImageIcon mainFrame_TAB_3;
	private final ImageIcon onboarding;
	private final ImageIcon emoteBorder;
	private final ImageIcon programIcon;
	private final ImageIcon replyButton;
	private final ImageIcon markReadButton;
	private final ImageIcon cancelButton;
	private final ImageIcon enterButton;
	private final ImageIcon emoteButton;
	private final ImageIcon waveButton;
	
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
		this.enterButton = new ImageIcon(texturePath + "enterButton.png");
		this.emoteButton = new ImageIcon(texturePath + "emoteButton.png");
		this.waveButton = new ImageIcon(texturePath + "waveButton.png");
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

	public ImageIcon getEnterButton() {
		return enterButton;
	}

	public ImageIcon getEmoteButton() {
		return emoteButton;
	}

	public ImageIcon getWaveButton() {
		return waveButton;
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
			downloadImage(uri, "Icons/"+channelId, "/profile.png");
			downloadImage(uri, "Icons/"+channelId, "/profile_75.png", new Dimension(75, 75));
//			resizeImage("Icons/"+channelId, "/profile_75.png", new Dimension(75, 75));
			logger.info("Image downloaded successfully.");
		} catch (IOException ex) {
			logger.warn("Can´t download profile image. \n URL: " + uri, ex);
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
	         
	         System.out.println("Bild erfolgreich heruntergeladen.");
	      } catch (IOException e) {
	         e.printStackTrace();
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
	
}
