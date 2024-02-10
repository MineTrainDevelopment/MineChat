package de.minetrain.minechat.data.eclipsestore;

import java.util.List;
import java.util.Map;

import org.eclipse.serializer.collections.lazy.LazyArrayList;
import org.eclipse.serializer.collections.lazy.LazyHashMap;
import org.eclipse.serializer.persistence.types.Persister;

import de.minetrain.minechat.gui.obj.messages.MessageComponentContent;
import de.minetrain.minechat.main.Channel;
import de.minetrain.minechat.twitch.obj.ChannelStatistics;

public class EclipseStoreRoot {
	private static final int maxMessagesSize = 10_000;
	private Map<String, List<MessageComponentContent>> twitchMessages;// Channel_id, data
	private Map<String, ChannelStatistics> channelStatics; //Channel_id, data
	public transient Persister persister;
	
	public void addMessage(String channelId, MessageComponentContent message) {
		Map<String, List<MessageComponentContent>> twitchMessages = getTwitchMessages();
		if(!twitchMessages.containsKey(channelId)){
			twitchMessages.put(channelId, new LazyArrayList<MessageComponentContent>());
			persister.store(twitchMessages);
		}
		
//		TODO: Zocki will testen... Mach wieder an :P
		List<MessageComponentContent> messages = twitchMessages.get(channelId);
//		if(messages.size() > maxMessagesSize){
//			messages.subList(0, 1000).clear();
//		}
		
		messages.add(message);
		persister.store(messages);
	}
	
	public List<MessageComponentContent> getMessages(Channel channel){
		return getTwitchMessages().getOrDefault(channel.getChannelId(), List.of());
	}
	
	
	private void addChannelStatistics(String channelId, ChannelStatistics statistics){
		getChannelStatics().put(channelId, statistics);
		persister.store(getChannelStatics());
	}
	
	public ChannelStatistics getChannelStatistics(String channelId){
		if(!getChannelStatics().containsKey(channelId)){
			addChannelStatistics(channelId, new ChannelStatistics(channelId));
		}
		
		return getChannelStatics().get(channelId);
	}
	
	public void saveAllChannelStatistics(){
		getChannelStatics().values().forEach(stats -> stats.saveChannelStatistics(persister));
	}
	
	
	
	
	
	//Layze loading.
	private Map<String, List<MessageComponentContent>> getTwitchMessages() {
		if(twitchMessages == null){
			twitchMessages = new LazyHashMap<String, List<MessageComponentContent>>();
			persister.store(this);
		}
		return twitchMessages;
	}

	private Map<String, ChannelStatistics> getChannelStatics() {
		if(channelStatics == null){
			channelStatics = new LazyHashMap<String, ChannelStatistics>();
			persister.store(this);
		}
		return channelStatics;
	}
	
	
	
	
	
	
	
}
