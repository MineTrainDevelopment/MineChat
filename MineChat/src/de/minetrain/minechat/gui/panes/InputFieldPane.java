package de.minetrain.minechat.gui.panes;

import de.minetrain.minechat.utils.MineTextFlow;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;

public class InputFieldPane extends BorderPane {
	
	public InputFieldPane() {
		setStyle("-fx-border-radius: 0px; -fx-border-width: 10px; -fx-border-color: transparent;");
		Button sendButton = new Button();
		sendButton.setId("send-button");
		sendButton.setPrefSize(70, 35);
		sendButton.setMinSize(70, 35);
		sendButton.setMaxSize(70, 35);
		
		SVGPath svg = new SVGPath();
		svg.setContent("M44 1V5C44 6.66667 43 10 39 10C35 10 13.3333 10 3 10M3 10L11 18M3 10L11 2");
        svg.setStroke(Color.web("#0E0E0E"));
        svg.setStrokeWidth(3);
        svg.setFill(Color.TRANSPARENT);
        sendButton.setGraphic(svg);
        
        HBox sendButtonContainer = new HBox(5);
        sendButtonContainer.getChildren().addAll(new Rectangle(0, 0, Color.PINK), sendButton);

//        TextArea inputField = new TextArea();
        TextField inputField = new TextField();
        VBox inputFieldContainer = new VBox(inputField);
        inputField.setId("message-input-field");
        inputField.setPromptText("Enter your chat message...");
        inputField.setFocusTraversable(false);
//        inputField.setWrapText(true);
        inputField.setMinHeight(35);
        inputField.setPrefHeight(35);
        inputField.setMaxHeight(200);

        
        
//        Text textHolder = new Text();
//        textHolder.textProperty().bind(inputField.textProperty());
//        textHolder.layoutBoundsProperty().addListener((ChangeListener<Bounds>) (observable, oldValue, newValue) -> {
//		    if (oldValue.getHeight() != newValue.getHeight()) {
//		        System.out.println("newValue = " + oldValue.getHeight()+" - "+newValue.getHeight());
//		        inputField.setPrefHeight(textHolder.getLayoutBounds().getHeight() + 30); // +20 is for paddings
//		    }
//		});
        
//        Text text = new Text();
//        text.textProperty().bind(inputField.textProperty());
//        inputField.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> text.getBoundsInLocal().getHeight(), text.boundsInLocalProperty()).add(20));
        
        
        MineTextFlow infoBox = new MineTextFlow()
	        .setDefaultFontSize(18d)
//	        .setAlignment(TextAlignment.CENTER)
//	        .appendString("Welcome to ", HTMLColors.WHITE)
//	        .appendString("MineChat ", HTMLColors.AQUA)
//	        .appendString("<3", HTMLColors.RED)
//	        .appendSpace()
//	        .appendString(" -  Version: "+Main.VERSION, HTMLColors.GRAY)
	        ;
        
        
        infoBox.clear()
	        .setAlignment(TextAlignment.LEFT)
//	        .appendString("Reply to thrad from: ", Color.AQUA)
//	        .appendString("Pferdchen", Color.LIGHTPINK)
	        .appendString("Reply to ", Color.AQUA)
	        .appendString("@MineTrainLP", Color.GREENYELLOW)
	        .appendString(" in thrad from: ", Color.AQUA)
	        .appendString("@Pferdchen", Color.LIGHTPINK)
	        .appendString(":", Color.WHITE)
	        .appendLineSplit()
//	        .appendString("@", Color.WHITE)
//	        .appendString("@MineTrainLP", Color.GREENYELLOW)
//	        .appendString(" --> ", Color.WHITE)
	        .appendString("Also ich finde ja das du gaaanz besonders fein bist!");
        

//        TextArea infoBox = new TextArea("Test");
//        Label infoBox = new Label("test\nte");
        infoBox.setId("message-info-field");
//        infoBox.setEditable(false);
        infoBox.setMinHeight(35);
        infoBox.setMaxHeight(105);
        infoBox.setMaxWidth(Double.POSITIVE_INFINITY);
//        infoBox.prefColumnCountProperty().bind(infoBox.textProperty().length());

        VBox infoBoxContainer = new VBox(5);
        infoBoxContainer.setVisible(true);
        infoBoxContainer.getChildren().addAll(infoBox, new Rectangle(0, 0, Color.PINK));
        
        sendButton.setOnMouseClicked(e -> {
        	if(infoBoxContainer.isVisible()){
                infoBoxContainer.setVisible(false);
        		setTop(null);
        	}else{
                infoBoxContainer.setVisible(true);
        		setTop(infoBoxContainer);
        	}
        });
        
        
		setTop(infoBoxContainer); //Info popup
		setCenter(inputFieldContainer); //input field
		setRight(sendButtonContainer); //send button
		
	}
	
}
