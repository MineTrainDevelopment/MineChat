package de.minetrain.minechat.data.eclipsestore;

import java.util.List;
import java.util.Map;

import org.eclipse.serializer.collections.lazy.LazyArrayList;
import org.eclipse.serializer.collections.lazy.LazyHashMap;
import org.eclipse.serializer.persistence.types.Persister;

import de.minetrain.minechat.main.Channel;
import de.minetrain.minechat.twitch.obj.ChannelStatistics;
import de.minetrain.minechat.utils.message.Message;

public class EclipseStoreRoot {
	private static final int maxMessagesSize = 10_000;
	private Map<String, List<Message>> message;// Channel_id, data
	private Map<String, ChannelStatistics> channelStatics; //Channel_id, data
	public transient Persister persister;
	
	public void addMessage(String channelId, Message message) {
		Map<String, List<Message>> twitchMessages = getTwitchMessages();
		if(!twitchMessages.containsKey(channelId)){
			twitchMessages.put(channelId, new LazyArrayList<Message>());
			persister.store(twitchMessages);
		}
		
//		TODO: Zocki will testen... Mach wieder an :P
		List<Message> messages = twitchMessages.get(channelId);
//		if(messages.size() > maxMessagesSize){
//			messages.subList(0, 1000).clear();
//		}
		
		messages.add(message);
		persister.store(messages);
	}
	
	public List<Message> getMessages(Channel channel){
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
	private Map<String, List<Message>> getTwitchMessages() {
		if(message == null){
			message = new LazyHashMap<String, List<Message>>();
			persister.store(this);
		}
		return message;
	}

	private Map<String, ChannelStatistics> getChannelStatics() {
		if(channelStatics == null){
			channelStatics = new LazyHashMap<String, ChannelStatistics>();
			persister.store(this);
		}
		return channelStatics;
	}
	
	
	
	
	
	
	
}
