package de.minetrain.minechat.data.objectdata;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.minetrain.minechat.utils.audio.AudioVolume;

public class ChannelData {
	private final String channelId;
	private final String loginName;
	private final String displayName;
	private final String chatRole; //Viwer, MODERATOR, VIP
	private final String chatlogLevel; //null, Highlight, everything
	private final String greetingText; //full string without seperating bye \n
	private final String goodbyText; //full string without seperating bye \n
	private final String returnText; //full string without seperating bye \n
	private final String audioPath;
	private final AudioVolume audioVolume;
	
	public ChannelData(ResultSet resultSet) throws SQLException {
		this.channelId = resultSet.getString("channel_id");
		this.loginName = resultSet.getString("login_name");
		this.displayName = resultSet.getString("display_name");
		this.chatRole = resultSet.getString("chat_role");
		this.chatlogLevel = resultSet.getString("chatlog_level");
		this.greetingText = resultSet.getString("greeting_text");
		this.goodbyText = resultSet.getString("goodby_text");
		this.returnText = resultSet.getString("return_text");
		this.audioPath = resultSet.getString("audio_path");
		this.audioVolume = AudioVolume.get(resultSet.getString("audio_volume"));
	}
	

	public String getChannelId() {
		return channelId;
	}

	public String getLoginName() {
		return loginName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getChatRole() {
		return chatRole;
	}

	public String getChatlogLevel() {
		return chatlogLevel;
	}

	public String getGreetingText() {
		return greetingText;
	}

	public String getGoodbyText() {
		return goodbyText;
	}

	public String getReturnText() {
		return returnText;
	}

	public String getAudioPath() {
		return audioPath;
	}

	public AudioVolume getAudioVolume() {
		return audioVolume;
	}
	
	
	
}
