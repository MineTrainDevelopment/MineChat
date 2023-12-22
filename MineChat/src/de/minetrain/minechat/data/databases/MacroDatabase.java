package de.minetrain.minechat.data.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.objectdata.MacroData;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;

//Table name: macros
// id, channel_id, button_type, button_id, button_row, title, emote_id, output
//      										   				null

public class MacroDatabase extends Database {
	private static final Logger logger = LoggerFactory.getLogger(MacroDatabase.class);
	
	private static final String tabelName = "macros";
	private static final String insert_SQL = "INSERT INTO "+tabelName+"(channel_id,button_type,button_id,button_row,title,emote_id,output) VALUES(?,?,?,?,?,?,?)";
	private static final String update_SQL = "UPDATE "+tabelName+" SET channel_id = ? , button_type = ? , button_id = ? , button_row = ? , title = ? , emote_id = ? , output = ? WHERE id = ?";
	private static final String check_SQL = "SELECT id FROM "+tabelName+" WHERE id = ?";
	private static final String select_sql = "SELECT id, channel_id, button_type, button_id, button_row, title, emote_id, output FROM "+tabelName;
	private static final String delete_sql = "DELETE FROM "+tabelName+" WHERE channel_id = ?";
	
	public MacroDatabase() throws SQLException {
		super("CREATE TABLE IF NOT EXISTS "+tabelName+" (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	channel_id text NOT NULL,\n"
                + "	button_type text NOT NULL,\n"
                + "	button_id text NOT NULL,\n"
                + "	button_row text NOT NULL,\n"
                + "	title text NOT NULL,\n"
                + "	emote_id text,\n"
                + "	output text NOT NULL\n"
                + ");");
	}
	
	
	public void insert(MacroData data, boolean autoCommit){
		insert(data.getId(), data.getChannelId(), data.getButton_type(), data.getButton_id(), data.getButton_row(), data.getTitle(), data.getEmoteId(), data.getOutput());
		if(autoCommit){
			DatabaseManager.commit();
		}
	}
	
	/**
	 * 
	 * @param id
	 * @param channel_id
	 * @param button_type
	 * @param button_id
	 * @param button_row
	 * @param title
	 * @param emote_id
	 * @param output
	 */
	public void insert(Long id, String channel_id, ButtonType button_type, String button_id, String button_row, String title, String emote_id, String[] output){
		logger.info("Insert new database entry");

		try{
			Connection connection = DatabaseManager.connect();

            //Write to the macro table
			PreparedStatement statement;
			if(id != null){
				statement = connection.prepareStatement(check_SQL);
				statement.setLong(1, id);
				ResultSet resultSet = statement.executeQuery();
				statement = connection.prepareStatement(insert_SQL);
				if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
					statement = connection.prepareStatement(update_SQL);
					statement.setLong(8, id);
				}
			}else{
				statement = connection.prepareStatement(insert_SQL);
			}
			

            
            statement.setString(1, channel_id);
            statement.setString(2, button_type.name());
            statement.setString(3, button_id);
            statement.setString(4, button_row);
            statement.setString(5, title);
            statement.setString(6, emote_id);
            statement.setString(7, String.join("\n", output));
            statement.executeUpdate();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void getAll(){
		try(Connection connection = DatabaseManager.connect(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(select_sql)){
			while(resultSet.next()){
				// Get all macros?
			}
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
	

	
	public void deleteChannelDataById(String channel_id, boolean autoCommit){
		try{
			Connection connection = DatabaseManager.connect(); 
			PreparedStatement statement = connection.prepareStatement(delete_sql);
			statement.setString(1, channel_id);
			statement.executeUpdate();
			
			if(autoCommit){
				DatabaseManager.commit();
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}


	public List<MacroData> getChannelDataById(String channel_id){
		ResultSet resultSet = get("channel_id", channel_id);
		try {
			List<MacroData> list = new ArrayList<MacroData>();
			while(resultSet.next()){
				list.add(new MacroData(resultSet));
			}
			
			DatabaseManager.commit();
			return list;
		} catch (SQLException ex) {
			logger.error("Can´t read data from channel id -> "+channel_id);
		}
		return new ArrayList<MacroData>();
	}

	public ResultSet getByChannelId(String channel_id){
		return get("channel_id", channel_id);
	}
	
}
