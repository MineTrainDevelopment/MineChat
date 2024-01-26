package de.minetrain.minechat.gui.frames.emote_selector;

import de.minetrain.minechat.gui.emotes.ChannelEmotes;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.gui.obj.buttons.ChannelTabButton;
import de.minetrain.minechat.gui.panes.TitleBarPane;
import de.minetrain.minechat.main.Channel;
import de.minetrain.minechat.main.ChannelManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class EmoteSelector extends MineDialog {

	public EmoteSelector() {
		super("Emote selector", 400, 400);
		
		VBox channelButtons = new VBox(5);
		channelButtons.setAlignment(Pos.CENTER_LEFT);
        ChannelManager.getAllChannels().forEach(channel -> channelButtons.getChildren().add(crateChannelButton(channel)));
//		channelButtons.getChildren().addAll(
//				crateChannelButton(ChannelManager.getChannel("177849882")),
//				crateChannelButton(ChannelManager.getChannel("35884167")),
//				crateChannelButton(ChannelManager.getChannel("99351845")),
//				crateChannelButton(ChannelManager.getChannel("151368796")),
//				crateChannelButton(ChannelManager.getChannel("12875057")),
//				crateChannelButton(ChannelManager.getChannel("41629029")),
//				crateChannelButton(ChannelManager.getChannel("40972890")),
//				crateChannelButton(ChannelManager.getChannel("438349822")),
//				crateChannelButton(ChannelManager.getChannel("884338173")),
//				crateChannelButton(ChannelManager.getChannel("962743570")),
//				crateChannelButton(ChannelManager.getChannel("605556313")));
		
		ScrollPane tabPane = new ScrollPane(channelButtons);
        tabPane.setFocusTraversable(false);
        tabPane.setFitToHeight(true);
        tabPane.setFitToWidth(true);
        tabPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tabPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        VBox batches = new VBox(5);
        ChannelManager.getAllChannels().forEach(channel -> {
        	ChannelEmotes channelEmotes = channel.getChannelEmotes();
			if(channelEmotes != null && !channelEmotes.getAllEmotes().isEmpty()){
        		batches.getChildren().add(new EmoteSelectorBatche(channel));
        	}
		});

//		ScrollPane test = new ScrollPane(new EmoteSelectorBatche(ChannelManager.getChannel("99351845")));
		ScrollPane test = new ScrollPane(batches);
        test.setFocusTraversable(false);
        test.setFitToHeight(true);
        test.setFitToWidth(true);
//        test.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        test.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        BorderPane layout = new BorderPane();
        tabPane.setStyle("-fx-border-width: 10; -fx-border-color: transparent;");
        layout.setRight(tabPane);
        layout.setCenter(test);
        setContent(layout);
	}
	
	public Button crateChannelButton(Channel channel){
		Button button = new Button();
		button.setFocusTraversable(false);
        button.setId("channel-tab");
        button.setStyle("-fx-min-width: 34; -fx-max-width: 34;");
        
        Rectangle profilePic = channel.getProfilePic(24);
        profilePic.setTranslateX(-5);
        button.setGraphic(profilePic);
		
		return button;
	}

}
