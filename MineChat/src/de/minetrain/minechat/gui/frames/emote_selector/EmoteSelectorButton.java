package de.minetrain.minechat.gui.frames.emote_selector;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.gui.emotes.EmoteType;
import de.minetrain.minechat.main.Main;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class EmoteSelectorButton extends Button {

	private static final PseudoClass TIER2_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("tier2");
	private static final PseudoClass TIER3_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("tier3");
	private static final PseudoClass BITS_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("bits");
	private static final PseudoClass FOLLOW_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("follow");
	private static final double EMOTE_SIZE = 24D;
	private static final ExecutorService IMAGE_LOADER = Executors.newVirtualThreadPerTaskExecutor();

	private ObjectProperty<Emote> emoteProperty;

	public EmoteSelectorButton(Emote emote) {
		this();
		setEmote(emote);
	}

	public EmoteSelectorButton() {
		setId("emote-button");

		ImageView imageView = new ImageView();
		emoteProperty().addListener((_, oldEmote, newEmote) -> {
			if (oldEmote != null) {
				setPseudoClassForEmoteType(oldEmote.getEmoteType(), false);
				imageView.setImage(null);
			}
			if (newEmote != null) {
				setPseudoClassForEmoteType(newEmote.getEmoteType(), true);
				CompletableFuture.runAsync(() -> {
					Image image = Main.getEmoteManager().getEmoteImage1x(newEmote.getEmoteId(), newEmote.isAnimated());
					Platform.runLater(() -> imageView.setImage(image));
				}, IMAGE_LOADER);
			}
		});
		tooltipProperty().bind(emoteProperty.map(emote -> new Tooltip(emote.getName())));

		imageView.setFitHeight(EMOTE_SIZE);
		imageView.setFitWidth(EMOTE_SIZE);
		imageView.setPreserveRatio(true);
		StackPane wrapper = new StackPane();
		wrapper.setMinSize(EMOTE_SIZE, EMOTE_SIZE);
		wrapper.getChildren().add(imageView);

		setGraphic(wrapper);
	}

	public ObjectProperty<Emote> emoteProperty() {
		if (emoteProperty == null) {
			emoteProperty = new SimpleObjectProperty<>(this, "emote");
		}
		return emoteProperty;
	}

	public Emote getEmote() {
		return emoteProperty().get();
	}

	public void setEmote(Emote emote) {
		emoteProperty().set(emote);
	}

	public void setPseudoClassForEmoteType(EmoteType type, boolean active) {
		switch (type) {
			case SUB_2 -> pseudoClassStateChanged(TIER2_PSEUDOCLASS_STATE, active);
			case SUB_3 -> pseudoClassStateChanged(TIER3_PSEUDOCLASS_STATE, active);
			case BIT -> pseudoClassStateChanged(BITS_PSEUDOCLASS_STATE, active);
			case FOLLOW -> pseudoClassStateChanged(FOLLOW_PSEUDOCLASS_STATE, active);
			default -> { /* No pseudo class for this type */ }
		}
	}
}
