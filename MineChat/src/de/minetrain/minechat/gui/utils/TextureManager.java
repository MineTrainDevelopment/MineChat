package de.minetrain.minechat.gui.utils;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.fmsware.gif.GifDecoder;
import com.fmsware.gif.GifEncoder;
import com.github.twitch4j.helix.domain.ChatBadge;
import com.github.twitch4j.helix.domain.ChatBadgeSet;
import com.github.twitch4j.helix.domain.Emote.Format;
import com.google.gson.Gson;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.Badge;
import de.minetrain.minechat.data.objectdata.BadgeId;
import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.gui.emotes.EmoteType;
import de.minetrain.minechat.twitch.TwitchHelper;
import de.minetrain.minechat.twitch.obj.BttvEmote;
import de.minetrain.minechat.twitch.obj.BttvUser;

public class TextureManager {

	private static final Logger LOG = LoggerFactory.getLogger(TextureManager.class);

	public static final Path PATH_BASE = Path.of("data", "texture");
	public static final Path PATH_BADGES = PATH_BASE.resolve("badges");
	public static final Path PATH_ICONS = PATH_BASE.resolve("Icons");
	public static final String TWITCH_EMOTE_URL = "https://static-cdn.jtvnw.net/emoticons/v2/{}/{}/dark/1.0"; // id, format(static, animated)
	public static final String BTTV_EMOTE_URL = "https://cdn.betterttv.net/emote/{}/{}x"; // id, scale (1, 2, 3)
	public static final String texturePath = "data/texture/";
	public static final String badgePath = texturePath + "badges/";
	public static final String profilePicPath = "data/texture/Icons/{ID}/profile_{SIZE}.png";

	private final ImageIcon mainFrame_TAB_1;
	private final ImageIcon mainFrame_TAB_2;
	private final ImageIcon mainFrame_TAB_3;
	private final ImageIcon mainFrame_Blank;
	private final ImageIcon onboarding;
	private final ImageIcon emoteBorder;
	private final ImageIcon programIcon;
	private final ImageIcon replyButton;
	private final ImageIcon markReadButton;
	private final ImageIcon cancelButton;
	private final ImageIcon confirmButton;
	private final ImageIcon editButton;
	private final ImageIcon infoButton;
	private final ImageIcon enterButton;
	private final ImageIcon emoteButton;
	private final ImageIcon waveButton;
	private final ImageIcon loveButton;
	private final ImageIcon statusButton_1;
	private final ImageIcon statusButton_2;
	private final ImageIcon statusButton_3;
	private final ImageIcon rowArrowRight;
	private final ImageIcon rowArrowLeft;
	private final ImageIcon replyChainButton;
	private final ImageIcon programClose;
	private final ImageIcon programMinimize;
	private final ImageIcon programSettings;
	private final ImageIcon macroKeyPressed;
	private final ImageIcon macroKeyHover;
	private final ImageIcon macroKey;
	private final ImageIcon macroEmoteKeyPressed;
	private final ImageIcon macroEmoteKeyHover;
	private final ImageIcon macroEmoteKey;
	private final ImageIcon liveIcon;
	private final ImageIcon notificationButton;
	private final ImageIcon notificationButtonHover;
	private final ImageIcon profilePicLoading;
	private final ImageIcon copyButton;

	public TextureManager() {
		LOG.debug("Loading textures...");
		this.mainFrame_TAB_1 = new ImageIcon(texturePath + "program/MineChatTextur.png");
		this.mainFrame_TAB_2 =new ImageIcon(texturePath + "program/MineChatTextur2.png");
		this.mainFrame_TAB_3 = new ImageIcon(texturePath + "program/MineChatTextur3.png");
		this.mainFrame_Blank = new ImageIcon(texturePath + "program/MineChatTextur_Blank.png");
		this.onboarding = new ImageIcon(texturePath + "program/MineChatTexturOnboarding.png");
		this.emoteBorder = new ImageIcon(texturePath + "emoteBorder/emoteBorder.png");
		this.programIcon = new ImageIcon(texturePath + "program/programIcon.png");
		this.replyButton = new ImageIcon(texturePath + "chatInput/replyButton.png");
		this.markReadButton = new ImageIcon(texturePath + "chatInput/markReadButton.png");
		this.cancelButton = new ImageIcon(texturePath + "utilIcon/cancelButton.png");
		this.confirmButton = new ImageIcon(texturePath + "utilIcon/confirmButton.png");
		this.editButton = new ImageIcon(texturePath + "utilIcon/editButton.png");
		this.infoButton = new ImageIcon(texturePath + "utilIcon/infoButton.png");
		this.enterButton = new ImageIcon(texturePath + "chatInput/enterButton.png");
		this.emoteButton = new ImageIcon(texturePath + "chatInput/emoteButton.png");
		this.waveButton = new ImageIcon(texturePath + "chatInput/waveButton.png");
		this.loveButton = new ImageIcon(texturePath + "chatInput/loveButton.png");
		this.statusButton_1 = new ImageIcon(texturePath + "program/statusButton_1.png");
		this.statusButton_2 = new ImageIcon(texturePath + "program/statusButton_2.png");
		this.statusButton_3 = new ImageIcon(texturePath + "program/statusButton_3.png");
		this.rowArrowLeft = new ImageIcon(texturePath + "program/rowArrowLeft.png");
		this.rowArrowRight = new ImageIcon(texturePath + "program/rowArrowRight.png");
		this.replyChainButton = new ImageIcon(texturePath + "chatInput/replyChainButton.png");
		this.programClose = new ImageIcon(texturePath + "program/programClose.png");
		this.programMinimize = new ImageIcon(texturePath + "program/programMinimize.png");
		this.programSettings = new ImageIcon(texturePath + "program/programSettings.png");
		this.macroKeyPressed = new ImageIcon(texturePath + "macroBorder/macroKeyPressed.gif");
		this.macroKeyHover = new ImageIcon(texturePath + "macroBorder/macroKeyHover.png");
		this.macroKey = new ImageIcon(texturePath + "macroBorder/macroKey.png");
		this.macroEmoteKeyPressed = new ImageIcon(texturePath + "macroBorder/macroEmoteKeyPressed.gif");
		this.macroEmoteKeyHover = new ImageIcon(texturePath + "macroBorder/macroEmoteKeyHover.png");
		this.macroEmoteKey = new ImageIcon(texturePath + "macroBorder/macroEmoteKey.png");
		this.liveIcon = new ImageIcon(texturePath + "liveNotification/LiveIcon.png");
		this.notificationButton = new ImageIcon(texturePath + "liveNotification/NotificationButton.png");
		this.notificationButtonHover = new ImageIcon(texturePath + "liveNotification/NotificationButtonHover.png");
		this.profilePicLoading = new ImageIcon(texturePath + "settingsMenu/profilePicLoading.gif");
		this.copyButton = new ImageIcon(texturePath + "utilIcon/copyButton.png");
		LOG.debug("Loading textures done.");
	}


	public ImageIcon getMainFrame_TAB_1() {
		return mainFrame_TAB_1;
	}

	public ImageIcon getMainFrame_TAB_2() {
		return mainFrame_TAB_2;
	}

	public ImageIcon getMainFrame_TAB_3() {
		return mainFrame_TAB_3;
	}

	public ImageIcon getMainFrame_Blank() {
		return mainFrame_Blank;
	}

	public ImageIcon getOnboarding() {
		return onboarding;
	}

	public ImageIcon getEmoteBorder() {
		return emoteBorder;
	}

	public ImageIcon getProgramIcon() {
		return programIcon;
	}

	public ImageIcon getReplyButton() {
		return replyButton;
	}


	public ImageIcon getMarkReadButton() {
		return markReadButton;
	}

	public ImageIcon getCancelButton() {
		return cancelButton;
	}

	public ImageIcon getConfirmButton() {
		return confirmButton;
	}

	public ImageIcon getEditButton() {
		return editButton;
	}

	public ImageIcon getInfoButton() {
		return infoButton;
	}

	public ImageIcon getEnterButton() {
		return enterButton;
	}

	public ImageIcon getEmoteButton() {
		return emoteButton;
	}

	public ImageIcon getWaveButton() {
		return waveButton;
	}

	public ImageIcon getLoveButton() {
		return loveButton;
	}

	public ImageIcon getStatusButton_1() {
		return statusButton_1;
	}


	public ImageIcon getStatusButton_2() {
		return statusButton_2;
	}


	public ImageIcon getStatusButton_3() {
		return statusButton_3;
	}

	public ImageIcon getRowArrowRight() {
		return rowArrowRight;
	}


	public ImageIcon getRowArrowLeft() {
		return rowArrowLeft;
	}

	public ImageIcon getReplyChainButton() {
		return replyChainButton;
	}

	public ImageIcon getProgramClose() {
		return programClose;
	}

	public ImageIcon getProgramMinimize() {
		return programMinimize;
	}

	public ImageIcon getProgramSettings() {
		return programSettings;
	}

	public ImageIcon getMacroKeyPressed() {
		return macroKeyPressed;
	}

	public ImageIcon getMacroKeyHover() {
		return macroKeyHover;
	}

	public ImageIcon getMacroKey() {
		return macroKey;
	}

	public ImageIcon getMacroEmoteKeyPressed() {
		return macroEmoteKeyPressed;
	}

	public ImageIcon getMacroEmoteKeyHover() {
		return macroEmoteKeyHover;
	}

	public ImageIcon getMacroEmoteKey() {
		return macroEmoteKey;
	}

	public ImageIcon getLiveIcon() {
		return liveIcon;
	}


	public ImageIcon getNotificationButton() {
		return notificationButton;
	}


	public ImageIcon getNotificationButtonHover() {
		return notificationButtonHover;
	}


	public ImageIcon getProfilePicLoading() {
		return profilePicLoading;
	}

	public ImageIcon getCopyButton() {
		return copyButton;
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

	@Deprecated
	public static void mergeEmoteImages(String fileLocation, String fileName, String background){
		mergeEmoteImages(fileLocation, fileName, background, "png");
	}

	@Deprecated
	public static void mergeEmoteImages(String fileLocation, String fileName, String background, String format){
		format = format.replace(".", "");
		try {
			File path = new File(texturePath + fileLocation); // base path of the images
			System.out.println(texturePath + fileLocation);

			// load source images
			BufferedImage image = ImageIO.read(new File(TextureManager.texturePath, background));
			BufferedImage overlay = ImageIO.read(new File(path, fileName));

			// create the new image, canvas size is the max. of both image sizes
			int w = Math.max(image.getWidth(), overlay.getWidth());
			int h = Math.max(image.getHeight(), overlay.getHeight());
			BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

			// paint both images, preserving the alpha channels
			Graphics g = combined.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.drawImage(overlay, 4, 4, null);

			g.dispose();

			// Save as new image
			ImageIO.write(combined, format.toUpperCase(), new File(path, fileName.replace("."+format, "_BG.png")));
		} catch (IOException ex) {
			LOG.error("Merging images whent wrong.", ex);
		}
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
		List<Badge> newBadges = new ArrayList<>(bagdes.stream().mapToInt(set -> set.getVersions().size()).sum());
		for (ChatBadgeSet badgeSet : bagdes) {
			for (ChatBadge twitchBadge : badgeSet.getVersions()) {
				BadgeId badgeId = new BadgeId(badgeSet.getSetId(), twitchBadge.getId());
				if (!forceUpdate && EclipseStoreKeeper.root().badges().of(channelId, badgeId) != null) {
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
					newBadges.add(badge);
				} catch (IOException e) {
					LOG.error("Error downloading badge images for badge '{}' in channel ID: {}", twitchBadge.getTitle(), channelId, e);
				}
			}
		}
		EclipseStoreKeeper.root().badges().addBadges(newBadges);
	}

	private static void downloadEmotes(List<com.github.twitch4j.helix.domain.Emote> emotes, String channelId, boolean forceUpdate) {
		List<Emote> newEmotes = new ArrayList<>(emotes.size());
		for (var twitchEmote : emotes) {
			if (!forceUpdate && EclipseStoreKeeper.root().emotes().ofId(twitchEmote.getId()) != null) {
				continue;
			}
			LOG.info("{} - Downloading emote: {}", channelId, twitchEmote);
			EmoteType type = twitchEmote.getEmoteType() == null || twitchEmote.getTier() == null ? null : EmoteType.get(twitchEmote.getEmoteType(), twitchEmote.getTier().ordinalName());
			boolean animated = twitchEmote.getFormat().contains(Format.ANIMATED);
			String fileFormat = animated ? "gif" : "png";
			try {
				byte[] image1x = downloadImageData(twitchEmote.getImages().getSmallImageUrl());
				byte[] image2x = downloadImageData(twitchEmote.getImages().getMediumImageUrl());
				byte[] image3x = downloadImageData(twitchEmote.getImages().getLargeImageUrl());

				if (animated) {
					image1x = reformatGif(image1x);
					image2x = reformatGif(image2x);
					image3x = reformatGif(image3x);
				}

				Emote emote = new Emote(twitchEmote.getId(), channelId, twitchEmote.getName(), type, false, animated,
						fileFormat, image1x, image2x, image3x);
				newEmotes.add(emote);
			} catch (IOException e) {
				LOG.error("Error downloading emote images for emote '{}' in channel ID: {}", twitchEmote.getName(), channelId, e);
			}
		}
		EclipseStoreKeeper.root().emotes().addEmotes(newEmotes);
	}

	private static void downloadBttvEmotes(List<BttvEmote> emotes, String channelId, boolean forceUpdate) {
		List<Emote> newEmotes = new ArrayList<>(emotes.size());
		for (var bttvEmote : emotes) {
			if (!forceUpdate && EclipseStoreKeeper.root().emotes().ofId(bttvEmote.getId()) != null) {
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
						bttvEmote.isAnimated(), bttvEmote.getImageType(), images[0], images[1], images[2]);
				newEmotes.add(emote);
			} catch (IOException e) {
				LOG.error("Error downloading emote images for emote '{}' in channel ID: {}", bttvEmote.getCode(), channelId, e);
			}
		}
		EclipseStoreKeeper.root().emotes().addEmotes(newEmotes);
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
		encoder.start(outputStream);

		encoder.setSize(decoder.getFrameSize());
		for (int i = 0; i < decoder.getFrameCount(); i++) {
			encoder.addFrame(decoder.getFrame(i), decoder.getDelay(i));
		}

		encoder.finish();
		return outputStream.toByteArray();
	}
}
