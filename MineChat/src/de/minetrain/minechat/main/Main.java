package de.minetrain.minechat.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.github.twitch4j.chat.events.ChatConnectionStateEvent;

import de.minetrain.minechat.config.ConfigManager;
import de.minetrain.minechat.gui.frames.EditChannelFrame;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.obj.StatusBar;
import de.minetrain.minechat.gui.obj.TitleBar;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.twitch.MessageManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.TwitchCredentials;
import de.minetrain.minechat.utils.Settings;

public class Main {
	public static final TextureManager TEXTURE_MANAGER = new TextureManager();
	public static final StatusBar LOADINGBAR = new StatusBar();
	public static final String VERSION = "V0.4";
	public static ConfigManager CONFIG;
	public static ConfigManager EMOTE_INDEX;
	public static MainFrame MAIN_FRAME;
	private static JFrame onboardingFrame;
//	sendMessage("minetrainlp", "", "test");
//	try {Thread.sleep(1500);} catch (InterruptedException e) { }
//	sendMessage("minetrainlp", "", "test "+ "ㅤ");
	
	
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
