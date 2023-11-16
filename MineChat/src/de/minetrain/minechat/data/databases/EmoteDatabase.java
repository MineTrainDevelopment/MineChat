package de.minetrain.minechat.data.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;


//Table: emotes
//emote_id, name, public, favorite, emote_type, tier, image_type, animated, file_location

//Table: channel_emotes
//channel_id, user_sub, tier1, tier2, tier3, follow, bits, bttv
//user_sub: null, tier1, tier2....


public class EmoteDatabase extends Database{
	private static final Logger logger = LoggerFactory.getLogger(EmoteDatabase.class);
	
	private static final String tabelName = "emotes";
	private static final String insert_SQL = "INSERT INTO "+tabelName+"(emote_id,name,public,favorite,emote_type,tier,image_type,animated,file_location) VALUES(?,?,?,?,?,?,?,?,?)";
	private static final String update_SQL = "UPDATE "+tabelName+" SET emote_id = ? , name = ? , public = ? , favorite = ? , emote_type = ? , tier = ? , image_type = ? , animated = ? , file_location = ? WHERE emote_id = ?";
	private static final String check_SQL = "SELECT emote_id FROM "+tabelName+" WHERE emote_id = ?";
	private static final String select_sql = "SELECT emote_id, name, public, favorite, emote_type, tier, image_type, animated, file_location FROM "+tabelName;

	private static final String tabelName_channel = "channel_emotes";
	private static final String insert_channel_SQL = "INSERT INTO "+tabelName_channel+"(channel_id,user_sub,tier1,tier2,tier3,follow,bits,bttv) VALUES(?,?,?,?,?,?,?,?)";
	private static final String update_channel_SQL = "UPDATE "+tabelName_channel+" SET channel_id = ? , channel_id = ? , user_sub = ? , tier1 = ? , tier2 = ? , tier3 = ? , follow = ? , bits = ? , bttv = ? WHERE channel_id = ?";
	private static final String check_channel_SQL = "SELECT channel_id FROM "+tabelName_channel+" WHERE channel_id = ?";
	private static final String select_channel_sql = "SELECT channel_id, user_sub, channel_id, tier1, tier2, tier3, follow, bits, bttv FROM "+tabelName_channel;
	
	public EmoteDatabase() throws SQLException {
		super("CREATE TABLE IF NOT EXISTS "+tabelName+" (\n"
                + "	emote_id text PRIMARY KEY,\n"
                + "	name text NOT NULL,\n"
                + "	public integer NOT NULL,\n"
                + "	favorite integer NOT NULL,\n"
                + "	emote_type text NOT NULL,\n"
                + "	tier text,\n"
                + "	image_type text NOT NULL,\n"
                + "	animated integer NOT NULL,\n"
                + "	file_location text NOT NULL\n"
                + ");",
                
                "CREATE TABLE IF NOT EXISTS "+tabelName_channel+" (\n"
                + "	channel_id text PRIMARY KEY,\n"
                + "	user_sub text,\n"
                + "	tier1 text,\n"
                + "	tier2 text,\n"
                + "	tier3 text,\n"
                + "	follow text,\n"
                + "	bits text,\n"
                + "	bttv text\n"
                + ");");
	}
	
	public void insert(String channel_id, String emote_id, String name, boolean isPublic, boolean favorite, String emote_type, String tier, String image_type, boolean animated, String file_location){
		logger.info("Insert new database entry");

		try{
			Connection connection = DatabaseManager.connect();
			connection.setAutoCommit(false);

            //Write to the emote_channel table
			PreparedStatement statement = connection.prepareStatement(check_SQL);
			statement.setString(1, emote_id);
            ResultSet resultSet = statement.executeQuery();
            statement = connection.prepareStatement(insert_SQL);

            if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
                statement = connection.prepareStatement(update_SQL);
            	statement.setString(10, emote_id);
            }
            
            statement.setString(1, emote_id);
            statement.setString(2, name);
            statement.setBoolean(3, isPublic);
            statement.setBoolean(4, favorite);
            statement.setString(5, emote_type);
            statement.setString(6, tier);
            statement.setString(7, image_type);
            statement.setBoolean(8, animated);
            statement.setString(9, file_location);
            statement.executeUpdate();
            
            
            //Write to the emote_channel table
            statement = connection.prepareStatement(check_channel_SQL);
			statement.setString(1, channel_id);
            resultSet = statement.executeQuery();
            statement = connection.prepareStatement(insert_channel_SQL);

            if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
                statement = connection.prepareStatement(update_channel_SQL);
            	statement.setString(9, channel_id);
            }
            
            statement.setString(1, channel_id);
            statement.setString(2, "tier1");
            statement.setString(3, "emotesv2_d5bd896aeb9e40739462a3080ca80dbf");
            statement.setString(4, null);
            statement.setString(5, null);
            statement.setString(6, null);
            statement.setString(7, null);
            statement.setString(8, "607757f739b5010444cff4ac");
            statement.executeUpdate();

//            connection.commit();
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
