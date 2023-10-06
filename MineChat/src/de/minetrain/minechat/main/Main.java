package de.minetrain.minechat.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;

import javax.naming.directory.InvalidAttributesException;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.frames.EditChannelFrame;
import de.minetrain.minechat.gui.frames.GetCredentialsFrame;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.gui.frames.settings.SettingsFrame;
import de.minetrain.minechat.gui.frames.settings.channel.ChannelSettings;
import de.minetrain.minechat.gui.obj.ChatStatusPanel;
import de.minetrain.minechat.gui.obj.StatusBar;
import de.minetrain.minechat.gui.obj.chat.userinput.textarea.MineTextArea;
import de.minetrain.minechat.gui.obj.chat.userinput.textarea.SuggestionObj;
import de.minetrain.minechat.gui.obj.panels.tabel.MineTabel;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.twitch.MessageManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.CredentialsManager;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	public static final TextureManager TEXTURE_MANAGER = new TextureManager();
	public static final StatusBar LOADINGBAR = new StatusBar();
	public static final String VERSION = "V0.8";
	public static YamlManager CONFIG;
	public static YamlManager EMOTE_INDEX;
	public static MainFrame MAIN_FRAME;
	public static JFrame onboardingFrame;
	public static Settings rftnfijdg;
	private static String[] args;
	
	public static void main(String[] args) throws Exception {
		Main.args = args;
//		JFrame frame = new JFrame();
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setVisible(true);
		
//		MineDialog dialog = new MineDialog(frame, ChatStatusPanel.getMineChatStatusText().toString(), new Dimension(500,500));
//		dialog.setExitOnCancelButton(true);
//		dialog.addContent(new MineTabel(), BorderLayout.CENTER);
//		dialog.setVisible(true);

//		new Settings();
//		Settings.reloadHighlights();
		
//		ChannelSettings settings = new ChannelSettings(frame);
//		settings.setExitOnCancelButton(true);
//		settings.setVisible(true);
		
		
		
		
		
		
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
	    
	    LOADINGBAR.setProgress("Reading config file.", 5);
	    try {
	    	CONFIG = new YamlManager("data/config.yml");
	    	EMOTE_INDEX = new YamlManager(TextureManager.texturePath+"Icons/emoteIndex.yml");
		} catch (Exception ex) {
	    	LOADINGBAR.setError("config.yml or emoteIndex.yml not found!");
	    	return;
		}
	    
	    LOADINGBAR.setProgress("Loading Twitch credentials.", 15);
	    try {
	    	new CredentialsManager();
		} catch (InvalidAttributesException ex) {
			new GetCredentialsFrame(onboardingFrame);
		} catch (Exception ex) {
			CredentialsManager.deleteCredentialsFile();
			Main.LOADINGBAR.setError("Invalid Twitch Credentials!");
			logger.error("Invalid twitch credentials!", ex);
			return;
		}

		CredentialsManager credentials = new CredentialsManager();
		
		new TwitchManager(credentials);
		new Settings();
		TextureManager.downloadPublicData();
		Settings.reloadHighlights();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	TwitchManager.leaveAllChannel();
		    }
		});
	}
	
	public static void openMainFrame(){
		LOADINGBAR.setProgress("Loading auto suggestion and emotes.", 60);
		new EmoteManager();
		EmoteManager.load();
		
        MineTextArea.addToStaticDictionary(new SuggestionObj("{TIME}", null, "{TIME} ---> 14:03"));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{DATE}", null, "{DATE} ---> 29.06.2023"));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{DAY}", null, "{DAY} ---> Monday"));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{MYSELF}", null, "{MYSELF} ---> @"+TwitchManager.ownerChannelName));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{STREAMER}", null, "{STREAMER} ---> @STREAMER"));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{VIEWER}", null, "{VIEWER} - NOT WORKING"));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{UPTIME}", null, "{UPTIME} - NOT WORKING"));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{GAME}", null, "{GAME} - NOT WORKING"));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{TITLE}", null, "{TITLE} - NOT WORKING"));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{MY_MESSAGES}", null));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{TOTAL_MESSAGES}", null));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{TOTAL_SUBS}", null));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{TOTAL_RESUBS}", null));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{TOTAL_GIFTSUB}", null));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{TOTAL_NEWSUB}", null));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{TOTAL_BITS}", null));// TODO
        MineTextArea.addToStaticDictionary(new SuggestionObj("{CLIP_BOARD}", null, "{CLIP_BOARD} - WARNING!"));// TODO
        
		LOADINGBAR.setProgress("Launching MainFrame", 70);
		MAIN_FRAME = new MainFrame();
        
		if(!MAIN_FRAME.getTitleBar().getMainTab().isOccupied()){
			new EditChannelFrame(MAIN_FRAME, MAIN_FRAME.getTitleBar().getMainTab());
		}
		onboardingFrame.dispose();
        MessageManager.updateQueueButton();
        
        
        
        
        
        
//        ChannelSettings settings = new ChannelSettings(MAIN_FRAME, MAIN_FRAME.getTitleBar().getMainTab());
//		settings.setExitOnCancelButton(true);
//		settings.setVisible(true);
        
        
//        Object[] array = TwitchEmote.getEmotesByName().entrySet().toArray();
//        Random random = new Random();
//        for (int i=0; i<50000; i++) {
//			String string = (array[Math.abs(random.nextInt(array.length))]).toString().split("/")[4];
//			System.out.println("Emote -> "+string);
//			TitleBar.currentTab.getChatWindow().displayMessage(string+" - Message", "test", Color.PINK);//5
//		}
        
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
	
	
	
	
	
	
//Programm hiernach gestorben
//	[22.06.2023 | 18:27:59] >> ERROR << [com.github.twitch4j.client.websocket.WebsocketConnection] - connection to webSocket server wss://irc-ws.chat.twitch.tv:443 failed: retrying ...
//		com.neovisionaries.ws.client.WebSocketException: Failed to resolve hostname irc-ws.chat.twitch.tv:443: Dies ist normalerweise ein zeitweiliger Fehler bei der Aufl�sung von Hostnamen. Grund ist, dass der lokale Server keine R�ckmeldung vom autorisierenden Server erhalten hat (irc-ws.chat.twitch.tv)
//			at com.neovisionaries.ws.client.SocketConnector.resolveHostname(SocketConnector.java:180)
//			at com.neovisionaries.ws.client.SocketConnector.connectSocket(SocketConnector.java:108)
//			at com.neovisionaries.ws.client.SocketConnector.doConnect(SocketConnector.java:238)
//			at com.neovisionaries.ws.client.SocketConnector.connect(SocketConnector.java:189)
//			at com.neovisionaries.ws.client.WebSocket.connect(WebSocket.java:2351)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.connect(WebsocketConnection.java:225)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.reconnect(WebsocketConnection.java:295)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.connect(WebsocketConnection.java:245)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.reconnect(WebsocketConnection.java:295)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.connect(WebsocketConnection.java:245)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.reconnect(WebsocketConnection.java:295)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.connect(WebsocketConnection.java:245)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.reconnect(WebsocketConnection.java:295)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.connect(WebsocketConnection.java:245)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.reconnect(WebsocketConnection.java:295)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.connect(WebsocketConnection.java:245)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.reconnect(WebsocketConnection.java:295)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.connect(WebsocketConnection.java:245)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.reconnect(WebsocketConnection.java:295)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.connect(WebsocketConnection.java:245)
//			at com.github.twitch4j.client.websocket.WebsocketConnection.reconnect(WebsocketConnection.java:295)
//			at com.github.twitch4j.client.websocket.WebsocketConnection$1.lambda$onDisconnected$1(WebsocketConnection.java:150)
//			at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:539)
//			at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
//			at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:304)
//			at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
//			at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
//			at java.base/java.lang.Thread.run(Thread.java:833)
//		Caused by: java.net.UnknownHostException: Dies ist normalerweise ein zeitweiliger Fehler bei der Aufl�sung von Hostnamen. Grund ist, dass der lokale Server keine R�ckmeldung vom autorisierenden Server erhalten hat (irc-ws.chat.twitch.tv)
//			at java.base/java.net.Inet6AddressImpl.lookupAllHostAddr(Native Method)
//			at java.base/java.net.InetAddress$PlatformNameService.lookupAllHostAddr(InetAddress.java:933)
//			at java.base/java.net.InetAddress.getAddressesFromNameService(InetAddress.java:1519)
//			at java.base/java.net.InetAddress$NameServiceAddresses.get(InetAddress.java:852)
//			at java.base/java.net.InetAddress.getAllByName0(InetAddress.java:1509)
//			at java.base/java.net.InetAddress.getAllByName(InetAddress.java:1367)
//			at java.base/java.net.InetAddress.getAllByName(InetAddress.java:1301)
//			at com.neovisionaries.ws.client.SocketConnector.resolveHostname(SocketConnector.java:139)
//			... 27 more
	
	
//	[08:38:00] >> ERROR<< | Unhandled exception caught dispatching event ChannelMessageEvent
//	java.lang.reflect.InvocationTargetException
//		at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
//		at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:64)
//		at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
//		at java.base/java.lang.reflect.Method.invoke(Method.java:564)
//		at com.github.philippheuer.events4j.simple.SimpleEventHandler.lambda$handleAnnotationHandlers$5(SimpleEventHandler.java:130)
//		at java.base/java.util.concurrent.CopyOnWriteArrayList.forEach(CopyOnWriteArrayList.java:807)
//		at com.github.philippheuer.events4j.simple.SimpleEventHandler.lambda$handleAnnotationHandlers$6(SimpleEventHandler.java:127)
//		at java.base/java.util.concurrent.ConcurrentHashMap.forEach(ConcurrentHashMap.java:1603)
//		at com.github.philippheuer.events4j.simple.SimpleEventHandler.handleAnnotationHandlers(SimpleEventHandler.java:126)
//		at com.github.philippheuer.events4j.simple.SimpleEventHandler.publish(SimpleEventHandler.java:100)
//		at com.github.philippheuer.events4j.core.EventManager.lambda$publish$0(EventManager.java:157)
//		at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
//		at com.github.philippheuer.events4j.core.EventManager.publish(EventManager.java:157)
//		at com.github.twitch4j.chat.events.IRCEventHandler.onChannelMessage(IRCEventHandler.java:124)
//		at com.github.philippheuer.events4j.simple.SimpleEventHandler.lambda$handleConsumerHandlers$3(SimpleEventHandler.java:112)
//		at java.base/java.util.concurrent.CopyOnWriteArrayList.forEach(CopyOnWriteArrayList.java:807)
//		at com.github.philippheuer.events4j.simple.SimpleEventHandler.lambda$handleConsumerHandlers$4(SimpleEventHandler.java:112)
//		at java.base/java.lang.Iterable.forEach(Iterable.java:75)
//		at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1087)
//		at com.github.philippheuer.events4j.simple.SimpleEventHandler.handleConsumerHandlers(SimpleEventHandler.java:109)
//		at com.github.philippheuer.events4j.simple.SimpleEventHandler.publish(SimpleEventHandler.java:99)
//		at com.github.philippheuer.events4j.core.EventManager.lambda$publish$0(EventManager.java:157)
//		at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
//		at com.github.philippheuer.events4j.core.EventManager.publish(EventManager.java:157)
//		at com.github.twitch4j.chat.TwitchChat.lambda$onTextMessage$16(TwitchChat.java:572)
//		at java.base/java.util.Arrays$ArrayList.forEach(Arrays.java:4203)
//		at com.github.twitch4j.chat.TwitchChat.onTextMessage(TwitchChat.java:544)
//		at com.github.twitch4j.client.websocket.WebsocketConnection$1.onTextMessage(WebsocketConnection.java:123)
//		at com.neovisionaries.ws.client.ListenerManager.callOnTextMessage(ListenerManager.java:353)
//		at com.neovisionaries.ws.client.ReadingThread.callOnTextMessage(ReadingThread.java:266)
//		at com.neovisionaries.ws.client.ReadingThread.callOnTextMessage(ReadingThread.java:244)
//		at com.neovisionaries.ws.client.ReadingThread.handleTextFrame(ReadingThread.java:969)
//		at com.neovisionaries.ws.client.ReadingThread.handleFrame(ReadingThread.java:752)
//		at com.neovisionaries.ws.client.ReadingThread.main(ReadingThread.java:108)
//		at com.neovisionaries.ws.client.ReadingThread.runMain(ReadingThread.java:64)
//		at com.neovisionaries.ws.client.WebSocketThread.run(WebSocketThread.java:45)
//	Caused by: java.lang.NullPointerException: pattern
//		at java.base/java.util.Objects.requireNonNull(Objects.java:233)
//		at java.base/java.time.format.DateTimeFormatterBuilder.appendPattern(DateTimeFormatterBuilder.java:1709)
//		at java.base/java.time.format.DateTimeFormatter.ofPattern(DateTimeFormatter.java:589)
//		at de.minetrain.minechat.gui.obj.ChatWindowMessageComponent.formatText(ChatWindowMessageComponent.java:298)
//		at de.minetrain.minechat.gui.obj.ChatWindowMessageComponent.<init>(ChatWindowMessageComponent.java:202)
//		at de.minetrain.minechat.gui.frames.ChatWindow.displayMessage(ChatWindow.java:127)
//		at de.minetrain.minechat.gui.frames.ChatWindow.displayMessage(ChatWindow.java:118)
//		at de.minetrain.minechat.twitch.TwitchListner.onAbstractChannelMessage(TwitchListner.java:116)
//		... 36 more

}
