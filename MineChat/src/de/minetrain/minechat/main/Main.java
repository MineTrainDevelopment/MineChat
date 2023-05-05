package de.minetrain.minechat.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import de.minetrain.minechat.Twitch.TwitchCredentials;
import de.minetrain.minechat.Twitch.TwitchManager;
import de.minetrain.minechat.config.ConfigManager;
import de.minetrain.minechat.gui.MainFrame;
import de.minetrain.minechat.gui.objects.StatusBar;

public class Main {
	public static StatusBar LOADINGBAR = new StatusBar();
	public static ConfigManager CONFIG;
	private static JFrame onboardingFrame;
//	sendMessage("minetrainlp", "", "test");
//	try {Thread.sleep(1500);} catch (InterruptedException e) { }
//	sendMessage("minetrainlp", "", "test "+ "ㅤ");
	
	public static void main(String[] args) {
		LOADINGBAR.setSize(400, 50);
		LOADINGBAR.setLocation(50, 600);
		LOADINGBAR.setFont(new Font(null, Font.BOLD, 10));
		
		JLabel textureLabel = new JLabel(new ImageIcon("MineChatTexturOnboarding.png"));
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
	    
	    CONFIG = new ConfigManager("data/config.yml");
		new TwitchManager(new TwitchCredentials());
	}
	
	public static void openMainFrame(){
		LOADINGBAR.setProgress("Launching MainFrame", 70);
		new MainFrame();
		onboardingFrame.dispose();
	}

}
