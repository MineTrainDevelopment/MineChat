package de.minetrain.minechat.config.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;

import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Main;

public class TwitchEmote {
	private static final String folderPath = TextureManager.texturePath+"Icons/";
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
		
		if(imageIcon == null && Main.mainFrame != null){
			Main.mainFrame.displayInfo("Can´t find the '"+imagePath+"' emote");
		}
	}
	
	public static HashMap<String, List<String>> getEmotes(){
	    HashMap<String, List<String>> emotes = new HashMap<String, List<String>>();
		Main.EMOTE_INDEX.getStringList("index").forEach(channel -> {
			List<String> tempList = new ArrayList<String>();
			Main.EMOTE_INDEX.getStringList(channel).forEach(emote -> {
				tempList.add(folderPath + channel.replace("Channel_", "") +"/"+ emote+"/"+emote+"_1_BG.png");
			});
			
			emotes.put(channel, tempList);
		});
		return emotes;
	}

	public ImageIcon getImageIcon() {
		return imageIcon;
	}

}
