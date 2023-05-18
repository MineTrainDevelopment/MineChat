package de.minetrain.minechat.config.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;

import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Main;

public class TwitchEmote {
	private static final String folderPath = TextureManager.texturePath+"Icons/";
	private static HashMap<String, TwitchEmote> emotesByName = new HashMap<String, TwitchEmote>();
	private ImageIcon imageIcon;
	
	public TwitchEmote(ImageIcon imageIcon) {
		this.imageIcon = imageIcon;
	}

	public TwitchEmote(String imagePath) {
		if(imagePath.equalsIgnoreCase("null")){
			return;
		}
		
		if(imagePath.split("/")[3].equalsIgnoreCase("bttv")){
			imagePath = imagePath.replace(".png", ".gif");
		}
		
		imageIcon = new ImageIcon(imagePath);
		
		if(imageIcon == null && Main.MAIN_FRAME != null){
			Main.MAIN_FRAME.displayInfo("Can´t find the '"+imagePath+"' emote");
		}
	}
	
	public static HashMap<String, List<String>> getEmotes(boolean withBorder){
		emotesByName.clear();
		System.out.println("Emote list -> "+emotesByName);
	    HashMap<String, List<String>> emotes = new HashMap<String, List<String>>();
		Main.EMOTE_INDEX.getStringList("index").forEach(channel -> {
			List<String> tempList = new ArrayList<String>();
			Main.EMOTE_INDEX.getStringList(channel).forEach(emote -> {
				String newEmote = folderPath + channel.replace("Channel_", "") +"/"+ emote+"/"+emote+"_1"+((withBorder) ? "_BG" : "")+".png";
				tempList.add(newEmote);
				emotesByName.put(emote, new TwitchEmote(newEmote.replace("_BG.png", ".png")));
			});
			
			emotes.put(channel, tempList);
		});
		System.out.println("Emote list -> "+emotesByName);
		return emotes;
	}

	public static HashMap<String, TwitchEmote> getEmotesByName() {
		if(emotesByName.isEmpty()){
			getEmotes(false);
		}
		return emotesByName;
	}
	


	public ImageIcon getImageIcon() {
		return imageIcon;
	}

}
