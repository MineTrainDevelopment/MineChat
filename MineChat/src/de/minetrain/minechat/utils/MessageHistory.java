package de.minetrain.minechat.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageHistory {
	private List<String> sendedMessages = new ArrayList<String>();
	private String tempUserInput = "";
	private int index = 0;
	
	public MessageHistory() { }
	
	public List<String> getSendedMessages() {
		return sendedMessages;
	}

	public void addSendedMessages(String... messages) {
		Arrays.asList(messages).forEach(message -> this.sendedMessages.add(message));
		resetIndex();
		System.out.println(sendedMessages);
	}
	
	public boolean isNewText(){
		return index > sendedMessages.size();
	}
	
	public void resetIndex(){
		index = sendedMessages.size()+1;
	}
	
	public String getNextItem(String currentUserInput){
		if(sendedMessages.isEmpty()){
			return currentUserInput;
		}
		
		if(index == sendedMessages.size()+1){
			tempUserInput = currentUserInput;
		}
		
		if(index > 1){
			index = index-1;
		}
		return getItem();
	}
	
	public String getPreviousItem(String currentUserInput){
		if(sendedMessages.isEmpty()){
			return currentUserInput;
		}
		
		if(index >= sendedMessages.size()){
			index = sendedMessages.size()+1;
			return tempUserInput;
		}
		index = index+1;
		return getItem();
	}

	private String getItem(){
		if(!sendedMessages.isEmpty()){
			return sendedMessages.get((index-1));
		}
		return null;
	}
	
}
