package de.minetrain.minechat.utils;

import java.util.ArrayList;
import java.util.List;

import de.minetrain.minechat.twitch.MessageManager;

public class MessageHistory extends ArrayList<String>{
	private static final long serialVersionUID = 5485805171115038665L;
	private String tempUserInput = "";
	private int index = 0;
	
	public MessageHistory() {
		super();
	}
	
	public List<String> getSendedMessages() {
		return this;
	}

	public void addSendedMessages(String... messages) {
		addAll(List.of(messages));
	}

	public void addSendedMessages(String message) {
		add(message);
	}
	
	public boolean isNewText(){
		return index > size();
	}
	
	public void resetIndex(){
		resetIndex(0);
	}
	
	public void resetIndex(int offset){
		index = (size()+1)+offset;
	}
	
	public String getNextItem(String currentUserInput){
		if(isEmpty()){
			return currentUserInput;
		}
		
		if(index == size()+1){
			tempUserInput = currentUserInput;
		}
		
		if(index > 1){
			index = index-1;
		}
		return getItem();
	}
	
	public String getPreviousItem(String currentUserInput){
		if(isEmpty()){
			return currentUserInput;
		}
		
		if(index >= size()){
			index = size()+1;
			return tempUserInput;
		}
		index = index+1;
		return getItem();
	}

	private String getItem(){
		if(!isEmpty()){
			return get((index-1));
		}
		return null;
	}
	
	@Override
	public boolean add(String message) {
		message = message.strip().trim();
		
		if(message.endsWith(MessageManager.getSpamprotector())){
			resetIndex(0);
			return false;
		}
		
		if(isEmpty() || !get(size()-1).equals(message)){
			resetIndex(1);
			return super.add(message);
		}

		resetIndex(0);
		return false;
	}
	
}
