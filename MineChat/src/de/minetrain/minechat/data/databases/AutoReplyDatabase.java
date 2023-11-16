package de.minetrain.minechat.data.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.features.autoreply.AutoReply;
import de.minetrain.minechat.features.autoreply.AutoReplyManager;

public class AutoReplyDatabase extends Database{
	private static final Logger logger = LoggerFactory.getLogger(AutoReplyDatabase.class);
	private static final String tabelName = "auto_reply";
	private static final String insert_SQL = "INSERT INTO "+tabelName+"(uuid,channel_id,state,chat_reply,messages_per_min,fire_delay,trigger,output) VALUES(?,?,?,?,?,?,?,?)";
	private static final String update_SQL = "UPDATE "+tabelName+" SET uuid = ? , channel_id = ? , state = ? , chat_reply = ? , messages_per_min = ? , fire_delay = ? , trigger = ? , output = ? WHERE uuid = ?";
	private static final String update_state_SQL = "UPDATE "+tabelName+" SET state = ? WHERE uuid = ?";
	private static final String check_SQL = "SELECT uuid FROM "+tabelName+" WHERE uuid = ?";
	private static final String delete_SQL = "DELETE FROM "+tabelName+" WHERE uuid = ?";
	private static final String select_sql = "SELECT uuid, channel_id, state, chat_reply, messages_per_min, fire_delay, trigger, output FROM "+tabelName;
	
	public AutoReplyDatabase() throws SQLException {
		super("CREATE TABLE IF NOT EXISTS "+tabelName+" (\n"
                + "	uuid text PRIMARY KEY,\n"
                + "	channel_id text NOT NULL,\n"
                + "	state integer NOT NULL,\n"
                + "	chat_reply integer NOT NULL,\n"
                + "	messages_per_min integer NOT NULL,\n"
                + "	fire_delay integer NOT NULL,\n"
                + "	trigger text NOT NULL,\n"
                + "	output text NOT NULL\n"
                + ");");
	}
	
	public void insert(String uuid, String channelId, boolean state, boolean chatReply, Long messagesPerMin, Long fireDelay, String trigger, String[] output){
		logger.info("Insert new database entry");
		
		try{
			Connection connection = DatabaseManager.connect();
			PreparedStatement statement = connection.prepareStatement(check_SQL);
			
			statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            statement = connection.prepareStatement(insert_SQL);

            if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
                statement = connection.prepareStatement(update_SQL);
            	statement.setString(9, uuid);
            }
            
            statement.setString(1, uuid);
            statement.setString(2, channelId);
            statement.setBoolean(3, state);
            statement.setBoolean(4, chatReply);
            statement.setLong(5, messagesPerMin);
            statement.setLong(6, fireDelay);
            statement.setString(7, trigger);
            statement.setString(8, String.join("\n", output));
            statement.executeUpdate();

			AutoReplyManager.addAutoReply(new AutoReply(uuid, channelId, state, chatReply, messagesPerMin, fireDelay, trigger, output));
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void remove(String uuid){
		try(Connection connection = DatabaseManager.connect()){
			PreparedStatement statement = connection.prepareStatement(check_SQL);
			
			statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
                statement = connection.prepareStatement(delete_SQL);
            	statement.setString(1, uuid);
            	statement.executeUpdate();
            	DatabaseManager.commit();
            }
            
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void getAll(){
		try(Connection connection = DatabaseManager.connect(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(select_sql)){
			while(resultSet.next()){
				AutoReplyManager.addAutoReply(new AutoReply(resultSet));
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public AutoReply get(String uuid){
		
		try(Connection connection = DatabaseManager.connect(); PreparedStatement statement = connection.prepareStatement(select_sql+" WHERE uuid = ?")) {
			statement.setString(1, uuid);
			ResultSet resultSet = statement.executeQuery();
			
			while(resultSet.next()){
				return new AutoReply(resultSet);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}
	
	public void setState(String uuid, boolean state){
		try(Connection connection = DatabaseManager.connect()){
			PreparedStatement statement = connection.prepareStatement(check_SQL);
			
			statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
                statement = connection.prepareStatement(update_state_SQL);
            	statement.setBoolean(1, state);
            	statement.setString(2, uuid);
            	statement.executeUpdate();
            	DatabaseManager.commit();
            }
            
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

}
