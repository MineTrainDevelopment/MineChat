package de.minetrain.minechat.gui.frames;

import de.minetrain.minechat.features.macros.MacroObject;
import de.minetrain.minechat.features.macros.MacroType;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.Emote.EmoteSize;
import de.minetrain.minechat.gui.emotes.EmoteSelectorButton;
import de.minetrain.minechat.gui.frames.emote_selector.EmoteSelector;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.main.Channel;
import de.minetrain.minechat.main.ChannelManager;
import de.minetrain.minechat.main.Main;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MacroEditorFrame extends MineDialog {
	private Emote selectedEmote;
	private EmoteSelector emoteSelector;

	public MacroEditorFrame(MacroObject macro, MacroType macroType, int button_id) {
		super("Edit this macro:", 420, 300);
		
		String title = "";
		String output = "";
		
		if(macro != null){
			title = macro.getTitle();
			output = macro.getAllOutputsAsString();
		}else{
			emoteSelector = new EmoteSelector(true, emote -> selectedEmote = emote);
			//Returns of no emote is selected.
			if(selectedEmote == null){
				emoteSelector = null; //Release memory.
				return;
			}
			title = selectedEmote.getName();
		}
		
		if(selectedEmote == null){
			selectedEmote = macro.getEmote();
		}
		
		TextField titleInputField = new TextField(title);
		
		EmoteSelectorButton emoteButton = new EmoteSelectorButton(selectedEmote, EmoteSize.MEDIUM, 4);
		emoteButton.setOnMouseClicked(event -> {
			if(emoteSelector == null){
				emoteSelector = new EmoteSelector(newEmote -> {
					selectedEmote = newEmote;
					emoteButton.changeImage(newEmote);
					titleInputField.setPromptText(newEmote.getName());
				});
			}else{
				emoteSelector.openStage(false);
			}
		});
		
        titleInputField.setId("message-input-field");
        titleInputField.setPromptText(selectedEmote.getName());
        titleInputField.setFocusTraversable(false);
        titleInputField.setStyle("-fx-font-size: 30px;");
        titleInputField.minHeightProperty().bind(emoteButton.heightProperty());
        

		TextArea outputInputField = new TextArea(output);
        outputInputField.setId("message-input-field");
        outputInputField.setPromptText("Output text\r\n"
				+ "Line split (ENTER key) == Random output text list\r\n"
				+ "\r\n"
				+ "You can use all message variables.\r\n"
				+ "  - Like {TIME}, {TOTAL_MESSAGES}...");
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
        	emoteSelector = null;
        	closeStage();
        });
		
		setContent(layout);
		openStage(false);
	}

}
