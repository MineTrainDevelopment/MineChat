package de.minetrain.minechat.utils;

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
