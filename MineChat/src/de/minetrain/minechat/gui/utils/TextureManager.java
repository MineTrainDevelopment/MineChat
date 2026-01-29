package de.minetrain.minechat.gui.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.fmsware.gif.GifDecoder;
import com.fmsware.gif.GifEncoder;
import com.github.twitch4j.helix.domain.ChatBadge;
import com.github.twitch4j.helix.domain.ChatBadgeSet;
import com.github.twitch4j.helix.domain.Emote.Format;
import com.github.twitch4j.helix.domain.Emote.Scale;
import com.github.twitch4j.helix.domain.Emote.Theme;
import com.github.twitch4j.helix.domain.EmoteList;
import com.google.gson.Gson;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.Badge;
import de.minetrain.minechat.data.objectdata.BadgeId;
import de.minetrain.minechat.data.objectdata.Badges;
import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.data.objectdata.Emotes;
import de.minetrain.minechat.gui.emotes.EmoteType;
import de.minetrain.minechat.twitch.TwitchHelper;
import de.minetrain.minechat.twitch.obj.BttvEmote;
import de.minetrain.minechat.twitch.obj.BttvUser;

public final class TextureManager {

	public static final String BTTV_EMOTE_URL = "https://cdn.betterttv.net/emote/{}/{}x"; // id, scale (1, 2, 3)

	private static final Logger LOG = LoggerFactory.getLogger(TextureManager.class);

	private TextureManager() {
		// Private constructor to prevent instantiation
	}

	public static void downloadPublicData() {
		boolean publicBadgesMissing = EclipseStoreKeeper.root().badges().getBadgesByChannelId(TwitchHelper.CHANNEL_ID_PUBLIC).isEmpty();
		downloadDefaultBadges(publicBadgesMissing);

		boolean publicEmotesMissing = EclipseStoreKeeper.root().emotes().getEmotesByChannelId(TwitchHelper.CHANNEL_ID_PUBLIC).isEmpty();
		downloadDefaultEmotes(publicEmotesMissing);
	}

	public static CompletableFuture<Void> downloadMissingChannelData(String channelId) {
		return CompletableFuture.allOf(
				downloadChannelBadges(channelId, false),
				downloadChannelEmotes(channelId, false),
				downloadBttvEmotes(channelId, false));
	}

	public static CompletableFuture<Void> downloadChannelBadges(String userId, boolean forceUpdate){
		return TwitchHelper.requestChannelBadges(userId)
			.thenAcceptAsync(bagdes -> downloadBadges(bagdes, userId, forceUpdate))
			.handle((_, e) -> {
				if (e != null) {
					LOG.error("Error while downloading channel badges for user ID: {}", userId, e);
				}
				return null;
			});
	}

	public static CompletableFuture<Void> downloadChannelEmotes(String userId, boolean forceUpdate) {
		return TwitchHelper.requestChannelEmotes(userId)
			.thenAcceptAsync(emotes -> downloadEmotes(emotes, userId, forceUpdate))
			.handle((_, e) -> {
				if (e != null) {
					LOG.error("Error while downloading channel emotes for user ID: {}", userId, e);
				}
				return null;
		});
	}

	public static CompletableFuture<Void> downloadBttvEmotes(String userId, boolean forceUpdate) {
		return CompletableFuture.supplyAsync(() -> {
			try (HttpClient client = HttpClient.newHttpClient()) {
				HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.betterttv.net/3/cached/users/twitch/" + userId))
					.header("accept", "application/json")
					.header("user-agent", "MineChat Client")
					.GET()
					.build();
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				if (response.statusCode() == 404) {
					LOG.warn("No BTTV emotes found for user ID: {}", userId);
					return List.<BttvEmote>of();
				}
				if (response.statusCode() != 200) {
					throw new IllegalStateException("Failed to fetch BTTV emotes, status code: " + response.statusCode());
				}
				Gson gson = new Gson();
				BttvUser user = gson.fromJson(response.body(), BttvUser.class);
				if (user.getMessage() != null) {
					throw new IllegalStateException("Failed to fetch BTTV emotes: " + user.getMessage());
				}
				List<BttvEmote> emotes = new ArrayList<>(user.getChannelEmotes().size() + user.getSharedEmotes().size());
				emotes.addAll(user.getChannelEmotes());
				emotes.addAll(user.getSharedEmotes());
				return emotes;
			} catch (IOException e) {
				throw new CompletionException(e.getMessage(), e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new CompletionException(e.getMessage(), e);
			}
		}).thenAcceptAsync(emotes -> downloadBttvEmotes(emotes, userId, forceUpdate))
			.handle((_, e) -> {
				if (e != null) {
					LOG.error("Error while downloading channel emotes for user ID: {}", userId, e);
				}
				return null;
			});
	}

	private static void downloadDefaultBadges(boolean forceUpdate) {
		TwitchHelper.requestGlobalBadges()
			.thenAcceptAsync(bagdes -> downloadBadges(bagdes, TwitchHelper.CHANNEL_ID_PUBLIC, forceUpdate))
			.handle((_, e) -> {
				if (e != null) {
					LOG.error("Error while downloading default badges", e);
				}
				return null;
			});
	}


	private static void downloadDefaultEmotes(boolean forceUpdate) {
		TwitchHelper.requestGlobalEmotes()
			.thenAcceptAsync(emotes -> downloadEmotes(emotes, TwitchHelper.CHANNEL_ID_PUBLIC, forceUpdate))
			.handle((_, e) -> {
				if (e != null) {
					LOG.error("Error while downloading default badges", e);
				}
				return null;
			});
	}


	private static void downloadBadges(List<ChatBadgeSet> bagdes, String channelId, boolean forceUpdate) {
		Badges badgeStore = EclipseStoreKeeper.root().badges();
		for (ChatBadgeSet badgeSet : bagdes) {
			for (ChatBadge twitchBadge : badgeSet.getVersions()) {
				BadgeId badgeId = new BadgeId(badgeSet.getSetId(), twitchBadge.getId());
				if (!forceUpdate && badgeStore.of(channelId, badgeId) != null) {
					continue;
				}
				LOG.info("{} - Downloading badge: {}", channelId, twitchBadge);
				try {
					byte[] image1x = downloadImageData(twitchBadge.getSmallImageUrl());
					byte[] image2x = downloadImageData(twitchBadge.getMediumImageUrl());
					byte[] image3x = downloadImageData(twitchBadge.getLargeImageUrl());

					Badge badge = new Badge(badgeId, channelId,
							twitchBadge.getTitle(), twitchBadge.getDescription(), twitchBadge.getClickAction(),
							twitchBadge.getClickUrl(), image1x, image2x, image3x);
					badgeStore.addBadge(badge);
				} catch (IOException e) {
					LOG.error("Error downloading badge images for badge '{}' in channel ID: {}", twitchBadge.getTitle(), channelId, e);
				}
			}
		}
	}

	private static void downloadEmotes(EmoteList emotes, String channelId, boolean forceUpdate) {
		Emotes emoteStore = EclipseStoreKeeper.root().emotes();
		for (var twitchEmote : emotes.getEmotes()) {
			if (!forceUpdate && emoteStore.ofId(twitchEmote.getId()) != null) {
				continue;
			}
			LOG.info("{} - Downloading emote: {}", channelId, twitchEmote);
			EmoteType type = twitchEmote.getEmoteType() == null || twitchEmote.getTier() == null ? null : EmoteType.get(twitchEmote.getEmoteType(), twitchEmote.getTier().ordinalName());
			boolean animated = twitchEmote.getFormat().contains(Format.ANIMATED);
			try {
				byte[] image1x = downloadImageData(emotes.getPopulatedTemplateUrl(twitchEmote.getId(), Format.DEFAULT, Theme.DARK, Scale.SMALL));
				byte[] image2x = downloadImageData(emotes.getPopulatedTemplateUrl(twitchEmote.getId(), Format.DEFAULT, Theme.DARK, Scale.MEDIUM));
				byte[] image3x = downloadImageData(emotes.getPopulatedTemplateUrl(twitchEmote.getId(), Format.DEFAULT, Theme.DARK, Scale.LARGE));

				if (animated) {
					image1x = reformatGif(image1x);
					image2x = reformatGif(image2x);
					image3x = reformatGif(image3x);
				}

				Emote emote = new Emote(twitchEmote.getId(), channelId, twitchEmote.getName(), type, false, animated,
						image1x, image2x, image3x);
				emoteStore.addEmote(emote);
			} catch (IOException e) {
				LOG.error("Error downloading emote images for emote '{}' in channel ID: {}", twitchEmote.getName(), channelId, e);
			}
		}
	}

	private static void downloadBttvEmotes(List<BttvEmote> emotes, String channelId, boolean forceUpdate) {
		Emotes emoteStore = EclipseStoreKeeper.root().emotes();
		for (var bttvEmote : emotes) {
			if (!forceUpdate && emoteStore.ofId(bttvEmote.getId()) != null) {
				continue;
			}
			LOG.info("{} - Downloading bttv emote: {}", channelId, bttvEmote);
			try {
				byte[][] images = new byte[3][];
				for (int scale = 1; scale <= 3; scale++) {
					String imageUrl = MessageFormatter.basicArrayFormat(BTTV_EMOTE_URL, new Object[] { bttvEmote.getId(), scale });
					images[scale - 1] = bttvEmote.isAnimated() ? reformatGif(downloadImageData(imageUrl)) : downloadImageData(imageUrl);
				}

				Emote emote = new Emote(bttvEmote.getId(), channelId, bttvEmote.getCode(), EmoteType.BTTV, false,
						bttvEmote.isAnimated(), images[0], images[1], images[2]);
				emoteStore.addEmote(emote);
			} catch (IOException e) {
				LOG.error("Error downloading emote images for emote '{}' in channel ID: {}", bttvEmote.getCode(), channelId, e);
			}
		}
	}

	private static byte[] downloadImageData(String url) throws IOException {
		LOG.debug("Downloading image from URL: {}", url);
		try (InputStream in = URI.create(url).toURL().openStream()) {
			byte[] imageData = in.readAllBytes();
			LOG.debug("Image downloaded");
			return imageData;
		}
	}

	/// Re-encodes the GIF to ensure infinite looping and proper transparency handling.
	///
	/// @param imageData The original GIF image data.
	/// @return The reformatted GIF image data.
	private static byte[] reformatGif(byte[] imageData) {
		GifDecoder decoder = new GifDecoder();
		decoder.read(new ByteArrayInputStream(imageData));

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		GifEncoder encoder = new GifEncoder();
		encoder.setRepeat(true);
		encoder.setTransparent();
		encoder.setQuality(1);
		encoder.start(outputStream);

		encoder.setSize(decoder.getFrameSize());
		for (int i = 0; i < decoder.getFrameCount(); i++) {
			encoder.addFrame(decoder.getFrame(i), decoder.getDelay(i));
		}

		encoder.finish();
		return outputStream.toByteArray();
	}
}
