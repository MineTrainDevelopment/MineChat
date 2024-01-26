package de.minetrain.minechat.data.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.objectdata.ChannelData;
import de.minetrain.minechat.utils.audio.AudioVolume;


//Table channels:
// channel_id, login_name, display_name, chat_role, chatlog_level, greeting_text, goodby_text, return_text, audio_path, audio_volume

public class ChannelsDatabase extends Database{
	private static final Logger logger = LoggerFactory.getLogger(ChannelsDatabase.class);
	
	private static final String tabelName = "channels";
	private static final String insert_SQL = "INSERT INTO "+tabelName+"(channel_id,login_name,display_name,chat_role,chatlog_level,greeting_text,goodby_text,return_text,audio_path,audio_volume) VALUES(?,?,?,?,?,?,?,?,?,?)";
	private static final String update_SQL = "UPDATE "+tabelName+" SET channel_id = ? , login_name = ? , display_name = ? , chat_role = ? , chatlog_level = ? , greeting_text = ? , goodby_text = ?  , return_text = ? , audio_path = ? , audio_volume = ?  WHERE channel_id = ?";
	private static final String check_SQL = "SELECT channel_id FROM "+tabelName+" WHERE channel_id = ?";
	private static final String select_sql = "SELECT channel_id, login_name, display_name, chat_role, chatlog_level, greeting_text, goodby_text, return_text, audio_path, audio_volume FROM "+tabelName;

	private static final String update_login_name_SQL = "UPDATE "+tabelName+" SET login_name = ? WHERE channel_id = ?";
	
	public ChannelsDatabase() throws SQLException {
		super("CREATE TABLE IF NOT EXISTS "+tabelName+" (\n"
                + "	channel_id text PRIMARY KEY,\n"
                + "	login_name text NOT NULL,\n"
                + "	display_name text NOT NULL,\n"
                + "	chat_role text NOT NULL,\n"
                + "	chatlog_level text,\n"
                + "	greeting_text text NOT NULL,\n"
                + "	goodby_text text NOT NULL,\n"
                + "	return_text text NOT NULL,\n"
                + "	audio_path text,\n"
                + "	audio_volume text NOT NULL\n"
                + ");");
	}
	
	/**
	 * NOTE: No autocommit.
	 * @param channel_id
	 * @param login_name
	 * @param display_name
	 * @param chat_role
	 * @param chatlog_level
	 * @param greeting_text
	 * @param goodby_text
	 * @param return_text
	 * @param audio_path
	 * @param audio_volume
	 */
	public void insert(String channel_id, String login_name, String display_name, String chat_role, String chatlog_level, String greeting_text, String goodby_text, String return_text, String audio_path, AudioVolume audio_volume){
		logger.info("Insert new database entry");

		try {
			Connection connection = DatabaseManager.connect();

            //Write to the channels table
			PreparedStatement statement = connection.prepareStatement(check_SQL);
			statement.setString(1, channel_id);
            ResultSet resultSet = statement.executeQuery();
            statement = connection.prepareStatement(insert_SQL);

            if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
                statement = connection.prepareStatement(update_SQL);
            	statement.setString(11, channel_id);
            }
            
			

            
            statement.setString(1, channel_id);
            statement.setString(2, login_name);
            statement.setString(3, display_name);
            statement.setString(4, chat_role);
            statement.setString(5, chatlog_level);
            statement.setString(6, greeting_text);
            statement.setString(7, goodby_text);
            statement.setString(8, return_text);
            statement.setString(9, audio_path);
            statement.setString(10, audio_volume.name());
            statement.executeUpdate();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	/**
	 * Warning: no auto commit.
	 * @param channel_id
	 * @param login_name
	 */
	public void updateChannelLoginName(String channel_id, String login_name){
		logger.info("Insert new database entry");

		try{
			Connection connection = DatabaseManager.connect();
			connection.setAutoCommit(false);

            //Write to the emote_channel table
			PreparedStatement statement = connection.prepareStatement(check_SQL);
			statement.setString(1, channel_id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
                statement = connection.prepareStatement(update_login_name_SQL);
            	statement.setString(1, login_name);
            	statement.setString(2, channel_id);
            	statement.executeUpdate();
            }else{
            	logger.warn("Can´t update channel login name for channel '"+channel_id+"'.");
            }

//            connection.commit();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public ResultSet getAll(){
		try{
			Connection connection = DatabaseManager.connect();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(select_sql);
			return resultSet;
//			while(resultSet.next()){
//				//Do something
//			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}
	
	private ResultSet get(String colum_name, Object value){
		try{
			Connection connection = DatabaseManager.connect();
			PreparedStatement statement = connection.prepareStatement(select_sql+" WHERE "+colum_name+" = ?") ;
			connection.setAutoCommit(false);
			
			switch(value.getClass().getSimpleName()) {
		    case "String":
		        statement.setString(1, String.valueOf(value));
		        break;
		    case "Boolean":
		        statement.setBoolean(1, (Boolean) value);
		        break;
		    case "Integer":
		        statement.setInt(1, (Integer) value);
		        break;

		    default:
		        // Handle the case where the type is not String, Boolean, or Integer
		        throw new IllegalArgumentException("Unsupported data type: " + value.getClass().getSimpleName());
		}


			ResultSet resultSet = statement.executeQuery();
			return resultSet;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}


	public ChannelData getChannelById(String channel_id){
		ResultSet resultSet = get("channel_id", channel_id);
		try {
			while(resultSet.next()){
				ChannelData channelData = new ChannelData(resultSet);
				DatabaseManager.commit();
				return channelData;
			}
		} catch (SQLException ex) {
			logger.error("Can´t read data from channel id -> "+channel_id);
		}
		return null;
	}

	/**
	 * channel_id, channelData
	 */
	public HashMap<String, ChannelData> getAllChannels(){
		ResultSet resultSet = getAll();
		HashMap<String, ChannelData> channelDatas = new HashMap<String, ChannelData>();
		try {
			while(resultSet.next()){
				ChannelData channelData = new ChannelData(resultSet);
				channelDatas.put(channelData.getChannelId(), channelData);
			}
			
		} catch (SQLException ex) {
			logger.error("", ex);
		}
		
		DatabaseManager.commit();
		return channelDatas;
	}
	
//	public void getById(String uuid){
//		get("channel_id", uuid);
//	}

//	public void getFilePath(String uuid){ //May only request the filepath.
//		get("emote_id", uuid);
//	}
	
	
}
