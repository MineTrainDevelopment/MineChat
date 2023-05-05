package de.minetrain.minechat.main;

import de.minetrain.minechat.Twitch.TwitchCredentials;
import de.minetrain.minechat.Twitch.TwitchManager;
import de.minetrain.minechat.gui.MainFrame;

public class Main {
//	sendMessage("minetrainlp", "", "test");
//	try {Thread.sleep(1500);} catch (InterruptedException e) { }
//	sendMessage("minetrainlp", "", "test "+ "ㅤ");
	
	public static void main(String[] args) {
		new MainFrame();
		new TwitchManager(new TwitchCredentials());
	}

}
