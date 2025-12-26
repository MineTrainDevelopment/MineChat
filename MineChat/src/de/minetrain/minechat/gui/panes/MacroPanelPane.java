package de.minetrain.minechat.gui.panes;

import de.minetrain.minechat.gui.obj.buttons.MacroButton;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.twitch.MessageManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MacroPanelPane extends BorderPane {

	private VBox macroRows;
	private ScrollPane macroScrollPane;

	private HBox macroRow1;
	private HBox macroRow2;
	private HBox emoteMacroRow;

	private ObjectProperty<ChannelViewModel> channelProperty;

	public MacroPanelPane() {
		macroRow1 = new HBox(10D);
		macroRow2 = new HBox(10D);
		emoteMacroRow = new HBox(6D);
		channelProperty().addListener((_, _, newChannel) -> {
			if (newChannel != null) {
				int macrosCount = newChannel.getMacros().size() / 2;
				ensureSize(macroRow1.getChildren(), macrosCount);
				ensureSize(macroRow2.getChildren(), macrosCount);
				ensureSize(emoteMacroRow.getChildren(), newChannel.getEmoteMacros().size());
				for (int i = 0; i < macrosCount; i++) {
					Node node = macroRow1.getChildren().get(i);
					if (node instanceof MacroButton mb) {
						mb.setMacro(newChannel.getMacros().get(i));
					}
					node = macroRow2.getChildren().get(i);
					if (node instanceof MacroButton mb) {
						mb.setMacro(newChannel.getMacros().get(macrosCount + i));
					}
				}
				for (int i = 0; i < newChannel.getEmoteMacros().size(); i++) {
					Node node = emoteMacroRow.getChildren().get(i);
					if (node instanceof MacroButton mb) {
						mb.setMacro(newChannel.getEmoteMacros().get(i));
					}
				}
			} else {
				macroRow1.getChildren().clear();
				macroRow2.getChildren().clear();
				emoteMacroRow.getChildren().clear();
			}
		});

		macroRows = new VBox(5, macroRow1, macroRow2);
//		macroRows = new VBox(5, createRow(), createRow(), createMacroEmoteRow());

		macroScrollPane = new ScrollPane(macroRows);
		macroScrollPane.setFocusTraversable(false);
		macroScrollPane.setFitToHeight(true);
		macroScrollPane.setFitToWidth(true);
		macroScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		macroScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		macroScrollPane.setTranslateY(-1);

		macroScrollPane.setOnScroll(event -> {
			if (event.getDeltaX() == 0 && event.getDeltaY() != 0) {
				macroScrollPane
						.setHvalue(macroScrollPane.getHvalue() - event.getDeltaY() * 0.3 / macroScrollPane.getWidth());
			}
		});

		ScrollPane emoteMacroScrollPane = new ScrollPane(emoteMacroRow);
		emoteMacroScrollPane.setFocusTraversable(false);
		emoteMacroScrollPane.setFitToHeight(true);
		emoteMacroScrollPane.setFitToWidth(true);
		emoteMacroScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		emoteMacroScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		emoteMacroScrollPane.setTranslateY(2);
		emoteMacroScrollPane.hvalueProperty().bind(macroScrollPane.hvalueProperty());

		Button queueButton = new Button("Queue: 0");
		queueButton.textProperty().bind(MessageManager.queueSizeProperty().map(value -> "Queue: " + value));
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

		ImageView profileImageView = new ImageView();
		profileImageView.setTranslateY(-6);
		profileImageView.setTranslateX(-5);
		profileImageView.imageProperty().bind(channelProperty().flatMap(ChannelViewModel::profileImageLargeProperty));

		BorderPane infoPane = new BorderPane();
		infoPane.setCenter(profileImageView);
		infoPane.setRight(new VBox(5, createRowSelector(), queueButton));
		infoPane.setBottom(statisticsButton);

		setMinHeight(132);
		setMaxHeight(132);
		setId("macro-panel");
		VBox macrosPanel = new VBox(macroScrollPane, emoteMacroScrollPane);
//		VBox.setVgrow(macroScrollPane, Priority.ALWAYS);
//		VBox.setVgrow(emoteMacroScrollPane, Priority.ALWAYS);
		setCenter(macrosPanel);
		setRight(new HBox(new Rectangle(5, 5, Color.TRANSPARENT), infoPane, new Rectangle(5, 5, Color.TRANSPARENT)));
//        setRight(programActionButtonBox);
	}

	public HBox createRowSelector() {
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

		// TODO Whatever we tried to do...
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
							if (button.localToScreen(button.getBoundsInLocal())
									.intersects(macroScrollPane.localToScreen(macroScrollPane.getBoundsInLocal()))) {
								visibleButtons++;
								lastButtonOnDisplayCount = visibleButtons;
								lastButtonOnDisplay = button;
								System.err.println("Button visible " + button.getText() + " - " + button.getLayoutX());
							}
						}
					}

					//
					//
					// Das problem ist, das die macro scroll pane nicht gescheit skaliert, und damit
					// die gr��en ber�chnung nicht geht.
					//
					//

					System.err.println("Scroll to -> " + lastButtonOnDisplay.getText() + " -> "
							+ lastButtonOnDisplayCount + "_" + buttonsPerRow);
//		            double percentage = (lastButtonOnDisplayCount / buttonsPerRow);
//					System.err.println(percentage);
//		            double value = lastButtonOnDisplay.getLayoutX() * (1 / macroScrollPane.getWidth());
					double value = lastButtonOnDisplay.getLayoutX()
							* (1 / (macroScrollPane.getWidth() - macroScrollPane.getViewportBounds().getWidth()));
					System.err.println(value + " - " + macroScrollPane.getWidth() + " - "
							+ macroScrollPane.getViewportBounds().getWidth());
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

	public ObjectProperty<ChannelViewModel> channelProperty() {
		if (channelProperty == null) {
			channelProperty = new SimpleObjectProperty<>(this, "channel");
		}
		return channelProperty;
	}

	public ChannelViewModel getChannel() {
		return channelProperty().get();
	}

	public void setChannel(ChannelViewModel channel) {
		channelProperty().set(channel);
	}

	private void ensureSize(ObservableList<Node> macroButtons, int size) {
		while (macroButtons.size() > size) {
			macroButtons.remove(size, macroButtons.size());
		}
		while (macroButtons.size() < size) {
			macroButtons.add(new MacroButton());
		}
	}
}
