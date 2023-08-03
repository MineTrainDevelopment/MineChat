package de.minetrain.minechat.twitch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.twitch.obj.TwitchMessage;

public class MessageLogger {
	public static final YamlManager chatLog = new YamlManager("data/ChatLog.yml");
	
	public MessageLogger() {
		chatLog.setSuppressWarnings(true);
	}
	
	public void log(TwitchMessage message){
		if(message.isEmoteOnly() || Settings.CHAT_LOG){
			return;
		}
		
		String loggingMessage = message.getUserName()+": "+message.getMessage();
		
		List<String> thrads = chatLog.getStringList("Messages."+message.getReplyId());
		thrads.add(loggingMessage);
		chatLog.setStringList("Messages."+message.getReplyId(), thrads);
		
		List<String> userThrads = chatLog.getStringList("User."+message.getUserId());
		if(!userThrads.contains(message.getReplyId())){
			userThrads.add(message.getReplyId());
			chatLog.setStringList("User."+message.getUserId(), userThrads);
		}
	}
	
	public void save(){
		chatLog.saveConfigToFile();
	}
	
}
