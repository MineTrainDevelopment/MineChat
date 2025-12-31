package de.minetrain.minechat.gui.frames.emote_selector;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.minetrain.minechat.gui.viewmodel.IEmoteViewModel;
import de.minetrain.minechat.main.Main;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class EmoteView extends StackPane {

	private static final ExecutorService IMAGE_LOADER = Executors.newVirtualThreadPerTaskExecutor();

	private ObjectProperty<IEmoteViewModel> emoteProperty;

	public EmoteView() {
		this(false);
	}

	public EmoteView(boolean adjustBaseline) {
		getStyleClass().add("emote-view");
		ImageView imageView = new ImageView() {

			@Override
			public double getBaselineOffset() {
				return adjustBaseline && getImage() != null ? getImage().getHeight() * 0.75 : super.getBaselineOffset();
			}
		};
		imageView.fitHeightProperty().bind(minHeightProperty());
		imageView.fitWidthProperty().bind(minWidthProperty());
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
		getChildren().add(imageView);
	}

	public ObjectProperty<IEmoteViewModel> emoteProperty() {
		if (emoteProperty == null) {
			emoteProperty = new SimpleObjectProperty<>(this, "emote");
		}
		return emoteProperty;
	}

	public IEmoteViewModel getEmote() {
		return emoteProperty().get();
	}

	public void setEmote(IEmoteViewModel emote) {
		emoteProperty().set(emote);
	}
}