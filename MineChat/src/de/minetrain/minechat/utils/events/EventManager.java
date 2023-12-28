package de.minetrain.minechat.utils.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EventManager extends HashMap<MineChatEventType, ArrayList<MineChatEvents>>{
	private static final long serialVersionUID = 2473359862545373575L;
	
	/**
	 * Subscribe a listener to the event
	 * @param eventListener The Listener class must extend from {@link EventListener}
	 * @param eventTypes all events you wanna subscribe to. Falls back to all available events, should this parameter be null or empty.
	 */
    public void addListener(MineChatEvents eventListener, MineChatEventType... eventTypes) {
    	if(eventTypes == null || eventTypes.length == 0){
    		eventTypes = MineChatEventType.getAllEvents();
    	}
    	
    	Arrays.stream(eventTypes).forEach(type -> computeIfAbsent(type, key -> new ArrayList<>()).add(eventListener));
    }

    public void removeListener(MineChatEventType eventType, MineChatEvents eventListener) {
		computeIfAbsent(eventType, key -> new ArrayList<>()).remove(eventListener);
    }

    public void fireEvent(MineChatEventType eventType, Object obj) {
    	if(containsKey(eventType)){
    		get(eventType).forEach(event -> eventType.fireEvent(event, obj));
    	}
    }


}
