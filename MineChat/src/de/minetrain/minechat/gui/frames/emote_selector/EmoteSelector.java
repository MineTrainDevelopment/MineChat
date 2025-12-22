package de.minetrain.minechat.gui.frames.emote_selector;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.gui.emotes.EmoteLegacy;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.main.Main;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class EmoteSelector extends MineDialog<Emote> {

	private static final Logger LOG = LoggerFactory.getLogger(EmoteSelector.class);

	private final ScrollPane emoteBatchsPane;
	private final VBox batches = new VBox(5);
	private EmoteSelectEvent selectEvent;
	public EmoteSelectorBatche favoriteEmoteBatche;
	private boolean closeOnSelect = true;

	public EmoteSelector(EmoteSelectEvent selectEvent) {
		this(false, selectEvent);
	}

	public EmoteSelector(boolean showAndWait, EmoteSelectEvent selectEvent) {
		setTitle("Emote Selector");
		setWidth(400);
		setHeight(400);
		this.selectEvent = selectEvent;

		BorderPane layout = new BorderPane();

		VBox channelButtons = new VBox(5);
		channelButtons.setAlignment(Pos.CENTER_LEFT);

//		ScrollPane test = new ScrollPane(new EmoteSelectorBatche(ChannelManager.getChannel("99351845")));
		emoteBatchsPane = new ScrollPane(batches);
		emoteBatchsPane.setFocusTraversable(false);
		emoteBatchsPane.setFitToHeight(true);
		emoteBatchsPane.setFitToWidth(true);
//        test.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		emoteBatchsPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		ObjectBinding<Node> firstVisibleBatche = createFirstVisibleNodeBinding();

		favoriteEmoteBatche = new EmoteSelectorBatche("Favorite", List.of());
		batches.getChildren().add(favoriteEmoteBatche);

		Main.getChannelManager().channelsProperty().get().forEach(channel -> {
			List<Emote> twitchEmotes = EclipseStoreKeeper.root().emotes().getEmotesByChannelId(channel.getChannelId());
			List<Emote> bttvEmotes = EclipseStoreKeeper.root().emotes().getBttvEmotesByChannelId(channel.getChannelId());
			if (!twitchEmotes.isEmpty() || !bttvEmotes.isEmpty()) {
				EmoteSelectorChannelButton channelButton = new EmoteSelectorChannelButton(channel);
				channelButtons.getChildren().add(channelButton);

				List<Emote> allEmotes = new ArrayList<>(twitchEmotes.size() + bttvEmotes.size());
				allEmotes.addAll(twitchEmotes);
				allEmotes.addAll(bttvEmotes);
				EmoteSelectorBatche selectorBatche = new EmoteSelectorBatche(channel.getChannelName(), allEmotes);
				batches.getChildren().add(selectorBatche);

				channelButton.selectedProperty().bind(firstVisibleBatche.isEqualTo(selectorBatche));
				channelButton.setOnAction(_ -> scrollToEmoteBatch(selectorBatche));
			}
		});
		batches.getChildren().add(new EmoteSelectorBatche("Default", List.of()));

		ScrollPane tabPane = new ScrollPane(channelButtons);
		tabPane.setStyle("-fx-border-width: 10; -fx-border-color: transparent;");
		tabPane.setFocusTraversable(false);
		tabPane.setFitToHeight(true);
		tabPane.setFitToWidth(true);
		tabPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		tabPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		layout.setRight(tabPane);
		layout.setCenter(emoteBatchsPane);
		getDialogPane().setContent(layout);
	}

	private ObjectBinding<Node> createFirstVisibleNodeBinding() {
		return Bindings.createObjectBinding(() -> {
			double scrollHeight = emoteBatchsPane.getViewportBounds().getHeight();
			double contentHeight = batches.getHeight();
			double scrollTop = emoteBatchsPane.getVvalue() * (contentHeight - scrollHeight);

			return batches.getChildren().stream().filter(node -> {
				double nodeY = node.getLayoutY();
				double nodeHeight = node.getLayoutBounds().getHeight();
				return nodeY < scrollTop + scrollHeight && nodeY + nodeHeight > scrollTop;
			}).findFirst().orElse(null);
		}, emoteBatchsPane.vvalueProperty(), emoteBatchsPane.viewportBoundsProperty(), batches.heightProperty());
	}

	public void scrollToEmoteBatch(EmoteSelectorBatche batch) {
		double targetValue = batch.getLayoutY()
				* (1 / (batches.getHeight() - emoteBatchsPane.getViewportBounds().getHeight()));

		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(400),
				new KeyValue(emoteBatchsPane.vvalueProperty(), targetValue, Interpolator.EASE_BOTH)));
		timeline.play();
	}

	public void setCloseOnSelect(boolean closeOnSelect) {
		this.closeOnSelect = closeOnSelect;
	}

	public void fireSelectEvent(EmoteLegacy emote) {
		if (selectEvent != null) {
			selectEvent.onSelect(emote);
//			if (closeOnSelect) {
//				closeStage();
//			}
		}
	}

	public void setOnSelect(EmoteSelectEvent event) {
		selectEvent = event;
	}

	public interface EmoteSelectEvent {
		void onSelect(EmoteLegacy emote);
	}

	@Override
	protected Emote yieldResultOnSuccess() {
		return null; // TODO Return selected emote if needed
	}
}
