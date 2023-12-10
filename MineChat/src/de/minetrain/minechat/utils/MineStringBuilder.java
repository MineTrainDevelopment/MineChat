package de.minetrain.minechat.utils;

import java.awt.Color;

import javax.swing.ImageIcon;

/**
 * The IconStringBuilder class is a utility class for building HTML strings that contain icons and text.
 * <br> It provides methods for appending text and icons to the string, as well as setting a prefix and suffix.
 * <br> The resulting HTML string can be retrieved using the toString() method.
 * 
 * @author MineTrain/Justin
 * @since 21.05.2023
 * @version 1.1
 */
public class MineStringBuilder {
	private int wordCount = 0;
	private int fontSize = 0;
	private String output="";
	private String prefix="";
	private String suffix="";
	//"<html><body>Username: <img src='file:data/texture/badges/Broadcaster/1.png'></body></html>"

	/**
     * Appends the specified string to the output string.
     *
     * @param string the string to be appended
     * @param color hex color code.
     * @return the IconStringBuilder object for method chaining
     */
	public MineStringBuilder appendString(String string, String hexColorCode){
		output += "<font color="+hexColorCode+"%FONT-SIZE%>"+string.replace("<", "&lt;").replace(">", "&gt;")+"</font>";
		wordCount += string.split(" ").length;
		return this;
	}
	
	/**
     * Appends the specified string to the output string.
     *
     * @param string the string to be appended
     * @param color {@link Color}
     * @return the IconStringBuilder object for method chaining
     */
	public MineStringBuilder appendString(String string, Color color){
		output += "<font color="+String.format("#%06x", color.getRGB() & 0x00FFFFFF)+"%FONT-SIZE%>"+string.replace("<", "&lt;").replace(">", "&gt;")+"</font>";
		wordCount += string.split(" ").length;
		return this;
	}
	
	/**
     * Appends the specified string to the output string.
     *
     * @param string the string to be appended
     * @param color {@link htmlColors}
     * @return the IconStringBuilder object for method chaining
     */
	public MineStringBuilder appendString(String string, HTMLColors color){
		appendString(string, color.getColorCode());
		return this;
	}
	
	/**
     * Appends the specified string to the output string.
     *
     * @param string the string to be appended
     * @return the IconStringBuilder object for method chaining
     */
	public MineStringBuilder appendString(String string){
		appendString(string, HTMLColors.WHITE);
		return this;
	}
	
	/**
     * Appends an icon to the output string.
     *
     * @param iconPath         the path of the icon image file - example: Example: data/texture/badges/Broadcaster/1.png
     * @param withPlaceholder  indicates whether to include a placeholder behind the icon
     * @return the IconStringBuilder object for method chaining
     */
	public MineStringBuilder appendIcon(String iconPath, boolean withPlaceholder){
		output += "<img src='file:"+iconPath+"'>" + (withPlaceholder ? "&nbsp;" : "");
//		if(fontSize == 0){setFontSize(iconPath);}
		wordCount++;
		return this;
	}
	
	/**
     * Appends a space to the output string.
     * @return the IconStringBuilder object for method chaining
     */
	public MineStringBuilder appendSpace(){
		output += " ";
		return this;
	}
	
	/**
     * Appends line split to the output string.
     * @return the IconStringBuilder object for method chaining
     */
	public MineStringBuilder appendLineSplit(){
		output += "<br>";
		return this;
	}
	
	/**
     * @param prefix the prefix string to be set
     * @return the IconStringBuilder object for method chaining
     */
	public MineStringBuilder setPrefix(String prefix){
		this.prefix = prefix;
		return this;
	}
	
	/**
     * @param prefix the prefix string to be set
     * @param color {@link htmlColors}
     * @return the IconStringBuilder object for method chaining
     */
	public MineStringBuilder setPrefix(String prefix, HTMLColors color){
		this.prefix = "<font color="+color+"%FONT-SIZE%>"+prefix.replace("<", "&lt;").replace(">", "&gt;")+"</font>";
		return this;
	}

	/**
     * @param suffix the suffix string to be set
     * @return the IconStringBuilder object for method chaining
     */
	public MineStringBuilder setSuffix(String suffix){
		this.suffix = suffix;
		return this;
	}

	/**
     * @param suffix the suffix string to be set
     * @param color {@link htmlColors}
     * @return the IconStringBuilder object for method chaining
     */
	public MineStringBuilder setSuffix(String suffix, HTMLColors color){
		this.suffix = "<font color="+color+"%FONT-SIZE%>"+suffix.replace("<", "&lt;").replace(">", "&gt;")+"</font>";
		return this;
	}

	/**
	 * Set the font size based on image height.
	 * @param iconPath system path only.
     */
	public MineStringBuilder setFontSize(String iconPath){
		fontSize = new ImageIcon(iconPath).getIconHeight()/3;
		return this;
	}

	/**
     * Clear all texts.
     * @return the IconStringBuilder object for method chaining
     */
	public MineStringBuilder clear(){
		this.wordCount = 0;
		this.fontSize = 0;
		this.suffix = "";
		this.prefix = "";
		this.output = "";
		return this;
	}
	
	/**
	 * Clear only the raw content string.
     * @return the IconStringBuilder object for method chaining
	 */
	public MineStringBuilder clearContent(){
		this.output = "";
		return this;
	}
	
	/**
     * Returns the HTML representation of the constructed string.
     * <br> The string is enclosed in HTML and body tags.
     *
     * @return the HTML string representation
     */
	@Override
	public String toString() {
		return "<html><body>"+prefix+output+suffix+"</body></html>".replace("%FONT-SIZE%", fontSize>0 ? " size="+fontSize : "");
	}
	
	/**
	 * Get the word cound
	 * @return
	 */
	public int getWordCount() {
		return wordCount;
	}
	
	/**
	 * @return the current content string without html hader and Pre/suffix.
	 */
	public String getRawContent(){
		return output;
	}
	
}
