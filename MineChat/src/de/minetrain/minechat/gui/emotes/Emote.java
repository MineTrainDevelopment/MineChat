package de.minetrain.minechat.gui.emotes;

import javax.swing.ImageIcon;

import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.gui.utils.TextureManager;

public class Emote {
	private boolean favorite;
	private final String name;
	private final String id;
	private final String tier;
	private final EmoteType emoteType;
	private final String filePath;
	private final String channelId;
	
	private final String key;
	
	public Emote(String key, YamlManager yaml, String channelId) {
		this.key = key;
		this.name = yaml.getString(key+".Name");
		this.favorite = yaml.getBoolean(key+".Favorite");
		this.id = yaml.getString(key+".Id");
		this.tier = yaml.getString(key+".Tier", "0");
		this.emoteType = EmoteType.get(yaml.getString(key+".EmoteType"));
		this.filePath = yaml.getString(key+".File");
		this.channelId = channelId;
	}
	
	public ImageIcon getImageIcon(){
		return new ImageIcon(filePath);
	}
	
	public void toggleFavorite() {
		setFavorite(!favorite);
	}
	
	public void setFavorite(boolean state) {
		favorite = state;
		EmoteManager.getYaml().setBoolean(key+".Favorite", state, true);
		
		if(state){
			EmoteManager.addFavoriteEmote(this);
			return;
		}

		EmoteManager.removeFavoriteEmote(this);
	}
	
	public boolean isFavorite() {
		return favorite;
	}
	
	public boolean isSubOnly() {
		return emoteType.isSubOnly();
	}
	
	public boolean isGlobal() {
		return emoteType.isGlobal();
	}

	public String getName() {
		return name;
	}
	
	public String getChannelId() {
		return channelId;
	}

	public String getId() {
		return id;
	}
	
	public String getTier() {
		return tier;
	}

	public EmoteType getEmoteType() {
		return emoteType;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getFileFormat() {
		try {
			return filePath.substring(filePath.lastIndexOf("."));
		} catch (Exception e) {
			return ".png";
		}
	}
	
	public ImageIcon getBorderImage(){
		switch (emoteType) {
			case SUB_2: return new ImageIcon(TextureManager.texturePath+"emoteBorder2.png");
			case SUB_3: return new ImageIcon(TextureManager.texturePath+"emoteBorder3.png");
			case BIT: return new ImageIcon(TextureManager.texturePath+"emoteBorderBits.png");
			case FOLLOW: return new ImageIcon(TextureManager.texturePath+"emoteBorderFollow.png");
			default: return new ImageIcon(TextureManager.texturePath+"emoteBorder.png");
		}
	}

	public enum EmoteType{
		SUB(true, true), SUB_2(true, true), SUB_3(true, true), BIT(true, true), FOLLOW(false, false), BTTV(false, false), DEFAULT(false, true);

		private boolean subOnly;
		public boolean isSubOnly(){return subOnly;}
		
		public boolean isBitOnly(){return this.equals(BIT);}
		
		private boolean global;
		public boolean isGlobal(){return global;}
		
		private EmoteType(boolean subOnly, boolean global){
			this.subOnly = subOnly;
			this.global = global;
		}
		
		public static EmoteType get(String input){
			switch (input) {
				case "subscriptions": return SUB;
				case "subscriptions:1000": return SUB;
				case "subscriptions:2000": return SUB_2;
				case "subscriptions:3000": return SUB_3;
				case "follower": return FOLLOW;
				case "bitstier": return BIT;
				case "bttv": return BTTV;
				default: return DEFAULT;
			}
		}
	}
	
	
}
