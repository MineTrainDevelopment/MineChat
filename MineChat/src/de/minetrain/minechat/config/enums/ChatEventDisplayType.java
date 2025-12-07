package de.minetrain.minechat.config.enums;

public enum ChatEventDisplayType {
	MESSAGE(true, false, false),
	POPUP(false, true, false),
	SYSTEM_NOTIFICATION(false, false, true),
	
	MESSAGE_POPUP(true, true, false),
	SYSTEM_POPUP(false, true, true),
	
	NON(false, false, false),
	ALL(true, true, true);

	public boolean isMessageList(){return messageList;}
	private boolean messageList;
	
	public boolean isChatPopUp(){return chatPopUp;}
	private boolean chatPopUp;
	
	public boolean isSystemNotification(){return systemNotification;}
	private boolean systemNotification;
	
	private ChatEventDisplayType(boolean messageList, boolean chatPopUp, boolean systemNotification) {
		this.messageList = messageList;
		this.chatPopUp = chatPopUp;
		this.systemNotification = systemNotification;
	}
	
	public static ChatEventDisplayType getFromString(String type){
		ChatEventDisplayType displayType = ChatEventDisplayType.valueOf(type.toUpperCase());
		return displayType != null ? displayType : MESSAGE_POPUP;
	}
	
	
}
