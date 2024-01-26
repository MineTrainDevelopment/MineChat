package de.minetrain.minechat.gui.frames.parant;

import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.utils.MineTextFlow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MineDialog {
	private final Stage stage;
	private BorderPane contentPane;
	private Button confirmButton, cancelButton;
	
	public MineDialog(String title, double width, double hight) {
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.initOwner(Main.primaryStage);
        
        BorderPane titleBar = new BorderPane();
        titleBar.setId("title-bar");
        titleBar.setCenter(new MineTextFlow().setAlignment(TextAlignment.CENTER).appendString(title, 20d));
        
        confirmButton = new Button();
        confirmButton.setFocusTraversable(false);
        confirmButton.setMaxSize(26, 26);
        confirmButton.setMinSize(26, 26);
        confirmButton.setId("program-action");
        confirmButton.setStyle("-fx-background-color: green;");
        
        cancelButton = new Button();
        cancelButton.setFocusTraversable(false);
        cancelButton.setMaxSize(26, 26);
        cancelButton.setMinSize(26, 26);
        cancelButton.setId("program-action");
        cancelButton.setOnAction(event -> closeStage());
        
        HBox actionButtonBox = new HBox(5, confirmButton, cancelButton);
        titleBar.setRight(new VBox(5, actionButtonBox, new Rectangle(0, 0, Color.TRANSPARENT)));
        
        contentPane = new BorderPane();
        contentPane.setTop(titleBar);
        
        Scene scene = new Scene(contentPane, width, hight);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().addAll(Main.primaryStage.getScene().getStylesheets());
        
        stage.setScene(scene);
        stage.show();
	}
	
	public MineDialog(String title, double width, double hight, Node content, EventHandler<ActionEvent> confirmEvent) {
		this(title, width, hight, content);
		setOnConfirm(confirmEvent);
	}
	
	public MineDialog(String title, double width, double hight, Node content) {
		this(title, width, hight);
		setContent(content);
	}
	
	public MineDialog setContent(Node node) {
		this.contentPane.setCenter(node);
		return this;
	}
	
	public MineDialog setOnConfirm(EventHandler<ActionEvent> event){
		confirmButton.setOnAction(event);
		return this;
	}
	
	public MineDialog setOnCancel(EventHandler<ActionEvent> event){
		cancelButton.setOnAction(event);
		return this;
	}
	
	public void closeStage(){
		stage.close();
//		System.gc(); //DEBUG ONLY!!!
	}
	

}
