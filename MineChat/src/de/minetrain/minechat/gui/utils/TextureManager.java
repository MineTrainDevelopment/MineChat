package de.minetrain.minechat.gui.utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fmsware.gif.GifDecoder;
import com.fmsware.gif.GifEncoder;
import com.github.twitch4j.helix.domain.ChatBadge;
import com.github.twitch4j.helix.domain.ChatBadgeSet;
import com.github.twitch4j.helix.domain.Emote.Format;
import com.google.gson.Gson;

import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.gui.emotes.EmoteLegacy;
import de.minetrain.minechat.gui.emotes.EmoteLegacy.EmoteType;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.twitch.TwitchHelper;
import de.minetrain.minechat.twitch.obj.BttvEmote;
import de.minetrain.minechat.twitch.obj.BttvUser;

public class TextureManager {

	private static final Logger LOG = LoggerFactory.getLogger(TextureManager.class);

	public static final Path PATH_BASE = Path.of("data", "texture");
	public static final Path PATH_BADGES = PATH_BASE.resolve("badges");
	public static final Path PATH_ICONS = PATH_BASE.resolve("Icons");
	public static final String TWITCH_EMOTE_URL = "https://static-cdn.jtvnw.net/emoticons/v2/{}/{}/dark/1.0"; // id, format(static, animated)
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
		if (!Files.exists(PATH_BADGES.resolve("vip"))) {
			downloadDefaultBadges();
		}

		if (EclipseStoreKeeper.root().emotes().getEmotesByChannelId("public").isEmpty()) {
			downloadDefaultEmotes();
		}
	}


	public static void downloadProfileImage(String uri, String channelId) {
		try {
			Path channelPath = PATH_ICONS.resolve(channelId);
			BufferedImage image = downloadImage(uri);
			writeImage(image, channelPath.resolve("profile.png"));
			writeImage(resizeImage(image, new Dimension(18, 18)), channelPath.resolve("profile_18.png"));
			writeImage(resizeImage(image, new Dimension(25, 25)), channelPath.resolve("profile_25.png"));
			writeImage(resizeImage(image, new Dimension(75, 75)), channelPath.resolve("profile_75.png"));
			writeImage(resizeImage(image, new Dimension(80, 80)), channelPath.resolve("profile_80.png"));
		} catch (IOException e) {
			LOG.error("Error while downloading image from: {}", uri, e);
		}
	}

	public static void downloadChannelBadges(String userId){
		TwitchHelper.requestChannelBadges(userId)
			.thenAcceptAsync(bagdes -> downloadBadges(bagdes, userId))
			.handle((_, e) -> {
				if (e != null) {
					LOG.error("Error while downloading channel badges for user ID: {}", userId, e);
				}
				return null;
			});
	}

	public static void downloadChannelEmotes(String userId) {
		TwitchHelper.requestChannelEmotes(userId).thenAcceptAsync(emotes -> {
			downloadEmotes(emotes, userId);
			downloadEmotesLegacy(emotes, userId);
		}).handle((_, e) -> {
			if (e != null) {
				LOG.error("Error while downloading channel emotes for user ID: {}", userId, e);
			}
			return null;
		});
	}

	public static void downloadBttvEmotes(String userId){
		CompletableFuture.supplyAsync(() -> {
			try (HttpClient client = HttpClient.newHttpClient()) {
				HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.betterttv.net/3/cached/users/twitch/" + userId))
					.header("accept", "application/json")
					.header("user-agent", "MineChat Client")
					.GET()
					.build();
				HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
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
		}).thenAcceptAsync(emotes -> downloadBttvEmotes(emotes, userId))
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


	private static void downloadDefaultBadges(){
		TwitchHelper.requestGlobalBadges()
			.thenAcceptAsync(bagdes -> downloadBadges(bagdes, null))
			.handle((_, e) -> {
				if (e != null) {
					LOG.error("Error while downloading default badges", e);
				}
				return null;
			});
	}


	private static void downloadDefaultEmotes() {
		TwitchHelper.requestGlobalEmotes()
			.thenAcceptAsync(emotes -> {
				downloadEmotes(emotes, "public");
				downloadDefaultEmotes(emotes);
			})
			.handle((_, e) -> {
				if (e != null) {
					LOG.error("Error while downloading default badges", e);
				}
				return null;
			});
	}


	private static void downloadBadges(List<ChatBadgeSet> bagdes, String channelId) {
		for (ChatBadgeSet badgeSet : bagdes) {
			Path setPath = PATH_BADGES.resolve(badgeSet.getSetId());
			if (channelId != null) {
				setPath = setPath.resolve("Channel_" + channelId);
			}
			for (ChatBadge version : badgeSet.getVersions()) {
				Path targetPath = setPath.resolve(version.getId());
				YamlManager config = new YamlManager(targetPath.resolve("meta.yml").toString());
				config.setString("Name", version.getTitle());
				config.setString("Description", version.getDescription());
				config.saveConfigToFile();

				try {
					downloadImage(version.getSmallImageUrl(), targetPath.resolve("1.png"));
					downloadImage(version.getMediumImageUrl(), targetPath.resolve("2.png"));
					downloadImage(version.getLargeImageUrl(), targetPath.resolve("3.png"));
				} catch (IOException e) {
					LOG.error("Error downloading badge images.", e);
				}
			}
		}
	}

	private static void downloadEmotes(List<com.github.twitch4j.helix.domain.Emote> emotes, String channelId) {
		List<Emote> newEmotes = new ArrayList<>(emotes.size());
		for (var twitchEmote : emotes) {
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

				Emote emote = new Emote(twitchEmote.getId(), twitchEmote.getEmoteSetId(), channelId,
						twitchEmote.getName(), type, false, animated, fileFormat, image1x, image2x, image3x);
				newEmotes.add(emote);
			} catch (IOException e) {
				LOG.error("Error downloading emote images for emote '{}' in channel ID: {}", twitchEmote.getName(), channelId, e);
			}
		}
		EclipseStoreKeeper.root().emotes().addEmotes(newEmotes);
	}

	@Deprecated
	private static void downloadEmotesLegacy(List<com.github.twitch4j.helix.domain.Emote> emotes, String channelId) {
		ArrayList<String> tier1 = new ArrayList<>();
		ArrayList<String> tier2 = new ArrayList<>();
		ArrayList<String> tier3 = new ArrayList<>();
		ArrayList<String> follower = new ArrayList<>();
		ArrayList<String> bits = new ArrayList<>();
		Path channelPath = PATH_ICONS.resolve(channelId);

		for (var emote : emotes) {
			switch (emote.getTier()) {
				case TIER1 -> tier1.add(emote.getId());
				case TIER2 ->tier2.add(emote.getId());
				case TIER3 -> tier3.add(emote.getId());
				default -> {
					String type = emote.getEmoteType();
					if (type.startsWith("bitstier")) {
						bits.add(emote.getId());
					} else if (type.startsWith("follower")) {
						follower.add(emote.getId());
					}
				}
			}

			boolean isFavorite = false;
			EmoteLegacy emoteByName = EmoteManager.getEmoteByName(emote.getName());
			if (emoteByName != null) {
				isFavorite = emoteByName.isFavorite();
			}

			boolean animated = emote.getFormat().contains(Format.ANIMATED);
			String fileFormat = animated ? "gif" : "png";
			Path targetPath = channelPath.resolve(emote.getId());
			DatabaseManager.getEmote().insert(emote.getId(), emote.getName(), false, isFavorite, emote.getEmoteType(), emote.getTier().ordinalName(),
					fileFormat, animated, targetPath.resolve(emote.getId() + "_1." + fileFormat).toString());

			try {
				downloadImage(emote.getImages().getSmallImageUrl(), targetPath.resolve(emote.getId() + "_1." + fileFormat));
				downloadImage(emote.getImages().getMediumImageUrl(), targetPath.resolve(emote.getId() + "_2." + fileFormat));
				downloadImage(emote.getImages().getLargeImageUrl(), targetPath.resolve(emote.getId() + "_3." + fileFormat));

//				TextureManager.mergeEmoteImages(fileLocation, emoteID+"_1"+fileFormat, "emoteBorder"+borderImageTyp+".png", fileFormat);
			} catch (IOException e) {
				LOG.error("Error downloading channel emote '{}' for channel ID: {}", emote.getName(), channelId, e);
			}
		}

		String tierlevel = EmoteManager.getChannelEmotes().containsKey(channelId)
				? EmoteManager.getChannelEmotes(channelId).getSubLevel()
				: "tier0";
		DatabaseManager.getEmote().insertChannel(channelId, tierlevel, tier1, tier2, tier3, bits, follower);
		DatabaseManager.commit();
		DatabaseManager.getEmote().getAll();
		DatabaseManager.getEmote().getAllChannels();
	}

	@Deprecated
	private static void downloadDefaultEmotes(List<com.github.twitch4j.helix.domain.Emote> emotes) {
		Path channelPath = PATH_ICONS.resolve("default");

		for (var emote : emotes) {
			boolean isFavorite = false;
			EmoteLegacy emoteByName = EmoteManager.getEmoteByName(emote.getName());
			if (emoteByName != null) {
				isFavorite = emoteByName.isFavorite();
			}

			boolean animated = emote.getFormat().contains(Format.ANIMATED);
			String fileFormat = animated ? "gif" : "png";
			Path targetPath = channelPath.resolve(emote.getId());
			DatabaseManager.getEmote().insert(emote.getId(), emote.getName(), true, isFavorite, "default", null,
					fileFormat, animated, targetPath.resolve(emote.getId() + "_1." + fileFormat).toString());

			try {
				downloadImage(emote.getImages().getSmallImageUrl(), targetPath.resolve(emote.getId() + "_1." + fileFormat));
				downloadImage(emote.getImages().getMediumImageUrl(), targetPath.resolve(emote.getId() + "_2." + fileFormat));
				downloadImage(emote.getImages().getLargeImageUrl(), targetPath.resolve(emote.getId() + "_3." + fileFormat));
			} catch (IOException e) {
				LOG.error("Error downloading global emote '{}'", emote.getName(), e);
			}

		}

		DatabaseManager.commit();
		DatabaseManager.getEmote().getAll();
	}

	private static void downloadBttvEmotes(List<BttvEmote> emotes, String userId) {
		Path channelPath = PATH_ICONS.resolve("bttv");
		ArrayList<String> emoteIDs = new ArrayList<>(emotes.size());

		for (var emote : emotes) {
			emoteIDs.add(emote.getId());

			boolean isFavorite = false;
			EmoteLegacy emoteByName = EmoteManager.getEmoteByName(emote.getCode());
			if (emoteByName != null) {
				isFavorite = emoteByName.isFavorite();
			}

			Path targetPath = channelPath.resolve(emote.getId());
			DatabaseManager.getEmote().insert(emote.getId(), emote. getCode(), false, isFavorite, "bttv", null,
					emote.getImageType(), emote.isAnimated(), targetPath.resolve(emote.getId() + "_1." + emote.getImageType()).toString());

			try {
				for (int scale = 1; scale <= 3; scale++) {
					String imageUrl = "https://cdn.betterttv.net/emote/" + emote.getId() + "/" + scale + "x";
					downloadImage(imageUrl, targetPath.resolve(emote.getId() + "_" + scale + "." + emote.getImageType()));
				}
			} catch (IOException e) {
				LOG.error("Error downloading bttv emote '{}'", emote.getCode(), e);
			}

		}

		DatabaseManager.getEmote().insertChannelBttv(userId, emoteIDs);
		DatabaseManager.commit();
		DatabaseManager.getEmote().getAll();
		DatabaseManager.getEmote().getAllChannels();
	}

	private static void downloadImage(String url, Path target) throws IOException {
		LOG.debug("Downloading image from URL: {}", url);
		try (InputStream in = URI.create(url).toURL().openStream()) {
			Files.createDirectories(target.getParent());
			Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
		}
		LOG.debug("Image downloaded and saved to: {}", target);
	}

	private static BufferedImage downloadImage(String url) throws IOException {
		LOG.debug("Downloading image from URL: {}", url);
		try (InputStream in = URI.create(url).toURL().openStream()) {
			BufferedImage image = ImageIO.read(in);
			LOG.debug("Image downloaded");
			return image;
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

	private static void writeImage(BufferedImage scaledImage, Path imagePath) throws IOException {
		String fileName = imagePath.getFileName().toString();
		String format = fileName.substring(fileName.lastIndexOf(".") + 1);
		Files.createDirectories(imagePath.getParent());
		ImageIO.write(scaledImage, format, imagePath.toFile());
		LOG.debug("Image saved to: {}", imagePath);
	}

	private static BufferedImage resizeImage(BufferedImage image, Dimension dimension) {
		BufferedImage scaledImage = new BufferedImage(dimension.width, dimension.height, image.getType());
		Graphics2D g2d = scaledImage.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawImage(image, 0, 0, dimension.width, dimension.height, null);
		g2d.dispose();
		return scaledImage;
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
