package de.minetrain.minechat.gui.panes;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MacroPanelPane extends BorderPane{
	
	public MacroPanelPane() {
		
		HBox emoteMacros = new HBox(5);
		emoteMacros.getChildren().addAll(
				createEmoteMacroButton(), 
				createEmoteMacroButton(), 
				createEmoteMacroButton(), 
				createEmoteMacroButton(), 
				createEmoteMacroButton(), 
				createEmoteMacroButton(), 
				createEmoteMacroButton());
		emoteMacros.setTranslateX(5);
		
		VBox macros = new VBox(5);
		macros.getChildren().addAll(createRow(), createRow(), emoteMacros);
		

		Button queueButton = new Button("Queue: 0");
		queueButton.setId("small-border-button");
		queueButton.setMaxSize(100, 35);
		queueButton.setMinSize(100, 35);
		queueButton.setFocusTraversable(false);
		
		VBox queueRowPane = new VBox(5);
		queueRowPane.getChildren().addAll(createRowSelector(), queueButton);
//		queueRowPane.setTranslateX(10);

		Button statisticsButton = new Button("Settings");
		statisticsButton.setId("small-border-button");
		statisticsButton.setMaxSize(185, 35);
		statisticsButton.setMinSize(185, 35);
		statisticsButton.setFocusTraversable(false);
		statisticsButton.setTranslateY(-7);
		
		Rectangle profilePic = new Rectangle(75, 75, Color.PINK);
		profilePic.setTranslateY(-6);
		profilePic.setTranslateX(-5);
		
		BorderPane infoPane = new BorderPane();
		infoPane.setCenter(profilePic);
		infoPane.setRight(queueRowPane);
		infoPane.setBottom(statisticsButton);
		infoPane.setTranslateX(-5);
		
        setMinHeight(132);
        setMaxHeight(132);
        setId("macro-panel");
        setLeft(macros);
        setRight(infoPane);
//        setRight(programActionButtonBox);
	}

	private HBox createRow() {
		HBox row = new HBox(10);
		row.getChildren().addAll(createMacroButton(), createMacroButton(), createMacroButton());
		row.setTranslateX(5);
		return row;
	}

	private Button createMacroButton() {
		Button key = new Button("");
		key.setId("macro-key");
		key.setPrefSize(85, 35);
		key.setFocusTraversable(false);
		return key;
	}

	private Button createEmoteMacroButton() {
		Button key = new Button();
		key.setId("small-border-button");
		key.setPrefSize(35, 35);
		key.setFocusTraversable(false);
		return key;
	}
	
	public HBox createRowSelector(){
		Button leftKey = new Button();
		leftKey.setId("small-border-button");
		leftKey.setPrefSize(35, 35);
		leftKey.setFocusTraversable(false);
		
		Label profilePic = new Label("0");
		profilePic.setMinSize(36, 35);
		profilePic.setMaxSize(36, 35);
		profilePic.setId("row-selector-indicator");
		
		Button rightKey = new Button();
		rightKey.setId("small-border-button");
		rightKey.setPrefSize(35, 35);
		rightKey.setFocusTraversable(false);
		
		
		HBox selector = new HBox(-3);
		selector.setId("row-selector-frame");
		selector.getChildren().addAll(leftKey, profilePic, rightKey);
		return selector;
	}
	
}
