package de.minetrain.minechat.utils.events;

import org.slf4j.LoggerFactory;

import de.minetrain.minechat.features.macros.MacroObject;
import de.minetrain.minechat.gui.obj.messages.MessageComponentContent;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.ChatMessage;

public enum MineChatEventType{
	INCOMING_MESSAGE {
        @Override
        public void fireEvent(MineChatEvents event, Object obj) {
            if (!(obj instanceof TwitchMessage)) {
                throwFireError("Can't fire onIncomingMessageEvent!", obj, TwitchMessage.class);
                return;
            }

            event.onIncomingMessageEvent((TwitchMessage) obj);
        }
    },
	
	MESSAGE_HIGHLITE {
		@Override
		public void fireEvent(MineChatEvents event, Object obj) {
			if(!(obj instanceof MessageComponentContent)){
				throwFireError("Can´t fire onMessageHighliteEvent!", obj, MessageComponentContent.class);
				return;
			}
			
			event.onMessageHighliteEvent((MessageComponentContent) obj);
		}
	},
	
	SENT_MESSAGE {
		@Override
		public void fireEvent(MineChatEvents event, Object obj) {
			if(!(obj instanceof ChatMessage)){
				throwFireError("Can´t fire onSentMessageEvent!", obj, ChatMessage.class);
				return;
			}
			
			event.onSentMessageEvent((ChatMessage) obj);
		}
	},
	
	EXECUTE_MACRO {
		@Override
		public void fireEvent(MineChatEvents event, Object obj) {
			if(!(obj instanceof MacroObject)){
				throwFireError("Can´t fire onExecuteMacroEvent!", obj, MacroObject.class);
				return;
			}
			
			event.onExecuteMacroEvent((MacroObject) obj);
		}
	};

	// Abstract method to be implemented by each enum constant.
    public abstract void fireEvent(MineChatEvents event, Object obj);
    
	private static void throwFireError(String message, Object obj, Class<?> clazz) {
		LoggerFactory.getLogger(MineChatEventType.class).warn(message,
				new IllegalArgumentException("Tryed to fire an event with invalid parameters.",
				new ClassCastException("Can´t cast "+obj.getClass()+" to "+clazz)));
	}
	
	public static MineChatEventType[] getAllEvents(){
		return new MineChatEventType[]{
				INCOMING_MESSAGE,
				MESSAGE_HIGHLITE,
				SENT_MESSAGE,
				EXECUTE_MACRO
		};
	}
	
	/**
	 * @return {@link MineChatEventType#INCOMING_MESSAGE} <br> {@link MineChatEventType#MESSAGE_HIGHLITE}
	 */
	public static MineChatEventType[] getAllMessageEvents(){
		return new MineChatEventType[]{
				INCOMING_MESSAGE,
				MESSAGE_HIGHLITE,
		};
	}
}