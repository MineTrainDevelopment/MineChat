package de.minetrain.minechat.gui.obj.buttons;

import de.minetrain.minechat.features.macros.MacroType;
import de.minetrain.minechat.features.macros.MacroViewModel;
import de.minetrain.minechat.gui.frames.emote_selector.EmoteView;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.MessageManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;

public class MacroButton extends Button {

	private static final PseudoClass EMOTE_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("emote");

	private ObjectProperty<MacroViewModel> macroProperty;
	private ReadOnlyBooleanWrapper emoteProperty;

	public MacroButton() {
		getStyleClass().add("macro-button");
		setFocusTraversable(false);
		textProperty().bind(macroProperty().flatMap(MacroViewModel::titleProperty));
		emotePropertyInternal().bind(macroProperty().flatMap(MacroViewModel::macroTypeProperty).map(type -> type == MacroType.EMOTE));
		EmoteView emoteView = new EmoteView();
		emoteView.emoteProperty().bind(macroProperty().flatMap(MacroViewModel::emoteProperty));
		setGraphic(emoteView);

		setOnAction(_ -> {
			MacroViewModel macro = getMacro();
			if (macro != null && macro.getOutput() != null) {
				MessageManager.sendMessage(macro);
			}
		});
		setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.SECONDARY) {
				Main.getChannelManager().editMacro(getMacro());
			}
		});
	}

	public ObjectProperty<MacroViewModel> macroProperty() {
		if (macroProperty == null) {
			macroProperty = new SimpleObjectProperty<>(this, "macro");
		}
		return macroProperty;
	}

	public void setMacro(MacroViewModel macro) {
		macroProperty().set(macro);
	}

	public MacroViewModel getMacro() {
		return macroProperty().get();
	}

	public ReadOnlyBooleanProperty emoteProperty() {
		return emotePropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyBooleanWrapper emotePropertyInternal() {
		if (emoteProperty == null) {
			emoteProperty = new ReadOnlyBooleanWrapper(this, "emote") {
				@Override
				protected void invalidated() {
					pseudoClassStateChanged(EMOTE_PSEUDOCLASS_STATE, get());
				}
			};
		}
		return emoteProperty;
	}
}
