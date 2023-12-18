package de.minetrain.minechat.gui.obj;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

import de.minetrain.minechat.gui.utils.ColorManager;

/**
 * I stolle the code from <a href="URL#https://stackoverflow.com/a/62613662/20851022">Stack Overflow</a> and optimized it for my use.
 * @author Pweisj
 */
public class CustomJSliderUI extends BasicSliderUI {

    private static final int TRACK_HEIGHT = 8;
    private static final int TRACK_WIDTH = 8;
    private static final int TRACK_ARC = 5;
    private static final Dimension THUMB_SIZE = new Dimension(20, 20);
    private final RoundRectangle2D.Float trackShape = new RoundRectangle2D.Float();

    private Color thumbColor = ColorManager.GUI_ACCENT_DEFAULT;
    private Color backgroundColor = ColorManager.GUI_BACKGROUND_LIGHT_DEFAULT;
    private Color shadowColor = backgroundColor;
    private Color sliderColor = ColorManager.GUI_BACKGROUND_DEFAULT;

    public CustomJSliderUI(final JSlider b) {
        super(b);
    }

    @Override
    protected void calculateTrackRect() {
        super.calculateTrackRect();
        if (isHorizontal()) {
            trackRect.y = trackRect.y + (trackRect.height - TRACK_HEIGHT) / 2;
            trackRect.height = TRACK_HEIGHT;
        } else {
            trackRect.x = trackRect.x + (trackRect.width - TRACK_WIDTH) / 2;
            trackRect.width = TRACK_WIDTH;
        }
        trackShape.setRoundRect(trackRect.x, trackRect.y, trackRect.width, trackRect.height, TRACK_ARC, TRACK_ARC);
    }

    @Override
    protected void calculateThumbLocation() {
        super.calculateThumbLocation();
        if (isHorizontal()) {
            thumbRect.y = trackRect.y + (trackRect.height - thumbRect.height) / 2;
        } else {
            thumbRect.x = trackRect.x + (trackRect.width - thumbRect.width) / 2;
        }
    }

    @Override
    protected Dimension getThumbSize() {
        return THUMB_SIZE;
    }

    private boolean isHorizontal() {
        return slider.getOrientation() == JSlider.HORIZONTAL;
    }

    @Override
    public void paint(final Graphics g, final JComponent c) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paint(g, c);
    }

    @Override
    public void paintTrack(final Graphics g) {
    	Graphics2D graphics = (Graphics2D) g;
        Shape clip = graphics.getClip();

        boolean horizontal = isHorizontal();
        boolean inverted = slider.getInverted();

        // Paint shadow.
        graphics.setColor(shadowColor);
        graphics.fill(trackShape);

        // Paint track background.
        graphics.setColor(backgroundColor);
        graphics.setClip(trackShape);
        trackShape.y += 1;
        graphics.fill(trackShape);
        trackShape.y = trackRect.y;

        graphics.setClip(clip);

        // Paint selected track.
        if (horizontal) {
            boolean ltr = slider.getComponentOrientation().isLeftToRight();
            if (ltr) inverted = !inverted;
            int thumbPos = thumbRect.x + thumbRect.width / 2;
            if (inverted) {
                graphics.clipRect(0, 0, thumbPos, slider.getHeight());
            } else {
                graphics.clipRect(thumbPos, 0, slider.getWidth() - thumbPos, slider.getHeight());
            }

        } else {
            int thumbPos = thumbRect.y + thumbRect.height / 2;
            if (inverted) {
                graphics.clipRect(0, 0, slider.getHeight(), thumbPos);
            } else {
                graphics.clipRect(0, thumbPos, slider.getWidth(), slider.getHeight() - thumbPos);
            }
        }
        graphics.setColor(sliderColor);
        graphics.fill(trackShape);
        graphics.setClip(clip);
    }

    @Override
    public void paintThumb(final Graphics g) {
        g.setColor(thumbColor);
        g.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
    }
    
    public void setSliderColor(Color thumb, Color slider){
    	this.thumbColor = thumb;
    	this.sliderColor = slider;
    }
    
    public void setBackgroundColor(Color background){
    	this.backgroundColor = background;
    	this.shadowColor = background;
    }
    
    @Override
    public void paintFocus(final Graphics g) {}
}
