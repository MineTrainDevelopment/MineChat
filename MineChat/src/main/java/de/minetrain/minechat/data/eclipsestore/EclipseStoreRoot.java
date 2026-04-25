package de.minetrain.minechat.data.eclipsestore;

import de.minetrain.minechat.data.objectdata.AutoReplies;
import de.minetrain.minechat.data.objectdata.Badges;
import de.minetrain.minechat.data.objectdata.Channels;
import de.minetrain.minechat.data.objectdata.CountVariables;
import de.minetrain.minechat.data.objectdata.Credentials;
import de.minetrain.minechat.data.objectdata.Emotes;
import de.minetrain.minechat.data.objectdata.Macros;
import de.minetrain.minechat.data.objectdata.Messages;
import de.minetrain.minechat.data.objectdata.UserSettings;

public class EclipseStoreRoot {

	private Channels channels;
	private Emotes emotes;
	private Credentials credentials;
	private Messages messages;
	private Badges badges;
	private UserSettings userSettings;
	private Macros macros;
	private AutoReplies autoReplies;
	private CountVariables countVariables;

	public Channels channels() {
		if (channels == null) {
			channels = new Channels();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return channels;
	}

	public Emotes emotes() {
		if (emotes == null) {
			emotes = new Emotes();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return emotes;
	}

	public Credentials credentials() {
		if (credentials == null) {
			credentials = new Credentials();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return credentials;
	}

	public Messages messages() {
		if (messages == null) {
			messages = new Messages();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return messages;
	}

	public Badges badges() {
		if (badges == null) {
			badges = new Badges();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return badges;
	}

	public UserSettings userSettings() {
		if (userSettings == null) {
			userSettings = new UserSettings();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return userSettings;
	}

	public Macros macros() {
		if (macros == null) {
			macros = new Macros();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return macros;
	}

	public AutoReplies autoReplies() {
		if (autoReplies == null) {
			autoReplies = new AutoReplies();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return autoReplies;
	}

	public CountVariables countVariables() {
		if (countVariables == null) {
			countVariables = new CountVariables();
			EclipseStoreKeeper.storeManager().store(this);
		}
		return countVariables;
	}
}
