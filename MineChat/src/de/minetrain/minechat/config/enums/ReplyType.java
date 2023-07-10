package de.minetrain.minechat.config.enums;

import org.slf4j.LoggerFactory;

public enum ReplyType {
	MESSAGE, // message might be debricatet from twitch side.
	THREAD, 
	USER_NAME; 
	
	public static ReplyType get(String name) {
		try {
			return ReplyType.valueOf(name);
		} catch (IllegalArgumentException ex) {
			LoggerFactory.getLogger(ReplyType.class).warn("Invalid ReplyType -> "+name+""
					+ "\n	You may need to check the settigns file."
					+ "\n	Valid types are: [MESSAGE, THREAD_PARENT, USER_NAME]");
			return THREAD;
		}
	}
}
