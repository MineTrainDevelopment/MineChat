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
		output += string;
		return this;
	}
	
	/**
     * Appends the specified string to the output string.
     *
     * @param string the string to be appended
     * @param color {@link htmlColors}
     * @return the IconStringBuilder object for method chaining
     */
	public IconStringBuilder appendString(String string, htmlColors color){
		output += "<font color="+color.toString().toLowerCase()+">"+string+"</font>";
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
		return this;
	}
	
	/**
     * Appends the space to the output string.
     * @return the IconStringBuilder object for method chaining
     */
	public IconStringBuilder appendSpace(){
		output += " ";
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
	 * 
	 */
	public enum htmlColors{
		AQUA, BLACK, BLUE, FUCHSIA, GRAY, GREEN, LIME, MAROON, NAVY, OLIVE, PURPLE, RED, SILVER, TEAL, WHITE, YELLOW;
	}
}
