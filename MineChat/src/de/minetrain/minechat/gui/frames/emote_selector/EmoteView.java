package de.minetrain.minechat.gui.frames.emote_selector;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.main.Main;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class EmoteView extends StackPane {

	private static final ExecutorService IMAGE_LOADER = Executors.newVirtualThreadPerTaskExecutor();
	private static final double EMOTE_SIZE = 24D;

	private ObjectProperty<Emote> emoteProperty;

	public EmoteView() {
		ImageView imageView = new ImageView();
		imageView.setFitHeight(EMOTE_SIZE);
		imageView.setFitWidth(EMOTE_SIZE);
		imageView.setPreserveRatio(true);
		emoteProperty().addListener((_, oldEmote, newEmote) -> {
			if (oldEmote != null) {
				imageView.setImage(null);
			}
			if (newEmote != null) {
				CompletableFuture.runAsync(() -> {
					Image image = Main.getEmoteManager().getEmoteImage1x(newEmote.getEmoteId(), newEmote.isAnimated());
					Platform.runLater(() -> imageView.setImage(image));
				}, IMAGE_LOADER);
			}
		});
		setMinSize(EMOTE_SIZE, EMOTE_SIZE);
		getChildren().add(imageView);
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
}