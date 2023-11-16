package de.minetrain.minechat.data.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;

//Table name: macros
// id, channel_id, button_id, button_row, title, emote_id, output
//      										   null

public class MacroDatabase extends Database {
	private static final Logger logger = LoggerFactory.getLogger(MacroDatabase.class);
	
	private static final String tabelName = "macros";
	private static final String insert_SQL = "INSERT INTO "+tabelName+"(channel_id,button_id,button_row,title,emote_id,output) VALUES(?,?,?,?,?,?)";
	private static final String update_SQL = "UPDATE "+tabelName+" SET channel_id = ? , button_id = ? , button_row = ? , title = ? , emote_id = ? , output = ? WHERE id = ?";
	private static final String check_SQL = "SELECT id FROM "+tabelName+" WHERE id = ?";
	private static final String select_sql = "SELECT id, channel_id, button_id, button_row, title, emote_id, output FROM "+tabelName;
	
	public MacroDatabase() throws SQLException {
		super("CREATE TABLE IF NOT EXISTS "+tabelName+" (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	channel_id text NOT NULL,\n"
                + "	button_id text NOT NULL,\n"
                + "	button_row text NOT NULL,\n"
                + "	title text NOT NULL,\n"
                + "	emote_id text,\n"
                + "	output text NOT NULL\n"
                + ");");
	}
	
	public void insert(Long id, String channel_id, String button_id, String button_row, String title, String emote_id, String[] output){
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
					statement.setLong(7, id);
				}
			}else{
				statement = connection.prepareStatement(insert_SQL);
			}
			

            
            statement.setString(1, channel_id);
            statement.setString(2, button_id);
            statement.setString(3, button_row);
            statement.setString(4, title);
            statement.setString(5, emote_id);
            statement.setString(6, String.join("\n", output));
            statement.executeUpdate();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void getAll(){
		try(Connection connection = DatabaseManager.connect(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(select_sql)){
			while(resultSet.next()){
				logger.info(
						resultSet.getString("emote_id")+" - "+
						resultSet.getString("name")+" - "+
						resultSet.getBoolean("public")+" - "+
			            resultSet.getBoolean("favorite")+" - "+
			            resultSet.getLong("emote_type")+" - "+
			            resultSet.getLong("tier")+" - "+
			            resultSet.getLong("image_type")+" - "+
			            resultSet.getLong("animated")+" - "+
			            resultSet.getLong("file_location")
						);
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
				logger.info(
						resultSet.getString("emote_id")+" - "+
						resultSet.getString("name")+" - "+
						resultSet.getBoolean("public")+" - "+
			            resultSet.getBoolean("favorite")+" - "+
			            resultSet.getLong("emote_type")+" - "+
			            resultSet.getLong("tier")+" - "+
			            resultSet.getLong("image_type")+" - "+
			            resultSet.getLong("animated")+" - "+
			            resultSet.getLong("file_location")
						);
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
