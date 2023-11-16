package de.minetrain.minechat.data.databases;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;

public class Database {
	private static final Logger logger = LoggerFactory.getLogger(Database.class);
	
	public Database(String... sql_tables) throws SQLException {
		logger.info("Create a new database table. --> "+sql_tables);
		
		try(Connection connection = DatabaseManager.connect(); Statement statement = connection.createStatement()){
			connection.setAutoCommit(true);
			for (String table : sql_tables) {
				statement.execute(table);
			}
		}
	}

}
