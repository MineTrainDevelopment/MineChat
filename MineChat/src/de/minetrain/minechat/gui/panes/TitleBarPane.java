package de.minetrain.minechat.gui.panes;

import de.minetrain.minechat.gui.obj.buttons.ChannelTabButton;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
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
	private final ListChangeListener<ChannelViewModel> listChangeListener;
	private ObjectProperty<ObservableList<ChannelViewModel>>channels;

	public TitleBarPane() {
		getStyleClass().add("title-bar");
		listChangeListener = this::handleListChange;

		Button settingsButton = new Button();
		settingsButton.setFocusTraversable(false);
		settingsButton.setOnMouseClicked(event -> {
			System.err.println("TODO: Settings");

//			new EmoteSelector(false, emote -> {
//				System.err.println(emote.getName());
//			});

		});

		HBox settingsButtonContainer = new HBox(5);
		settingsButtonContainer.getChildren().addAll(settingsButton, new Rectangle(0, 0, Color.PINK));

		tabBar = new HBox(5);
		tabBar.setAlignment(Pos.CENTER_LEFT);

		tabPane = new ScrollPane(tabBar);
		tabPane.setFocusTraversable(false);
		tabPane.setFitToHeight(true);
		tabPane.setFitToWidth(true);
		tabPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		tabPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		tabPane.setOnScroll(event -> {
			if (event.getDeltaX() == 0 && event.getDeltaY() != 0) {
				tabPane.setHvalue(tabPane.getHvalue() - event.getDeltaY() * 5 / tabBar.getWidth());
			}
		});

		Rectangle verticalStrut = new Rectangle(5, 26, ColorManager.decode("#0E0E0E"));
		verticalStrut.setTranslateY(2);

		setLeft(settingsButtonContainer);
		setCenter(tabPane);
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

	public void handleListChange(Change<? extends ChannelViewModel> c) {
		while (c.next()) {
			if (c.wasRemoved()) {
				tabBar.getChildren().subList(c.getFrom(), c.getFrom() + c.getRemovedSize()).clear();
			}
			if (c.wasAdded()) {
				for (int i = c.getFrom(); i < c.getTo(); i++) {
					ChannelViewModel addedChannel = c.getList().get(i);
					ChannelTabButton tabButton = new ChannelTabButton(addedChannel, this);
					tabBar.getChildren().add(i, tabButton);
				}
			}
		}
	}

	public ObjectProperty<ObservableList<ChannelViewModel>> channelsProperty() {
		if (channels == null) {
			channels = new SimpleObjectProperty<>(this, "channels");
			channels.addListener((_, oldList, newList) -> {
				if (oldList != null) {
					oldList.removeListener(listChangeListener);
				}
				tabBar.getChildren().clear();
				if (newList != null) {
					tabBar.getChildren().addAll(newList.stream().map(cvm -> new ChannelTabButton(cvm, this)).toList());
					newList.addListener(listChangeListener);
				}
			});
		}
		return channels;
	}

	public ObservableList<ChannelViewModel> getChannels() {
		return channelsProperty().get();
	}

	public void setChannels(ObservableList<ChannelViewModel> channels) {
		channelsProperty().set(channels);
	}
}
