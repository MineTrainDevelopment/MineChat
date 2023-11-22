package de.minetrain.minechat.gui.emotes;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.gui.utils.TextureManager;

public class Emote {
	private static final ImageIcon defaultBorderImage = new ImageIcon(TextureManager.texturePath+"emoteBorder.png");
	private static final ImageIcon borderSub2 = new ImageIcon(TextureManager.texturePath+"emoteBorder2.png");
	private static final ImageIcon borderSub3 = new ImageIcon(TextureManager.texturePath+"emoteBorder3.png");
	private static final ImageIcon borderBits = new ImageIcon(TextureManager.texturePath+"emoteBorderBits.png");
	private static final ImageIcon borderFollow = new ImageIcon(TextureManager.texturePath+"emoteBorderFollow.png");
	
	private boolean favorite;
	private final String name;
//	private final String id;
//	private final String tier;
	private final String emoteId;
	private final EmoteType emoteType;
	private final String filePath;
	
	public Emote(ResultSet resultSet) throws SQLException {
		this.name = resultSet.getString("name");
		this.emoteId = resultSet.getString("emote_id");
		this.favorite = resultSet.getBoolean("favorite");
		this.emoteType = EmoteType.get(resultSet.getString("emote_type"), resultSet.getString("tier"));
		this.filePath = resultSet.getString("file_location");
	}
	
	public Emote(boolean dummyData){
		this.name = ">null<";
		this.emoteId = ">null<";
		this.favorite = false;
		this.emoteType = EmoteType.DEFAULT;
		this.filePath = ">null<";
	}
	
	public ImageIcon getImageIcon(){
		if(filePath.equalsIgnoreCase(">null<")){
			return null;
		}
		return new ImageIcon(filePath);
	}
	
	public void toggleFavorite() {
		setFavorite(!favorite);
	}
	
	public void setFavorite(boolean state) {
		favorite = state;
		DatabaseManager.getEmote().updateFavoriteState(emoteId, state);
		DatabaseManager.commit();
//		
//		if(state){
//			EmoteManager.addFavoriteEmote(this);
//			return;
//		}
//
//		EmoteManager.removeFavoriteEmote(this);
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

	public EmoteType getEmoteType() {
		return emoteType;
	}

	public String getName() {
		return name;
	}

	public String getFilePath() {
		return filePath;
	}
	

	public String getEmoteId() {
		return emoteId;
	}

	public String getFileFormat() {
		try {
			return filePath.substring(filePath.lastIndexOf("."));
		} catch (Exception e) {
			return ".png";
		}
	}
	
	
	public ImageIcon getBorderImage(){
		switch (getEmoteType()) {
			case SUB_2: return borderSub2;
			case SUB_3: return borderSub3;
			case BIT: return borderBits;
			case FOLLOW: return borderFollow;
			default: return defaultBorderImage;
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
		
		public static EmoteType get(String input, String tier){
			switch (input) {
				case "subscriptions":
					switch (tier) {
					case "1000": return SUB;
					case "2000": return SUB_2;
					case "3000": return SUB_3;
					default: return SUB;}
					
				case "follower": return FOLLOW;
				case "bitstier": return BIT;
				case "bttv": return BTTV;
				default: return DEFAULT;
			}
		}
	}
	
	
	@Override
	public String toString() {
		return getName()+" - "+getEmoteId();
	}

	
	
}
