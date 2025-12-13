package de.minetrain.minechat.gui.emotes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.main.ChannelActions;

@Deprecated
public class ChannelEmotes {
	private static final Logger logger = LoggerFactory.getLogger(ChannelEmotes.class);
	private String subLevel = "tier0";
	private String channelId;
	private final HashMap<String, String> nameToId = new HashMap<>();
	private final List<String> tier1;
	private final List<String> tier2;
	private final List<String> tier3;
	private final List<String> follower;
	private final List<String> bits;
	private final List<String> bttv;

	@Deprecated
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
			this.bttv = new ArrayList<>();
		}

		if(subLevel == null || channelId == null){
			subLevel = "tier0";
			channelId = "0";
		}

//		nameToId.putAll(getAllEmotes().stream().collect(Collectors.toMap(Emote::getName, Emote::getEmoteId)));

		nameToId.putAll(getAllEmotes().stream().collect(Collectors.toMap(EmoteLegacy::getName, EmoteLegacy::getEmoteId,
			            (existingValue, newValue) -> {
			            	logger.warn("Duplicate key found for emote ID \"" + existingValue + "\". Skipping.");
			                return existingValue;
			            }
			        ))
			);

	}

	@Deprecated
	public boolean isSub(){
		return subLevel != null ? !(subLevel.isEmpty() || subLevel.equals("tier0")) : false;
	}

	@Deprecated
	public void setSubTier(String tier){
		DatabaseManager.getEmote().updateSubscriptionState(channelId, tier);
		subLevel = tier;
	}

//	public void setState(String state){
//		DatabaseManager.getEmote().updateSubscriptionState(channelId, state);
//		DatabaseManager.commit();
//	}


	@Deprecated
	public String getSubLevel(){
		return subLevel;
	}

	@Deprecated
	public List<EmoteLegacy> getTier1Emotes(){
		return EmoteManager.getAllEmotes().values().stream()
				.filter(emote -> tier1.contains(emote.getEmoteId()))
				.sorted(Comparator.comparing(EmoteLegacy::getName))
				.collect(Collectors.toList());
	}

	@Deprecated
	public List<EmoteLegacy> getTier2Emotes(){
		return EmoteManager.getAllEmotes().values().stream()
				.filter(emote -> tier2.contains(emote.getEmoteId()))
				.sorted(Comparator.comparing(EmoteLegacy::getName))
				.collect(Collectors.toList());
	}

	@Deprecated
	public List<EmoteLegacy> getTier3Emotes(){
		return EmoteManager.getAllEmotes().values().stream()
				.filter(emote -> tier3.contains(emote.getEmoteId()))
				.sorted(Comparator.comparing(EmoteLegacy::getName))
				.collect(Collectors.toList());
	}

	@Deprecated
	public List<EmoteLegacy> getFollowerEmotes(){
		return EmoteManager.getAllEmotes().values().stream()
				.filter(emote -> follower.contains(emote.getEmoteId()))
				.sorted(Comparator.comparing(EmoteLegacy::getName))
				.collect(Collectors.toList());
	}

	@Deprecated
	public List<EmoteLegacy> getBitEmotes(){
		return EmoteManager.getAllEmotes().values().stream()
				.filter(emote -> bits.contains(emote.getEmoteId()))
				.sorted(Comparator.comparing(EmoteLegacy::getName))
				.collect(Collectors.toList());
	}

	@Deprecated
	public List<EmoteLegacy> getBttvEmotes(){
		return EmoteManager.getAllEmotes().values().stream()
				.filter(emote -> bttv.contains(emote.getEmoteId()))
				.sorted(Comparator.comparing(EmoteLegacy::getName))
				.collect(Collectors.toList());
	}

	@Deprecated
	public static final List<EmoteLegacy> sortEmotesByEasterEgg(ChannelActions channel, List<EmoteLegacy> emotes){
		return emotes.stream()
				.sorted(Comparator.comparing((EmoteLegacy emote) -> !emote.getName().equals("GAMBA") && channel.getChannelId().equals("605556313")))
				.collect(Collectors.toList());
	}


	@Deprecated
	public List<EmoteLegacy> getAllEmotes(){
		List<EmoteLegacy> emotes = new ArrayList<>();
		emotes.addAll(getTier1Emotes());
		emotes.addAll(getTier2Emotes());
		emotes.addAll(getTier3Emotes());
		emotes.addAll(getBitEmotes());
		emotes.addAll(getFollowerEmotes());
		emotes.addAll(getBttvEmotes());
		return emotes;
	}

	@Deprecated
	public HashMap<String, String> getEmotesByName(){
		return nameToId;
	}



}
