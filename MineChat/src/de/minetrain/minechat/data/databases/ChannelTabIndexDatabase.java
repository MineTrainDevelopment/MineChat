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
import de.minetrain.minechat.data.objectdata.ChannelData;
import de.minetrain.minechat.data.objectdata.MacroData;
import de.minetrain.minechat.gui.obj.TabButtonType;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;

//Table name: macros
// tab_id, channel_id

public class ChannelTabIndexDatabase extends Database {
	private static final Logger logger = LoggerFactory.getLogger(ChannelTabIndexDatabase.class);
	
	private static final String tabelName = "channel_tab_ids";
	private static final String insert_SQL = "INSERT INTO "+tabelName+"(tab_id, channel_id) VALUES(?,?)";
	private static final String update_SQL = "UPDATE "+tabelName+" SET tab_id = ? , channel_id = ? WHERE tab_id = ?";
	private static final String check_SQL = "SELECT tab_id FROM "+tabelName+" WHERE tab_id = ?";
	private static final String select_sql = "SELECT tab_id, channel_id FROM "+tabelName;
	
	public ChannelTabIndexDatabase() throws SQLException {
		super("CREATE TABLE IF NOT EXISTS "+tabelName+" (\n"
                + "	tab_id text PRIMARY KEY,\n"
                + "	channel_id text NOT NULL\n"
                + ");");
	}
	
	public void insert(TabButtonType tabButtonType, String channel_id){
		logger.info("Insert new database entry");

		try{
			Connection connection = DatabaseManager.connect();

            //Write to the macro table
			PreparedStatement statement;
			if(tabButtonType != null){
				statement = connection.prepareStatement(check_SQL);
				statement.setString(1, tabButtonType.name());
				ResultSet resultSet = statement.executeQuery();
				statement = connection.prepareStatement(insert_SQL);
				if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
					statement = connection.prepareStatement(update_SQL);
					statement.setString(3, tabButtonType.name());
				}
			}else{
				statement = connection.prepareStatement(insert_SQL);
			}
			


            statement.setString(1, tabButtonType.name());
            statement.setString(2, channel_id);
            statement.executeUpdate();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public HashMap<TabButtonType, String> getAll(){
		HashMap<TabButtonType, String> channelTabs = new HashMap<TabButtonType, String>();//tab_id, channel_id
		channelTabs.put(TabButtonType.TAB_MAIN, "0");
		channelTabs.put(TabButtonType.TAB_SECOND, "0");
		channelTabs.put(TabButtonType.TAB_THIRD, "0");
		channelTabs.put(TabButtonType.TAB_MAIN_ROW_2, "0");
		channelTabs.put(TabButtonType.TAB_SECOND_ROW_2, "0");
		channelTabs.put(TabButtonType.TAB_THIRD_ROW_2, "0");
		
		try(Connection connection = DatabaseManager.connect(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(select_sql)){
			
			
			while(resultSet.next()){
				channelTabs.put(TabButtonType.get(resultSet.getString("tab_id")), resultSet.getString("channel_id"));
			}
			
			return channelTabs;
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
		
		return channelTabs;
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
	
	public String getChannelId(TabButtonType buttonType){
		ResultSet resultSet = get("tab_id", buttonType.name());
		
		try {
			while(resultSet.next()){
				return resultSet.getString("channel_id");
			}
		} catch (SQLException ex) {
			logger.error("Can´t get channel ID for channel tab ->"+buttonType.name(), ex);
			return "0";
		}
		
		return "0";
	}

	public ResultSet getByChannelId(String channel_id){
		return get("channel_id", channel_id);
	}
	
}
