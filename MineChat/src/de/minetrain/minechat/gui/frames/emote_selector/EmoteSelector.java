package de.minetrain.minechat.gui.frames.emote_selector;

import de.minetrain.minechat.gui.emotes.ChannelEmotes;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.main.ChannelManager;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class EmoteSelector extends MineDialog {
	private final ScrollPane emoteBatchsPane;
	private final VBox batches = new VBox(5);
	private EmoteSelectEvent selectEvent;
	public EmoteSelectorBatche favoriteEmoteBatche;
	private boolean closeOnSelect = true;
	
	public EmoteSelector(EmoteSelectEvent selectEvent) {
		this(false, selectEvent);
	}

	public EmoteSelector(boolean showAndWait, EmoteSelectEvent selectEvent) {
		super("Emote selector", 400, 400);
		this.selectEvent = selectEvent;
		
		BorderPane layout = new BorderPane();
		setContent(layout);
		layout.setCenter(new Text("Test..."));
		
		VBox channelButtons = new VBox(5);
		channelButtons.setAlignment(Pos.CENTER_LEFT);

		favoriteEmoteBatche = new EmoteSelectorBatche(null, "Favorite", this);
		batches.getChildren().add(favoriteEmoteBatche);
		
        ChannelManager.getAllChannels().forEach(channel -> {
			
			ChannelEmotes channelEmotes = channel.getChannelEmotes();
			if(channelEmotes != null && !channelEmotes.getAllEmotes().isEmpty()){
				
				EmoteSelectorChannelButton channelButton = new EmoteSelectorChannelButton(channel, this);
				channelButtons.getChildren().add(channelButton);
				
        		EmoteSelectorBatche selectorBatche = new EmoteSelectorBatche(channel, channelButton, this);
				batches.getChildren().add(selectorBatche);
				channelButton.setParentBatche(selectorBatche);
        	}
        });
		batches.getChildren().add(new EmoteSelectorBatche(null, "Default", this));
		
		ScrollPane tabPane = new ScrollPane(channelButtons);
		tabPane.setStyle("-fx-border-width: 10; -fx-border-color: transparent;");
        tabPane.setFocusTraversable(false);
        tabPane.setFitToHeight(true);
        tabPane.setFitToWidth(true);
        tabPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tabPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        

//		ScrollPane test = new ScrollPane(new EmoteSelectorBatche(ChannelManager.getChannel("99351845")));
		emoteBatchsPane = new ScrollPane(batches);
        emoteBatchsPane.setFocusTraversable(false);
        emoteBatchsPane.setFitToHeight(true);
        emoteBatchsPane.setFitToWidth(true);
//        test.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        emoteBatchsPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        emoteBatchsPane.vvalueProperty().addListener(event -> {
			Bounds paneBounds = emoteBatchsPane.localToScene(emoteBatchsPane.getBoundsInParent());
			boolean isDetermined = false;
			
			if (emoteBatchsPane.getContent() instanceof Parent) {
				for (Node node : ((Parent) emoteBatchsPane.getContent()).getChildrenUnmodifiable()) {
					if(node instanceof EmoteSelectorBatche){
						EmoteSelectorBatche batch = (EmoteSelectorBatche) node;
						batch.scrollPrirorty(false);
						Bounds nodeBounds = node.localToScene(node.getBoundsInLocal());
						if (paneBounds.intersects(nodeBounds) && !isDetermined) {
							batch.scrollPrirorty(true);
							isDetermined = true;
						}
					}
				}
			}
        });
        
        layout.setRight(tabPane);
        layout.setCenter(emoteBatchsPane);
        openStage(true);
	}
	
	public void scrollToEmoteBatch(EmoteSelectorBatche batch) {
		double targetValue = batch.getLayoutY() * (1/(batches.getHeight() - emoteBatchsPane.getViewportBounds().getHeight()));
		
	    Timeline timeline = new Timeline(
	            new KeyFrame(Duration.millis(400),
	                    new KeyValue(emoteBatchsPane.vvalueProperty(), targetValue, Interpolator.EASE_BOTH)
	            )
	    );
	    timeline.play();
	}
	
	public void setCloseOnSelect(boolean closeOnSelect){
		this.closeOnSelect = closeOnSelect;
	}
	
	public void fireSelectEvent(Emote emote){
		if(selectEvent != null){
			selectEvent.onSelect(emote);
			if(closeOnSelect){
				closeStage();
			}
		}
	}
	
	public void setOnSelect(EmoteSelectEvent event){
		selectEvent = event;
	}
	
	public interface EmoteSelectEvent {
		void onSelect(Emote emote);
	}

}
