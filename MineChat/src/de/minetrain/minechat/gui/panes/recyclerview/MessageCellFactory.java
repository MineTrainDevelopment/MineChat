package de.minetrain.minechat.gui.panes.recyclerview;

import de.minetrain.minechat.data.databases.OwnerCacheDatabase.UserChatData;
import de.minetrain.minechat.gui.obj.messages.MessageComponent;
import de.minetrain.minechat.gui.obj.messages.MessageComponentContent;
import javafx.scene.AccessibleRole;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class MessageCellFactory extends ListCell<MessageComponentContent>{
	private MessageComponent component;
	
	private MessageCellFactory() {
		component = new MessageComponent();
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setGraphic(component);
        setPrefWidth(0);
	}
	
	@Override
    protected void updateItem(MessageComponentContent item, boolean empty){
        super.updateItem(item, empty);
        
//        new IllegalArgumentException("test").printStackTrace();
        
        
        
        if(empty){
        	component.free();
        	return;
        }
        
        if(getAccessibleRole().equals(AccessibleRole.NODE)){
        	component.fillData(testComp);
        	return;
        }
        
        System.err.println("hilfe?  -  "+ getAccessibleRole().equals(AccessibleRole.NODE)+" - "+ (item != null ? item.getMessage() : ""));
        component.fillData(item);
        
//        Stack<MessageComponent> stack = new Stack<MessageComponent>();
    }
	
	private static final MessageComponentContent testComp = new MessageComponentContent(new UserChatData("0", "2f2f2f", "name", null), "", 0l, null);
	
	
    /**
     * Utility method to use as ListCellFactory.
     *
     * @return a Callback to use in ListView.
     */
    public static Callback<ListView<MessageComponentContent>, ListCell<MessageComponentContent>> getFactory(){
    	System.err.println("FACTORY?-------------------------------");
        return i -> new MessageCellFactory();
    }

}
