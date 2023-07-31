package de.minetrain.minechat.gui.emotes;

import java.util.TreeMap;

import de.minetrain.minechat.config.YamlManager;

public class ChannelEmotes extends TreeMap<String, Emote>{
	private static final long serialVersionUID = 4516934760139844971L;
	private final String channelId;
	private final String channelName;
	private final boolean sub;
	
	public ChannelEmotes(String key, YamlManager yaml) {
		this.channelId = yaml.getString(key+".Id");
		this.channelName = yaml.getString(key+".Name");
		this.sub = yaml.getBoolean(key+".sub");
	}
	
	public void addEmote(Emote emote){
		put(emote.getName(), emote);
	}
	
	public Emote getEmote(String emoteName){
		return get(emoteName);
	}
	
	/**
	 * Checks if the user can use it or not.
	 * @return
	 */
	public boolean hasPermission(Emote emote){
		return emote.isSubOnly() ? sub : true;
	}

	public String getChannelId() {
		return channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public boolean isSubscriber() {
		return sub;
	}

	
	
}
