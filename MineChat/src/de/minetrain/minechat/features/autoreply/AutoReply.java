package de.minetrain.minechat.features.autoreply;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.twitch.MessageManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import de.minetrain.minechat.utils.CallCounter;
import de.minetrain.minechat.utils.ChatMessage;

public class AutoReply {
	private final String uuid;
	private final String channelId;
	private final String channelName;
	private boolean aktiv;
	private final String trigger;
	private final String[] outputs;
	
	private final CallCounter messageCounter;
	private final Long messagesPerMin;
	private final Long fireDelay;
	private final boolean chatReply;
	
	private static final Random random = new Random();
	private int previousRandom = 0;
	
	private Instant firedTimeStamp = Instant.now().minusSeconds(Integer.MAX_VALUE);

	public AutoReply(ResultSet resultSet) throws SQLException {
		this.uuid = resultSet.getString("uuid");
		this.channelId = resultSet.getString("channel_Id");
		this.channelName = TwitchManager.getTwitchUser(TwitchApiCallType.ID, channelId).getLoginName();
		this.aktiv = resultSet.getBoolean("state");
		this.trigger = resultSet.getString("trigger");
		this.outputs = resultSet.getString("output").split("\n");
		this.messagesPerMin = resultSet.getLong("messages_per_min");
		this.messageCounter = new CallCounter(60);
		this.fireDelay = resultSet.getLong("fire_delay");
		this.chatReply = resultSet.getBoolean("chat_reply");
	}
	
	public AutoReply(String uuid, String channelId, boolean state, boolean chatReply, Long messagesPerMin, Long fireDelay, String trigger, String[] output){
		this.uuid = uuid;
		this.channelId = channelId;
		this.channelName = TwitchManager.getTwitchUser(TwitchApiCallType.ID, channelId).getLoginName();
		this.aktiv = state;
		this.trigger = trigger;
		this.outputs = output;
		this.messagesPerMin = messagesPerMin;
		this.messageCounter = new CallCounter(60);
		this.fireDelay = fireDelay;
		this.chatReply = chatReply;
	}
	
	public void fire(TwitchMessage message){
		Duration difference = Duration.between(firedTimeStamp, Instant.now());
		if(difference.getSeconds() < fireDelay || !isAktiv()){
			return; //Alrady on delay.
		}
		
		messageCounter.recordCallTime();
		if(messageCounter.getCallCount() < messagesPerMin){
			return; //Not enough messages.
		}
		
		firedTimeStamp = Instant.now();
		messageCounter.clear();
		
		ChatMessage chatMessage = new ChatMessage(ChannelTab.getById(channelId), TwitchManager.ownerChannelName, getOutput());
		chatMessage.overrideReplyMessage(isChatReply() ? message : null);
		MessageManager.getDefaultMessageHandler().addMessage(chatMessage);
	}

	public String getUuid() {
		return uuid;
	}

	public String getChannelId() {
		return channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public boolean isAktiv() {
		return aktiv;
	}

	public void setAktiv(boolean state) {
		this.aktiv = state;
		DatabaseManager.getAutoReply().setState(uuid, state);
	}

	public String getTrigger() {
		return trigger;
	}

	public String[] getOutputs() {
		return outputs;
	}
	
	public String getOutput() {
		int newRandom = 0;
		while(this.outputs.length > 1 && (newRandom == previousRandom)){
			newRandom = random.nextInt(this.outputs.length);
		}
		
		previousRandom = newRandom;
		return outputs[newRandom];
	}

	public String getTrulyRandomOutput() {
		return outputs[random.nextInt(outputs.length)];
	}

	public CallCounter getMessageCounter() {
		return messageCounter;
	}

	public Long getMessagesPerMin() {
		return messagesPerMin;
	}

	public Long getFireDelay() {
		return fireDelay;
	}

	public boolean isChatReply() {
		return chatReply;
	}

	
	
	
	

}
