package de.minetrain.minechat.gui.frames;

import de.minetrain.minechat.data.objectdata.MacroData;
import de.minetrain.minechat.features.macros.MacroObject;
import de.minetrain.minechat.features.macros.MacroType;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.Emote.EmoteSize;
import de.minetrain.minechat.gui.emotes.EmoteSelectorButton;
import de.minetrain.minechat.gui.emotes.EmoteSelectorButton.EmoteBorderType;
import de.minetrain.minechat.gui.frames.emote_selector.EmoteSelector;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.main.Channel;
import de.minetrain.minechat.main.ChannelManager;
import de.minetrain.minechat.main.Main;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MacroEditorFrame extends MineDialog {
	private Emote selectedEmote;

	public MacroEditorFrame(MacroObject macro, MacroType macroType, int button_id) {
		super("Edit this macro:", 420, 300);
		
		String title = "";
		String output = "Output text\r\n"
				+ "Line split (ENTER key) == Random output text list\r\n"
				+ "\r\n"
				+ "You can use all message variables.\r\n"
				+ "  - Like {TIME}, {TOTAL_MESSAGES}...";
		
		if(macro != null){
			title = macro.getTitle();
			output = macro.getAllOutputsAsString();
		}else{
			new EmoteSelector(true, emote -> selectedEmote = emote);
			title = selectedEmote.getName();
		}
		
		if(selectedEmote == null){
			selectedEmote = macro.getEmote();
		}
		
		EmoteSelectorButton emoteButton = new EmoteSelectorButton(selectedEmote, EmoteSize.MEDIUM, 4);
		emoteButton.setOnAction(event -> new EmoteSelector(newEmote -> {
			selectedEmote = newEmote;
			emoteButton.changeImage(newEmote);
		}));
		
		TextField titleInputField = new TextField(title);
        titleInputField.setId("message-input-field");
        titleInputField.setPromptText("Enter your title...");
        titleInputField.setFocusTraversable(false);
        titleInputField.setStyle("-fx-font-size: 30px;");
        titleInputField.minHeightProperty().bind(emoteButton.heightProperty());
        

		TextArea outputInputField = new TextArea(output);
        outputInputField.setId("message-input-field");
        outputInputField.setPromptText("Enter your chat message...");
        outputInputField.setStyle("-fx-font-size: 14px;");
        outputInputField.setFocusTraversable(false);
		
//		BorderPane layout = new BorderPane();
//		layout.setStyle("-fx-border-width: 10; -fx-border-color: transparent;");
//		layout.setLeft(emoteButton);
//		layout.setCenter(titleInputField);
//		layout.setBottom(outputInputField);
        
        VBox layout = new VBox(10, new HBox(10, emoteButton, titleInputField), outputInputField);
        layout.setStyle("-fx-border-width: 10; -fx-border-color: transparent;");
        
        setOnConfirm(event -> {
        	if(macro != null){
        		ChannelManager.getChannel(macro.getChannelId()).getMacros()
        			.updateMacro(macro, selectedEmote.getEmoteId(), titleInputField.getText(), outputInputField.getText().split("\n\r"));
        	}else{
        		Channel channel = ChannelManager.getCurrentChannel();
        		MacroObject newMacro = new MacroObject(
        				macroType, 
        				selectedEmote.getEmoteId(), 
        				button_id, 
        				titleInputField.getText(), 
        				channel.getChannelId(), 
        				outputInputField.getText().split("\n\r"));
        		
        		channel.getMacros().createMacro(newMacro);
        	}
        	
        	Main.macroPane.loadMacros();
        	closeStage();
        });
		
		setContent(layout);
		openStage(false);
	}

}
