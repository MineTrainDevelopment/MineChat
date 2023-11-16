package de.minetrain.minechat.data.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;


//Table channels:
// channel_id, login_name, display_name, chat_role, subscribe_state, chatlog_level, greeting_text, goodby_text, return_text

public class ChannelsDatabase extends Database{
	private static final Logger logger = LoggerFactory.getLogger(ChannelsDatabase.class);
	
	private static final String tabelName = "channels";
	private static final String insert_SQL = "INSERT INTO "+tabelName+"(channel_id,login_name,display_name,chat_role,subscribe_state,chatlog_level,greeting_text,goodby_text,return_text) VALUES(?,?,?,?,?,?,?,?,?)";
	private static final String update_SQL = "UPDATE "+tabelName+" SET channel_id = ? , login_name = ? , display_name = ? , chat_role = ? , subscribe_state = ? , chatlog_level = ? , greeting_text = ? , goodby_text = ?  , return_text = ? WHERE channel_id = ?";
	private static final String check_SQL = "SELECT channel_id FROM "+tabelName+" WHERE channel_id = ?";
	private static final String select_sql = "SELECT channel_id, login_name, display_name, chat_role, subscribe_state, chatlog_level, greeting_text, goodby_text, return_text FROM "+tabelName;
	
	public ChannelsDatabase() throws SQLException {
		super("CREATE TABLE IF NOT EXISTS "+tabelName+" (\n"
                + "	channel_id integer PRIMARY KEY,\n"
                + "	login_name text NOT NULL,\n"
                + "	display_name text NOT NULL,\n"
                + "	chat_role text NOT NULL,\n"
                + "	subscribe_state text,\n"
                + "	chatlog_level text,\n"
                + "	greeting_text text NOT NULL,\n"
                + "	goodby_text text NOT NULL,\n"
                + "	return_text text NOT NULL\n"
                + ");");
	}
	
	public void insert(String channel_id, String login_name, String display_name, String chat_role, String subscribe_state, String chatlog_level, String[] greeting_text, String[] goodby_text, String[] return_text){
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
            	statement.setString(10, channel_id);
            }
            
			

            
            statement.setString(1, channel_id);
            statement.setString(2, login_name);
            statement.setString(3, display_name);
            statement.setString(4, chat_role);
            statement.setString(5, subscribe_state);
            statement.setString(6, chatlog_level);
            statement.setString(7, String.join("\n", greeting_text));
            statement.setString(8, String.join("\n", goodby_text));
            statement.setString(9, String.join("\n", return_text));
            statement.executeUpdate();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void getAll(){
		try(Connection connection = DatabaseManager.connect(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(select_sql)){
			while(resultSet.next()){
				//Do something
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	private void get(String colum_name, Object value){
		try(Connection connection = DatabaseManager.connect(); PreparedStatement statement = connection.prepareStatement(select_sql+" WHERE "+colum_name+" = ?")) {
			
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
			
			while(resultSet.next()){
				//Do something
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}


	public void getById(String uuid){
		get("emote_id", uuid);
	}

//	public void getFilePath(String uuid){ //May only request the filepath.
//		get("emote_id", uuid);
//	}
	
	public void getByName(String name){
		get("name", name);
	}
	
	public void getPublicEmotes(){
		get("public", true);
	}
	
}
