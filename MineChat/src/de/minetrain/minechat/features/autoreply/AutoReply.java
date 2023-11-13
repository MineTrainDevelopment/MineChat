package de.minetrain.minechat.features.autoreply;

import java.time.Duration;
import java.time.Instant;

import de.minetrain.minechat.config.YamlManager;
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
	
	private Instant firedTimeStamp = Instant.now().minusSeconds(Integer.MAX_VALUE);
	
	public AutoReply(YamlManager file, String uuid) {
		String filePath = "data."+uuid;
		this.uuid = uuid;
		this.channelId = file.getString(filePath+".channelId");
		this.channelName = TwitchManager.getTwitchUser(TwitchApiCallType.ID, channelId).getLoginName();
		this.aktiv = file.getBoolean(filePath+".aktiv");
		this.trigger = file.getString(filePath+".trigger");
		this.outputs = file.getStringList(filePath+".output").toArray(new String[0]);
		this.messagesPerMin = file.getLong(filePath+".messagesPerMin");
		this.messageCounter = new CallCounter(60);
		this.fireDelay = file.getLong(filePath+".fireDelay");
		this.chatReply = file.getBoolean(filePath+".chatReply");
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
		
		ChatMessage chatMessage = new ChatMessage(ChannelTab.getById(channelId), TwitchManager.ownerChannelName, outputs[(int) (Math.random() * outputs.length)]);
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
		AutoReplyManager.getAutoReplyData().setBoolean("data."+uuid+".aktiv", state, true);
	}

	public String getTrigger() {
		return trigger;
	}

	public String[] getOutputs() {
		return outputs;
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
