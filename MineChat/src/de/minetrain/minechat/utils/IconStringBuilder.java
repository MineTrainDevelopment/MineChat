package de.minetrain.minechat.utils;

/**
 * The IconStringBuilder class is a utility class for building HTML strings that contain icons and text.
 * <br> It provides methods for appending text and icons to the string, as well as setting a prefix and suffix.
 * <br> The resulting HTML string can be retrieved using the toString() method.
 * 
 * @author MineTrain/Justin
 * @since 21.05.2023
 * @version 1.0
 */
public class IconStringBuilder {
	private int wordCount = 0;
	private String output="";
	private String prefix="";
	private String suffix="";
	//"<html><body>Username: <img src='file:data/texture/badges/Broadcaster/1.png'></body></html>"

	/**
     * Appends the specified string to the output string.
     *
     * @param string the string to be appended
     * @return the IconStringBuilder object for method chaining
     */
	public IconStringBuilder appendString(String string){
		output += string.replace("<", "&lt;").replace(">", "&gt;");
		wordCount++;
		return this;
	}
	
	/**
     * Appends the specified string to the output string.
     *
     * @param string the string to be appended
     * @param color hex color code.
     * @return the IconStringBuilder object for method chaining
     */
	public IconStringBuilder appendString(String string, String hexColorCode){
		output += "<font color="+hexColorCode+">"+string.replace("<", "&lt;").replace(">", "&gt;")+"</font>";
		wordCount++;
		return this;
	}
	
	/**
     * Appends the specified string to the output string.
     *
     * @param string the string to be appended
     * @param color {@link htmlColors}
     * @return the IconStringBuilder object for method chaining
     */
	public IconStringBuilder appendString(String string, HTMLColors color){
		appendString(string, color.getColorCode());
		return this;
	}
	
	/**
     * Appends an icon to the output string.
     *
     * @param iconPath         the path of the icon image file - example: Example: data/texture/badges/Broadcaster/1.png
     * @param withPlaceholder  indicates whether to include a placeholder behind the icon
     * @return the IconStringBuilder object for method chaining
     */
	public IconStringBuilder appendIcon(String iconPath, boolean withPlaceholder){
		output += "<img src='file:"+iconPath+"'>" + (withPlaceholder ? "&nbsp;" : "");
		wordCount++;
		return this;
	}
	
	/**
     * Appends a space to the output string.
     * @return the IconStringBuilder object for method chaining
     */
	public IconStringBuilder appendSpace(){
		output += " ";
		return this;
	}
	
	/**
     * Appends line split to the output string.
     * @return the IconStringBuilder object for method chaining
     */
	public IconStringBuilder appendLineSplit(){
		output += "<br>";
		return this;
	}
	
	/**
     * @param prefix the prefix string to be set
     * @return the IconStringBuilder object for method chaining
     */
	public IconStringBuilder setPrefix(String prefix){
		this.prefix = prefix;
		return this;
	}

	/**
     * @param suffix the suffix string to be set
     * @return the IconStringBuilder object for method chaining
     */
	public IconStringBuilder setSuffix(String suffix){
		this.suffix = suffix;
		return this;
	}

	/**
     * Clear all texts.
     * @return the IconStringBuilder object for method chaining
     */
	public IconStringBuilder clear(){
		this.wordCount = 0;
		this.suffix = "";
		this.prefix = "";
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
		return "<html><body>"+prefix+output+suffix+"</body></html>";
	}
	
	/**
	 * Get the word cound
	 * @return
	 */
	public int getWordCount() {
		return wordCount;
	}
}
