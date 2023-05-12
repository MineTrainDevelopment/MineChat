package de.minetrain.minechat.gui.obj;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.ImageIcon;

/**
 * This is a convenience class to handle creating a single composite icon from
 * several icons, and caching the created icons to eliminate duplicate work.
 * This class is basically used as a key into a map, allowing us to define both
 * a hashCode and equals in a single place.
 * 
 * <p> Pleas note, i stole this class!
 * <br> This code is from <a href="https://stackoverflow.com/users/115018/nate-w">Nate W</a>
 * on StackOverflow.
 */
public class CachedCompositeIcon {
	private static final byte ICON_PADDING = 2;
	private static final HashMap<CachedCompositeIcon, ImageIcon> CACHED_ICONS = new HashMap<CachedCompositeIcon, ImageIcon>(4);

	private final ImageIcon[] m_icons;

	public CachedCompositeIcon(final ImageIcon... icons) {
		m_icons = icons;
	}

	public ImageIcon getIcon() {
		if (!CACHED_ICONS.containsKey(this)) {
			CACHED_ICONS.put(this, lcl_combineIcons());
		}

		return CACHED_ICONS.get(this);
	}

	/**
	 * Generates an icon that is a composition of several icons by appending each
	 * icon together with some padding between them.
	 *
	 * @return An icon that is the concatenation of all the icons this was
	 *         constructed with.
	 */
	private ImageIcon lcl_combineIcons() {
		// First determine how big our composite icon will be; we need to know how wide
		// & tall to make it.
		int totalWidth = (m_icons.length - 1) * ICON_PADDING; // Take into account the padding between icons
		int minHeight = 0;
		for (int i = 0; i < m_icons.length; ++i) {
			totalWidth += m_icons[i].getIconWidth();
			if (m_icons[i].getIconHeight() > minHeight) {
				minHeight = m_icons[i].getIconHeight();
			}
		}

		// Create an image big enough and acquire the image canvas to draw on
		final BufferedImage compositeImage = new BufferedImage(totalWidth, minHeight, BufferedImage.TYPE_INT_ARGB);
		final Graphics graphics = compositeImage.createGraphics();

		// Iterate over the icons, painting each icon and adding some padding space
		// between them
		int x = 0;
		for (int i = 0; i < m_icons.length; ++i) {
			final ImageIcon icon = m_icons[i];
			graphics.drawImage(icon.getImage(), x, 0, null);
			x += icon.getIconWidth() + ICON_PADDING;
		}

		return new ImageIcon(compositeImage);
	}

	/** Generates a hash that takes into account the number of icons this composition includes and the hash &
     *  order of those icons.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int weakHash = m_icons.length;
        for ( int i = 0; i < m_icons.length; ++i ) {
            weakHash += m_icons[i].hashCode() * (i + 1);
        }
        return weakHash;
    }
}