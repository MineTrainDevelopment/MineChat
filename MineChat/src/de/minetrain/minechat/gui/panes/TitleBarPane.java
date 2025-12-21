package de.minetrain.minechat.gui.panes;

import de.minetrain.minechat.gui.frames.emote_selector.EmoteSelector;
import de.minetrain.minechat.gui.obj.buttons.ChannelTabButton;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class TitleBarPane extends BorderPane {

	private static final Duration ANIMATION_SCROLL_DURATION = Duration.millis(150);

	private final ScrollPane tabPane;
	private final HBox tabBar;
	private final ObservableList<ChannelViewModel> channels;
	private ObjectProperty<ChannelViewModel> selectedChannel;

	public TitleBarPane() {
		channels = FXCollections.observableArrayList();
		channels.addListener(this::handleListChange);

		Button settingsButton = new Button();
		settingsButton.setFocusTraversable(false);
		settingsButton.setMaxSize(30, 30);
		settingsButton.setMinSize(30, 30);
		settingsButton.setId("program-action");
		settingsButton.setOnMouseClicked(event -> {
			System.err.println("TODO: Settings");

			new EmoteSelector(false, emote -> {
				System.err.println(emote.getName());
			});

		});

		HBox settingsButtonContainer = new HBox(5);
		settingsButtonContainer.getChildren().addAll(settingsButton, new Rectangle(0, 0, Color.PINK));

		tabBar = new HBox(5);
		tabBar.setAlignment(Pos.CENTER_LEFT);
//        ChannelManager.getAllChannels().forEach(channel -> tabBar.getChildren().add(new ChannelTabButton(channel, this))); //Moved to channel manager.

		tabPane = new ScrollPane(tabBar);
		tabPane.setFocusTraversable(false);
		tabPane.setFitToHeight(true);
		tabPane.setFitToWidth(true);
		tabPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		tabPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		tabPane.setMinHeight(40);
		tabPane.setTranslateY(-3);

		tabPane.setOnScroll(event -> {
			if (event.getDeltaX() == 0 && event.getDeltaY() != 0) {
				tabPane.setHvalue(tabPane.getHvalue() - event.getDeltaY() * 5 / tabBar.getWidth());
			}
		});

		Button minimizeButton = new Button();
		minimizeButton.setFocusTraversable(false);
		minimizeButton.setMaxSize(30, 30);
		minimizeButton.setMinSize(30, 30);
		minimizeButton.setId("program-action");

		Button closeButton = new Button();
		closeButton.setFocusTraversable(false);
		closeButton.setMaxSize(30, 30);
		closeButton.setMinSize(30, 30);
		closeButton.setId("program-action");

		Rectangle verticalStrut = new Rectangle(5, 26, ColorManager.decode("#0E0E0E"));
		verticalStrut.setTranslateY(2);

		HBox programActionButtonBox = new HBox(5);
		programActionButtonBox.getChildren().addAll(new Rectangle(0, 0, Color.PINK), verticalStrut, minimizeButton,
				closeButton);

		setMinHeight(45);
		setMaxHeight(45);
		setId("title-bar");
		setLeft(settingsButtonContainer);
		setCenter(tabPane);
		setRight(programActionButtonBox);
	}

	public ObservableList<ChannelViewModel> getChannels() {
		return channels;
	}

	public HBox getTabBar() {
		return tabBar;
	}

	public void scrollToTab(ChannelTabButton channelTab) {
		double contentWidth = tabBar.getWidth();
		double viewportWidth = tabPane.getViewportBounds().getWidth();
		double scrollableWidth = contentWidth - viewportWidth;

		if (scrollableWidth <= 0) {
			return; // No scrolling needed
		}

		double nodeLeft = channelTab.getLayoutX();
		double nodeRight = nodeLeft + channelTab.getBoundsInParent().getWidth();

		// Current visible area boundaries
		double visibleLeft = tabPane.getHvalue() * scrollableWidth;
		double visibleRight = visibleLeft + viewportWidth;

		double targetHvalue;
		if (nodeLeft < visibleLeft) {
			// Node is to the left of viewport - scroll left
			targetHvalue = nodeLeft / scrollableWidth;
		} else if (nodeRight > visibleRight) {
			// Node is to the right of viewport - scroll right
			targetHvalue = (nodeRight - viewportWidth) / scrollableWidth;
		} else {
			return; // Already fully visible
		}

		targetHvalue = Math.clamp(targetHvalue, 0d, 1d);
		new Timeline(new KeyFrame(ANIMATION_SCROLL_DURATION, new KeyValue(tabPane.hvalueProperty(), targetHvalue, Interpolator.EASE_BOTH))).play();
	}

	private void handleListChange(Change<? extends ChannelViewModel> change) {
		while (change.next()) {
			if (change.wasRemoved()) {
				tabBar.getChildren().remove(change.getFrom(), change.getTo());
			}
			if (change.wasAdded()) {
				change.getAddedSubList().forEach(cvm -> cvm.selectedProperty().bind(selectedChannelProperty().isEqualTo(cvm)));
				tabBar.getChildren().addAll(change.getFrom(), change.getAddedSubList().stream().map(channel -> new ChannelTabButton(channel, this)).toList());
			}
		}
	}

	public ObjectProperty<ChannelViewModel> selectedChannelProperty() {
		if (selectedChannel == null) {
			selectedChannel = new SimpleObjectProperty<>(this, "selectedChannel");
		}
		return selectedChannel;
	}

	public void setSelectedChannel(ChannelViewModel channel) {
		selectedChannelProperty().set(channel);
	}

	public ChannelViewModel getSelectedChannel() {
		return selectedChannelProperty().get();
	}
}
