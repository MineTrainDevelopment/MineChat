package de.minetrain.minechat.gui.viewmodel;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.data.objectdata.Macro;
import de.minetrain.minechat.features.macros.MacroType;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MacroViewModel {
	private static final Random random = new Random();

	private ReadOnlyObjectWrapper<UUID> uuidProperty;
	private ReadOnlyObjectWrapper<ChannelViewModel> channelProperty;
	private ReadOnlyObjectWrapper<MacroType> macroTypeProperty;
	private ReadOnlyIntegerWrapper indexProperty;
	private ObjectProperty<Emote> emoteProperty;
	private StringProperty titleProperty;
	private ObjectProperty<String[]> outputProperty;

	private int previousRandom = 0;

	public static MacroViewModel of(Macro macro, ChannelViewModel channel, Emote emote) {
		MacroViewModel macroViewModel = new MacroViewModel(
			macro.getUuid(),
			channel,
			macro.getMacroType(),
			macro.getIndex()
		);
		macroViewModel.setEmote(emote);
		macroViewModel.setTitle(macro.getTitle());
		macroViewModel.setOutput(macro.getOutput());
		return macroViewModel;
	}

	public MacroViewModel(ChannelViewModel channel, int index, MacroType macroType) {
		this(UUID.randomUUID(), channel, macroType, index);
	}

	public MacroViewModel(UUID uuid, ChannelViewModel channel, MacroType macroType, int index) {
		uuidPropertyInternal().set(uuid);
		channelPropertyInternal().set(channel);
		macroTypePropertyInternal().set(macroType);
		indexPropertyInternal().set(index);
	}

	protected ReadOnlyObjectWrapper<UUID> uuidPropertyInternal() {
		if (uuidProperty == null) {
			uuidProperty = new ReadOnlyObjectWrapper<>(this, "uuid");
		}
		return uuidProperty;
	}

	public ReadOnlyObjectProperty<UUID> uuidProperty() {
		return uuidPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyObjectWrapper<ChannelViewModel> channelPropertyInternal() {
		if (channelProperty == null) {
			channelProperty = new ReadOnlyObjectWrapper<>(this, "channel");
		}
		return channelProperty;
	}

	public ReadOnlyObjectProperty<ChannelViewModel> channelProperty() {
		return channelPropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyObjectWrapper<MacroType> macroTypePropertyInternal() {
		if (macroTypeProperty == null) {
			macroTypeProperty = new ReadOnlyObjectWrapper<>(this, "macroType");
		}
		return macroTypeProperty;
	}

	public ReadOnlyObjectProperty<MacroType> macroTypeProperty() {
		return macroTypePropertyInternal().getReadOnlyProperty();
	}

	protected ReadOnlyIntegerWrapper indexPropertyInternal() {
		if (indexProperty == null) {
			indexProperty = new ReadOnlyIntegerWrapper(this, "index");
		}
		return indexProperty;
	}

	public ReadOnlyIntegerProperty indexProperty() {
		return indexPropertyInternal().getReadOnlyProperty();
	}

	public ObjectProperty<Emote> emoteProperty() {
		if (emoteProperty == null) {
			emoteProperty = new SimpleObjectProperty<>(this, "emote");
		}
		return emoteProperty;
	}

	public StringProperty titleProperty() {
		if (titleProperty == null) {
			titleProperty = new SimpleStringProperty(this, "title");
		}
		return titleProperty;
	}

	public ObjectProperty<String[]> outputProperty() {
		if (outputProperty == null) {
			outputProperty = new SimpleObjectProperty<>(this, "output");
		}
		return outputProperty;
	}

	public UUID getUuid() {
		return uuidProperty().get();
	}

	public ChannelViewModel getChannel() {
		return channelProperty().get();
	}

	public MacroType getMacroType() {
		return macroTypeProperty().get();
	}

	public int getIndex() {
		return indexProperty().get();
	}

	public Emote getEmote() {
		return emoteProperty().get();
	}

	public String getTitle() {
		return titleProperty().get();
	}

	public String[] getOutput() {
		return outputProperty().get();
	}

	public void setEmote(Emote emote) {
		emoteProperty().set(emote);
	}

	public void setTitle(String title) {
		titleProperty().set(title);
	}

	public void setOutput(String[] output) {
		outputProperty().set(output);
	}

	/**
	 * @return a random output from the {@link MacroViewModel#getAllOutputs()}
	 */
	public String getRandomOutput() {
		String[] output = getOutput();
		int newRandom = 0;
		while(output.length > 1 && (newRandom == previousRandom)){
			newRandom = random.nextInt(output.length);
		}

		previousRandom = newRandom;
		return output[newRandom];
	}

	public String getTrulyRandomOutput() {
		String[] output = getOutput();
		return output[random.nextInt(output.length)];
	}

	public String getAllOutputsAsString() {
		return String.join(" \n", getOutput());
	}

	public void apply(Macro macro) {
		setTitle(macro.getTitle());
		setOutput(macro.getOutput());
		if (!Objects.equals(macro.getEmoteId(), getEmote() != null ? getEmote().getEmoteId() : null)) {
			setEmote(EmoteManager.getEmoteById(macro.getEmoteId()));
		}
	}

	public Macro toMacro() {
		Emote emote = getEmote();
		return new Macro(getUuid(), getChannel().getChannelId(), getMacroType(), getIndex(), getTitle(), emote != null ? emote.getEmoteId() : null, getOutput());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getUuid());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MacroViewModel other = (MacroViewModel) obj;
		return Objects.equals(getUuid(), other.getUuid());
	}
}
