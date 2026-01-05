package de.minetrain.minechat.utils;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.twitch4j.common.enums.SubscriptionPlan;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.viewmodel.ChannelViewModel;
import de.minetrain.minechat.main.ChannelActions;
import de.minetrain.minechat.twitch.TwitchHelper;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.ChannelStatistics;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;

public class OutboundChatMessage {
	private static final Logger logger = LoggerFactory.getLogger(OutboundChatMessage.class);
	private final String message;
	private final String messageRaw;
	private final String senderNamem;
	private final ChannelViewModel channelViewModel;
	private final String replyId;
	private long sendTime;

	public OutboundChatMessage(ChannelActions channel, ChannelViewModel channelViewModel, String senderNamem, String message, String replyId) {
		this.channelViewModel = channelViewModel;
		this.messageRaw = message;
		this.senderNamem = senderNamem;
		this.replyId = replyId;

//		https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns
		if(message.contains("{")){
			LocalDateTime localDateTime = LocalDateTime.now();
			Locale locale = new Locale(System.getProperty("user.language"), System.getProperty("user.country"));
			ChannelStatistics statistics = channel.getStatistics();

			String clipBoard = "";
			try {
				clipBoard = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
			} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
				logger.info("Can´t readout the System ClipBoard. It may be empty.");
			}

			if(message.contains("{VIEWER}") || message.contains("{UPTIME}") || message.contains("{GAME}") || message.contains("{TITLE}") || message.contains("{TAGS}")){
				String channelId = channel.getChannelId();
				List<TwitchUserObj> liveUseres = TwitchHelper.requestLiveUsers(TwitchApiCallType.ID, channelId).join().stream()
						.filter(user -> user.getUserId().equals(channelId)).toList();

				if(!liveUseres.isEmpty()){
					TwitchUserObj user = liveUseres.get(0);
					message = message
						.replace("{VIEWER}", String.valueOf(user.getStreamViewer()))
						.replace("{UPTIME}", user.getStreamLiveSince())
						.replace("{GAME}", user.getStreamGame())
						.replace("{TITLE}", user.getStreamTitle())
						.replace("{TAGS}", String.join(", ", user.getStreamTags()));
				}
			}

			message = message
					.replace("{TIME}", localDateTime.format(DateTimeFormatter.ofPattern(Settings.timeFormat, locale)))
					.replace("{DATE}", localDateTime.format(DateTimeFormatter.ofPattern(Settings.dateFormat, locale)))
					.replace("{DAY}", localDateTime.format(DateTimeFormatter.ofPattern(Settings.dayFormat, locale)))
					.replace("{STREAMER}", "@"+channel.getChannel().getLoginName())
					.replace("{MYSELF}", "@"+TwitchManager.ownerChannelName)
					.replace("{VIEWER}", "0")
					.replace("{UPTIME}", "0")
					.replace("{GAME}", "\"\"")
					.replace("{TITLE}", "\"\"")
					.replace("{TAGS}", "\"\"")
					.replace("{MY_MESSAGES}", ""+statistics.getTotalSelfMessages())
					.replace("{TOTAL_MESSAGES}", ""+statistics.getTotalMessages())
					.replace("{TOTAL_UNIQUE_MESSAGES}", ""+statistics.getTotalUniqueMessages())
					.replace("{TOTAL_SUBS}", ""+statistics.getTotalSubs())
					.replace("{TOTAL_RESUBS}", ""+statistics.getTotalResubs())
					.replace("{TOTAL_GIFTSUB}", ""+statistics.getTotalGiftedSubs(SubscriptionPlan.NONE))
					.replace("{TOTAL_GIFTSUB_1}", ""+statistics.getTotalGiftedSubs(SubscriptionPlan.TIER1))
					.replace("{TOTAL_GIFTSUB_2}", ""+statistics.getTotalGiftedSubs(SubscriptionPlan.TIER2))
					.replace("{TOTAL_GIFTSUB_3}", ""+statistics.getTotalGiftedSubs(SubscriptionPlan.TIER3))
					.replace("{TOTAL_NEWSUB}", ""+statistics.getTotalNewSubs())
					.replace("{TOTAL_BITS}", ""+statistics.getTotalBits())
					.replace("{ClipBoard}", clipBoard)
					.replace("{CLIP_BOARD}", clipBoard)
					.replace("{Clip}", clipBoard)
					.replace("{CLIP}", clipBoard);
			this.message = message;
		}else{
			this.message = message;
		}
	}


	public String getMessage() {
		return message;
	}

	public String getSenderName() {
		return senderNamem;
	}

	public ChannelViewModel getChannelViewModel() {
		return channelViewModel;
	}

	public String getMessageRaw() {
		return messageRaw;
	}

	public String getReplyId() {
		return replyId;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}
}
