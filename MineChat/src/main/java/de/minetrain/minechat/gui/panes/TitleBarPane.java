package de.minetrain.minechat.gui.panes;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

import de.minetrain.minechat.gui.obj.buttons.ChannelTabButton;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.Observable;
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
	private ObservableList<ChannelViewModel> channels;


	public TitleBarPane() {
		getStyleClass().add("title-bar");
		channels = FXCollections.observableList(new ArrayList<>(), cvm -> new Observable[] { cvm.sortIndexProperty() });
		channels.addListener(this::handleListChange);

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

		Rectangle verticalStrut = new Rectangle(5, 26, Color.web("#0E0E0E"));
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
				Set<String> removedChannelIds = c.getRemoved().stream().map(ChannelViewModel::getChannelId).collect(toSet());
				tabBar.getChildren().removeIf(nullOrButton -> {
					if (nullOrButton instanceof ChannelTabButton tabButton) {
						return removedChannelIds.contains(tabButton.getChannelViewModel().getChannelId());
					}
					return false;
				});
			}
			if (c.wasAdded()) {
				c.getAddedSubList().stream().map(cvm -> new ChannelTabButton(cvm, this)).forEach(tabBar.getChildren()::add);
			}
		}
		tabBar.getChildren().sort(Comparator.comparingInt(
			node -> node instanceof ChannelTabButton ctb ? ctb.getChannelViewModel().getSortIndex() : Integer.MAX_VALUE));
	}

	public ObservableList<ChannelViewModel> getChannels() {
		return channels;
	}
}
