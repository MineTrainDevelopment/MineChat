package de.minetrain.minechat.gui.emotes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;

public class ChannelEmotes {
	private static final Logger logger = LoggerFactory.getLogger(ChannelEmotes.class);
	private String subLevel;
	private final String channelId;
	private final HashMap<String, String> nameToId = new HashMap<String, String>();
	private final List<String> tier1;
	private final List<String> tier2;
	private final List<String> tier3;
	private final List<String> follower;
	private final List<String> bits;
	private final List<String> bttv;
	
	public ChannelEmotes(ResultSet resultSet) throws SQLException {
		this.subLevel = resultSet.getString("user_sub");
		this.channelId = resultSet.getString("channel_id");
		this.tier1 = Arrays.asList(resultSet.getString("tier1").split("\n"));
		this.tier2 = Arrays.asList(resultSet.getString("tier2").split("\n"));
		this.tier3 = Arrays.asList(resultSet.getString("tier3").split("\n"));
		this.follower = Arrays.asList(resultSet.getString("follow").split("\n"));
		this.bits = Arrays.asList(resultSet.getString("bits").split("\n"));
		
		if(resultSet.getString("bttv") != null){
			this.bttv = Arrays.asList(resultSet.getString("bttv").split("\n"));
		}else{
			this.bttv = new ArrayList<String>();
		}
		
//		nameToId.putAll(getAllEmotes().stream().collect(Collectors.toMap(Emote::getName, Emote::getEmoteId)));
		
		nameToId.putAll(getAllEmotes().stream().collect(Collectors.toMap(Emote::getName, Emote::getEmoteId,
			            (existingValue, newValue) -> {
			            	logger.warn("Duplicate key found for emote ID \"" + existingValue + "\". Skipping.");
			                return existingValue;
			            }
			        ))
			);

	}
	
	public boolean isSub(){
		return !subLevel.isEmpty();
	}
	
	public void toggleSub(){
		setSubState(!isSub());
	}
	
	public void setSubState(boolean state){
		subLevel = state ? "tier3" : "";
		DatabaseManager.getEmote().updateSubscriptionState(channelId, state);
	}
	
//	public void setState(String state){
//		DatabaseManager.getEmote().updateSubscriptionState(channelId, state);
//		DatabaseManager.commit();
//	}
	
	
	public String getSubLevel(){
		return subLevel;
	}
	
	public List<Emote> getTier1Emotes(){
		return EmoteManager.getAllEmotes().values().stream()
				.filter(emote -> tier1.contains(emote.getEmoteId()))
				.sorted(Comparator.comparing(Emote::getName))
				.collect(Collectors.toList());
	}
	
	public List<Emote> getTier2Emotes(){
		return EmoteManager.getAllEmotes().values().stream()
				.filter(emote -> tier2.contains(emote.getEmoteId()))
				.sorted(Comparator.comparing(Emote::getName))
				.collect(Collectors.toList());
	}
	
	public List<Emote> getTier3Emotes(){
		return EmoteManager.getAllEmotes().values().stream()
				.filter(emote -> tier3.contains(emote.getEmoteId()))
				.sorted(Comparator.comparing(Emote::getName))
				.collect(Collectors.toList());
	}
	
	public List<Emote> getFollowerEmotes(){
		return EmoteManager.getAllEmotes().values().stream()
				.filter(emote -> follower.contains(emote.getEmoteId()))
				.sorted(Comparator.comparing(Emote::getName))
				.collect(Collectors.toList());
	}
	
	public List<Emote> getBitEmotes(){
		return EmoteManager.getAllEmotes().values().stream()
				.filter(emote -> bits.contains(emote.getEmoteId()))
				.sorted(Comparator.comparing(Emote::getName))
				.collect(Collectors.toList());
	}
	
	public List<Emote> getBttvEmotes(){
		return EmoteManager.getAllEmotes().values().stream()
				.filter(emote -> bttv.contains(emote.getEmoteId()))
				.sorted(Comparator.comparing(Emote::getName))
				.collect(Collectors.toList());
	}
	
	public List<Emote> getAllEmotes(){
		List<Emote> emotes = new ArrayList<Emote>();
		emotes.addAll(getTier1Emotes());
		emotes.addAll(getTier2Emotes());
		emotes.addAll(getTier3Emotes());
		emotes.addAll(getBitEmotes());
		emotes.addAll(getFollowerEmotes());
		emotes.addAll(getBttvEmotes());
		return emotes;
	}
	
	public HashMap<String, String> getEmotesByName(){
		return nameToId;
	}
	
	

}
