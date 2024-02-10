package de.minetrain.minechat.gui.emotes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.gui.emotes.EmoteSelectorButton.EmoteBorderType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Emote {
	private static final ConcurrentHashMap<Emote, Image> imageCacheSmall = new ConcurrentHashMap<Emote, Image>();
	private boolean favorite;
	private boolean dummyData = false;
	private final String name;
//	private final String id;
//	private final String tier;
	private final String emoteId;
	private final EmoteType emoteType;
	private final String filePath;
	private final String fileFormat;
	
	public Emote(ResultSet resultSet) throws SQLException {
		this.name = resultSet.getString("name");
		this.emoteId = resultSet.getString("emote_id");
		this.favorite = resultSet.getBoolean("favorite");
		this.emoteType = EmoteType.get(resultSet.getString("emote_type"), resultSet.getString("tier"));
		this.filePath = resultSet.getString("file_location");
		this.fileFormat = resultSet.getString("image_type");
	}
	
	public Emote(boolean dummyData){
		this.dummyData = true;
		this.name = ">null<";
		this.emoteId = ">null<";
		this.favorite = false;
		this.emoteType = EmoteType.NON;
		this.filePath = ">null<";
		this.fileFormat = "png";
	}
	
	public Emote(String name, String emoteId, String fileFormat){
		this.name = name;
		this.emoteId = emoteId;
		this.favorite = false;
		this.emoteType = EmoteType.NON;
		this.filePath = WebEmote.getTwitchEmoteUrl(emoteId, fileFormat.equals("gif"));
		this.fileFormat = fileFormat;
	}


	public Image getEmoteImage(EmoteSize emoteSize) {
		return getEmoteImage(emoteSize, emoteSize.getSize());
	}
	
	public Image getEmoteImage(EmoteSize emoteSize, int prefSize) {
		if(emoteSize.equals(EmoteSize.SMALL)){
			return imageCacheSmall.computeIfAbsent(this, emote -> createEmoteImage(emoteSize, prefSize, emote));
		}
		return createEmoteImage(emoteSize, prefSize, this);
	}

	private Image createEmoteImage(EmoteSize emoteSize, int prefSize, Emote emote) {
		return new Image((emote.getFilePath(true).replace("1"+emote.getFileFormat(), emoteSize.getFileEnding(emote))), prefSize, prefSize, false, false);
	}
	
	public final ImageView getEmoteNode(EmoteSize emoteSize) {
		return getEmoteNode(emoteSize, emoteSize.getSize());
	}
	
	public final ImageView getEmoteNode(EmoteSize emoteSize, int prefSize) {
		if(isDummyData()){
			return null;
		}
		
		return new ImageView(getEmoteImage(emoteSize, prefSize));
	}
	
	/**
	 * @return the new favorite sate.
	 */
	public boolean toggleFavorite() {
		setFavorite(!favorite);
		return favorite;
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

	public String getFilePath(boolean filePrefix) {
		if(filePrefix && !filePath.startsWith("https://")){
			return "file:"+filePath;
		}
		return filePath;
	}
	

	public String getEmoteId() {
		return emoteId;
	}

	/**
	 * Returns the file format with a leading dot.
	 * <br> This is due to the previews implementation.
	 */
	public String getFileFormat() {
		return "."+fileFormat;
	}
	
	public boolean isAnimated(){
		return fileFormat.equals("gif");
	}
	
	public boolean isDummyData() {
		return dummyData;
	}
	
	public EmoteBorderType getBorderType(){
		switch (emoteType) {
		case BIT:
			return EmoteBorderType.BITS;
			
		case FOLLOW:
			return EmoteBorderType.FOLLOW;
			
		case SUB_2:
			return EmoteBorderType.TIER_2;
			
		case SUB_3:
			return EmoteBorderType.TIER_2;

		default:
			return EmoteBorderType.DEFAULT;
		}
	}

	public enum EmoteType{
		SUB(true, true), SUB_2(true, true), SUB_3(true, true), BIT(true, true), FOLLOW(false, false), BTTV(false, false), DEFAULT(false, true), NON(false, false);

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
				case "non": return NON;
				default: return DEFAULT;
			}
		}
	}
	
	public enum EmoteSize {
		SMALL("1", 28),
		MEDIUM("2", 56),
		BIG("3", 112);
		
		private String fileEnding;
		private int size;
		
		public String getFileEnding(Emote emote){
			return fileEnding + emote.getFileFormat();
		}
		
		public int getSize(){
			return size;
		}
		
		private EmoteSize(String fileEnding, int size) {
			this.fileEnding = fileEnding;
			this.size = size;
		}
	};
	
	
	@Override
	public String toString() {
		return getName()+" - "+getEmoteId();
	}
	
}
