package de.minetrain.minechat.main;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.features.autoreply.AutoReplyManager;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.panes.ChannelPane;
import de.minetrain.minechat.gui.panes.TitleBarPane;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.twitch.TwitchHelper;
import de.minetrain.minechat.twitch.TwitchListener;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.TwitchPollingService;
import de.minetrain.minechat.utils.audio.AudioManager;
import de.minetrain.minechat.utils.events.EventManager;
import de.minetrain.minechat.utils.plugins.PluginManager;
import javafx.application.Application;
import javafx.collections.MapChangeListener.Change;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	public static final String VERSION = "V0.9";
	// TODO resolve statics...
	public static AudioManager audioManager;
	public static EventManager eventManager;
	public static PluginManager pluginManager;
	private static ChannelManager channelManager;
	private static EmoteManager emoteManager;
	private static final int loadingSteps = 13;
	public static boolean isGuiOpen = false;

	public static void test(String[] args) throws Exception {
		loadingProgressLogging(1, "Initialising database manager");
		new DatabaseManager();

		loadingProgressLogging(2, "Prepare eclipse store.");
		EclipseStoreKeeper.init();

		loadingProgressLogging(3, "Initialising user settings");
		new Settings();

		loadingProgressLogging(4, "Preparing emotes");
		emoteManager = new EmoteManager();

		loadingProgressLogging(5, "Fetching audio fiels.");
		audioManager = new AudioManager();

		loadingProgressLogging(6, "Preparing MineChat events.");
		eventManager = new EventManager();

		loadingProgressLogging(7, "Loading custom plugins.");
		pluginManager = new PluginManager();

		loadingProgressLogging(8, "Login in...");

		try {
			String oAuth2Token = aquireOAuth2Token();
			if (oAuth2Token == null) {
				logger.error("Unable to acquire OAuth2 token. Exiting...");
				System.exit(0);
			}
			loadingProgressLogging(9, "Connecting to Twitch Helix.");
			TwitchManager.init(oAuth2Token).registerListener(new TwitchListener(new AutoReplyManager()));
			loadingProgressLogging(10, "Prepare message highlight strings.");
			if (!EclipseStoreKeeper.root().userSettings().isInitialized()) {
				String color = ColorManager.encode(ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT);
				EclipseStoreKeeper.root().userSettings().addHighlightString(new HighlightString(TwitchHelper.generateNameRegex(TwitchHelper.getSelfUser().getDisplayName()), color, color));
			}
			loadingProgressLogging(11, "Validate public badges and emotes.");
			TextureManager.downloadPublicData();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			System.exit(0);
		}

		loadingProgressLogging(12, "Building main frame.");
		launch(args);

        //First open the frame, then load all channels and add them live into frame.

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
//		    	DatabaseManager.getChannelStatistics().saveAllChannelStatistics();
//		    	EclipseStoreTest.getStoreRoot().saveAllChannelStatistics();
				TwitchHelper.leaveAllChannel();
				TwitchHelper.shutdown();
			}
		});
	}

	// TODO Do we need to refresh periodically?
	private static String aquireOAuth2Token() throws IOException {
		String oAuth2Token = EclipseStoreKeeper.root().credentials().getOAuth2Token();
		if (oAuth2Token != null && !TwitchManager.validateOAuthToken(oAuth2Token).join()) {
			oAuth2Token = null;
		}
		if (oAuth2Token == null || StringUtils.isBlank(oAuth2Token)) {
			try (InputStream is = Main.class.getResourceAsStream("/client.id")) {
				if (is == null) {
					logger.error("Twitch Client ID resource not found. Exiting...");
					return null;
				}
				String twitchClientId = new String(is.readAllBytes());
				if (StringUtils.isBlank(twitchClientId) || twitchClientId.contains("<client_id>")) {
					logger.error("Twitch Client ID is not properly set. Exiting...");
					return null;
				}
				oAuth2Token = TwitchManager.requestOAuthToken(twitchClientId).join();
				EclipseStoreKeeper.root().credentials().setOAuth2Token(oAuth2Token);
			}
		}
		return oAuth2Token;
	}

	public static TitleBarPane titleBar;
	public static ChannelPane channelPane;
	public static Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		Main.primaryStage = primaryStage;
		primaryStage.setTitle("MineChat - JavaFX rework");
		primaryStage.initStyle(StageStyle.UNIFIED);

		titleBar = new TitleBarPane();

		channelPane = new ChannelPane();

		BorderPane mainContentPane = new BorderPane();
		mainContentPane.setId("main-pane");
		mainContentPane.setTop(titleBar);
		mainContentPane.setCenter(channelPane);

		// Set up the scene
		Scene scene = new Scene(mainContentPane, 500, 700);
		scene.setFill(Color.TRANSPARENT);
//		scene.getStylesheets().add("style.css");
		scene.getStylesheets().add("style_v2.css");

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
		primaryStage.setMinWidth(516);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(_ -> System.exit(0));
		isGuiOpen = true;

		// LoadChannels
		loadingAsyncProgressLogging(1, "Loading channels.");
		TwitchPollingService twitchPollingService = new TwitchPollingService();
		channelManager = new ChannelManager(twitchPollingService);
		channelManager.init();

		channelManager.getChannelViewModelMap().addListener((Change<? extends String, ? extends ChannelViewModel> change) -> {
			if (change.wasRemoved()) {
				titleBar.getChannels().remove(change.getValueRemoved());
			}
			if (change.wasAdded()) {
				titleBar.getChannels().add(change.getValueAdded());
			}
		});
		titleBar.getChannels().setAll(channelManager.getChannelViewModels());
		channelPane.channelProperty().bind(channelManager.activeChannelProperty());

		twitchPollingService.start();
		twitchPollingService.queueChannelChatSettingsRefresh();
	}

	public static ChannelManager getChannelManager() {
		return channelManager;
	}

	public static AudioManager getAudioManager() {
		return audioManager;
	}

	public static EventManager getEventManager() {
		return eventManager;
	}

	public static EmoteManager getEmoteManager() {
		return emoteManager;
	}

	/**
	 * Extracts the domain from a given URL.
	 *
	 * @param input The input URL to extract the domain from.
	 * @return The extracted domain or the original input if the domain can´t be extracted.
	 */
	// TODO Can´t extract the domain from -> instagram.com/die.doni
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

	private static void loadingAsyncProgressLogging(int stage, String message) {
		logger.info("[Async] Loading... "+new DecimalFormat("0").format(Math.round(((double) stage / loadingSteps) * 100)) + "%"+" - "+message);
	}

}
