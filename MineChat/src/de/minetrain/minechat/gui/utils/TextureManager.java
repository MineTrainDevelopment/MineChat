package de.minetrain.minechat.gui.utils;

import java.awt.Dimension;
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
	
}
