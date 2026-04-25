package de.minetrain.minechat.gui.frames.emote_selector;

import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.gui.emotes.EmoteType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

public class EmoteSelectorButton extends Button {

	private static final PseudoClass TIER2_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("tier2");
	private static final PseudoClass TIER3_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("tier3");
	private static final PseudoClass BITS_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("bits");
	private static final PseudoClass FOLLOW_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("follow");

	private ObjectProperty<Emote> emoteProperty;

	public EmoteSelectorButton(Emote emote) {
		this();
		setEmote(emote);
	}

	public EmoteSelectorButton() {
		getStyleClass().add("emote-button");

		EmoteView emoteView = new EmoteView();
		emoteView.emoteProperty().bind(emoteProperty());

		emoteProperty().addListener((_, oldEmote, newEmote) -> {
			if (oldEmote != null) {
				setPseudoClassForEmoteType(oldEmote.getEmoteType(), false);
			}
			if (newEmote != null) {
				setPseudoClassForEmoteType(newEmote.getEmoteType(), true);
			}
		});
		tooltipProperty().bind(emoteProperty().map(emote -> new Tooltip(emote.getName())));

		setGraphic(emoteView);
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
