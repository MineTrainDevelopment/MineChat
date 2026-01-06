package de.minetrain.minechat.features.autoreply;

import java.time.Instant;
import java.util.Objects;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.config.enums.AutoReplyState;
import de.minetrain.minechat.gui.viewmodel.AutoReplyViewModel;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.MessageManager;

public class AutoReplyManager {

	public void handleMessage(String channelId, String messageId, String message, Instant timestamp) {
		if (Settings.autoReplyState == AutoReplyState.CURRENT_TAB && !Objects.equals(Main.getChannelManager().getActiveChanneldId(), channelId)) {
			return;
		}

		ChannelViewModel channelViewModel = Main.getChannelManager().getChannelViewModel(channelId);
		channelViewModel.getAutoReplies().stream()
			.filter(AutoReplyViewModel::isEnabled)
			.filter(arvm -> arvm.getPattern().matcher(message).matches())
			.forEach(arvm -> tryFire(arvm, messageId, timestamp));
	}

	private void tryFire(AutoReplyViewModel autoReply, String messageId, Instant timestamp) {
		Instant lastFired = autoReply.getLastFired();
		if(lastFired != null && lastFired.plusSeconds(autoReply.getDelay()).isAfter(timestamp)) {
			return;
		}

		autoReply.getLastMessageHits().removeIf(time -> time.plusSeconds(60).isBefore(timestamp));
		autoReply.getLastMessageHits().add(timestamp);
		if (autoReply.getLastMessageHits().size() < autoReply.getMessagesPerMinute()) {
			return;
		}

		autoReply.setLastFired(timestamp);
		autoReply.getLastMessageHits().clear();

		String channelId = autoReply.getChannel().getChannelId();
		ChannelViewModel cvm = Main.getChannelManager().getChannelViewModel(channelId);
		MessageManager.sendMessage(cvm, autoReply.getRandomOutput(), autoReply.isReply() ?  messageId : null);
	}
}
