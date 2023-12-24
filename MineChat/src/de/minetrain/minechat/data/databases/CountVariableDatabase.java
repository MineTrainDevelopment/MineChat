package de.minetrain.minechat.data.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;

//Table name: counter_variables
//name, value

public class CountVariableDatabase extends Database {
	private static final Logger logger = LoggerFactory.getLogger(CountVariableDatabase.class);
	public record CounterVariables(String name, long value){};
	private record Name(String name, int multiplier){};
	private static HashMap<String, Long> cache = new HashMap<String, Long>();//name, value
	
	private static final String tabelName = "counter_variables";
	private static final String insert_SQL = "INSERT INTO "+tabelName+"(name, value) VALUES(?,?)";
	private static final String update_SQL = "UPDATE "+tabelName+" SET name = ? , value = ? WHERE name = ?";
	private static final String check_SQL = "SELECT name FROM "+tabelName+" WHERE name = ?";
	private static final String select_sql = "SELECT name, value FROM "+tabelName;
	
	public CountVariableDatabase() throws SQLException {
		super("CREATE TABLE IF NOT EXISTS "+tabelName+" (\n"
                + "	name text PRIMARY KEY,\n"
                + "	value integer NOT NULL\n"
                + ");");
		
		getAll();
	}

	/**
	 * NOTE: Auto commit if no value is provided.
	 * <br> This sets the value to 0.
	 * @param message
	 * @return The new value.
	 */
	private static Long insert(String variableName){
		return insert(variableName, 0l);
	}
	
	/**
	 * NOTE: Auto commit = true;
	 * @param message
	 * @return The new value.
	 */
	private static Long insert(String variableName, Long value){
		logger.info("Insert new database entry");
		
		try{
			Connection connection = DatabaseManager.connect();
			PreparedStatement statement = connection.prepareStatement(check_SQL);
			statement.setString(1, variableName);
			ResultSet resultSet = statement.executeQuery();
			statement = connection.prepareStatement(insert_SQL);
			if (resultSet.next() && resultSet.getString(1) != null && !resultSet.getString(1).isEmpty()) {
				statement = connection.prepareStatement(update_SQL);
				statement.setString(3, variableName);
			}

            statement.setString(1, variableName);
            statement.setLong(2, value);
            statement.executeUpdate();
            DatabaseManager.commit();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
		
		return 0l;
	}
	
	public void saveAll(){
		cache.entrySet().forEach(entry -> insert(entry.getKey(), entry.getValue()));
	}
	
	private void getAll(){
		try{
			Connection connection = DatabaseManager.connect();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(select_sql);
			while(resultSet.next()){
				cache.put(resultSet.getString("name"), resultSet.getLong("value"));
			}
			DatabaseManager.commit();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		}
		
	}
	
	/**
	 * Resets the value for the specific variable to 0
	 */
	public void clearValue(String name) {
		setValue(name, 0l);
	}
	
	/**
	 * Increase the value for a specific variable by 1.
	 */
	public long increaseValue(String name){
		return increaseValue(name, removeVariableFromName(name).multiplier);
	}
	
	/**
	 * Increase the value for a specific variable by a specific amound.
	 */
	public long increaseValue(String name, int multiplier){
		return setValue(name, getValue(name)+multiplier);
	}
	
	/**
	 * @return
	 */
	public long getValue(String name){
		return cache.computeIfAbsent(removeVariableFromName(name).name, CountVariableDatabase::insert);
	}
	
	public HashMap<String, Long> getAllValues(){
		return cache;
	}
	
	/**
	 * Set the value for a specific variable to a specific amound.
	 */
	public long setValue(String name, Long value){
		cache.put(removeVariableFromName(name).name, value);
		return value;
	}

	private Name removeVariableFromName(String name){
		name = name.substring(0, name.endsWith("}") ? name.length()-1 : name.length())
			.replace("{COUNT_DISPLAY_", "")
			.replace("{COUNT_", "")
			.replace("{C_D_", "")
			.replace("{C_", "");
		
		String multiplier = Optional.ofNullable(name)
	        .filter(s -> s.contains("_"))
	        .map(s -> s.substring(0, s.indexOf('_')))
	        .filter(numericSubstring -> numericSubstring.matches("\\d+"))
	        .orElse("");
		
		if(!multiplier.isBlank()){
			name = name.replaceFirst(multiplier+"_", "");
		}
		
		return new Name(name, Integer.valueOf(multiplier.isBlank() ? "1" : multiplier));
	}
	
}
