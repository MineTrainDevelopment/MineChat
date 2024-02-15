package de.minetrain.minechat.gui.panes.recyclerview;

import de.minetrain.minechat.gui.obj.messages.MessageComponent;
import de.minetrain.minechat.gui.obj.messages.MessageComponentContent;
import javafx.scene.control.ListView;

public class MessageListView extends ListView<MessageComponentContent>{
	
	public MessageListView() {
		// TODO Auto-generated constructor stub
		setCellFactory(MessageCellFactory.getFactory());
		
//		setFixedCellSize(100);
		
		itemsProperty().addListener(event -> System.err.println(this.getItems().size()));
		
	}
	

}
