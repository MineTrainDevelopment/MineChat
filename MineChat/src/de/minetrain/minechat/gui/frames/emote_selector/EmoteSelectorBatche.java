package de.minetrain.minechat.gui.frames.emote_selector;

import java.util.List;

import de.minetrain.minechat.gui.emotes.ChannelEmotes;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.emotes.EmoteSelectorButton;
import de.minetrain.minechat.gui.emotes.Emote.EmoteSize;
import de.minetrain.minechat.main.Channel;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;


//Some sort of emtoe select event to append to a frame.
//  - When i select an emote in the current instance of this frame, it triggers an event that
//  - for examle changes the emote inside a macro editor or appends an emote to the current input cursor.
//
//Option to dispose or close frame.
//  - A new select listner whould be requert to open the frame again??
public class EmoteSelectorBatche extends TitledPane {
	private static final int maxItemsPerRow = 8;
	private EmoteSelectorChannelButton batcheButton;
	private final Channel channel;
	private final EmoteSelector emoteSelector;
	private final GridPane grid = new GridPane();
	
	public EmoteSelectorBatche(Channel channel, EmoteSelectorChannelButton batcheButton, EmoteSelector emoteSelector) {
		this(channel, channel.getChannelData().getDisplayName(), emoteSelector);
		this.batcheButton = batcheButton;
	}
	
	public EmoteSelectorBatche(Channel channel, String name, EmoteSelector emoteSelector) {
		this.channel = channel;
		this.emoteSelector = emoteSelector;
		
		setText(name);
		setContent(grid);
		setBorder(null);
//		setExpanded(false);
		
		grid.setHgap(5); 
		grid.setVgap(5);
		loadEmotes();
	}

	private void loadEmotes() {
		int columIndex = 0;
		int rowIndex = 0;
		
		List<Emote> emotes;
		if(channel != null){
			emotes = ChannelEmotes.sortEmotesByEasterEgg(channel, channel.getChannelEmotes().getAllEmotes());
		}else if(getText().equalsIgnoreCase("Favorite")){
			emotes = EmoteManager.getAllFavoriteEmotes(true);
		}else{
			emotes = EmoteManager.getAllDefaultEmotes();
		}
		
		for(Emote emote : emotes){
			EmoteSelectorButton selectorButton = new EmoteSelectorButton(emote, EmoteSize.SMALL, 2);
			selectorButton.setOnAction(event -> emoteSelector.fireSelectEvent(emote));
			selectorButton.setOnMouseClicked(event -> {
				if(event.getButton().equals(MouseButton.SECONDARY) && emoteSelector.favoriteEmoteBatche != null){
					emote.toggleFavorite();
					emoteSelector.favoriteEmoteBatche.grid.getChildren().clear();
					emoteSelector.favoriteEmoteBatche.loadEmotes();
				}
			});
			
			grid.add(selectorButton, columIndex, rowIndex);

			columIndex++;
			if(columIndex == maxItemsPerRow){
				columIndex = 0;
				rowIndex++;
			}
		};
	}
	
	public void scrollPrirorty(boolean priority){
		if(batcheButton != null){
			batcheButton.setColor(priority);
		}
	}

}
