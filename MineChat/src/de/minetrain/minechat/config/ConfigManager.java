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

import de.minetrain.minechat.main.Main;

/**
 * This class manages the configuration for the application.
 * @author MineTrain/Justin
 * @since 29.04.2023
 * @version 1.0
 */
public class ConfigManager {
	private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
	private final String configFileName; //Name of the configuration file to be loaded.
    private final Yaml yaml; //Yaml instance for parsing the configuration file.
    private Map<String, Object> config; //Map containing the configuration values.

    /**
     * Constructor for the ConfigManager class.
     * @param configFileName Name of the configuration file to be loaded.
     */
    public ConfigManager(String configFileName, boolean createFile){
    	Main.LOADINGBAR.setProgress("Reading config file", 5);
    	logger.info("Reading config file...");
//    	configFileName = configFileName.replaceAll("[" + Pattern.quote("<>:\"\\|?*()") + "]", "_");
    	this.configFileName = configFileName;
        yaml = new Yaml();
        
        try {
        	if(createFile){
        		Files.createDirectories(Paths.get(configFileName.substring(0, configFileName.lastIndexOf("/"))));
        		new File(configFileName).createNewFile();
        	}
        	
			reloadConfig();
			
			if(getRawConfig() == null && createFile){
				config = new HashMap<String, Object>();
			}
			
		} catch (FileNotFoundException ex) {
	    	Main.LOADINGBAR.setError(configFileName+" not found!");
			throw new IllegalArgumentException("Canot initialize ConfigManager. File not found!", ex);
		} catch (IOException ex) {
			throw new IllegalArgumentException("Canot initialize ConfigManager. Can´t create file", ex);
		}
    }
    
    /**
     * Method for reloading the configuration file.
     * @throws FileNotFoundException If the configuration file is not found.
     */
    public void reloadConfig() throws FileNotFoundException{
    	logger.info("Reload the ocnfig file!");
        config = yaml.load(new FileInputStream(configFileName));
//        Main.SETTINGS = new Settings(this); //Initialize settings using configuration values
        logger.info("Config reloadet...");
    }
    
    /**
     * Save the Config to the file.
     */
    public void saveConfigToFile() {
    	DumperOptions options = new DumperOptions();
    	options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    	
        Yaml yaml = new Yaml(options);
        try {
            FileWriter writer = new FileWriter(configFileName, StandardCharsets.UTF_8);
            yaml.dump(getRawConfig(), writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
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
		Map<String, Object> current = getRawConfig();
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
	    Map<String, Object> current = getRawConfig();
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
	    	logger.warn("Intiger is to larg! Use getLong instat!");
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
	    Map<String, Object> current = getRawConfig();
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
	    Map<String, Object> current = getRawConfig(); //Start at the root of the configuration map
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
	    Map<String, Object> current = getRawConfig();
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
	 * @param value the String value to set.
	 * @param saveFile Whether the file should be saved immediately.
	 */
	@SuppressWarnings("unchecked")
	public void setString(String path, String value, boolean saveFile) {
	    String[] keys = path.split("\\.");
	    Map<String, Object> current = getRawConfig();
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
	    Map<String, Object> current = getRawConfig();
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
	    Map<String, Object> current = getRawConfig();
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
	    Map<String, Object> current = getRawConfig();
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

	public Map<String, Object> getRawConfig() {
		return config;
	}
}
