package de.minetrain.minechat.gui.obj.buttons;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.minetrain.minechat.gui.panes.TitleBarPane;
import de.minetrain.minechat.main.Channel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
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

public class MacroButton extends Button{
	
	public MacroButton(Channel channel, TitleBarPane titleBarPane) {
		super(channel.getChannelData().getDisplayName(), getProfilePic(channel, 24));
        setFocusTraversable(false);
        setId("channel-tab");
        setMinWidth(42d);
        
        
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
                int sourceIndex = titleBarPane.tabBar.getChildren().indexOf(source);
                int targetIndex = titleBarPane.tabBar.getChildren().indexOf(this);
                ArrayList<Node> nodes = new ArrayList<>(titleBarPane.tabBar.getChildren());
                if (sourceIndex < targetIndex) {
                    Collections.rotate(nodes.subList(sourceIndex, targetIndex + 1), -1);
                } else {
                    Collections.rotate(nodes.subList(targetIndex, sourceIndex + 1), 1);
                }
                titleBarPane.tabBar.getChildren().clear();
                titleBarPane.tabBar.getChildren().addAll(nodes);
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

}
