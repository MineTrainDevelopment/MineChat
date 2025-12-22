package de.minetrain.minechat.gui.emotes;

import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.main.Main;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class EmoteSelectorButton extends Button {

	private static final double EMOTE_SIZE = 24D;

	public EmoteSelectorButton(Emote emote) {
		setId("emote-button");

		ImageView imageView = new ImageView(Main.getEmoteManager().getEmoteImage1x(emote.getEmoteId(), emote.isAnimated()));
		imageView.setFitHeight(EMOTE_SIZE);
		imageView.setFitWidth(EMOTE_SIZE);
		imageView.setPreserveRatio(true);
		StackPane wrapper = new StackPane();
		wrapper.setMinSize(EMOTE_SIZE, EMOTE_SIZE);
		wrapper.getChildren().add(imageView);

		setGraphic(wrapper);
		setTooltip(new Tooltip(emote.getName()));

		switch (emote.getEmoteType()) {
			case SUB_2 -> pseudoClassStateChanged(PseudoClass.getPseudoClass("tier2"), true);
			case SUB_3 -> pseudoClassStateChanged(PseudoClass.getPseudoClass("tier3"), true);
			case BIT -> pseudoClassStateChanged(PseudoClass.getPseudoClass("bits"), true);
			case FOLLOW -> pseudoClassStateChanged(PseudoClass.getPseudoClass("follow"), true);
			default ->  { /* Not special css class */ }
		}
	}
}
