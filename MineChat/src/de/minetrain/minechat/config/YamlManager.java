package de.minetrain.minechat.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * This class manages the configuration for the application.
 * @author MineTrain/Justin
 * @since 29.04.2023
 * @version 1.0
 */
public class YamlManager extends HashMap<String, Object>{
	private static final long serialVersionUID = 3482232292003401166L;
	private static final Logger logger = LoggerFactory.getLogger(YamlManager.class);
	private final String filePath; //Name of the configuration file to be loaded.
    private final Yaml yaml; //Yaml instance for parsing the configuration file.
	private static final String invalidFileChars = "<>:\"\\|?*"; //List of invalid chars, that operating systems don´t allow in there file names.

    /**
     * Constructor for the ConfigManager class.
     * @param configFileName Name of the configuration file to be loaded.
     */
    public YamlManager(String filePath) {
    	super(new HashMap<String, Object>());
    	if(!filePath.endsWith(".yml")){
    		logger.warn("Tryed to load a non yaml file! - Automatically appending a '.yml'...", new IllegalArgumentException(filePath+" does not end with '.yml'!"));
    		filePath = filePath+".yml";
    	}
    	
		filePath = filePath
			.replaceAll("[" + Pattern.quote(invalidFileChars) + "]", "_")
			.replaceAll("(?i)null", "")
			.replaceAll("\\s", "_");
    	
        this.filePath = filePath;
		this.yaml = new Yaml();
    	logger.info("Reading config file: ["+filePath+"]");
    	
    	try {
    		File file = new File(filePath);
    		if(!file.isFile()) {
    			logger.warn("["+filePath+"] Not found! Trying to create this file.");
    			Files.createDirectories(Paths.get(filePath.substring(0, filePath.lastIndexOf("/"))));
    			file.createNewFile();
    		}
    		
    		reloadConfig();
    		
	    } catch (FileNotFoundException ex) {
			throw new IllegalArgumentException("Can't initialize YamlManager. File not found!", ex);
		} catch (IOException ex) {
			throw new IllegalArgumentException("Can't initialize YamlManager. Can´t create file", ex);
		}
	}
    
    /**
     * Method for reloading the configuration file.
     * @throws FileNotFoundException If the configuration file is not found.
     */
    public void reloadConfig() throws FileNotFoundException{
    	logger.info("Reload config file! - ["+filePath+"]");
    	Map<String, Object> yamlLoad = yaml.load(new FileInputStream(filePath));
    	if(yamlLoad != null){
    		clear();
    		putAll(yamlLoad);
    		logger.info("Config reloadet...");
    		return;
    	}
    	
		logger.info("Can´t reload the config file...");
    }
    
    /**
     * Save the Config to the file.
     */
    public void saveConfigToFile() {
    	DumperOptions options = new DumperOptions();
    	options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    	
        Yaml yaml = new Yaml(options);
        try {
            FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8);
            yaml.dump(this, writer);
            writer.close();
        } catch (IOException ex) {
        	logger.error("Can´t save config file!", ex);
        }
    }
    
    /**
     * Method for getting a boolean value from the configuration file.
     * @param path Path to the boolean value in the configuration file.
     * @return The boolean value.
     */
	public final boolean getBoolean(String path) {
		return getBoolean(path, false);
	}
    
	/**
     * Method for getting a long value from the configuration file.
     * @param path Path to the long value in the configuration file.
     * @return The long value.
     */

	public final long getLong(String path) {
		return getLong(path, 0l);
	}
    
	/**
     * Method for getting an integer value from the configuration file.
     * @param path Path to the integer value in the configuration file.
     * @return The integer value.
     */
	public final int getInt(String path) {
		return getInt(path, 0);
	}
    
	/**
     * Method for getting a string value from the configuration file.
     * @param path Path to the string value in the configuration file.
     * @return The string value.
     */
	public final String getString(String path) {
		return getString(path, ">null<");
	}
	
	/**
     * Method for getting a string array from the configuration file.
     * @param path Path to the string array in the configuration file.
     * @param regex Regular expression used to split the string array.
     * @return The string array.
     */
	public final String[] getStringArray(String path, String regex) {
        return getStringArray(path, regex, ">null<");
    }
	
	/**
     * Method for getting a string array from the configuration file.
     * @param path Path to the string array in the configuration file.
     * @param regex Regular expression used to split the string array.
     * @param defaultValue Default value to be returned if the string array is not found.
     * @return The string array.
     */
	public final String[] getStringArray(String path, String regex, String defaultValue) {
        return getString(path, defaultValue).replace(" ", "").split(regex);
    }
	
	/**
	 * Returns a boolean value from the configuration file based on the given path.
	 * If the path is not found, it returns the default value.
	 * @param path the path of the boolean value in the configuration file
	 * @param defaultValue the default value to return if the path is not found
	 * @return the boolean value from the configuration file or the default value if the path is not found
	 * @throws ConfigurationException if the value is not a boolean type
	 */
	@SuppressWarnings("unchecked")
	public final boolean getBoolean(String path, boolean defaultValue) {
		String[] keys = path.split("\\.");
		Map<String, Object> current = this;
		for (String key : keys) {
			if (current.containsKey(key)) {
				Object value = current.get(key);
				if (value instanceof Map) {
					current = (Map<String, Object>) value;
				} else if (value instanceof Boolean) {
					return (boolean) value;
				}
			}
		}

		throwWarn(path);
		return defaultValue;
	}
	
	/**
	 * Returns a long value from the configuration file based on the given path.
	 * If the path is not found, it returns the default value.
	 * @param path the path of the long value in the configuration file
	 * @param defaultValue the default value to return if the path is not found
	 * @return the long value from the configuration file or the default value if the path is not found
	 * @throws ConfigurationException if the value is not a number type
	 */
	@SuppressWarnings("unchecked")
	public Long getLong(String path, long defaultValue) {
	    String[] keys = path.split("\\.");
	    Map<String, Object> current = this;
	    for (String key : keys) {
	        if (current.containsKey(key)) {
	            Object value = current.get(key);
	            if (value instanceof Map) {
	                current = (Map<String, Object>) value;
	            } else if (value instanceof Number) {
	                return ((Number) value).longValue();
	            }
	        }
	    }
	    
	    throwWarn(path);
	    return defaultValue;
	}
	
	/**
	 * Returns an int value from the configuration file based on the given path.
	 * If the path is not found, it returns the default value.
	 * @param path the path of the int value in the configuration file
	 * @param defaultValue the default value to return if the path is not found
	 * @return the int value from the configuration file or the default value if the path is not found
	 * @throws ConfigurationException if the value is not a number type
	 */
	public int getInt(String path, int defaultValue) {
	    Long value = getLong(path, defaultValue);
	    if(value > Integer.MAX_VALUE){
	    	logger.warn("Intiger is to large! Use getLong instat!");
	    	return defaultValue;
	    }
	    
	    return Integer.parseInt(String.valueOf(value));
	}

	/**
	 * Returns a string value from the configuration file based on the given path.
	 * If the path is not found, it returns the default value.
	 * @param path the path of the string value in the configuration file
	 * @param defaultValue the default value to return if the path is not found
	 * @return the string value from the configuration file or the default value if the path is not found
	 * @throws ConfigurationException if the value is not a string type
	 */
	@SuppressWarnings("unchecked")
	public final String getString(String path, String defaultValue) {
	    String[] keys = path.split("\\.");
	    Map<String, Object> current = this;
	    for (String key : keys) {
	        if (current.containsKey(key)) {
	            Object value = current.get(key);
	            if (value instanceof Map) {
	                current = (Map<String, Object>) value;
	            } else if (value instanceof String) {
	                return (String) value;
	            }
	        }
	    }
	    
	    throwWarn(path);
	    return defaultValue;
	}
	
	/**
	 * Returns a list of long values from the configuration file based on the given path.
	 * If the path is not found, it returns the default value.
	 * @param path the path of the long list in the configuration file
	 * @return the list of long values from the configuration file
	 * @throws ConfigurationException if the value is not a list of longs
	 */
	@SuppressWarnings("unchecked")
	public final List<Long> getLongList(String path) {
	    String[] keys = path.split("\\."); //Split the path into individual keys
	    Map<String, Object> current = this; //Start at the root of the configuration map
	    for (String key : keys) { //Traverse down the map to the requested key
	        if (current.containsKey(key)) {
	            Object value = current.get(key);
	            if (value instanceof Map) { //If the value is a map, traverse further down
	                current = (Map<String, Object>) value;
	            } else if (value instanceof List) { //If the value is a list, convert the values to longs and return the list
	            	List<Object> values = (List<Object>) value;
	    	        List<Long> longList = new ArrayList<>();
	    	        
	    	        values.forEach(index -> {
	    	        	if(index instanceof Long || index instanceof Integer){
	    	                longList.add(Long.valueOf(String.valueOf(index)));
	    	        	}
	    	        });
	    	        
	    	        return longList;
	            }
	        }
		}

		//If the key wasn't found, throw an exception
		throwWarn(path);
		return new ArrayList<Long>();
	}
	
	/**
	 * Returns a list of string values from the configuration file based on the given path.
	 * If the path is not found, it returns the default value.
	 * @param path the path of the string list in the configuration file
	 * @return the list of string values from the configuration file
	 * @throws ConfigurationException if the value is not a list of strings
	 */
	@SuppressWarnings("unchecked")
	public final List<String> getStringList(String path) {
	    String[] keys = path.split("\\.");
	    Map<String, Object> current = this;
	    for (String key : keys) {
	        if (current.containsKey(key)) {
	            Object value = current.get(key);
	            if (value instanceof Map) {
	                current = (Map<String, Object>) value;
	            } else if (value instanceof List) {
	            	List<Object> values = (List<Object>) value;
	    	        List<String> stringList = new ArrayList<>();
	    	        
	    	        values.forEach(index -> {
	    	        	if(index instanceof String){
	    	        		stringList.add(String.valueOf(index));
	    	        	}
	    	        });
	    	        
	    	        return stringList;
	            }
	        }
	    }
	    

	    throwWarn(path);
	    return new ArrayList<String>();
	}
	
	/**
	 * Throws a warning message indicating that an invalid configuration path was provided.
	 * @param path the invalid configuration path
	 */
	private void throwWarn(String path) {
		logger.warn("Invalid config path!", new ConfigurationException("Cant find a value on this path: "+path));
	}
	
	

	/**
	 * Sets the value of a String at the given path in the configuration.
	 * @param path the path to the value, specified as a dot-separated string.
	 * @param value the String value to set.
	 */
	public void setString(String path, String value) {
		setString(path, value, false);
	}

	/**
	 * Sets the value of a boolean at the given path in the configuration.
	 * @param path the path to the value, specified as a dot-separated string
	 * @param value the boolean value to set
	 */
	public void setBoolean(String path, Boolean value) {
		setBoolean(path, value, false);
	}

	/**
	 * Sets the value of a number at the given path in the configuration.
	 * @param path the path to the value, specified as a dot-separated string
	 * @param value the number value to set
	 */
	public void setNumber(String path, Number value) {
		setNumber(path, value, false);
	}
	

	/**
	 * Sets the value of a String at the given path in the configuration.
	 * @param path the path to the value, specified as a dot-separated string.
	 * @param value the String {@link List} value to set.
	 */
	public void setStringList(String path, List<String> values) {
		setStringList(path, values, false);
	}
	
	/**
	 * Sets the value of a String at the given path in the configuration.
	 * @param path the path to the value, specified as a dot-separated string.
	 * @param value the String value to set.
	 * @param saveFile Whether the file should be saved immediately.
	 */
	@SuppressWarnings("unchecked")
	public void setString(String path, String value, boolean saveFile) {
	    String[] keys = path.split("\\.");
	    Map<String, Object> current = this;
	    for (int i = 0; i < keys.length - 1; i++) {
	        String key = keys[i];
	        if (!current.containsKey(key)) {
	            current.put(key, new LinkedHashMap<>());
	        }
	        Object valueObj = current.get(key);
	        if (valueObj instanceof Map) {
	            current = (Map<String, Object>) valueObj;
	        } else {
	            Map<String, Object> newMap = new LinkedHashMap<>();
	            current.put(key, newMap);
	            current = newMap;
	        }
	    }
	    current.put(keys[keys.length - 1], value);
	    if(saveFile){saveConfigToFile();}
	}
	
	/**
	 * Sets the value of a boolean at the given path in the configuration.
	 * @param path the path to the value, specified as a dot-separated string
	 * @param value the boolean value to set
	 * @param saveFile Whether the file should be saved immediately.
	 */
	@SuppressWarnings("unchecked")
	public void setBoolean(String path, boolean value, boolean saveFile) {
	    String[] keys = path.split("\\.");
	    Map<String, Object> current = this;
	    for (int i = 0; i < keys.length - 1; i++) {
	        String key = keys[i];
	        if (!current.containsKey(key)) {
	            current.put(key, new LinkedHashMap<>());
	        }
	        Object valueObj = current.get(key);
	        if (valueObj instanceof Map) {
	            current = (Map<String, Object>) valueObj;
	        } else {
	            Map<String, Object> newMap = new LinkedHashMap<>();
	            current.put(key, newMap);
	            current = newMap;
	        }
	    }
	    current.put(keys[keys.length - 1], value);
	    if(saveFile){saveConfigToFile();}
	}

	/**
	 * Sets the value of a number at the given path in the configuration.
	 * @param path the path to the value, specified as a dot-separated string
	 * @param value the number value to set
	 * @param saveFile Whether the file should be saved immediately.
	 */
	@SuppressWarnings("unchecked")
	public void setNumber(String path, Number value, boolean saveFile) {
	    String[] keys = path.split("\\.");
	    Map<String, Object> current = this;
	    for (int i = 0; i < keys.length - 1; i++) {
	        String key = keys[i];
	        if (!current.containsKey(key)) {
	            current.put(key, new LinkedHashMap<>());
	        }
	        Object valueObj = current.get(key);
	        if (valueObj instanceof Map) {
	            current = (Map<String, Object>) valueObj;
	        } else {
	            Map<String, Object> newMap = new LinkedHashMap<>();
	            current.put(key, newMap);
	            current = newMap;
	        }
	    }
	    current.put(keys[keys.length - 1], value);
	    if(saveFile){saveConfigToFile();}
	}
	
	/**
	 * Sets the value of a String at the given path in the configuration.
	 * @param path the path to the value, specified as a dot-separated string.
	 * @param value the String {@link List} value to set.
	 * @param saveFile Whether the file should be saved immediately.
	 */
	@SuppressWarnings("unchecked")
	public void setStringList(String path, List<String> values, boolean saveFile) {
	    String[] keys = path.split("\\.");
	    Map<String, Object> current = this;
	    for (int i = 0; i < keys.length - 1; i++) {
	        String key = keys[i];
	        if (!current.containsKey(key)) {
	            current.put(key, new LinkedHashMap<>());
	        }
	        Object valueObj = current.get(key);
	        if (valueObj instanceof Map) {
	            current = (Map<String, Object>) valueObj;
	        } else {
	            Map<String, Object> newMap = new LinkedHashMap<>();
	            current.put(key, newMap);
	            current = newMap;
	        }
	    }
	    current.put(keys[keys.length - 1], values);
	    if(saveFile){saveConfigToFile();}
	}
	
	/**
	 * Removes everything after a given path.
	 * @param path
	 */
	@SuppressWarnings("unchecked")
	public final void remove(String path) {
		String[] keys = path.split("\\.");
	    Map<String, Object> current = this;
	    for (int i = 0; i < keys.length - 1; i++) {
	        String key = keys[i];
	        if (!current.containsKey(key)) {
	            current.put(key, new LinkedHashMap<>());
	        }
	        Object valueObj = current.get(key);
	        if (valueObj instanceof Map) {
	            current = (Map<String, Object>) valueObj;
	        } else {
	            Map<String, Object> newMap = new LinkedHashMap<>();
	            current.put(key, newMap);
	            current = newMap;
	        }
	    }
	    
	    current.remove(keys[keys.length-1]);
		saveConfigToFile();
	}
}
