package de.minetrain.minechat.utils.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import de.minetrain.minechat.config.YamlManager;

public class PluginManager extends ArrayList<MinePlugin>{
	private static final Logger logger = LoggerFactory.getLogger(PluginManager.class);
	private static final long serialVersionUID = 9018932615425049118L;
	
	public PluginManager() {
		loadPluginsFromDirectory(Path.of("data/plugins/"));
		executeAllPlugins();
	}

	public void loadPlugin(MinePlugin plugin) {
        add(plugin);
        // Implement loading logic as needed
    }

    public void unloadPlugin(MinePlugin plugin) {
        remove(plugin);
        // Implement unloading logic as needed
    }

    public void executeAllPlugins() {
        for (MinePlugin plugin : this) {
            plugin.load();
        }
    }
    
    public void loadPluginsFromDirectory(Path directoryPath) {
        File directory = directoryPath.toFile();

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".jar"));

            if (files != null) {
                for (File file : files) {
                    loadPluginFromFile(file);
                }
            }
        } else {
            logger.error("Invalid directory path: " + directoryPath);
        }
    }
    
    private void loadPluginFromFile(File file) {
        try {
            JarFile jarFile = new JarFile(file.getAbsolutePath());
            URL[] urls = { new URL("jar:file:" + file.getAbsolutePath() + "!/") };
            URLClassLoader classLoader = URLClassLoader.newInstance(urls);
            Enumeration<JarEntry> entries = jarFile.entries();
            PluginMetaData metaData = extraktPluginYamlData(jarFile);
            
            if(metaData == null){
            	logger.warn("Can´t load plugin -> "+jarFile.getName()+"\n   missing plugin.yml");
            	return;
            }
            
            boolean success = false;
            
            if(metaData.main != null){
            	while (entries.hasMoreElements()) {
            		JarEntry jarEntry = entries.nextElement();
            		
            		if(jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")){
            			continue;
            		}
            		
            		// -6 because of .class
            		String className = jarEntry.getName().substring(0,jarEntry.getName().length()-6);
            		className = className.replace('/', '.');
            		
            		if (className.equals(metaData.main)) {
            			Class<?> clazz = classLoader.loadClass(className);
            			
            			if (MinePlugin.class.isAssignableFrom(clazz)) {
            				MinePlugin plugin = (MinePlugin) clazz.getDeclaredConstructor().newInstance();
            				add(plugin);
            				success = true;
            				logger.info("Loaded plugin -> \""+metaData.name+" - "+metaData.version+"\" by \""+metaData.author+"\" successfully!");
            			}
            		}
            	}
            }
            
            if(!success){
            	logger.warn("Can´t load plugin \""+metaData.name+"\" by \""+metaData.author+"\" because \"class path\" is either null, or invalid.", 
            			new IllegalArgumentException("Provided main class path -> " + metaData.main));
            }

        } catch (Exception ex) {
            logger.error("Failed to load plugin from file: "+file.getName());
        }
    }

	private record PluginMetaData(String name, String author, String version, String main){};

	private PluginMetaData extraktPluginYamlData(JarFile jarFile) throws FileNotFoundException, IOException {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
        	if(jarEntry.getName().replace(".yaml", ".yml").equals("plugin.yml")){
        		YamlManager yaml = new YamlManager(null).reloadConfig(new Yaml().load(jarFile.getInputStream(jarEntry)));
        		return new PluginMetaData(
        				yaml.getString("name", "Untitled plugin"),
        				yaml.getString("author", "Undefined author"),
        				yaml.getString("version", "Undefined version"),
        				yaml.getString("main", null));
        	}
        }
		return null;
	}

}
