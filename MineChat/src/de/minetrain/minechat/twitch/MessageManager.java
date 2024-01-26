package de.minetrain.minechat.twitch;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.features.macros.MacroObject;
import de.minetrain.minechat.main.Channel;
import de.minetrain.minechat.main.ChannelManager;
import de.minetrain.minechat.twitch.obj.AsyncMessageHandler;
import de.minetrain.minechat.utils.ChatMessage;


/**
 * Provides functionality to format messages and send them out to the Twitch API. 
 * And work around spamming protection by using a spam protector character and a cooldown timer.
 * 
 * The spam protector is a Unicode character that is not visible in most fonts, 
 * making it an effective way to prevent duplicate messages from being detected as spam.
 * 
 * @author MineTrain/Justin
 * @since 15.05.2023
 * @version 2.0
 */
public class MessageManager {
	private static final Logger logger = LoggerFactory.getLogger(MessageManager.class);
	private static AsyncMessageHandler defaultMessageHandler;
	private static AsyncMessageHandler moderatorMessageHandler;
//	private static final String spamProtector = "ㅤ"; //The spam protector character.
//	private static final String spamProtector = "᲼"; //The spam protector character.
	private static final String spamProtector = "󠀀"; //The spam protector character.
	private static String lastMessage = ">null<"; //The last message sent by this manager.
    private static Instant lastSentTime = Instant.now(); //The time when the last message was sent.
    private static final int MAX_MESSAGE_LENGTH = 490;
    public static HashMap<String, Long> channelSlowMods = new HashMap<String, Long>(); //Channel_id, miliseconds
    
    public MessageManager() {
    	defaultMessageHandler = new AsyncMessageHandler(0);
    	defaultMessageHandler.start();
    	
    	moderatorMessageHandler = new AsyncMessageHandler(300);
    	moderatorMessageHandler.start();
	}
    

	/**
	 * Sends a message to the Twitch API.
	 * <br>Split messages if they are longer then 500 messages.
	 * <br>If the same message has been sent recently, the spam protector character is
	 * appended to the message.
	 * <br>Waits 1.5 Seconds befor sending the next one out. 
	 * 
	 * <p>NOTE: If the user says he is a channel moderator, the message gets send out without limits.
	 * 
	 * @param message the message to send to the Twitch API
	 */
    public static void sendMessage(Channel channel, String message) {
    	if(message.length()>MAX_MESSAGE_LENGTH){
    		splitString(message).forEach(newMessage -> {
    			sendMessage(channel, newMessage);
    		});
    		
    		return;
    	}
    	
    	if(channel.isModerator()){
            getModeratorMessageHandler().addMessage(new ChatMessage(channel, TwitchManager.ownerChannelName, message));
    	}else{
    		sendDelayedMessage(channel, message);
    	}
    }
    
    public static void sendMessage(MacroObject macro) {
    	try {
    		sendMessage(ChannelManager.getChannel(macro.getChannelId()), macro.getRandomOutput());
		} catch (Exception ex) {
			logger.error("Can´t send message from a macro:",ex);
		}
	}
    
    private static void sendDelayedMessage(Channel channel, String message) {
        Instant now = Instant.now(); //Get the current time.
        
        //If the same message has been sent recently, append the spam protector character.
        if(message.equals(lastMessage) && lastSentTime.plusSeconds(30).isAfter(now)) {
            message += " "+getSpamprotector();
        }
        
        //Update the last message and last sent time variables.
        lastMessage = message;
        lastSentTime = now;

        getDefaultMessageHandler().addMessage(new ChatMessage(channel, TwitchManager.ownerChannelName, message));
    }
    
    
    
    public static List<String> splitString(String input) {
        List<String> chunks = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int chunkSize = MAX_MESSAGE_LENGTH-10;

        int wordBoundary = -1; // Index of the last space character within the chunk limit
        int sentenceBoundary = -1; // Index of the last sentence-ending character within the last 50 characters

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            builder.append(c);

            if (c == ' ') {
                wordBoundary = builder.length() - 1;
            }

//            if (builder.length() >= chunkSize - 100 && (c == '.')) {
            if (builder.length() >= chunkSize - 100 && (c == '.' || c == '!' || c == '?')) {
                sentenceBoundary = builder.length() - 1;
            }

            if (builder.length() == chunkSize) {
                if (sentenceBoundary != -1) {
                    chunks.add(builder.substring(0, sentenceBoundary + 1));
                    builder.delete(0, sentenceBoundary + 1);
                    wordBoundary = -1;
                    sentenceBoundary = -1;
                } else if (wordBoundary != -1) {
                    chunks.add(builder.substring(0, wordBoundary));
                    builder.delete(0, wordBoundary + 1);
                    wordBoundary = -1;
                    sentenceBoundary = -1;
                } else {
                    chunks.add(builder.toString());
                    builder.setLength(0);
                    sentenceBoundary = -1;
                }
            }
        }

        // Add the remaining characters as the last chunk
        if (builder.length() > 0) {
            chunks.add(builder.toString());
        }
        
        for(int i = 0; i < chunks.size(); i++){
			chunks.set(i, "("+(i+1)+") "+chunks.get(i));
		}

        return chunks;
    }


    
    /**
     * Updates the user interface queue button text based on the current message count.
     */
    public static void updateQueueButton() {
//    	Main.MAIN_FRAME.queueButton.setText("Message Queue: "+(getDefaultMessageHandler().getMessageCount()+getModeratorMessageHandler().getMessageCount()));
    }
    
	public static AsyncMessageHandler getDefaultMessageHandler() {
		return defaultMessageHandler;
	}

	public static AsyncMessageHandler getModeratorMessageHandler() {
		return moderatorMessageHandler;
	}
	
	public static String getLastMessage() {
		return lastMessage;
	}

	public static void setLastMessage(String lastMessage) {
		MessageManager.lastMessage = lastMessage;
	}

	public static String getSpamprotector() {
		return spamProtector;
	}

}

