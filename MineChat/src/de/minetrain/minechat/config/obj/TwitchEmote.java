package de.minetrain.minechat.config.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Main;

public class TwitchEmote {
	private static final String folderPath = TextureManager.texturePath+"Icons/";
	private static HashMap<String, String> emotesByName = new HashMap<String, String>();
	public static final HashMap<String, String> CACHED_WEB_EMOTES = new HashMap<String, String>();
	
	public static HashMap<String, List<String>> getEmotes(boolean withBorder){
		emotesByName.clear();
	    HashMap<String, List<String>> emotes = new HashMap<String, List<String>>();
		Main.EMOTE_INDEX.getStringList("index").forEach(channel -> {
			List<String> tempList = new ArrayList<String>();
			Main.EMOTE_INDEX.getStringList(channel).forEach(emote -> {
				String format = emote.split("%&%")[1];
				emote = emote.split("%&%")[0];
				String newEmote = folderPath + channel.replace("Channel_", "") +"/"+ emote+"/"+emote+"_1"+((withBorder) ? "_BG.png%&%"+format : format);
				tempList.add(newEmote);
//				emotesByName.put(emote, new TwitchEmote(newEmote.replace("_BG.png%&%", "")).getImageIcon());
				emotesByName.put(emote, newEmote.replace("_BG.png%&%", ""));
			});
			
			emotes.put(channel, tempList);
		});
		return emotes;
	}

	public static HashMap<String, String> getEmotesByName() {
		if(emotesByName.isEmpty()){
			getEmotes(false);
		}
		return emotesByName;
	}

}
