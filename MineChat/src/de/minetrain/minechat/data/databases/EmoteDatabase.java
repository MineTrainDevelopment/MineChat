package de.minetrain.minechat.data.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

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
	private static final String check_SQL = "SELECT emote_id FROM "+tabelName+" WHERE emote_id = ?";

	private static final String tabelName_channel = "channel_emotes";
	private static final String check_channel_SQL = "SELECT channel_id FROM "+tabelName_channel+" WHERE channel_id = ?";

	private static final String update_favorite_state_SQL = "UPDATE "+tabelName+" SET favorite = ? WHERE emote_id = ?";
	private static final String update_subscription_state_SQL = "UPDATE "+tabelName_channel+" SET user_sub = ? WHERE channel_id = ?";

	/**channel_id,   subLevel*/
	private static final HashMap<String, String> subLevelCache = new HashMap<>();

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
            }else{
            	logger.warn("Can´t update sub tier for channel -> '"+chanelId+"'.");
            }

//            connection.commit();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
}
