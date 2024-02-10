package de.minetrain.minechat.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.Emote.EmoteSize;
import de.minetrain.minechat.main.Main;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class MineTextFlow extends TextFlow{
	private String DEFAULT_FONT_FAMILY = "Inter";
	private double DEFAULT_FONT_SIZE = 15d;
	private FontPosture DEFAULT_FONT_POSTURE = FontPosture.REGULAR;
	private FontWeight DEFAULT_FONT_WEIGHT = FontWeight.BOLD;
	private Color DEFAULT_FONT_FILL = Color.WHITE;
	
	private static final ConcurrentHashMap<Integer, Image> imageCache = new ConcurrentHashMap<Integer, Image>();
	
	public MineTextFlow(double fontSize) {
		this();
		DEFAULT_FONT_SIZE = fontSize;
	}

	public MineTextFlow() {
		Rectangle clip = new Rectangle();

		clip.widthProperty().bind(widthProperty());
		clip.heightProperty().bind(heightProperty());
		setClip(clip);
	}

	
	
	/**
     * Appends the specified string to the output string.
     *
     * @param string the string to be appended
     * @param color {@link htmlColors}
     * @return the IconStringBuilder object for method chaining
     */
	public MineTextFlow appendString(String string, HTMLColors color){
		return appendString(string, Color.web(color.getColorCode()));
	}
	
	/**
	 * Appends the specified string to the output string.
	 */
	public MineTextFlow appendString(String string){
		return appendString(string, DEFAULT_FONT_FAMILY, DEFAULT_FONT_SIZE, DEFAULT_FONT_POSTURE, DEFAULT_FONT_WEIGHT, DEFAULT_FONT_FILL);
	}
	
	/**
     * Appends the specified string to the output string.
     */
	public MineTextFlow appendString(String string, double font_size){
		return appendString(string, DEFAULT_FONT_FAMILY, font_size, DEFAULT_FONT_POSTURE, DEFAULT_FONT_WEIGHT, DEFAULT_FONT_FILL);
	}
	
	/**
     * Appends the specified string to the output string.
     */
	public MineTextFlow appendString(String string, Color font_fill){
		return appendString(string, DEFAULT_FONT_FAMILY, DEFAULT_FONT_SIZE, DEFAULT_FONT_POSTURE, DEFAULT_FONT_WEIGHT, font_fill);
	}
	
	/**
     * Appends the specified string to the output string.
     */
	public MineTextFlow appendString(String string, double font_size, Color font_fill){
		return appendString(string, DEFAULT_FONT_FAMILY, font_size, DEFAULT_FONT_POSTURE, DEFAULT_FONT_WEIGHT, font_fill);
	}
	
	/**
     * Appends the specified string to the output string.
     */
	public MineTextFlow appendString(String string, double font_size, FontWeight font_weight, Color font_fill){
		return appendString(string, DEFAULT_FONT_FAMILY, font_size, DEFAULT_FONT_POSTURE, font_weight, font_fill);
	}
	
	
	/**
     * Appends the specified string to the output string.
     */
	public MineTextFlow appendString(String string, String font_family, double font_size, FontPosture font_posture, FontWeight font_weight, Color font_fill){
		Text text = new Text(string);
		text.setFont(Font.font(font_family, font_weight, font_posture, font_size));
		text.setFill(font_fill);
		getChildren().add(text);
		return this;
	}
	
	public MineTextFlow appendHyperLink(String url){
		Hyperlink hyperlink = new Hyperlink(Main.extractDomain(url));
		hyperlink.setTooltip(new Tooltip(url));
		hyperlink.setOnAction(event -> {
			try{Desktop.getDesktop().browse(new URI(url));} catch (IOException | URISyntaxException e) { }
		});
		
		hyperlink.setFocusTraversable(false);
		hyperlink.setFont(Font.font(DEFAULT_FONT_FAMILY, DEFAULT_FONT_WEIGHT, DEFAULT_FONT_POSTURE, DEFAULT_FONT_SIZE));
		
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
	 * @param emote
	 * @param size
	 * @return
	 */
	public MineTextFlow appendEmote(Emote emote){
		ImageView imageView = new ImageView(emote.getEmoteImage(EmoteSize.SMALL)){
			@Override
			public double getBaselineOffset() {
				return getImage().getHeight() * 0.75;
			}
		};
//		imageView.setTranslateY(-((DEFAULT_FONT_SIZE - size.getSize()) / 2));
		appendImage(imageView);
		return this;
	}

	public MineTextFlow appendImage(Path imagePath){
		appendImage(new ImageView(imageCache.computeIfAbsent(imagePath.hashCode(), hash -> new Image(imagePath.toUri().toString()))));
		return this;
	}
	
	public MineTextFlow appendImage(Path imagePath, double pixelSize){
		appendImage(new ImageView(imageCache.computeIfAbsent(imagePath.hashCode(), hash -> new Image(imagePath.toUri().toString(), pixelSize, pixelSize, true, false))));
		return this;
	}
	
	public void appendImage(ImageView image){
		getChildren().add(image);
	}
	

	/**
     * Appends a space to the output string.
     * @return the {@link MineTextFlow} object for method chaining
     */
	public MineTextFlow appendSpace(){
		return appendString(" ");
	}

	/**
     * Appends a linesplit to the output string.
     * @return the {@link MineTextFlow} object for method chaining
     */
	public MineTextFlow appendLineSplit(){
		return appendString("\n");
	}

	
	public MineTextFlow clear(){
		getChildren().clear();
		return this;
	}
	
	public MineTextFlow setAlignment(TextAlignment alignment){
		setTextAlignment(alignment);
		return this;
	}
	
	
	public MineTextFlow setDefaultFontFamily(String default_font_family) {
		DEFAULT_FONT_FAMILY = default_font_family;
		return this;
	}

	public MineTextFlow setDefaultFontSize(double default_font_size) {
		DEFAULT_FONT_SIZE = default_font_size;
		return this;
	}

	public MineTextFlow setDefaultFontPosture(FontPosture default_font_posture) {
		DEFAULT_FONT_POSTURE = default_font_posture;
		return this;
	}

	public MineTextFlow setDefaultFontWeight(FontWeight default_font_weight) {
		DEFAULT_FONT_WEIGHT = default_font_weight;
		return this;
	}

	public MineTextFlow setdDefaultFontFill(Color default_font_fill) {
		DEFAULT_FONT_FILL = default_font_fill;
		return this;
	}
	
	
	
	
}
