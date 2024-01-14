package de.minetrain.minechat.main;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.gui.panes.InputFieldPane;
import de.minetrain.minechat.gui.panes.MacroPanelPane;
import de.minetrain.minechat.gui.panes.TitleBarPane;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.CredentialsManager;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.audio.AudioManager;
import de.minetrain.minechat.utils.events.EventManager;
import de.minetrain.minechat.utils.plugins.PluginManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	public static final String VERSION = "V0.9";
	public static AudioManager audioManager;
	public static EventManager eventManager;
	public static PluginManager pluginManager;
	private static final int loadingSteps = 12;
	
	public static void test(String[] args) throws Exception {
		loadingProgressLogging(1, "Initialising database manager");
		new DatabaseManager();
		TwitchManager.credentials = new CredentialsManager(); //TEMP
//		
//		loadingProgressLogging(2, "Initialising user settings");
//		new Settings();
//		
//		loadingProgressLogging(3, "Fetching audio fiels.");
//		audioManager = new AudioManager();
//		
//		loadingProgressLogging(4, "Preparing MineChat events.");
//		eventManager = new EventManager();
//		
//		loadingProgressLogging(5, "Loading custom plugins.");
//		pluginManager = new PluginManager();
//		
//		loadingProgressLogging(6, "Decrypt credentials file...");
//		try {
//	    	new CredentialsManager();
//		} catch (InvalidAttributesException ex) {
//			JFrame tempFrame = new JFrame();
//			tempFrame.setVisible(true);
//			new GetCredentialsFrame(tempFrame);
//		} catch (Exception ex) {
//			CredentialsManager.deleteCredentialsFile();
//			logger.error("Invalid twitch credentials!", ex);
//			System.exit(0);
//		}
//		
//		CredentialsManager credentials = new CredentialsManager();
//
//		
//		try {
//			loadingProgressLogging(7, "Connecting to Twitch Helix.");
//			new TwitchManager(credentials);
//			loadingProgressLogging(8, "Prepare message highlight strings.");
//			Settings.reloadHighlights();
//			loadingProgressLogging(9, "Validate public badges and emotes.");
//			TextureManager.downloadPublicData();
//		} catch (Exception ex) {
//			logger.error(ex.getMessage(), ex);
//			System.exit(0);
//		}
//
//		loadingProgressLogging(10, "Load AutoReply manager.");
//		new AutoReplyManager();

		loadingProgressLogging(11, "Building main frame.");
		launch(args);
		
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//		    public void run() {
////		    	DatabaseManager.getChannelStatistics().saveAllChannelStatistics();
//		    	TwitchManager.leaveAllChannel();
//		    }
//		});
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Hello JavaFX");
//        primaryStage.initStyle(StageStyle.TRANSPARENT);
        
		BorderPane topPane = new BorderPane();
		topPane.setTop(new TitleBarPane());
		topPane.setBottom(new MacroPanelPane());
		
		
		TabPane tabPane = new TabPane();

        Tab tab1 = new Tab("Planes", new Label("Show all planes available"));
        Tab tab2 = new Tab("Cars"  , new Label("Show all cars available"));
        Tab tab3 = new Tab("Boats" , new Label("Show all boats available"));

        tab1.setId("channel-tab");
        tab1.setGraphic(new ImageView(new Image("file:"+TextureManager.profilePicPath.replace("{ID}", "99351845").replace("{SIZE}", "25"), 24, 24, false, false)));
        tab2.setId("channel-tab");
        tab3.setId("channel-tab");

        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        tabPane.getTabs().add(tab3);

        Button messagePanel = new Button("Jen");
        messagePanel.setPrefSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        messagePanel.setStyle("-fx-background-color: #505050; -fx-padding: 5; -fx-text-fill: white; -fx-font-size: 20px");
//        vBox.getChildren().addAll(new Button("ttt"), tabPane, new Button("ttt"));
		

		BorderPane mainContentPane = new BorderPane();
		mainContentPane.setTop(topPane);
		mainContentPane.setCenter(messagePanel);
		mainContentPane.setBottom(new InputFieldPane());
		

        //Set up the scene
        Scene scene = new Scene(mainContentPane, 500, 700);
        scene.setFill(Color.TRANSPARENT);
		scene.getStylesheets().add("file:///C:/MineTrainDev/git_repos/MineChat/MineChat/src/de/minetrain/minechat/main/style.css");

		//TODO: Keep multiframe in mind.
		//TODO: Keep multiframe in mind.
		//TODO: Keep multiframe in mind.
		//TODO: Keep multiframe in mind.
		//TODO: Keep multiframe in mind.
		//TODO: Keep multiframe in mind.
		//TODO: Keep multiframe in mind.
		//TODO: Keep multiframe in mind.
		//TODO: Keep multiframe in mind.
		//TODO: Keep multiframe in mind.
		//TODO: Keep multiframe in mind.

        // Set the scene to the stage
		primaryStage.setMinWidth(500);
        primaryStage.setScene(scene);
        primaryStage.show();
	}



	public static AudioManager getAudioManager(){
		return audioManager;
	}
	
	public static EventManager getEventManager(){
		return eventManager;
	}
	
	/**
	 * Extracts the domain from a given URL.
	 *
	 * @param input The input URL to extract the domain from.
	 * @return The extracted domain or the original input if the domain can´t be extracted.
	 */
	public static String extractDomain(String input) {
        try {
        	if(!isValidURL(input)){throw new MalformedURLException("Invalid URL.");}
        	
        	String url = input.replace("https://", "");
	        String host = url.substring(0, url.contains("/") ? url.indexOf("/") : url.length());
	        String[] split = host.split("\\.");
	        String domain = split[split.length - 2] + "." + split[split.length - 1];
	        return domain;
		} catch (Exception ex) {
			logger.warn("Can´t extract the domain from -> "+input);
			return input;
		}
	}
	
	public static boolean isValidURL(String input){
		try {
			new URL(input).toURI();
			return true;
		} catch (MalformedURLException | URISyntaxException e) {
			return false;
		}
	}
	
	public static boolean isValidImageURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (responseCode >= 200 && responseCode < 400);
        } catch (Exception e) {
            return false;
        }
    }
	
	
	private static void loadingProgressLogging(int stage, String message) {
		logger.info("Loading... "+new DecimalFormat("0").format(Math.round(((double) stage / loadingSteps) * 100)) + "%"+" - "+message);
	}

}
