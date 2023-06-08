package de.minetrain.minechat.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.JLabel;

import de.minetrain.minechat.config.ConfigManager;
import de.minetrain.minechat.gui.frames.EditChannelFrame;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.obj.StatusBar;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.twitch.MessageManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.TwitchCredentials;
import de.minetrain.minechat.utils.Settings;

public class Main {
	public static final TextureManager TEXTURE_MANAGER = new TextureManager();
	public static final StatusBar LOADINGBAR = new StatusBar();
	public static final String VERSION = "V0.5";
	public static ConfigManager CONFIG;
	public static ConfigManager EMOTE_INDEX;
	public static MainFrame MAIN_FRAME;
	private static JFrame onboardingFrame;
	private static final String OAuth2_URL = "https://id.twitch.tv/oauth2/authorize?client_id={CLIENT_ID}&redirect_uri={REDIRECT_URL}&response_type=token&scope=chat:edit+chat:read+channel:moderate+moderation:read";
	
//	public static void main(String[] args) throws IOException {
//        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
//        server.createContext("/oauth_callback", new OAuthCallbackHandler());
//        server.start();
//        System.out.println("OAuth2 Server gestartet. Öffnen Sie den Browser und besuchen Sie http://localhost:8000/oauth_callback");
//    }
//	
//	
//	static class OAuthCallbackHandler implements HttpHandler {
//	    @Override
//	    public void handle(HttpExchange exchange) throws IOException {
//	        // Extrahieren Sie den Autorisierungscode aus der Anfrage
//	        String requestURI = exchange.getRequestURI().toString();
//	        System.out.println("Query: "+exchange.getRequestURI().getQuery());
//	        
//	        // Ausgabe der aufgerufenen URL in der Konsole
//	        System.out.println("Aufgerufene URL: " + requestURI);
//
//	        // Extrahieren Sie das URL-Fragment mit JavaScript und senden Sie es an den Server
//	        String script = "<script>" +
//	                "var fragment = window.location.hash.substring(1);" +
//	                "var xhr = new XMLHttpRequest();" +
//	                "xhr.open('GET', '/oauth_callback/fragment?fragment=' + fragment, true);" +
//	                "xhr.send();" +
//	                "</script>";
//
//	        // Senden Sie die HTML-Seite mit dem JavaScript-Code zurück zum Client
//	        String response = "<html><body>" + script + "</body></html>";
//	        exchange.sendResponseHeaders(200, response.length());
//	        OutputStream outputStream = exchange.getResponseBody();
//	        outputStream.write(response.getBytes());
//	        outputStream.close();
//	        
//	        if(requestURI.contains("fragment=access_token=")){
//	        	String bearerToken = requestURI.substring(0, requestURI.indexOf("&")).replace("/oauth_callback/fragment?fragment=access_token=", "");
//	        	System.out.println("Token -> "+bearerToken);
//	        }
//	    }
//	}
	
	public static void main(String[] args) {
		new Settings();
		LOADINGBAR.setSize(400, 50);
		LOADINGBAR.setLocation(50, 600);
		LOADINGBAR.setFont(new Font(null, Font.BOLD, 10));
		
		JLabel textureLabel = new JLabel(TEXTURE_MANAGER.getOnboarding());
	    textureLabel.setSize(500, 700);
	    textureLabel.add(LOADINGBAR);
		
	    onboardingFrame = new JFrame("MineChat "+VERSION);
	    onboardingFrame.setLayout(null);
	    onboardingFrame.setSize(500, 700);
	    onboardingFrame.setUndecorated(true);
	    onboardingFrame.setLayout(null);
	    onboardingFrame.setResizable(false);
	    onboardingFrame.setLocationRelativeTo(null);
	    onboardingFrame.getContentPane().setBackground(new Color(173, 96, 164));
	    onboardingFrame.setShape(new RoundRectangle2D.Double(0, 0, 500, 700, 50, 50));
	    onboardingFrame.add(textureLabel);
	    onboardingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    onboardingFrame.setVisible(true);
	    
	    CONFIG = new ConfigManager("data/config.yml", false);
	    EMOTE_INDEX = new ConfigManager(TextureManager.texturePath+"Icons/emoteIndex.yml", true);
		new TwitchManager(new TwitchCredentials());
		
		TextureManager.downloadPublicData();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	TwitchManager.leaveAllChannel();
		    }
		});
	}
	
	public static void openMainFrame(){
		LOADINGBAR.setProgress("Launching MainFrame", 70);
		MAIN_FRAME = new MainFrame();
		if(!MAIN_FRAME.getTitleBar().getMainTab().isOccupied()){
			new EditChannelFrame(MAIN_FRAME, MAIN_FRAME.getTitleBar().getMainTab());
		}
		onboardingFrame.dispose();
		MessageManager.getMessageHandler().updateQueueButton();
		
//		for (int i=0; i<20; i++) {
//			TitleBar.currentTab.getChatWindow().displayMessage("Message "+i, "test", Color.PINK);
//		}
//		
//		TitleBar.currentTab.getChatWindow().displayMessage("Message mine", "test", Color.PINK);
//		
//		for (int i=21; i<1000; i++) {
//			TitleBar.currentTab.getChatWindow().displayMessage("Message "+i, "test", Color.PINK);
//		}
	}
	
}
