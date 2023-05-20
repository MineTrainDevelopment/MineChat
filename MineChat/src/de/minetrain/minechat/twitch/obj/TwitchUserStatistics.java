package de.minetrain.minechat.twitch.obj;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.minetrain.minechat.config.ConfigManager;
import de.minetrain.minechat.twitch.TwitchManager;
import kong.unirest.Unirest;

public class TwitchUserStatistics {
	private static Map<String, TwitchChatUser> chatUsers = new HashMap<String, TwitchChatUser>();
	private static final ConfigManager userIndex = new ConfigManager("data/UserIndex.yml", false);
	private static String usersToCall = "";
	private static int usersToCallCount = 0;
	private static Instant lastCallTime = Instant.now();
	
	@SuppressWarnings("unchecked")
	public TwitchUserStatistics() {
		 Map<Object, Object> userMap = (Map<Object, Object>) userIndex.getRawConfig().get("Users");
		 userMap.entrySet().forEach(user -> {
			 Map<Object, Object> value = (Map<Object, Object>) user.getValue();
			 chatUsers.put(value.get("id").toString(), new TwitchChatUser(value.get("id").toString(), value.get("login").toString(), value.get("DisplayName").toString(), value.get("MessageColor").toString(), Long.parseLong(value.get("TotalMessages").toString())));
		 });
	}
	
	public static TwitchChatUser getTwitchUser(String userID){
		if(usersToCallCount >= 50 || (usersToCallCount > 0 && lastCallTime.plusSeconds(180).isBefore(Instant.now()) && usersToCall.length()>5)) {
			newAPICall();
		}
		
		if(chatUsers.containsKey(userID)){
			return chatUsers.get(userID);
		}

		usersToCall += "&user_id="+userID;
		usersToCallCount++;
		return null;
	}
	
	private static void newAPICall() {
    	JsonObject fromJson = new Gson().fromJson(Unirest.get("https://api.twitch.tv/helix/chat/color?"+usersToCall.substring(1))// 'https://api.twitch.tv/helix/users?id=141981764&id=4845668'
			.header("Authorization", "Bearer "+TwitchManager.getAccesToken())
			.header("Client-Id", new TwitchCredentials().getClientID())
			.asString()
			.getBody(), JsonObject.class);
		
    	usersToCall = "";
    	usersToCallCount = 0;
    	lastCallTime = Instant.now();
		System.out.println(fromJson.get("data"));
		
		getUsersFromJsonArray(fromJson.getAsJsonArray("data")).forEach(user -> {
			String path = "Users.User_"+user.getUserId();
			userIndex.setString(path+".login", user.getUserLogin());
			userIndex.setString(path+".id", user.getUserId());
			userIndex.setString(path+".DisplayName", user.getUserName());
			userIndex.setString(path+".MessageColor", user.getColorCode());
			userIndex.setNumber(path+".TotalMessages", 0);
			chatUsers.put(user.getUserId(), user);
		});
		
		userIndex.saveConfigToFile();
    }
	
	public static List<TwitchChatUser> getUsersFromJsonArray(JsonArray jsonArray) {
        List<TwitchChatUser> users = new ArrayList<>();

        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String userId = jsonObject.get("user_id").getAsString();
            String userLogin = jsonObject.get("user_login").getAsString();
            String userName = jsonObject.get("user_name").getAsString();
            String color = jsonObject.get("color").getAsString();

            users.add(new TwitchChatUser(userId, userLogin, userName, color, 0l));
        }

        return users;
    }
	
}
