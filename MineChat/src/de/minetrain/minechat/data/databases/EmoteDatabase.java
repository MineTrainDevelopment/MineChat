package de.minetrain.minechat.data.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.gui.emotes.ChannelEmotes;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.EmoteManager;


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
	private static final String insert_channel_SQL = "INSERT INTO "+tabelName_channel+"(channel_id,user_sub,tier1,tier2,tier3,follow,bits) VALUES(?,?,?,?,?,?,?)";
	private static final String update_channel_SQL = "UPDATE "+tabelName_channel+" SET channel_id = ? , user_sub = ? , tier1 = ? , tier2 = ? , tier3 = ? , follow = ? , bits = ? , bttv = ? WHERE channel_id = ?";
	private static final String check_channel_SQL = "SELECT channel_id FROM "+tabelName_channel+" WHERE channel_id = ?";
	private static final String select_channel_sql = "SELECT channel_id, user_sub, channel_id, tier1, tier2, tier3, follow, bits, bttv FROM "+tabelName_channel;

	private static final String update_channel_bttv_SQL = "UPDATE "+tabelName_channel+" SET bttv = ? WHERE channel_id = ?";
	private static final String update_favorite_state_SQL = "UPDATE "+tabelName+" SET favorite = ? WHERE emote_id = ?";
	private static final String update_subscription_state_SQL = "UPDATE "+tabelName_channel+" SET user_sub = ? WHERE channel_id = ?";
	
	/**channel_id,   subLevel*/
	private static final HashMap<String, String> subLevelCache = new HashMap<String, String>();
	
	//TEMP to say if the default emotes are instald.
	private boolean publicEmotes = false;
	
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
	
	/**
	 * 
	 * @param channel_id
	 * @param emote_id
	 * @param name
	 * @param isPublic
	 * @param favorite
	 * @param emote_type
	 * @param tier
	 * @param image_type
	 * @param animated
	 * @param file_location
	 */
	public void insert(String emote_id, String name, boolean isPublic, boolean favorite, String emote_type, String tier, String image_type, boolean animated, String file_location){
		logger.info("Insert new database entry");
		if(tier != null && tier.isEmpty()){tier = null;}

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

//            connection.commit();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	
	public void insertChannel(String channel_id, String subTier, ArrayList<String> tier1, ArrayList<String> tier2, ArrayList<String> tier3, ArrayList<String> bits, ArrayList<String> follower){
		logger.info("Insert new database entry");

		try{
			Connection connection = DatabaseManager.connect();
			connection.setAutoCommit(false);

            //Write to the emote_channel table
			PreparedStatement statement = connection.prepareStatement(check_channel_SQL);
			statement.setString(1, channel_id);
            ResultSet resultSet = statement.executeQuery();
            statement = connection.prepareStatement(insert_channel_SQL);

            if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
                statement = connection.prepareStatement(update_channel_SQL);
            	statement.setString(9, channel_id);
            }

            statement.setString(1, channel_id);
            statement.setString(2, subTier);
            statement.setString(3, String.join("\n", tier1));
            statement.setString(4, String.join("\n", tier2));
            statement.setString(5, String.join("\n", tier3));
            statement.setString(6, String.join("\n", follower));
            statement.setString(7, String.join("\n", bits));
            statement.executeUpdate();

//            connection.commit();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void insertChannelBttv(String channel_id, ArrayList<String> bttv){
		logger.info("Insert new database entry");

		try{
			Connection connection = DatabaseManager.connect();
			connection.setAutoCommit(false);

            //Write to the emote_channel table
			PreparedStatement statement = connection.prepareStatement(check_channel_SQL);
			statement.setString(1, channel_id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
                statement = connection.prepareStatement(update_channel_bttv_SQL);
            	statement.setString(1, String.join("\n", bttv));
            	statement.setString(2, channel_id);
            	statement.executeUpdate();
            }else{
            	logger.warn("Can´t set bttv emotes IDs for channel '"+channel_id+"'.");
            }

//            connection.commit();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void updateFavoriteState(String emote_id, boolean state){
		logger.info("Updating emote favorite state for -> "+emote_id);

		try{
			Connection connection = DatabaseManager.connect();
			connection.setAutoCommit(false);

            //Write to the emote_channel table
			PreparedStatement statement = connection.prepareStatement(check_SQL);
			statement.setString(1, emote_id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
                statement = connection.prepareStatement(update_favorite_state_SQL);
            	statement.setBoolean(1, state);
            	statement.setString(2, emote_id);
            	statement.executeUpdate();
            }else{
            	logger.warn("Can´t update favorite state for emote -> '"+emote_id+"'.");
            }

//            connection.commit();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	/**
	 * Autocomit.
	 * <br> NOTE: If the sub level is eual to the preview one, it simply cacnsels it.
	 * @param chanelId
	 * @param tier
	 */
	public void updateSubscriptionState(String chanelId, String tier){
		if(subLevelCache.containsKey(chanelId) && subLevelCache.get(chanelId).equals(tier)){
			return;
		}
		
		logger.info("Updating sub state state for -> "+chanelId);
		subLevelCache.put(chanelId, tier);
		
		try{
			Connection connection = DatabaseManager.connect();
			connection.setAutoCommit(false);

            //Write to the emote_channel table
			PreparedStatement statement = connection.prepareStatement(check_channel_SQL);
			statement.setString(1, chanelId);
            ResultSet resultSet = statement.executeQuery();
            

            if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
                statement = connection.prepareStatement(update_subscription_state_SQL);
            	statement.setString(1, tier);
            	statement.setString(2, chanelId);
            	statement.executeUpdate();
            	connection.commit();
            	EmoteManager.load();
            }else{
            	logger.warn("Can´t update sub tier for channel -> '"+chanelId+"'.");
            }

//            connection.commit();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void getAll(){
		try(Connection connection = DatabaseManager.connect(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(select_sql)){
			EmoteManager.clear();
			while(resultSet.next()){
				EmoteManager.addEmote(new Emote(resultSet));
//				logger.info(
//						resultSet.getString("emote_id")+" - "+
//						resultSet.getString("name")+" - "+
//						resultSet.getBoolean("public")+" - "+
//			            resultSet.getBoolean("favorite")+" - "+
//			            resultSet.getLong("emote_type")+" - "+
//			            resultSet.getLong("tier")+" - "+
//			            resultSet.getLong("image_type")+" - "+
//			            resultSet.getLong("animated")+" - "+
//			            resultSet.getLong("file_location")
//						);
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void getAllChannels(){
		try(Connection connection = DatabaseManager.connect(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(select_channel_sql)){
			EmoteManager.clearChannel();
			while(resultSet.next()){
				EmoteManager.addChannel(resultSet.getString("channel_id"), new ChannelEmotes(resultSet));
				subLevelCache.put(resultSet.getString("channel_id"), resultSet.getString("user_sub"));
//				logger.info(
//						resultSet.getString("emote_id")+" - "+
//						resultSet.getString("name")+" - "+
//						resultSet.getBoolean("public")+" - "+
//			            resultSet.getBoolean("favorite")+" - "+
//			            resultSet.getLong("emote_type")+" - "+
//			            resultSet.getLong("tier")+" - "+
//			            resultSet.getLong("image_type")+" - "+
//			            resultSet.getLong("animated")+" - "+
//			            resultSet.getLong("file_location")
//						);
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	private ResultSet get(String colum_name, Object value){
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
				publicEmotes = resultSet.getBoolean("public");
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
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
	
	public boolean isPublicEmotesInstald(){
		getPublicEmotes();
		return publicEmotes;
	}

}
