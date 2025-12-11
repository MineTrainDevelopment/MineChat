package de.minetrain.minechat.data.databases;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.objectdata.Channel;


//Table channels:
// channel_id, login_name, display_name, chat_role, chatlog_level, greeting_text, goodby_text, return_text, audio_path, audio_volume

public class ChannelsDatabase {

	private static final String TABLE_NAME = "channels";
	private static final String STATEMENT_SELECT = "SELECT channel_id, login_name, display_name, chat_role, chatlog_level, greeting_text, goodby_text, return_text, audio_path, audio_volume FROM "+TABLE_NAME;

	/// Gets all channels from the database.
	///
	/// @return A map of channel ID to Channel object.
	public Map<String, Channel> getAllChannels(){
		return DatabaseManager.executeSelect(connection -> {
			HashMap<String, Channel> channelDatas = new HashMap<>();
			try (ResultSet tableCheck = connection.getMetaData().getTables(null, null, TABLE_NAME, new String[] {"TABLE"})) {
				if (tableCheck.next()) {
					try (Statement statement = connection.createStatement()) {
						try (ResultSet resultSet = statement.executeQuery(STATEMENT_SELECT)) {
							while(resultSet.next()){
								Channel channelData = new Channel(resultSet);
								channelDatas.put(channelData.getChannelId(), channelData);
							}
						}
					}
				}
			}
			return channelDatas;
		});
	}
}
