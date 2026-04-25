package de.minetrain.minechat.gui.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.twitch4j.helix.domain.ChatBadge;
import com.github.twitch4j.helix.domain.ChatBadgeSet;
import com.github.twitch4j.helix.domain.Emote.Format;
import com.github.twitch4j.helix.domain.Emote.Scale;
import com.github.twitch4j.helix.domain.Emote.Theme;
import com.github.twitch4j.helix.domain.EmoteList;

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
import javafx.scene.image.Image;

public final class TextureManager {

	private static final byte[] NETSCAPE2_0 = "NETSCAPE2.0".getBytes();

	public static final String BTTV_EMOTE_URL = "https://cdn.betterttv.net/emote/{}/{}x"; // id, scale (1, 2, 3)

	private static final Logger LOG = LoggerFactory.getLogger(TextureManager.class);

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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
				HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
				if (response.statusCode() == 404) {
					LOG.warn("No BTTV emotes found for user ID: {}", userId);
					return List.<BttvEmote>of();
				}
				if (response.statusCode() != 200) {
					throw new IllegalStateException("Failed to fetch BTTV emotes, status code: " + response.statusCode());
				}
				BttvUser user = OBJECT_MAPPER.readValue(response.body(), BttvUser.class);
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
					image1x = validate(image1x);
					image2x = validate(image2x);
					image3x = validate(image3x);
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
					images[scale - 1] = downloadImageData(imageUrl);
					if (bttvEmote.isAnimated()) {
						images[scale - 1] = validate(images[scale - 1]);
					}
				}

				Emote emote = new Emote(bttvEmote.getId(), channelId, bttvEmote.getCode(), EmoteType.BTTV, false,
						bttvEmote.isAnimated(), images[0], images[1], images[2]);
				emoteStore.addEmote(emote);
			} catch (IOException e) {
				LOG.error("Error downloading emote images for emote '{}' in channel ID: {}", bttvEmote.getCode(), channelId, e);
			}
		}
	}

	/// Downloads the raw image data from the given URL.
	///
	/// @param url The URL of the image to download.
	/// @return The raw image data as a byte array.
	/// @throws IOException If an error occurs while downloading the image.
	public static byte[] downloadImageData(String url) throws IOException {
		LOG.debug("Downloading image from URL: {}", url);
		try (InputStream in = URI.create(url).toURL().openStream()) {
			byte[] imageData = in.readAllBytes();
			LOG.debug("Image downloaded");
			return imageData;
		}
	}

	/// Checks if the given image data represents a GIF image by verifying the header bytes.
	/// This is a simple check and does not guarantee that the image data is a valid GIF, but it is sufficient for our use case since we only need to identify GIFs to apply specific handling for them.
	///
	/// @param imageData The image data to check.
	/// @return true if the image data is likely a GIF, false otherwise.
	public static boolean isGif(byte[] imageData) {
		return imageData.length >= 6
			&& imageData[0] == 'G'
			&& imageData[1] == 'I'
			&& imageData[2] == 'F'
			&& imageData[3] == '8'
			&& (imageData[4] == '7' || imageData[4] == '9')
			&& imageData[5] == 'a';
	}

	/// Enables looping for the given GIF image data.
	/// The modification is done in-place.
	///
	/// @param imageData The image data of the GIF.
	public static void enableLoop(byte[] imageData) {
		int offset = 13 + ((imageData[10] & 0b10000000) != 0 ? (2 << (imageData[10] & 0b00000111)) * 3 : 0);
		for (int i = offset; i < imageData.length; i++) {
			if (imageData[i] == (byte) 0x21 && imageData[i + 1] == (byte) 0xFF) {
				byte size = imageData[i + 2];
				if (size == NETSCAPE2_0.length && Arrays.equals(NETSCAPE2_0, 0, size, imageData, i + 3, i + 3 + size)) {
					int pos = i + 3 + size;
					if (imageData[pos] != 3 || imageData[pos + 1] != 1) {
						LOG.warn("Invalid Netscape extension format, cannot enable looping");
						return;
					}
					if (imageData[pos + 2] != 0) {
						LOG.info("Enabling looping for GIF image");
						imageData[pos + 2] = 0;
					}
					return;
				}
			}
		}
		LOG.warn("No Netscape extension found in GIF, cannot enable looping");
	}

	private final record FrameData(BufferedImage image, IIOMetadata metadata) {}

	/// Validates the given GIF image data by attempting to decode its frames and re-encoding them.
	/// If the image data is valid, it is returned unchanged. If it is invalid but
	/// frames can be decoded, a new valid GIF image data is returned. If no frames can be decoded, the original data is returned.
	/// This is a workaround for some invalid GIFs that can be decoded by Java's ImageIO but not displayed correctly in JavaFX.
	/// The method also enables looping for the image if a Netscape extension is found, as some invalid GIFs are missing the looping flag in the extension.
	///
	/// @param imageData The image data of the GIF to validate.
	/// @return The validated (and possibly modified) image data.
	/// @see #enableLoop(byte[])
	public static byte[] validate(byte[] imageData) {
		byte[] finalData = imageData;
		Image image = new Image(new ByteArrayInputStream(imageData));
		if (image.isError()) {
			LOG.warn("Image data is invalid, attempting to re-encode it");

			ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
			try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(new ByteArrayInputStream(imageData))) {
				reader.setInput(imageInputStream, false, false);
				List<FrameData> frames = readGifFrames(reader);

				if (!frames.isEmpty()) {
					IIOMetadata streamMeta = reader.getStreamMetadata();
					finalData = writeNewGifData(imageData, frames, streamMeta);
					LOG.info("Image re-encoded successfully ({} frame(s))", frames.size());
				} else {
					LOG.error("No frames could be decoded – returning original data");
				}
			} catch (IOException e) {
				LOG.error("Error while re-encoding image data", e);
			} finally {
				reader.dispose();
			}
		}

		enableLoop(finalData);
		return finalData;
	}

	private static byte[] writeNewGifData(byte[] imageData, List<FrameData> frames, IIOMetadata streamMeta)
			throws IOException {
		byte[] finalData;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(imageData.length);
		ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
		try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
			writer.setOutput(ios);
			writer.prepareWriteSequence(streamMeta);
			for (FrameData frameData : frames) {
				writer.writeToSequence(new IIOImage(frameData.image(), null, frameData.metadata()), null);
			}
			writer.endWriteSequence();
		} finally {
			writer.dispose();
		}
		finalData = baos.toByteArray();
		return finalData;
	}

	private static List<FrameData> readGifFrames(ImageReader reader) throws IOException {
		int numFrames = reader.getNumImages(true);
		List<FrameData> frames = new ArrayList<>(numFrames);
		for (int i = 0; i < numFrames; i++) {
			try {
				frames.add(new FrameData(reader.read(i), reader.getImageMetadata(i)));
			} catch (IOException e) {
				LOG.warn("Could not decode frame {}, stopping at {} frame(s)", i, frames.size());
				LOG.debug("Error details: ", e);
				break;
			}
		}
		return frames;
	}
}
