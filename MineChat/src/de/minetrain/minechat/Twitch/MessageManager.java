package de.minetrain.minechat.Twitch;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.gui.objects.TitleBar;

/**
 * Provides functionality to format messages and send them out to the Twitch API. 
 * And work around spamming protection by using a spam protector character and a cooldown timer.
 * 
 * The spam protector is a Unicode character that is not visible in most fonts, 
 * making it an effective way to prevent duplicate messages from being detected as spam.
 * 
 * @author MineTrain/Justin
 * @since 05.05.2023
 * @version 1.0
 */
public class MessageManager {
	private static final Logger logger = LoggerFactory.getLogger(MessageManager.class);
	private static final String spamProtector = "ㅤ"; //The spam protector character.
	private static String lastMessage = ">null<"; //The last message sent by this manager.
    private static Instant lastSentTime; //The time when the last message was sent.

	/**
	 * Sends a message to the Twitch API.
	 * 
	 * <br>If the same message has been sent recently, the spam protector character is
	 * appended to the message.
	 * 
	 * <br>Waits 1.5 Seconds befor sending the next one out. 
	 * TODO: Make it Async.
	 * 
	 * @param message the message to send to the Twitch API
	 */
    public static void sendMessage(String message) {
        Instant now = Instant.now(); //Get the current time.
        
        //If the same message has been sent recently, append the spam protector character.
        if(message.equals(lastMessage) && lastSentTime.plusSeconds(30).isAfter(now)) {
            message += " "+spamProtector;
        }
        
        //Update the last message and last sent time variables.
        lastMessage = message;
        lastSentTime = now;
        
        System.out.println("Sending message {"+message+"}");
//        TwitchManager.sendMessage(TitleBar.currentTab.getTabName(), null, message);
        try {Thread.sleep(1500);} catch (InterruptedException ex) {logger.error("Thread sleep faild!", ex);}
    }
}

