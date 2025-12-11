package de.minetrain.minechat.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;

import de.minetrain.minechat.data.databases.AutoReplyDatabase;
import de.minetrain.minechat.data.databases.ChannelsDatabase;
import de.minetrain.minechat.data.databases.CountVariableDatabase;
import de.minetrain.minechat.data.databases.EmoteDatabase;
import de.minetrain.minechat.data.databases.MacroDatabase;
import de.minetrain.minechat.data.databases.MessageHighlightDatabase;
import de.minetrain.minechat.data.databases.OwnerCacheDatabase;

public class DatabaseManager {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
	public static final String DATABASE_URL = "jdbc:sqlite:data/data.db";
	public static Connection connection = null;

	private static AutoReplyDatabase autoReply;
	private static MacroDatabase macro;
	private static ChannelsDatabase channel;
	private static EmoteDatabase emote;
	private static MessageHighlightDatabase messageHighlight;
	private static OwnerCacheDatabase ownerCacheDatabase;
	private static CountVariableDatabase countVariableDatabase;

	public DatabaseManager() {
		try {
			autoReply = new AutoReplyDatabase();
			macro = new MacroDatabase();
			channel = new ChannelsDatabase();
			emote = new EmoteDatabase();
			messageHighlight = new MessageHighlightDatabase();
			ownerCacheDatabase = new OwnerCacheDatabase();
			countVariableDatabase = new CountVariableDatabase();
		} catch (SQLException ex) {
			logger.error("Can´t prepare all databases.", ex);
			System.exit(1);
		}
	}

	/// @deprecated Use execute(Consumer<Connection> action) or openReadOnlyConnection() instead.
	@Deprecated
	public static void commit() {
		try {
			logger.debug("Commiting database changes.");
			connection.commit();
		} catch (SQLException ex) {
			logger.error("Can´t commit.", ex);
		}
	}

	/// @deprecated Use execute(Consumer<Connection> action) or openReadOnlyConnection() instead.
	@Deprecated
	public static Connection connect() {
		try {
			if (connection != null && !connection.isClosed()) {
				logger.debug("Connection is still open");
				return connection;
			}

			logger.debug("Opening a new database connection.");
			connection = DriverManager.getConnection(DATABASE_URL);
			connection.setAutoCommit(false);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		return connection;
	}

	/// Executes the given select with a new database connection.
	/// Closing the connection is handled automatically.
	///
	/// @param select The select to execute with the database connection.
	public static <T> T executeSelect(SqlFunction<Connection, T> select) {
		SQLiteConfig config = new SQLiteConfig();
		config.setReadOnly(true);
		try (Connection connection = config.createConnection(DATABASE_URL)) {
			return select.apply(connection);
		} catch (Exception e) {
			logger.error("Error reading from database", e);
		}
		return null;
	}

	/// Executes the given action with a new database connection.
	/// Closing and committing the connection is handled automatically.
	///
	/// @param action The action to execute with the database connection.
	public static void execute(SqlConsumer<Connection> action) {
		try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
			connection.setAutoCommit(false);
			action.accept(connection);
			connection.commit();
		} catch (Exception e) {
			logger.error("Error executing database action", e);
		}
	}


	public static AutoReplyDatabase getAutoReply() {
		return autoReply;
	}

	public static MacroDatabase getMacro() {
		return macro;
	}

	public static ChannelsDatabase getChannel() {
		return channel;
	}

	public static EmoteDatabase getEmote() {
		return emote;
	}

	public static MessageHighlightDatabase getMessageHighlight() {
		return messageHighlight;
	}

	public static OwnerCacheDatabase getOwnerCache() {
		return ownerCacheDatabase;
	}

	public static CountVariableDatabase getCountVariableDatabase() {
		return countVariableDatabase;
	}


}
