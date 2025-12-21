package de.minetrain.minechat.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;
import de.minetrain.minechat.data.objectdata.BadgeId;
import de.minetrain.minechat.data.objectdata.ChatMessageToken;
import de.minetrain.minechat.data.objectdata.Emote;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.main.Main;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class MineTextFlow extends TextFlow {

	private static final Logger LOG = LoggerFactory.getLogger(MineTextFlow.class);

	private static final Map<Integer, Image> imageCache = new ConcurrentHashMap<>();

	private String defaultFontFamily;
	private double defaultFontSize;
	private FontPosture defaultFontPosture;
	private FontWeight defaultFontWeight;
	private Color defaultFontFill;
	private List<HighlightString> highlightStrings;

	public MineTextFlow(double defaultFontSize) {
		this("Inter", defaultFontSize, FontPosture.REGULAR, FontWeight.BOLD, Color.WHITE);
	}

	public MineTextFlow() {
		this(15d);
	}

	public MineTextFlow(String fontFamily, double fontSize, FontPosture fontPosture, FontWeight fontWeight, Color fontFill) {
		defaultFontFamily = fontFamily;
		defaultFontSize = fontSize;
		defaultFontPosture = fontPosture;
		defaultFontWeight = fontWeight;
		defaultFontFill = fontFill;

		Rectangle clip = new Rectangle();
		clip.widthProperty().bind(widthProperty());
		clip.heightProperty().bind(heightProperty());
		setClip(clip);
	}

	/**
	 * Appends the specified string to the output string.
	 *
	 * @param string the string to be appended
	 * @param color  {@link htmlColors}
	 * @return the IconStringBuilder object for method chaining
	 */
	public MineTextFlow appendString(String string, HTMLColors color) {
		return appendString(string, Color.web(color.getColorCode()));
	}

	/**
	 * Appends the specified string to the output string.
	 */
	public MineTextFlow appendString(String string) {
		return appendString(string, defaultFontFamily, defaultFontSize, defaultFontPosture, defaultFontWeight, defaultFontFill);
	}

	/**
	 * Appends the specified string to the output string.
	 */
	public MineTextFlow appendString(String string, double font_size) {
		return appendString(string, defaultFontFamily, font_size, defaultFontPosture, defaultFontWeight, defaultFontFill);
	}

	/**
	 * Appends the specified string to the output string.
	 */
	public MineTextFlow appendString(String string, Color font_fill) {
		return appendString(string, defaultFontFamily, defaultFontSize, defaultFontPosture, defaultFontWeight, font_fill);
	}

	/**
	 * Appends the specified string to the output string.
	 */
	public MineTextFlow appendString(String string, double font_size, Color font_fill) {
		return appendString(string, defaultFontFamily, font_size, defaultFontPosture, defaultFontWeight, font_fill);
	}

	/**
	 * Appends the specified string to the output string.
	 */
	public MineTextFlow appendString(String string, double font_size, FontWeight font_weight, Color font_fill) {
		return appendString(string, defaultFontFamily, font_size, defaultFontPosture, font_weight, font_fill);
	}

	/**
	 * Appends the specified string to the output string.
	 */
	public MineTextFlow appendString(String string, String font_family, double font_size, FontPosture font_posture, FontWeight font_weight, Color font_fill) {
		Text text = new Text(string);
		text.setFont(Font.font(font_family, font_weight, font_posture, font_size));
		text.setFill(font_fill);
		getChildren().add(text);
		return this;
	}

	public MineTextFlow appendHyperLink(String url) {
		Hyperlink hyperlink = new Hyperlink(Main.extractDomain(url));
		hyperlink.setTooltip(new Tooltip(url));
		hyperlink.setOnAction(event -> {
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
			}
		});

		hyperlink.setFocusTraversable(false);
		hyperlink.setFont(Font.font(defaultFontFamily, defaultFontWeight, defaultFontPosture, defaultFontSize));

		hyperlink.setOnDragDetected(event -> {
			Dragboard dragboard = hyperlink.startDragAndDrop(TransferMode.COPY_OR_MOVE);
			ClipboardContent content = new ClipboardContent();
			content.putHtml(hyperlink.getText());
			content.putString(hyperlink.getText());
			content.putUrl(url);

			SnapshotParameters snapshotParameters = new SnapshotParameters();
			snapshotParameters.setFill(Color.TRANSPARENT);
			content.putImage(hyperlink.snapshot(snapshotParameters, null));

			dragboard.setContent(content);
			hyperlink.setVisited(true);
			event.consume();
		});

		getChildren().add(hyperlink);
		return this;
	}

	/**
	 * Emotes are centert to the text.
	 *
	 * @param emote
	 * @param size
	 * @return
	 */
	public MineTextFlow appendEmote(Emote emote) {
		ImageView imageView = createAlignedImageView(Main.getEmoteManager().getEmoteImage1x(emote.getEmoteId(), emote.isAnimated()));
//		imageView.setTranslateY(-((DEFAULT_FONT_SIZE - size.getSize()) / 2));
		appendImage(imageView);
		return this;
	}

	public MineTextFlow appendBadge(String channelId, BadgeId badgeId) {
		Image badgeImage = Main.getEmoteManager().getBadgeImage1x(channelId, badgeId);
		if (badgeImage != null) {
			ImageView imageView = createAlignedImageView(badgeImage);
			appendImage(imageView);
		} else {
			LOG.warn("Badge image not found for badge ID: {}", badgeId);
		}
		return this;
	}

	public HighlightString appendToken(ChatMessageToken token) {
		switch (token.getType()) {
			case EMOTE -> {
				Image image = Main.getEmoteManager().getEmoteImage1x(token.getEmoteId(), token.isAnimated());
				if (image != null) {
					ImageView imageView = createAlignedImageView(image);
					appendImage(imageView);
				} else {
					LOG.warn("Emote image not found for emote ID: {}", token.getEmoteId());
					appendString(token.getText());
				}
			}
			case LINK -> appendHyperLink(token.getText());
			case MENTION -> appendString(token.getText(), HTMLColors.MAROON);
			case SPACE -> appendString(token.getText());
			default -> {
				Optional<HighlightString> hightlight = getHighlightStrings().stream()
					.filter(hs -> hs.getCompiledPattern().matcher(token.getText()).matches())
					.findFirst();
				if (hightlight.isPresent()) {
					appendString(token.getText(), hightlight.get().getWordColor());
					return hightlight.get();
				} else {
					appendString(token.getText());
				}
			}
		}
		return null;
	}

	private ImageView createAlignedImageView(Image image) {
		return new ImageView(image) {

			@Override
			public double getBaselineOffset() {
				return getImage().getHeight() * 0.75;
			}
		};
	}

	public MineTextFlow appendImage(Path imagePath) {
		appendImage(new ImageView( imageCache.computeIfAbsent(imagePath.hashCode(), hash -> new Image(imagePath.toUri().toString()))));
		return this;
	}

	public MineTextFlow appendImage(ImageView image) {
		getChildren().add(image);
		return this;
	}

	/**
	 * Appends a space to the output string.
	 *
	 * @return the {@link MineTextFlow} object for method chaining
	 */
	public MineTextFlow appendSpace() {
		return appendString(" ");
	}

	/**
	 * Appends a linesplit to the output string.
	 *
	 * @return the {@link MineTextFlow} object for method chaining
	 */
	public MineTextFlow appendLineSplit() {
		return appendString("\n");
	}

	public MineTextFlow clear() {
		getChildren().clear();
		return this;
	}

	public MineTextFlow setAlignment(TextAlignment alignment) {
		setTextAlignment(alignment);
		return this;
	}

	public MineTextFlow setDefaultFontFamily(String defaultFontFamily) {
		this.defaultFontFamily = defaultFontFamily;
		return this;
	}

	public MineTextFlow setDefaultFontSize(double defaultFontSize) {
		this.defaultFontSize = defaultFontSize;
		return this;
	}

	public MineTextFlow setDefaultFontPosture(FontPosture defaultFontPosture) {
		this.defaultFontPosture = defaultFontPosture;
		return this;
	}

	public MineTextFlow setDefaultFontWeight(FontWeight defaultFontWeight) {
		this.defaultFontWeight = defaultFontWeight;
		return this;
	}

	public MineTextFlow setdDefaultFontFill(Color defaultFontFill) {
		this.defaultFontFill = defaultFontFill;
		return this;
	}

	private List<HighlightString> getHighlightStrings() {
		if (highlightStrings == null) {
			highlightStrings = EclipseStoreKeeper.root().userSettings().computeHighlightStrings(hs -> hs.filter(HighlightString::isEnabled).toList());
		}
		return highlightStrings;
	}
}
