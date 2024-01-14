package de.minetrain.minechat.gui.obj.buttons;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.minetrain.minechat.gui.panes.TitleBarPane;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Channel;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ChannelTab extends Button{
	private static final List<ChannelTab> buttons = new ArrayList<ChannelTab>();
	private static final Label messageContainer = new Label("This is a test");
	private static final double MIN_VALUE = 34d;
	private static final int ANIMATION_TIME = 150;
	
	private TitleBarPane parentTitleBar;
	private boolean liveState = false;
	
	public ChannelTab(Channel channel, TitleBarPane titleBarPane) {
		super(channel.getChannelData().getDisplayName(), getProfilePic(channel, 24));
        setFocusTraversable(false);
        setId("channel-tab");
        setMinWidth(42d);
        
        parentTitleBar = titleBarPane;
        
        setOnDragDetected(event -> {
            Dragboard dragboard = startDragAndDrop(TransferMode.COPY_OR_MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putHtml(channel.getChannelData().getDisplayName());
            content.putString(channel.getChannelData().getDisplayName());
            content.putFiles(List.of(new File(channel.getProfileImage(80).getUrl().replace("file:", ""))));
            content.putUrl("https://www.twitch.tv/"+channel.getChannelData().getLoginName());
            
            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setFill(Color.TRANSPARENT);
            content.putImage(snapshot(snapshotParameters, null));
            
            dragboard.setContent(content);
            event.consume();
        });

        setOnDragOver(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        //Hover effect
        setOnDragEntered(event -> {
        	if(!event.getGestureSource().equals(this)){
        		setBorder(new Border(new BorderStroke(Color.AQUA, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2), new Insets(-2))));
        	}
        });

        setOnDragExited(event -> {
        	if(!event.getGestureSource().equals(this)){
        		setBorder(null);
        	}
        });

        setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                Object source = event.getGestureSource();
                int sourceIndex = titleBarPane.getTabBar().getChildren().indexOf(source);
                int targetIndex = titleBarPane.getTabBar().getChildren().indexOf(this);
                ArrayList<Node> nodes = new ArrayList<>(titleBarPane.getTabBar().getChildren());
                if (sourceIndex < targetIndex) {
                    Collections.rotate(nodes.subList(sourceIndex, targetIndex + 1), -1);
                } else {
                    Collections.rotate(nodes.subList(targetIndex, sourceIndex + 1), 1);
                }
                titleBarPane.getTabBar().getChildren().clear();
                titleBarPane.getTabBar().getChildren().addAll(nodes);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        setOnDragDone(DragEvent::consume);

        
        //NOTE: Right click gets consumed bsc the computePrefWidth behaves strangely.
        setOnMouseClicked(event -> {
        	if (event.getButton() == MouseButton.SECONDARY) {
        		//Open channel editor
                event.consume();
                return;
            }
        	
        	select();
        });
        
        
        if(buttons.size() == 2){setLiveState(true);}
        buttons.add(this);
	}


	public void select() {
		buttons.forEach(button -> {
			button.setId("channel-tab");
			button.setLiveState(button.isLiveState());
			if(button.getMinWidth() != MIN_VALUE && !button.equals(this)){
				Timeline buttonAnimation = new Timeline();
				buttonAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(ANIMATION_TIME), new KeyValue(button.minWidthProperty(), MIN_VALUE, Interpolator.EASE_BOTH)));
				buttonAnimation.play();
			}
		});
		
		if(getId().equals("channel-tab")){
			setId("channel-tab-selected");
			setLiveState(isLiveState());
			
			double endWidth = computePrefWidth(Double.NEGATIVE_INFINITY);
			
			Timeline buttonAnimation = new Timeline();
			KeyFrame key1 = new KeyFrame(Duration.millis(ANIMATION_TIME+20), e -> parentTitleBar.scrollToTab(this));
			KeyFrame key2 = new KeyFrame(Duration.millis(ANIMATION_TIME), new KeyValue(minWidthProperty(), endWidth, Interpolator.EASE_BOTH));
			buttonAnimation.getKeyFrames().addAll(key1, key2);
			buttonAnimation.play();
		}
	}
	
	
	public double computePrefWidth(double height) {
		return super.computePrefWidth(height);
	}
	
	private static final Rectangle getProfilePic(Channel channel, int size) {
		Rectangle profilePic = channel.getProfilePic(size);
        profilePic.setTranslateX(-5);
		return profilePic;
	}
	
	public void setLiveState(boolean liveState){
		this.liveState = liveState;
		if(liveState && getId() != null){
			setStyle("-fx-background-color: "+ (getId().equals("channel-tab-selected") ? ColorManager.encode(Color.RED) : ColorManager.encode(Color.DARKRED.darker())) +";");
		}else{
			setStyle(null);
		}
	}
	
	public boolean isLiveState() {
		return liveState;
	}
	
}
