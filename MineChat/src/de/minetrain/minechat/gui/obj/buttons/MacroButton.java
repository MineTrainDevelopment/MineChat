package de.minetrain.minechat.gui.obj.buttons;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.minetrain.minechat.features.macros.MacroObject;
import de.minetrain.minechat.gui.emotes.Emote.EmoteSize;
import de.minetrain.minechat.gui.frames.MacroEditorFrame;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.panes.TitleBarPane;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Channel;
import de.minetrain.minechat.main.ChannelManager;
import de.minetrain.minechat.twitch.MessageManager;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class MacroButton extends Button{
	public static final double MIN_WIDTH = 85d;
	public static final double MAX_WIDTH = 110d;
	public static final double HEIGHT = 35d;
	
	private MacroObject macro;
	private int buttonId;
	
	private static int t = 1;
	
	public MacroButton(int button_id, HBox scaleRefrence) {
		this.buttonId = button_id;
        setFocusTraversable(false);
        setId("macro-key");
        
        t++;
        
        setMinSize(MIN_WIDTH, HEIGHT);
        setMaxSize(MAX_WIDTH, HEIGHT);
        setPrefSize(getMaxWidth(), getMaxHeight());

        minWidthProperty().bind(scaleRefrence.widthProperty());

        
        //NOTE: Right click gets consumed bsc the computePrefWidth behaves strangely.
        setOnMouseClicked(event -> {
        	if (event.getButton() == MouseButton.SECONDARY) {
        		//Open channel editor
        		if(macro != null){
        			new MacroEditorFrame(macro);
        		}
                event.consume();
                return;
            }
        	
        	if(macro != null){
        		MessageManager.sendMessage(macro);
        	}
        });
        
	}
	
	public MacroButton asEmoteOnly(){
		minWidthProperty().unbind();
        setMinSize(35, 35);
        setMaxSize(35, 35);
        setPrefSize(getMaxWidth(), getMaxHeight());
        setStyle("-fx-border-width: 3px;");
		return this;
	}
	
	private static final Rectangle getEmoteNode(MacroObject macro, int size) {
		if(macro.getEmote() == null){
			return null;
		}
		
		Rectangle emoteNode = macro.getEmote().getEmoteNode(EmoteSize.SMALL, size);
		emoteNode.setTranslateX(-5);
		return emoteNode;
	}
	
	
	/**
	 * Loads the macro from the current selected channel.
	 * <br> Do nothing when no channel is selected.
	 */
	public void setMacro(){
		Channel currentChannel = ChannelManager.getCurrentChannel();
		if(currentChannel != null){
			setMacro(currentChannel);
		}
	}
	
	public void setMacro(Channel channel){
        this.macro = channel.getMacros().getMacro(buttonId);
        
		if(macro == null){
			setText("");
        	setGraphic(null);
        	return;
		}
		
		setText(macro.getTitle());
    	setGraphic(getEmoteNode(macro, 27));
	}
	
}
