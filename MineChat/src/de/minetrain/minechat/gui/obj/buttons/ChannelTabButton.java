package de.minetrain.minechat.gui.obj.buttons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.util.Logging;

import de.minetrain.minechat.gui.panes.TitleBarPane;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.main.ChannelManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.TwitchHelper;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ChannelTabButton extends Button {
	private static final Logger LOG = LoggerFactory.getLogger(ChannelTabButton.class);
	private static final double MIN_WIDTH = 34d;
	private static final Duration ANIMATION_RESIZE_DURATION = Duration.millis(150);
	private static final PseudoClass LIVE_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("live");
	private static final PseudoClass SELECTED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("selected");

	private ReadOnlyObjectWrapper<ChannelViewModel> channelViewModelProperty;
	private ReadOnlyBooleanWrapper liveProperty;
	private ReadOnlyBooleanWrapper selectedProperty;

	private TitleBarPane parentTitleBar;

	public ChannelTabButton(ChannelViewModel channelViewModel, TitleBarPane titleBarPane) {
		ImageView profileImageView = createProfileImageView(channelViewModel.getProfileImageUrl());
		super(channelViewModel.getChannelName(), profileImageView);
		this.parentTitleBar = titleBarPane;
		setFocusTraversable(false);
		setId("channel-tab");
		setMinWidth(MIN_WIDTH);
		channelViewModelPropertyInternal().set(channelViewModel);
		livePropertyInternal().bind(channelViewModel.liveProperty());
		selectedPropertyInternal().bind(channelViewModel.selectedProperty());
		profileImageView.imageProperty().bind(channelViewModel.profileImageUrlProperty().map(ChannelTabButton::createProfileImage));

		selectedProperty().addListener((_, _, newVal) -> {
			double newMinWidth = newVal.booleanValue() ? computePrefWidth(Double.NEGATIVE_INFINITY) : MIN_WIDTH;
			Timeline timeline = new Timeline(new KeyFrame(ANIMATION_RESIZE_DURATION, new KeyValue(minWidthProperty(), newMinWidth, Interpolator.EASE_BOTH)));
			timeline.setOnFinished(_ -> Platform.runLater(() -> parentTitleBar.scrollToTab(this)));
			timeline.play();
		});

		setOnDragDetected(this::handleDrag);

		setOnDragOver(event -> {
			if (!Objects.equals(event.getGestureSource(), this) && event.getDragboard().hasString()) {
				event.acceptTransferModes(TransferMode.MOVE);
			}
			event.consume();
		});

		// Hover effect
		setOnDragEntered(event -> {
			if (!Objects.equals(event.getGestureSource(), this)) {
				setBorder(new Border(new BorderStroke(Color.AQUA, BorderStrokeStyle.SOLID, new CornerRadii(5),
						new BorderWidths(2), new Insets(-2))));
			}
		});

		setOnDragExited(event -> {
			if (!Objects.equals(event.getGestureSource(), this)) {
				setBorder(null);
			}
		});

		setOnDragDropped(this::handleDragDropped);

		setOnDragDone(DragEvent::consume);

		setOnAction(_ -> Main.getChannelManager().setActiveChannel(getChannelViewModel()));
	}


	private void handleDragDropped(DragEvent event) {
		Dragboard dragboard = event.getDragboard();

		if (event.getGestureSource() instanceof ChannelTabButton ctb) {
			int sourceIndex = parentTitleBar.getChannels().indexOf(ctb.getChannelViewModel());
			int targetIndex = parentTitleBar.getChannels().indexOf(this.getChannelViewModel());
			List<ChannelViewModel> tempList = new ArrayList<>(parentTitleBar.getChannels());
			if (sourceIndex < targetIndex) {
				Collections.rotate(tempList.subList(sourceIndex, targetIndex + 1), -1);
			} else {
				Collections.rotate(tempList.subList(targetIndex, sourceIndex + 1), 1);
			}
			// Handle as single permutation to avoid multiple change events.
			parentTitleBar.getChannels().setAll(tempList);
			event.setDropCompleted(true);
			event.consume();
			return;
		}

		String url = dragboard.getUrl() != null ? dragboard.getUrl() : dragboard.getString();
		TwitchHelper.extracktUserLoginFromUrl(url)
			.thenAccept(user -> {
				ChannelManager channelManager = Main.getChannelManager();
				if (user != null && !user.isDummy()) {
					parentTitleBar.getChannels().stream()
						.filter(cvm -> cvm.getChannelId().equals(user.getUserId()))
						.findFirst()
						.ifPresentOrElse(
							channelManager::setActiveChannel,
							() -> channelManager.addChannel(user.getUserId())
						);
				}
			}).exceptionally(e -> {
				LOG.error("Failed to extract user from dragged url: {}", url, e);
				return null;
			});

		event.setDropCompleted(true);
		event.consume();
	}


	private void handleDrag(MouseEvent event) {
		Dragboard dragboard = startDragAndDrop(TransferMode.COPY_OR_MOVE);
		ClipboardContent content = new ClipboardContent();
		ChannelViewModel channelViewModel = getChannelViewModel();
		String channelName = channelViewModel.getChannelName();
		content.putHtml(channelName);
		content.putString(channelName);
		content.putUrl("https://www.twitch.tv/" + channelViewModel.getLoginName());

		SnapshotParameters snapshotParameters = new SnapshotParameters();
		snapshotParameters.setFill(Color.TRANSPARENT);
		content.putImage(snapshot(snapshotParameters, null));

		dragboard.setContent(content);
		event.consume();
	}


	private static ImageView createProfileImageView(String url) {
		ImageView imageView = new ImageView(createProfileImage(url));
		imageView.setTranslateX(-5);
		return imageView;
	}

	private static Image createProfileImage(String url) {
		return new Image(url, 24d, 24d, false, true, true);
	}

	public ReadOnlyObjectProperty<ChannelViewModel> channelViewModelProperty() {
		return channelViewModelPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyObjectWrapper<ChannelViewModel> channelViewModelPropertyInternal() {
		if (channelViewModelProperty == null) {
			channelViewModelProperty = new ReadOnlyObjectWrapper<>(this, "channelViewModel");
		}
		return channelViewModelProperty;
	}

	public ReadOnlyBooleanProperty liveProperty() {
		return livePropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyBooleanWrapper livePropertyInternal() {
		if (liveProperty == null) {
			liveProperty = new ReadOnlyBooleanWrapper(this, "live") {
				@Override
				protected void invalidated() {
					PlatformLogger logger = Logging.getInputLogger();
					if (logger.isLoggable(Level.FINER)) {
						logger.finer(this + " live=" + get());
					}
					pseudoClassStateChanged(LIVE_PSEUDOCLASS_STATE, get());
				}
			};
		}
		return liveProperty;
	}

	public ReadOnlyBooleanProperty selectedProperty() {
		return selectedPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyBooleanWrapper selectedPropertyInternal() {
		if (selectedProperty == null) {
			selectedProperty = new ReadOnlyBooleanWrapper(this, "selected") {
				@Override
				protected void invalidated() {
					PlatformLogger logger = Logging.getInputLogger();
					if (logger.isLoggable(Level.FINER)) {
						logger.finer(this + " selected=" + get());
					}
					pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, get());
				}
			};
		}
		return selectedProperty;
	}

	public ChannelViewModel getChannelViewModel() {
		return channelViewModelProperty().get();
	}

	public boolean isLive() {
		return liveProperty().get();
	}

	public boolean isSelected() {
		return selectedProperty().get();
	}
}
