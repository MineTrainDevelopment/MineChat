package de.minetrain.minechat.twitch.obj;

import java.time.Duration;
import java.time.Instant;

import com.google.gson.JsonObject;

import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.TwitchManager.LiveMetaData;

public class TwitchUserObj {
	private static final String placeHolder = ">null<";
	private final boolean dummy;
	private final String userId;
	private final String loginName;
	private final String displayName;
	private final TwitchUserType userType;
	private final TwitchBroadcasterType broadcasterType;
	private final String channelDescription;
	private final String profileImageUrl;
	private final String offlineImageUrl;
	private final String userAge;

	private String streamTitle = ">null<";
	private String streamGame = ">null<";
	private Instant streamStartTimeStamp = Instant.ofEpochSecond(0);
	private int streamViewer = 0;
	private String[] streamTags = new String[]{""};
	
	
	public TwitchUserObj(JsonObject data) {
		String offlineImageUrl = data.get("offline_image_url").getAsString().replace("\"", "");
		dummy = false;
		
		this.userId = data.get("id").getAsString().replace("\"", "");
		this.loginName = data.get("login").getAsString().replace("\"", "");
		this.displayName = data.get("display_name").getAsString().replace("\"", "");
		this.userType = TwitchUserType.fromString(data.get("type")+"");
		this.broadcasterType = TwitchBroadcasterType.fromString(data.get("broadcaster_type")+"");
		this.channelDescription = data.get("description").getAsString().replace("\"", "");
		this.profileImageUrl = data.get("profile_image_url").getAsString().replace("\"", "");
		this.offlineImageUrl = (offlineImageUrl.length()>0) ? offlineImageUrl : profileImageUrl;
		this.userAge = data.get("created_at").getAsString().replace("\"", "");
	}
	
	public TwitchUserObj(TwitchApiCallType type, String dummyNameId, boolean dummy) {
		this.dummy = true;
		
		this.userId = (type!=TwitchApiCallType.ID) ? placeHolder:dummyNameId;
		this.loginName = (type!=TwitchApiCallType.LOGIN) ? placeHolder:dummyNameId;
		this.displayName = placeHolder;
		this.userType = TwitchUserType.DEFAULD;
		this.broadcasterType = TwitchBroadcasterType.DEFAULD;
		this.channelDescription = placeHolder;
		this.profileImageUrl = placeHolder;
		this.offlineImageUrl = placeHolder;
		this.userAge = placeHolder;
	}
	
	public static enum TwitchUserType{
		ADMIN, // Twitch administrator
		GLOBAL_MOD, 
		STAFF, // Twitch staff
		DEFAULD; // Normal user
		
		public static TwitchUserType fromString(String input){
			input = input.replace("\"", "");
			return (input.length() == 0) ? TwitchUserType.DEFAULD : TwitchUserType.valueOf(input.toUpperCase());
		}
	}
	
	public static enum TwitchBroadcasterType{
		PARTNER, // An partner broadcaster
		AFFILIATE, // A affiliate broadcaster
		DEFAULD; // A normal broadcaster
		
		public static TwitchBroadcasterType fromString(String input){
			input = input.replace("\"", "");
			return (input.length() == 0) ? TwitchBroadcasterType.DEFAULD : TwitchBroadcasterType.valueOf(input.toUpperCase());
		}
	}
	
	public static enum TwitchApiCallType{
		LOGIN("login"),
		ID("id");

		private String url;
		public String getUrl(){
			return "user_"+url;
		}
		public String getShortUrl(){
			return url;
		}
		
		private TwitchApiCallType(String url) {
			this.url = url;
		}
	}
	
	public void join(){
		if(!dummy){
			TwitchManager.joinChannel(loginName);
		}
	}
	
	public void leave(){
		if(!dummy){
			TwitchManager.leaveChannel(loginName);
		}
	}

	public String getUserId() {
		return userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public String getIdentifier(TwitchApiCallType callType) {
		return (callType == TwitchApiCallType.LOGIN) ? getLoginName() : getUserId();
	}

	public String getDisplayName() {
		return displayName;
	}

	public TwitchUserType getUserType() {
		return userType;
	}

	public TwitchBroadcasterType getBroadcasterType() {
		return broadcasterType;
	}

	public String getChannelDescription() {
		return channelDescription;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public String getOfflineImageUrl() {
		return offlineImageUrl;
	}

	public String getUserAge() {
		return userAge;
	}

	public String getChannelLink() {
		return "https://www.twitch.tv/"+loginName;
	}

	public boolean isDummy() {
		return dummy;
	}
	
	/**
	 * @param streamTitle
	 * @param streamGame
	 * @param streamStartTimeStamp
	 * @param streamViwer
	 * @param streamTags
	 * @return true
	 */
	public boolean setLiveData(LiveMetaData metaData){
		this.streamTitle = metaData.title();
		this.streamGame = metaData.game();
		this.streamStartTimeStamp = metaData.startTime();
		this.streamViewer = metaData.viewer();
		this.streamTags = metaData.tags();
		return true;
	}
	
	
	public String getStreamTitle() {
		return streamTitle;
	}

	public String getStreamGame() {
		return streamGame;
	}

	public Instant getStreamStartTimeStamp() {
		return streamStartTimeStamp;
	}

	public String getStreamLiveSince() {
		Duration duration = Duration.between(getStreamStartTimeStamp(), Instant.now());
		
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        
        if(days == 0){
        	return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        return String.format("%d days and %02d:%02d:%02d", days, hours, minutes, seconds);
	}

	public int getStreamViewer() {
		return streamViewer;
	}

	public String[] getStreamTags() {
		return streamTags;
	}

	@Override
	public String toString() {
		return loginName;
	}
}
