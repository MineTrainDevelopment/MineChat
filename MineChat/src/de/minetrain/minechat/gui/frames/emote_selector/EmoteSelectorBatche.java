package de.minetrain.minechat.gui.frames.emote_selector;

import java.util.ArrayList;

import de.minetrain.minechat.gui.emotes.ChannelEmotes;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.EmoteSelectorButton;
import de.minetrain.minechat.gui.emotes.Emote.EmoteSize;
import de.minetrain.minechat.main.Channel;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;


//Some sort of emtoe select event to append to a frame.
//  - When i select an emote in the current instance of this frame, it triggers an event that
//  - for examle changes the emote inside a macro editor or appends an emote to the current input cursor.
//
//Option to dispose or close frame.
//  - A new select listner whould be requert to open the frame again??
public class EmoteSelectorBatche extends TitledPane {
	private static final int maxItemsPerRow = 8;
	
	public EmoteSelectorBatche(Channel channel) {
//		super(channel.getChannelData().getDisplayName(), grid);
		GridPane grid = new GridPane();
		
		setText(channel.getChannelData().getDisplayName());
		setContent(grid);
		setBorder(null);
		setExpanded(false);
		
//		expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
//		    if (isNowExpanded) {
//		        // The TitledPane has been expanded, do something...
//		    	loadEmotes(channel, grid);
//		    } else {
//		        // The TitledPane has been collapsed, do something else...
//		    	ArrayList<Node> nodes = new ArrayList<Node>();
//		    	nodes.addAll(grid.getChildren());
//				nodes.forEach(node -> grid.getChildren().remove(node));
//		    }
//		});
		
		
//		grid.setGridLinesVisible(true);
		grid.setHgap(5); 
		grid.setVgap(5);
		loadEmotes(channel, grid);
	}

	private void loadEmotes(Channel channel, GridPane grid) {
		int columIndex = 0;
		int rowIndex = 0;
		
		for(Emote emote : ChannelEmotes.sortEmotesByEasterEgg(channel, channel.getChannelEmotes().getAllEmotes())){
			grid.add(new EmoteSelectorButton(emote, EmoteSize.SMALL, 2), columIndex, rowIndex);

			columIndex++;
			if(columIndex == maxItemsPerRow){
				columIndex = 0;
				rowIndex++;
			}
		};
	}

}
