package de.minetrain.minechat.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import de.minetrain.minechat.config.ConfigManager;
import de.minetrain.minechat.gui.frames.EditChannelFrame;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.obj.StatusBar;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.TwitchCredentials;
import de.minetrain.minechat.utils.TextureManager;

public class Main {
	public static final TextureManager TEXTURE_MANAGER = new TextureManager();
	public static final StatusBar LOADINGBAR = new StatusBar();
	public static ConfigManager CONFIG;
	public static MainFrame mainFrame;
	private static JFrame onboardingFrame;
//	sendMessage("minetrainlp", "", "test");
//	try {Thread.sleep(1500);} catch (InterruptedException e) { }
//	sendMessage("minetrainlp", "", "test "+ "ㅤ");
	
	//TODO Cansel message queue.
	
	public static void main(String[] args) {
		LOADINGBAR.setSize(400, 50);
		LOADINGBAR.setLocation(50, 600);
		LOADINGBAR.setFont(new Font(null, Font.BOLD, 10));
		
		JLabel textureLabel = new JLabel(new ImageIcon("data/texture/MineChatTexturOnboarding.png"));
	    textureLabel.setSize(500, 700);
	    textureLabel.add(LOADINGBAR);
		
	    onboardingFrame = new JFrame();
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
		new TwitchManager(new TwitchCredentials());
	}
	
	public static void openMainFrame(){
		LOADINGBAR.setProgress("Launching MainFrame", 70);
		mainFrame = new MainFrame();
		if(!mainFrame.titleBar.mainTab.isOccupied()){
			new EditChannelFrame(mainFrame, mainFrame.titleBar.mainTab);
		}
		onboardingFrame.dispose();
	}

}
