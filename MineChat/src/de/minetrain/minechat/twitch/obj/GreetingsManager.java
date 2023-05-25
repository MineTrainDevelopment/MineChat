package de.minetrain.minechat.twitch.obj;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.utils.ChatMessage;

/**
 * The GreetingsManager class is a specialized String list class that stores
 * usernames of ungreetsed users and marks them as greeted when mentioned.
 * 
 * This class extends the {@link ArrayList} class.
 * 
 * @author MineTrain/Justin
 * @since 23.05.2023
 * @version 1.0
 */
public class GreetingsManager extends ArrayList<String>{
	private static final Logger logger = LoggerFactory.getLogger(GreetingsManager.class);
	private static final long serialVersionUID = -6152772888792843950L;
	private static final Random random = new Random();
	
	private final List<String> mentionedUsers = new ArrayList<String>();
	private final ChannelTab parentTab;
	private String greetingsText = "";
	
	/**
     * Constructs a new GreetingsManager object with the specified ChannelTab.
     * @param tab the ChannelTab associated with this GreetingsManager
     */
	public GreetingsManager(ChannelTab tab) {
		this.parentTab = tab;
	}
	
	/**
     * Adds the specified name to the list of users to greet.
     * <br>The name will be formatted before adding to remove special chars.
     * 
     * @param name the name to be added
     * @return true if the name was successfully added, false otherwise
     */
	@Override
	public boolean add(String name) {
		return super.add(formatUserName(name));
	}

	/**
     * Removes the specified object from the list of useres to greeted.
     * 
     * <p>If the object is a String, it will be formatted before removal.
     * <br>The removed name will be added to the list of mentioned users.
     * 
     * @param object the object to be removed
     * @return true if the object was successfully removed, false otherwise
     */
	@Override
	public boolean remove(Object object) {
		if(!isInstanceofString(object)){return false;}
		
		String name = formatUserName(object.toString());
		mentionedUsers.add(name);
		return super.remove(name);
	}
	
	/**
     * Removes all elements from the list of greeted users.
     * <br>Each removed name will be added to the list of mentioned users.
     */
	@Override
	public void clear() {
		forEach(name -> remove(name));
		super.clear();
	}
	
	/**
     * Checks if the list of greeted and ungreeted users contains the specified object.
     * <br>If the object is a String, it will be formatted before the check.
     * 
     * @param object the object to be checked for containment
     * @return true if the object is found in the list, false otherwise
     */
	@Override
	public boolean contains(Object object) {
		if(!isInstanceofString(object)){return false;}

		String name = formatUserName(object.toString());
		return super.contains(name) || mentionedUsers.contains(name);
	}
	
	/**
     * Marks the specified name as mentioned, removing it from the list of users to greeted.
     * 
     * @param name the name to be marked as mentioned
     * @return true if the name was successfully marked as mentioned, false otherwise
     */
	public boolean setMentioned(String name){
		return remove(name);
	}
	
	/**
     * Checks if the specified name has been mentioned.
     * 
     * @param name the name to be checked
     * @return true if the name has been mentioned, false otherwise
     */
	public boolean isMentioned(String name){
		return mentionedUsers.contains(formatUserName(name));
	}
	
	/**
     * Sends greetings to all useres that warned mentioned befor.
     * <br>If the list is empty, no greetings will be sent.
     * 
     * <p>A random greeting message gets chosen.
     * The greetings message is sent using the TwitchManager.
     */
	public void sendGreetingToAll(){
		if(isEmpty()){return;}
		String greeting = parentTab.getGreetingTexts().get(random.nextInt(parentTab.getGreetingTexts().size()));
		
		forEach(name -> {
			if(greetingsText.length() + greeting.length() + name.length() < 490){
				greetingsText += "@"+name+", ";
			}
		});

		String greetingsTextSubstring = greetingsText.substring(0, greetingsText.lastIndexOf(","));
		String message = (greeting.contains("{USER}") ? greeting.replace("{USER}", greetingsTextSubstring) : greetingsTextSubstring+" - "+greeting);
		TwitchManager.sendMessage(new ChatMessage(parentTab, TwitchManager.ownerChannelName, message));
		greetingsText = "";
	}
	
	/**
     * Formats the specified name by converting it to lowercase and removing certain characters.
     * 
     * @param name the name to be formatted
     * @return the formatted name
     */
	private String formatUserName(String name) {
		return name.toLowerCase().replaceAll("[-+.^:,]","");
	}
	
	/**
     * Checks if the specified object is an instance of String.
     * <br>If the object is not a String, a warning log message is generated.
     * 
     * @param object the object to be checked
     * @return true if the object is an instance of String, false otherwise
     */
	private static final boolean isInstanceofString(Object object) {
		if(object instanceof String == false){
			logger.warn("Object [name] is not an instance of String! \n --> "+object.toString());
			return false;
		}
		
		return true;
	}
}
