package de.minetrain.minechat.main;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.UserSettings;
import de.minetrain.minechat.features.autoreply.AutoReplyManager;
import de.minetrain.minechat.features.messagehighlight.Highlight;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.panes.ChannelPane;
import de.minetrain.minechat.gui.panes.TitleBarPane;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.gui.viewmodel.AppInfoViewModel;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.gui.viewmodel.HighlightViewModel;
import de.minetrain.minechat.gui.viewmodel.SettingsViewModel;
import de.minetrain.minechat.twitch.MessageManager;
import de.minetrain.minechat.twitch.TwitchHelper;
import de.minetrain.minechat.twitch.TwitchListener;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.TwitchPollingService;
import de.minetrain.minechat.utils.audio.AudioManager;
import javafx.application.Application;
import javafx.collections.MapChangeListener.Change;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	public static final String VERSION = "V0.9";
	// TODO resolve statics...
	public static AudioManager audioManager;
	private static ChannelManager channelManager;
	private static EmoteManager emoteManager;
	private static SettingsViewModel settingsViewModel;
	private static AppInfoViewModel appInfoViewModel;
	private static final int loadingSteps = 13;
	public static boolean isGuiOpen = false;

	public static void test(String[] args) throws Exception {
		loadingProgressLogging(2, "Prepare eclipse store.");
		EclipseStoreKeeper.init();

		loadingProgressLogging(3, "Initialising user settings");
		settingsViewModel = loadSettings();

		loadingProgressLogging(4, "Preparing emotes");
		emoteManager = new EmoteManager();

		loadingProgressLogging(5, "Fetching audio fiels.");
		audioManager = new AudioManager();

		loadingProgressLogging(8, "Login in...");

		try {
			String oAuth2Token = aquireOAuth2Token();
			if (oAuth2Token == null) {
				LOG.error("Unable to acquire OAuth2 token. Exiting...");
				System.exit(0);
			}
			loadingProgressLogging(9, "Connecting to Twitch Helix.");
			TwitchManager.init(oAuth2Token).registerListener(new TwitchListener(new AutoReplyManager()));
			loadingProgressLogging(10, "Prepare message highlight strings.");
			if (!EclipseStoreKeeper.root().userSettings().isInitialized()) {
				int color = ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT_DEFAULT;
				EclipseStoreKeeper.root().userSettings().addHighlightString(new HighlightString(TwitchHelper.generateNameRegex(TwitchHelper.getSelfUser().getDisplayName()), color, color));
				MessageManager.setDefaultHighlightSettings();
				EclipseStoreKeeper.root().userSettings().setInitialized();
			}
			loadingProgressLogging(11, "Validate public badges and emotes.");
			TextureManager.downloadPublicData();
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			System.exit(0);
		}

		loadingProgressLogging(12, "Building main frame.");
		launch(args);

        //First open the frame, then load all channels and add them live into frame.

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				TwitchHelper.shutdown();
			}
		});
	}

	private static SettingsViewModel loadSettings() {
		SettingsViewModel settingsViewModel = new SettingsViewModel();
		UserSettings userSettings = EclipseStoreKeeper.root().userSettings();
		Collection<Highlight> storedHighlights = userSettings.getAllHighlights().values();
		for (Highlight highlight : storedHighlights) {
			HighlightViewModel highlightViewModel = settingsViewModel.getHighlightViewModel(highlight.getType());
			highlightViewModel.apply(highlight);
		}
		settingsViewModel.setAutoReplyOnlyInActiveTab(userSettings.isAutoReplyOnlyInActiveTab());
		settingsViewModel.setUndoSteps(userSettings.getUndoSteps());
		settingsViewModel.setUndoLetterMode(userSettings.isUndoLetterMode());
		settingsViewModel.setReplyType(userSettings.getReplyType());
		settingsViewModel.setGreetingType(userSettings.getGreetingType());
		settingsViewModel.setShowEmoteOnlyMessages(userSettings.isShowEmoteOnlyMessages());
		settingsViewModel.setUserAwayThreshold(Duration.ofMillis(userSettings.getUserAwayThreshold()));
		settingsViewModel.setMessageTimeFormat(userSettings.getMessageTimeFormat());
		settingsViewModel.setTimeFormat(userSettings.getTimeFormat());
		settingsViewModel.setDateFormat(userSettings.getDateFormat());
		settingsViewModel.setDayFormat(userSettings.getDayFormat());
		settingsViewModel.setBaseColor(userSettings.getBaseColor());
		settingsViewModel.setAccentColor(userSettings.getAccentColor());
		settingsViewModel.setBorderColor(userSettings.getBorderColor());
		return settingsViewModel;
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
					LOG.error("Twitch Client ID resource not found. Exiting...");
					return null;
				}
				String twitchClientId = new String(is.readAllBytes());
				if (StringUtils.isBlank(twitchClientId) || twitchClientId.contains("<client_id>")) {
					LOG.error("Twitch Client ID is not properly set. Exiting...");
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
	private static Application instance;

	@Override
	public void start(Stage primaryStage) throws Exception {
		instance = this;
		Main.primaryStage = primaryStage;
		primaryStage.setTitle("MineChat - JavaFX rework");
		primaryStage.initStyle(StageStyle.UNIFIED);

		titleBar = new TitleBarPane();

		channelPane = new ChannelPane();
		MessageManager.setHighlightChangeListener(channelPane::refreshMessageWidgets);

		BorderPane mainContentPane = new BorderPane();
		mainContentPane.setId("main-pane");
		mainContentPane.setTop(titleBar);
		mainContentPane.setCenter(channelPane);

		// Set up the scene
		Scene scene = new Scene(mainContentPane, 500, 700);
		scene.setFill(Color.TRANSPARENT);
		scene.getStylesheets().add("style_v2.css");
		mainContentPane.setStyle(getSettingsViewModel().getRootStyle());
		getSettingsViewModel().rootStyleProperty().addListener((_, _, newStyle) -> mainContentPane.setStyle(newStyle));

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

	public static EmoteManager getEmoteManager() {
		return emoteManager;
	}

	public static SettingsViewModel getSettingsViewModel() {
		return settingsViewModel;
	}

	public static AppInfoViewModel getAppInfoViewModel() {
		if (appInfoViewModel == null) {
			appInfoViewModel = loadAppInfo();
		}
		return appInfoViewModel;
	}

	public static Application getApplication() {
		return instance;
	}

	private static AppInfoViewModel loadAppInfo() {
		Properties properties = loadResourceProperties("/version.properties");
		return new AppInfoViewModel(
			properties.getProperty("app.name", "MineChat"),
			properties.getProperty("app.version", "?"),
			parseIsoTimestamp(properties.getProperty("build.timestamp")),
			System.getProperty("java.version"),
			System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")",
			properties.getProperty("git.commit", "?"),
			properties.getProperty("git.branch", "?"),
			properties.getProperty("project.url", ""),
			properties.getProperty("copyright.start", "?"),
			properties.getProperty("copyright.holders", "?")
		);
	}

	private static Instant parseIsoTimestamp(String timestamp) {
		if (timestamp == null) {
			return null;
		}
		try {
			return Instant.parse(timestamp);
		} catch (DateTimeParseException e) {
			LOG.warn("Could not parse ISO timestamp: {}", timestamp);
			LOG.debug("Exception details:", e);
			return null;
		}
	}

	private static Properties loadResourceProperties(String resourcePath) {
		Properties properties = new Properties();
		try (InputStream is = Main.class.getResourceAsStream(resourcePath)) {
			if (is != null) {
				properties.load(is);
			} else {
				LOG.warn("Resource not found: {}", resourcePath);
			}
		} catch (IOException e) {
			LOG.error("Error loading properties from resource: {}", resourcePath, e);
		}
		return properties;
	}

	private static void loadingProgressLogging(int stage, String message) {
		LOG.info("Loading... "+new DecimalFormat("0").format(Math.round(((double) stage / loadingSteps) * 100)) + "%"+" - "+message);
	}

	private static void loadingAsyncProgressLogging(int stage, String message) {
		LOG.info("[Async] Loading... "+new DecimalFormat("0").format(Math.round(((double) stage / loadingSteps) * 100)) + "%"+" - "+message);
	}
}
