package de.minetrain.minechat.data.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.objectdata.MacroData;
import de.minetrain.minechat.gui.obj.TabButtonType;
import de.minetrain.minechat.twitch.obj.TwitchMessage;

//Table name: owner_cache
//channel_id, color_code, display_name, badges

public class OwnerCacheDatabase extends Database {
	private static final Logger logger = LoggerFactory.getLogger(OwnerCacheDatabase.class);
	public record UserChatData(String channelId, String color_code, String displa_name, String badges){};
	private static HashMap<String, UserChatData> cache = new HashMap<String, UserChatData>();//Channel_id, data
	
	private static final String tabelName = "owner_cache";
	private static final String insert_SQL = "INSERT INTO "+tabelName+"(channel_id, color_code, display_name, badges) VALUES(?,?,?,?)";
	private static final String update_SQL = "UPDATE "+tabelName+" SET channel_id = ? , color_code = ? , display_name = ? , badges = ? WHERE channel_id = ?";
	private static final String check_SQL = "SELECT channel_id FROM "+tabelName+" WHERE channel_id = ?";
	private static final String select_sql = "SELECT channel_id, color_code, display_name, badges FROM "+tabelName;
	
	public OwnerCacheDatabase() throws SQLException {
		super("CREATE TABLE IF NOT EXISTS "+tabelName+" (\n"
                + "	channel_id text PRIMARY KEY,\n"
                + "	color_code text NOT NULL,\n"
                + "	display_name text NOT NULL,\n"
                + "	badges text NOT NULL\n"
                + ");");
		
		getAll();
	}
	
	/**
	 * NOTE: Auto commit = true!
	 * @param message
	 */
	public void insert(TwitchMessage message){
		logger.info("Insert new database entry");
		
		UserChatData ownerCacheData = new UserChatData(message.getChannelId(), message.getUserColorCode(), message.getUserName(), message.getRawBadgeTags());
		if(cache.values().contains(ownerCacheData)){
			return;
		}
		cache.put(message.getChannelId(), ownerCacheData);

		try{
			Connection connection = DatabaseManager.connect();
			PreparedStatement statement = connection.prepareStatement(check_SQL);
			statement.setString(1, message.getChannelId());
			ResultSet resultSet = statement.executeQuery();
			statement = connection.prepareStatement(insert_SQL);
			if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
				statement = connection.prepareStatement(update_SQL);
				statement.setString(5, message.getChannelId());
			}

            statement.setString(1, message.getChannelId());
            statement.setString(2, message.getUserName());
            statement.setString(3, message.getUserColorCode());
            statement.setString(4, message.getRawBadgeTags());
            statement.executeUpdate();
            DatabaseManager.commit();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void getAll(){
		try{
			Connection connection = DatabaseManager.connect();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(select_sql);
			while(resultSet.next()){
				cache.put(resultSet.getString("channel_id"), new UserChatData(
						resultSet.getString("channel_id"),
						resultSet.getString("display_name"),
						resultSet.getString("color_code"),
						resultSet.getString("badges")));
			}
			DatabaseManager.commit();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
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


	public UserChatData getById(String channel_id){
		if(cache.containsKey(channel_id)){
			return cache.get(channel_id);
		}
		
		ResultSet resultSet = get("channel_id", channel_id);
		try {
			while(resultSet.next()){
				UserChatData ownerData = new UserChatData(
						resultSet.getString("channel_id"),
						resultSet.getString("display_name"),
						resultSet.getString("color_code"),
						resultSet.getString("badges"));
				cache.put(resultSet.getString("channel_id"), ownerData);
				DatabaseManager.commit();
				return ownerData;
			}

			DatabaseManager.commit();
			return null;
		} catch (SQLException ex) {
			logger.error("Can´t read data from channel id -> "+channel_id);
			return null;
		}
	}
	
	public ResultSet getByChannelId(String channel_id){
		return get("channel_id", channel_id);
	}
	
}
