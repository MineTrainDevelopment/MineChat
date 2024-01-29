package de.minetrain.minechat.gui.panes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.minetrain.minechat.features.macros.MacroObject;
import de.minetrain.minechat.features.macros.MacroType;
import de.minetrain.minechat.gui.obj.buttons.MacroButton;
import de.minetrain.minechat.main.Channel;
import de.minetrain.minechat.main.ChannelManager;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MacroPanelPane extends BorderPane{
	HBox sizeGuideButton = new HBox();
	VBox macroRows;
	ScrollPane macroScrollPane;
	
	private final List<MacroButton> macroButtons = new ArrayList<MacroButton>();
	
	public MacroPanelPane() {
		sizeGuideButton.setStyle("-fx-background-color: lime;");
		sizeGuideButton.setPrefWidth(MacroButton.MAX_WIDTH);
		sizeGuideButton.setMinWidth(MacroButton.MIN_WIDTH);
		sizeGuideButton.setMaxWidth(MacroButton.MAX_WIDTH);
		
		HBox sizeGuideButton2 = new HBox();
		sizeGuideButton2.setStyle("-fx-background-color: lime;");
		sizeGuideButton2.setPrefWidth(sizeGuideButton.getPrefWidth());
		sizeGuideButton2.setMinWidth(sizeGuideButton.getMinWidth());
		sizeGuideButton2.setMaxWidth(sizeGuideButton.getMaxWidth());
		
		HBox sizeGuideButton3 = new HBox();
		sizeGuideButton3.setStyle("-fx-background-color: lime;");
		sizeGuideButton3.setPrefWidth(sizeGuideButton.getPrefWidth());
		sizeGuideButton3.setMinWidth(sizeGuideButton.getMinWidth());
		sizeGuideButton3.setMaxWidth(sizeGuideButton.getMaxWidth());
		
		HBox sizeGuide = new HBox(5);
		sizeGuide.getChildren().add(new Rectangle(0, 0, Color.TRANSPARENT));
		sizeGuide.getChildren().add(sizeGuideButton);
		sizeGuide.getChildren().add(new Rectangle(0, 0, Color.TRANSPARENT));
		sizeGuide.getChildren().add(sizeGuideButton2);
		sizeGuide.getChildren().add(new Rectangle(0, 0, Color.TRANSPARENT));
		sizeGuide.getChildren().add(sizeGuideButton3);
		sizeGuide.getChildren().add(new Rectangle(0, 0, Color.TRANSPARENT));
		sizeGuide.setPrefWidth(Double.NEGATIVE_INFINITY);
		sizeGuide.setMaxHeight(0);
		

		macroRows = new VBox(5, createRow(0), createRow(1));
//		macroRows = new VBox(5, createRow(), createRow(), createMacroEmoteRow());
		
		macroScrollPane = new ScrollPane(macroRows);
		macroScrollPane.setFocusTraversable(false);
		macroScrollPane.setFitToHeight(true);
		macroScrollPane.setFitToWidth(true);
        macroScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        macroScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        macroScrollPane.setTranslateY(-1);
        
        macroScrollPane.setOnScroll(event -> {
            if(event.getDeltaX() == 0 && event.getDeltaY() != 0) {
            	macroScrollPane.setHvalue(macroScrollPane.getHvalue() - event.getDeltaY() * 0.3 / macroScrollPane.getWidth());
            }
        });
		
        ScrollPane emoteMacroScrollPane = new ScrollPane(createMacroEmoteRow(2));
        emoteMacroScrollPane.setFocusTraversable(false);
        emoteMacroScrollPane.setFitToHeight(true);
        emoteMacroScrollPane.setFitToWidth(true);
        emoteMacroScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        emoteMacroScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        emoteMacroScrollPane.setTranslateY(2);
        emoteMacroScrollPane.hvalueProperty().bind(macroScrollPane.hvalueProperty());
		

		Button queueButton = new Button("Queue: 0");
		queueButton.setId("small-border-button");
		queueButton.setMaxSize(100, 35);
		queueButton.setMinSize(100, 35);
		queueButton.setFocusTraversable(false);
		
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
		infoPane.setRight(new VBox(5, createRowSelector(), queueButton));
		infoPane.setBottom(statisticsButton);
		
		
        setMinHeight(132);
        setMaxHeight(132);
        setId("macro-panel");
        setCenter(new VBox(sizeGuide, macroScrollPane, emoteMacroScrollPane));
        setRight(new HBox(new Rectangle(5, 5, Color.TRANSPARENT), infoPane, new Rectangle(5, 5, Color.TRANSPARENT)));
//        setRight(programActionButtonBox);
	}

	private HBox createMacroEmoteRow(int rowId) {
		HBox emoteMacros = new HBox(5);
		emoteMacros.setTranslateX(4);

		
		for(int i=0; i < 18; i++){//200
			emoteMacros.getChildren().add(createEmoteMacroButton((rowId * 10) + i));
			System.err.println("Add emote");
		}
		
		//Add a placeholder to match the pixel with from macro and emote scroll pane.
		emoteMacros.getChildren().add(new Rectangle(0, 0, Color.TRANSPARENT));
		return emoteMacros;
	}

	private HBox createRow(int rowId) {
		HBox row = new HBox(10);
		for(int i=0; i < 6; i++){//100
			
			row.getChildren().add(createMacroButton((rowId * 10) + i));
			System.err.println("Add macro");
		}

		//Add a placeholder to match the pixel with from macro and emote scroll pane.
		row.getChildren().add(new Rectangle(0, 0, Color.TRANSPARENT));
		row.setTranslateX(4);
		return row;
	}

	private Button createMacroButton(int buttonId) {
		MacroButton macroButton = new MacroButton(MacroType.TEXT, buttonId, sizeGuideButton);
		macroButtons.add(macroButton);
		return macroButton;
	}
	

	private Button createEmoteMacroButton(int buttonId) {
		MacroButton macroButton = new MacroButton(MacroType.EMOTE, buttonId, sizeGuideButton).asEmoteOnly();
		macroButtons.add(macroButton);
		return macroButton;
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
		

		rightKey.setOnAction(e -> {
		    int visibleButtons = 0;
		    boolean firstRow = true;
		    for (Node node : macroRows.getChildren()) {
		        if (node instanceof HBox && firstRow) {
		        	
		            HBox hbox = (HBox) node;
		            Button lastButtonOnDisplay = null;
		            int lastButtonOnDisplayCount = 0;
				    int buttonsPerRow = 0;
		            firstRow = false;
		            
		            for (Node child : hbox.getChildren()) {
		                if (child instanceof Button) {
		                    Button button = (Button) child;
		                    buttonsPerRow++;
		                    if (button.localToScreen(button.getBoundsInLocal()).intersects(macroScrollPane.localToScreen(macroScrollPane.getBoundsInLocal()))) {
		                        visibleButtons++;
		                        lastButtonOnDisplayCount = visibleButtons;
		                        lastButtonOnDisplay = button;
		                        System.err.println("Button visible "+button.getText()+" - "+button.getLayoutX());
		                    }
		                }
		            }
		            
		            //
		            //
		            //Das problem ist, das die macro scroll pane nicht gescheit skaliert, und damit die größen berächnung nicht geht.
		            //
		            //
		            
		            System.err.println("Scroll to -> "+lastButtonOnDisplay.getText()+" -> "+lastButtonOnDisplayCount+"_"+buttonsPerRow);
//		            double percentage = (lastButtonOnDisplayCount / buttonsPerRow);
//					System.err.println(percentage);
//		            double value = lastButtonOnDisplay.getLayoutX() * (1 / macroScrollPane.getWidth());
		            double value = lastButtonOnDisplay.getLayoutX() * (1 / (macroScrollPane.getWidth() - macroScrollPane.getViewportBounds().getWidth()));
		            System.err.println(value+" - "+macroScrollPane.getWidth()+" - "+macroScrollPane.getViewportBounds().getWidth());
					macroScrollPane.setHvalue(value);
		        }
		    }
//		    macroScrollPane.setHvalue(macroScrollPane.getHvalue() + visibleButtons);
		});

		
		
		HBox selector = new HBox(-3);
		selector.setId("row-selector-frame");
		selector.getChildren().addAll(leftKey, profilePic, rightKey);
		return selector;
	}

	
	public void loadMacros(){
		macroButtons.forEach(MacroButton::setMacro);
	}
	
	public void loadMacros(Channel channel){
		macroButtons.forEach(button -> button.setMacro(channel));
	}
	
}
