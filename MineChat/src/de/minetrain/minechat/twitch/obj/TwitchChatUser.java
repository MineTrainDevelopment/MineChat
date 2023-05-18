package de.minetrain.minechat.twitch.obj;

import java.awt.Color;

public class TwitchChatUser {
	private final String userId;
    private final String userLogin;
    private final String userName;
    private final Color color;
    private final String colorCode;
    private final long totalMessages;

    public TwitchChatUser(String userId, String userLogin, String userName, String color, Long totalMessages) {
    	System.out.println(color);
        this.userId = userId;
        this.userLogin = userLogin;
        this.userName = userName;
        this.color = (color.isEmpty()) ? Color.WHITE : Color.decode(color);
        this.colorCode = color;
        this.totalMessages = totalMessages;
    }

    // Getter-Methoden
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
}
