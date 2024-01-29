package de.minetrain.minechat.gui.panes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.minetrain.minechat.gui.frames.emote_selector.EmoteSelector;
import de.minetrain.minechat.gui.obj.buttons.ChannelTabButton;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Channel;
import de.minetrain.minechat.main.ChannelManager;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class TitleBarPane extends BorderPane{
	private final ScrollPane tabPane;
	private final HBox tabBar;
	
	public void scrollToTab(ChannelTabButton channelTab){
//		tabPane.setHvalue(channelTab.getLayoutX() * (1/(tabBar.getWidth() - tabPane.getViewportBounds().getWidth())));
//		System.err.println(tabBar.getWidth() +" - "+ tabPane.getViewportBounds().getWidth());
		animateScrollPane(tabPane, channelTab.getLayoutX() * (1/(tabBar.getWidth() - tabPane.getViewportBounds().getWidth())));
	}
	
	private void animateScrollPane(ScrollPane scrollPane, double targetValue) {
	    Timeline timeline = new Timeline(
	            new KeyFrame(Duration.millis(400),
	                    new KeyValue(scrollPane.hvalueProperty(), targetValue, Interpolator.EASE_BOTH)
	            )
	    );
	    timeline.play();
	}

	
	public TitleBarPane() {
		Button settingsButton = new Button();
        settingsButton.setFocusTraversable(false);
        settingsButton.setMaxSize(30, 30);
        settingsButton.setMinSize(30, 30);
        settingsButton.setId("program-action");
        settingsButton.setOnMouseClicked(event -> {
        	System.err.println("TODO: Settings");
        	
        	 new EmoteSelector(false, emote -> {
        		System.err.println(emote.getName());
        	});
        	
        });
        

        HBox settingsButtonContainer = new HBox(5);
        settingsButtonContainer.getChildren().addAll(settingsButton, new Rectangle(0, 0, Color.PINK));
        
        

        tabBar = new HBox(5);
        tabBar.setAlignment(Pos.CENTER_LEFT);
        ChannelManager.getAllChannels().forEach(channel -> tabBar.getChildren().add(new ChannelTabButton(channel, this)));
		
        tabPane = new ScrollPane(tabBar);
        tabPane.setFocusTraversable(false);
        tabPane.setFitToHeight(true);
        tabPane.setFitToWidth(true);
        tabPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tabPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tabPane.setMinHeight(40);
        tabPane.setTranslateY(-3);
        
        tabPane.setOnScroll(event -> {
            if(event.getDeltaX() == 0 && event.getDeltaY() != 0) {
            	tabPane.setHvalue(tabPane.getHvalue() - event.getDeltaY() * 5 / tabBar.getWidth());
            }
        });
        
        
        

        Button minimizeButton = new Button();
        minimizeButton.setFocusTraversable(false);
        minimizeButton.setMaxSize(30, 30);
        minimizeButton.setMinSize(30, 30);
        minimizeButton.setId("program-action");
        
        Button closeButton = new Button();
        closeButton.setFocusTraversable(false);
        closeButton.setMaxSize(30, 30);
        closeButton.setMinSize(30, 30);
        closeButton.setId("program-action");
        
        Rectangle verticalStrut = new Rectangle(5, 26, ColorManager.decode("#0E0E0E"));
        verticalStrut.setTranslateY(2);

        HBox programActionButtonBox = new HBox(5);
        programActionButtonBox.getChildren().addAll(new Rectangle(0, 0, Color.PINK), verticalStrut, minimizeButton, closeButton);
        
        setMinHeight(45);
        setMaxHeight(45);
        setId("title-bar");
        setLeft(settingsButtonContainer);
        setCenter(tabPane);
        setRight(programActionButtonBox);
	}
	
	
	public HBox getTabBar() {
		return tabBar;
	}
	
}
