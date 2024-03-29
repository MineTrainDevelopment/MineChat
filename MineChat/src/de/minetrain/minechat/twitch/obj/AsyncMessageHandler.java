package de.minetrain.minechat.twitch.obj;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.twitch.MessageManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.utils.CallCounter;
import de.minetrain.minechat.utils.ChatMessage;

/**
 * The AsyncMessageHandler class handles asynchronous message processing.
 * <br>It provides functionality to add messages to a queue, send them at regular intervals,
 * and update the message count and user interface accordingly.
 * 
 * @author MineTrain/Justin
 * @since 15.05.2023
 * @version 1.0
 */
public class AsyncMessageHandler {
	private static final Logger logger = LoggerFactory.getLogger(AsyncMessageHandler.class); //system logger
	private BlockingQueue<ChatMessage> messageQueue; //The message queue to store the incoming messages.
    private ScheduledExecutorService executorService; //The executor service responsible for scheduling and executing message sending tasks.
    private final long defaultDelayMilliseconds = 1500; //The default time between messages.
    private final long messageDelayMilliseconds; //The time between messages.
    private static final CallCounter callCounter = new CallCounter(30);
    private Instant lastSendMessage; //Timestamp when the last message was send.
    private int messageCount; //The count of messages in the message queue.

    /**
     * Constructs a new AsyncMessageHandler with an empty message queue,
     * a single-threaded executor service, and initializes the message count to zero.
     */
    public AsyncMessageHandler(long delayMilliseconds) {
        messageQueue = new LinkedBlockingQueue<>();
        executorService = Executors.newSingleThreadScheduledExecutor();
        this.messageDelayMilliseconds = (delayMilliseconds <= 0) ? this.defaultDelayMilliseconds : delayMilliseconds;
        messageCount = 0;
    }

    /**
     * Starts the message sending process by scheduling a task to send messages at a fixed rate.
     */
    public void start() {
        executorService.scheduleAtFixedRate(this::sendMessage, 0, 1, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the message sending process by shutting down the executor service.
     */
    public void stop() {
        executorService.shutdown();
    }

    /**
     * Adds a new message to the message queue and updates the message count.
     * @param message The message to be added to the queue.
     */
    public void addMessage(ChatMessage message) {
        messageQueue.offer(message);
        messageCount++;
        MessageManager.updateQueueButton();
    }

    /**
     * @return The number of messages in the queue.
     */
    public int getMessageCount() {
        return messageCount;
    }
    
    /**
     * Clears the message queue and resets the message count to zero.
     * <br>Also updates the user interface queue button accordingly.
     */
    public void clearQueue(){
    	messageQueue.clear();
    	messageCount = 0;
        MessageManager.updateQueueButton();
    }
    
    /**
     * Sends a message from the message queue if it is not empty.
     * 
     * <br>Decreases the message count and updates the user interface queue button accordingly. 
     * And Sends the message through the {@link TwitchManager} class.
     */
    private void sendMessage() {
        if (!messageQueue.isEmpty()) {
            ChatMessage chatMessage = messageQueue.poll();
    		
            messageCount--;
            MessageManager.updateQueueButton();
            logger.debug("Sending message: {" + chatMessage.getMessage()+"}");
            
        	TwitchManager.sendMessage(chatMessage);
        	lastSendMessage = Instant.now();
        	
			try {
				long i = 1000;
				while(i > 0){
					i = getSleepTime(chatMessage.getChannel().getChannelId());
					Thread.sleep(i);
				}
				callCounter.recordCallTime();
			} catch (InterruptedException e) {}
        }
    }
    
    /**
     * Calculates the sleep time based on the {@link AsyncMessageHandler#getCurrentDelay()}.
     * Sleep time is capped at 1000 milliseconds.
     *
     * @return The sleep time in milliseconds.
     */
    private long getSleepTime(String channel_id){
    	long currentDelay = getCurrentDelay(channel_id);
		return currentDelay > 1000 ? 1000 : currentDelay;
    }
    
    

	/**
	 * Gets the current delay for message sending, considering slow mode.
	 * @return The current delay in milliseconds.
	 */
    private long getCurrentDelay(String channel_id){
    	long delay = (callCounter.getCallCount() > 19) ? defaultDelayMilliseconds : messageDelayMilliseconds;
    	Long slowMode = MessageManager.channelSlowMods.get(channel_id);
    	if(slowMode != null && slowMode > delay){
    		delay = slowMode;
    	}
    	
    	Duration duration = Duration.between(lastSendMessage.plusMillis(delay), Instant.now());
    	return Math.abs(duration.getSeconds() > 0 ? 0 : duration.getSeconds() * 1000);
    }
    
}
