package de.minetrain.minechat.data.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.features.messagehighlight.HighlightString;

// uuid, word, word_color, border_color, sound, state

public class MessageHighlightDatabase extends Database{
	private static final Logger logger = LoggerFactory.getLogger(MessageHighlightDatabase.class);
	private static final String tabelName = "message_highlight";
	private static final String insert_SQL = "INSERT INTO "+tabelName+"(uuid,word,word_color,border_color,sound,state) VALUES(?,?,?,?,?,?)";
	private static final String update_SQL = "UPDATE "+tabelName+" SET uuid = ? , word = ? , word_color = ? , border_color = ? , sound = ? , state = ? WHERE uuid = ?";
	private static final String check_SQL = "SELECT uuid FROM "+tabelName+" WHERE uuid = ?";
	private static final String delete_SQL = "DELETE FROM "+tabelName+" WHERE uuid = ?";
	private static final String select_sql = "SELECT uuid, word, word_color, border_color, sound, state FROM "+tabelName;
	
	private static final String update_state_SQL = "UPDATE "+tabelName+" SET state = ? WHERE uuid = ?";
	
	public MessageHighlightDatabase() throws SQLException {
		super("CREATE TABLE IF NOT EXISTS "+tabelName+" (\n"
                + "	uuid text PRIMARY KEY,\n"
                + "	word text NOT NULL,\n"
                + "	word_color text NOT NULL,\n"
                + "	border_color text NOT NULL,\n"
                + "	sound text,\n"
                + "	state integer NOT NULL\n"
                + ");");
	}
	
	public void insert(String uuid, String word, String word_color, String border_color, String sound, boolean state){
		logger.info("Insert new database entry");
		
		try{
			Connection connection = DatabaseManager.connect();
			PreparedStatement statement = connection.prepareStatement(check_SQL);
			
			statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            statement = connection.prepareStatement(insert_SQL);

            if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
                statement = connection.prepareStatement(update_SQL);
            	statement.setString(7, uuid);
            }
            
            statement.setString(1, uuid);
            statement.setString(2, word);
            statement.setString(3, word_color);
            statement.setString(4, border_color);
            statement.setString(5, sound);
            statement.setBoolean(6, state);
            statement.executeUpdate();

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
				Settings.highlightStrings.put(resultSet.getString("word"), new HighlightString(resultSet));
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
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
