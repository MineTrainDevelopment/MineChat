package de.minetrain.minechat.data.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.twitch4j.common.enums.SubscriptionPlan;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.twitch.obj.ChannelStatistics;

//dalay_stats.
//id, channel_id, unix_timestamp, messages, unique_messages, total_subs, resubs, gift_subs, gift_tier1, gift_tier2, gift_tier3, new_subs, bits, follower

//user_stats
//channel_user_id, messages, gifted_subs, gifted_tier1, gifted_tier2, gifted_tier3, cheerd_bits
//141456-54689,

public class ChannelStatisticsDatabase extends Database {
	private static final Logger logger = LoggerFactory.getLogger(ChannelStatisticsDatabase.class);
	//TODO Automaticly save at 00:00 each day.
	
	private static final String tabelName = "dalay_stats";
	private static final String insert_SQL = "INSERT INTO "+tabelName+"(channel_id, unix_timestamp, messages, unique_messages, total_subs, resubs, gift_subs, gift_tier1, gift_tier2, gift_tier3, new_subs, bits, follower) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String update_SQL = "UPDATE "+tabelName+" SET channel_id = ? , unix_timestamp = ? , messages = ? , unique_messages = ? , total_subs = ? , resubs = ? , gift_subs = ? , gift_tier1 = ? , gift_tier2 = ? , gift_tier3 = ? , new_subs = ? , bits = ? , follower = ? WHERE id = ?";
	private static final String check_SQL = "SELECT id, channel_id, unix_timestamp, messages, unique_messages, total_subs, resubs, gift_subs, gift_tier1, gift_tier2, gift_tier3, new_subs, bits, follower FROM "+tabelName+" WHERE channel_id = ? AND unix_timestamp = ?";
	private static final String select_sql = "SELECT channel_id, unix_timestamp, messages, unique_messages, total_subs, resubs, gift_subs, gift_tier1, gift_tier2, gift_tier3, new_subs, bits, follower FROM "+tabelName;
	
	private static final String userTabelName = "user_stats";
	private static final String insert_user_SQL = "INSERT INTO "+userTabelName+"(channel_user_id, messages, gifted_subs, gifted_tier1, gifted_tier2, gifted_tier3, cheerd_bits) VALUES(?,?,?,?,?,?,?)";
	private static final String update_user_SQL = "UPDATE "+userTabelName+" SET messages = ? , gifted_subs = ? , gifted_tier1 = ? , gifted_tier2 = ? , gifted_tier3 = ? , cheerd_bits = ? WHERE channel_user_id = ?";
	private static final String check_user_SQL = "SELECT channel_user_id, messages, gifted_subs, gifted_tier1, gifted_tier2, gifted_tier3, cheerd_bits FROM "+userTabelName+" WHERE channel_user_id = ?";
	//private static final String select_user_sql = "SELECT channel_user_id, messages, gifted_subs, gifted_tier1, gifted_tier2, gifted_tier3, cheerd_bits FROM "+userTabelName;
	
	
	public ChannelStatisticsDatabase() throws SQLException {
		super("CREATE TABLE IF NOT EXISTS "+tabelName+" (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	channel_id text NOT NULL,\n"
                + "	unix_timestamp text NOT NULL,\n"
                + "	messages integer NOT NULL,\n"
                + "	unique_messages integer NOT NULL,\n"
                + "	total_subs integer NOT NULL,\n"
                + "	resubs integer NOT NULL,\n"
                + "	gift_subs integer NOT NULL,\n"
                + "	gift_tier1 integer,\n"
                + "	gift_tier2 integer,\n"
                + "	gift_tier3 integer,\n"
                + "	new_subs integer NOT NULL,\n"
                + "	bits integer NOT NULL,\n"
                + "	follower integer NOT NULL\n"
                + ");",
                
            "CREATE TABLE IF NOT EXISTS "+userTabelName+" (\n"
                + "	channel_user_id text PRIMARY KEY,\n"
                + "	messages integer NOT NULL,\n"
                + "	gifted_subs integer NOT NULL,\n"
                + "	gifted_tier1 integer,\n"
                + "	gifted_tier2 integer,\n"
                + "	gifted_tier3 integer,\n"
                + "	cheerd_bits integer NOT NULL\n"
                + ");"
				);
		
		startScheduler();
	}
	
	public void insert(ChannelStatistics channelStatistics, String channel_id){
		logger.info("Insert new database entry");

		try{
			Connection connection = DatabaseManager.connect();
			connection.setAutoCommit(false);
			
            //Write to the emote_channel table
			PreparedStatement statement = connection.prepareStatement(check_SQL);
			statement.setString(1, channel_id);
			statement.setString(2, getCurrentUnixTimestamp());
            ResultSet resultSet = statement.executeQuery();
            statement = connection.prepareStatement(insert_SQL);
            

          //id, channel_id, unix_timestamp, messages, total_subs, resubs, gift_subs, new_subs, bits, follower

            if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
                statement = connection.prepareStatement(update_SQL);
                statement.setString(1, resultSet.getString("channel_id"));
                statement.setString(2, resultSet.getString("unix_timestamp"));
                statement.setLong(3, resultSet.getLong("messages") + channelStatistics.getTotalMessages());
                statement.setLong(4, resultSet.getLong("unique_messages") + channelStatistics.getTotalMessages());
                statement.setLong(5, resultSet.getLong("total_subs") + channelStatistics.getTotalSubs());
                statement.setLong(6, resultSet.getLong("resubs") + channelStatistics.getTotalResubs());
                statement.setLong(7, resultSet.getLong("gift_subs") + channelStatistics.getTotalGiftedSubs(SubscriptionPlan.NONE));
                statement.setLong(8, resultSet.getLong("gift_tier1") + channelStatistics.getTotalGiftedSubs(SubscriptionPlan.TIER1));
                statement.setLong(9, resultSet.getLong("gift_tier2") + channelStatistics.getTotalGiftedSubs(SubscriptionPlan.TIER2));
                statement.setLong(10, resultSet.getLong("gift_tier3") + channelStatistics.getTotalGiftedSubs(SubscriptionPlan.TIER3));
                statement.setLong(11, resultSet.getLong("new_subs") + channelStatistics.getTotalNewSubs());
                statement.setLong(12, resultSet.getLong("bits") + channelStatistics.getTotalBits());
                statement.setLong(13, resultSet.getLong("follower") + channelStatistics.getTotalFollower());
            	statement.setString(14, resultSet.getString("id"));
            	statement.executeUpdate();
            }else{
            	statement.setString(1, channel_id);
                statement.setString(2, getCurrentUnixTimestamp());
                statement.setLong(3, channelStatistics.getTotalMessages());
                statement.setLong(4, channelStatistics.getTotalUniqueMessages());
                statement.setLong(5, channelStatistics.getTotalSubs());
                statement.setLong(6, channelStatistics.getTotalResubs());
                statement.setLong(7, channelStatistics.getTotalGiftedSubs(SubscriptionPlan.NONE));
                statement.setLong(8, channelStatistics.getTotalGiftedSubs(SubscriptionPlan.TIER1));
                statement.setLong(9, channelStatistics.getTotalGiftedSubs(SubscriptionPlan.TIER2));
                statement.setLong(10, channelStatistics.getTotalGiftedSubs(SubscriptionPlan.TIER3));
                statement.setLong(11, channelStatistics.getTotalNewSubs());
                statement.setLong(12, channelStatistics.getTotalBits());
                statement.setLong(13, channelStatistics.getTotalFollower());
                statement.executeUpdate();
            }

            
            List<String> userIds = new ArrayList<String>();
            userIds.addAll(channelStatistics.getGiftedSubs().keySet());
            userIds.addAll(channelStatistics.getCheerdBits().keySet());
            userIds.addAll(channelStatistics.getSendedMessages().keySet());
            
            for(String userId : userIds){
            	String channel_user_id = channel_id+"-"+userId;
            	statement = connection.prepareStatement(check_user_SQL);
            	statement.setString(1, channel_user_id);
            	resultSet = statement.executeQuery();
            	statement = connection.prepareStatement(insert_user_SQL);
            	
            	if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
            		statement = connection.prepareStatement(update_user_SQL);
            		statement.setLong(1, resultSet.getLong("messages") + channelStatistics.getSendedMessages(userId));
            		statement.setLong(2, resultSet.getLong("gifted_subs") + channelStatistics.getTotalGiftedSubs(SubscriptionPlan.NONE, userId));
            		statement.setLong(3, resultSet.getLong("gifted_tier1") + channelStatistics.getTotalGiftedSubs(SubscriptionPlan.TIER1, userId));
            		statement.setLong(4, resultSet.getLong("gifted_tier2") + channelStatistics.getTotalGiftedSubs(SubscriptionPlan.TIER2, userId));
            		statement.setLong(5, resultSet.getLong("gifted_tier3") + channelStatistics.getTotalGiftedSubs(SubscriptionPlan.TIER3, userId));
            		statement.setLong(6, resultSet.getLong("cheerd_bits") + channelStatistics.getCheerdBits(userId));
            		statement.setString(7, resultSet.getString("channel_user_id"));
            		statement.executeUpdate();
            	}else{
            		statement.setString(1, channel_user_id);
            		statement.setLong(2, channelStatistics.getSendedMessages(userId));
            		statement.setLong(3, channelStatistics.getTotalGiftedSubs(SubscriptionPlan.NONE, userId));
            		statement.setLong(4, channelStatistics.getTotalGiftedSubs(SubscriptionPlan.TIER1, userId));
            		statement.setLong(5, channelStatistics.getTotalGiftedSubs(SubscriptionPlan.TIER2, userId));
            		statement.setLong(6, channelStatistics.getTotalGiftedSubs(SubscriptionPlan.TIER3, userId));
            		statement.setLong(7, channelStatistics.getCheerdBits(userId));
            		statement.executeUpdate();
            	}
            	
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


	
	public ResultSet getByChannelId(String channel_id){
		return get("channel_id", channel_id);
	}
	
	private String getCurrentUnixTimestamp() {
		return String.valueOf(LocalDate.now().atStartOfDay(ZoneOffset.systemDefault()).toEpochSecond());
	}
	
	public void startScheduler(){
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				saveAllChannelStatistics();
				startScheduler();
			}
		};
		new Timer().schedule(task, getAutoSaveTime());
	}
	
	private Date getAutoSaveTime() {
		LocalDateTime tomorrowMidnight = LocalDateTime.of(LocalDate.now(ZoneId.systemDefault()), LocalTime.MIDNIGHT).minusMinutes(5).plusDays(1);
		
		LocalTime now = LocalTime.now(ZoneId.systemDefault());
		if(now.getHour() == 23 && now.getMinute() >= 50){
			tomorrowMidnight = tomorrowMidnight.plusDays(1);
		}
		
		logger.info("Channel statistics Autosave is scheduled vor: ["+tomorrowMidnight.format(DateTimeFormatter.ofPattern("HH:mm | dd-MM-yyy"))+"]");
		return Date.from(tomorrowMidnight.atZone(ZoneId.systemDefault()).toInstant());
	}
	

	/**
	 * Also saves all data drom {@link CountVariableDatabase}.
	 */
	public void saveAllChannelStatistics() {
//		MessageComponent.clearDocumentCache();
//		Main.MAIN_FRAME.getTitleBar().getMainTab().getStatistics().save(false);
//		Main.MAIN_FRAME.getTitleBar().getSecondTab().getStatistics().save(false);
//		Main.MAIN_FRAME.getTitleBar().getThirdTab().getStatistics().save(false);
//		TitleBar.toggleTitleRowIndex();
//		Main.MAIN_FRAME.getTitleBar().getMainTab().getStatistics().save(false);
//		Main.MAIN_FRAME.getTitleBar().getSecondTab().getStatistics().save(false);
//		Main.MAIN_FRAME.getTitleBar().getThirdTab().getStatistics().save(false);
//		TitleBar.toggleTitleRowIndex();
		DatabaseManager.getCountVariableDatabase().saveAll();
		DatabaseManager.commit();
	}
	
}
