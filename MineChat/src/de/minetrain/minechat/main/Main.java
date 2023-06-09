package de.minetrain.minechat.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;

import javax.naming.directory.InvalidAttributesException;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.config.obj.TwitchEmote;
import de.minetrain.minechat.gui.frames.EditChannelFrame;
import de.minetrain.minechat.gui.frames.GetCredentialsFrame;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.gui.frames.settings.SettingsFrame;
import de.minetrain.minechat.gui.frames.settings.editors.AddWordHighlightFrame;
import de.minetrain.minechat.gui.frames.settings.editors.CustomiseTimeFormatFrame;
import de.minetrain.minechat.gui.obj.StatusBar;
import de.minetrain.minechat.gui.obj.chat.userinput.textarea.MineTextArea;
import de.minetrain.minechat.gui.obj.chat.userinput.textarea.SuggestionObj;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.twitch.MessageManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.CredentialsManager;

public class Main {
	public static final TextureManager TEXTURE_MANAGER = new TextureManager();
	public static final StatusBar LOADINGBAR = new StatusBar();
	public static final String VERSION = "V0.7";
	public static YamlManager CONFIG;
	public static YamlManager EMOTE_INDEX;
	public static MainFrame MAIN_FRAME;
	private static JFrame onboardingFrame;
	public static Settings rftnfijdg;
	
	public static void main(String[] args) throws Exception {
		LOADINGBAR.setSize(400, 50);
		LOADINGBAR.setLocation(50, 600);
		LOADINGBAR.setFont(new Font(null, Font.BOLD, 10));
//		
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
		LOADINGBAR.setProgress("Loading auto suggestion.", 60);
		
		TwitchEmote.getEmotesByName().entrySet().forEach(entry -> {//May also be caled when new emotes get addet.
			MineTextArea.addToStaticDictionary(new SuggestionObj(":"+entry.getKey()+(entry.getValue().contains("bttv") ? " -- BTTV" : ""), entry.getValue().replace("_1.png", "_1_BG.png")));
		});
		
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
        
		LOADINGBAR.setProgress("Launching MainFrame", 70);
		MAIN_FRAME = new MainFrame();
        
		if(!MAIN_FRAME.getTitleBar().getMainTab().isOccupied()){
			new EditChannelFrame(MAIN_FRAME, MAIN_FRAME.getTitleBar().getMainTab());
		}
		onboardingFrame.dispose();
        MessageManager.updateQueueButton();
        
        
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
	
}
