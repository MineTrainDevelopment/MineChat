package de.minetrain.minechat.twitch.obj;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.twitch4j.chat.events.AbstractChannelMessageEvent;

import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.utils.TextureManager;

public class TwitchChatUser {
	private final String userId;
    private final String userLogin;
    private final String userName;
    private final Color color;
    private final String colorCode;
    private long totalMessages;

    public TwitchChatUser(String userId, String userLogin, String userName, String color, Long totalMessages) {
        this.userId = userId;
        this.userLogin = userLogin;
        this.userName = userName;
        this.color = (color.isEmpty()) ? Color.WHITE : Color.decode(color);
        this.colorCode = color;
        this.setTotalMessages(totalMessages);
    }
    
    public void setBadges(AbstractChannelMessageEvent event, ChannelTab tab){
    	if(tab.getChatWindow().badges.containsKey(event.getUser().getName().toLowerCase())){return;}
    	List<String> list = new ArrayList<String>();
    	String[] badgeTags = event.getMessageEvent().getTagValue("badges").toString().replace("Optional[", "").replace("]", "").split(",");
		Arrays.asList(badgeTags).forEach(badge -> {
			String path = TextureManager.badgePath+badge+"/1.png";
			
			if(badge.startsWith("subscriber")){
				String channelSubBadgePath = TextureManager.badgePath+badge.substring(0, badge.indexOf("/"))+"/"+event.getChannel().getId();
				if (Files.exists(Paths.get(channelSubBadgePath))) {
					path = channelSubBadgePath+badge.substring(badge.indexOf("/"))+"/1.png";
				}
			}
			
			if (Files.exists(Paths.get(path))) {
				list.add(path);
			}
		});
		
		tab.getChatWindow().badges.put(event.getUser().getName().toLowerCase(), list);
    }

    public String getUserId() {
        return userId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getUserName() {
        return userName;
    }

    public Color getColor() {
        return color;
    }

	public String getColorCode() {
		return colorCode;
	}

	public long getTotalMessages() {
		return totalMessages;
	}
	
	public void increaseMessageCound() {
		this.totalMessages++;
	}

	public void setTotalMessages(long totalMessages) {
		this.totalMessages = totalMessages;
	}
}
