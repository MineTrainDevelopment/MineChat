package de.minetrain.minechat.gui.obj.buttons;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

public class MacroEmoteButton extends MineButton{
	private static final long serialVersionUID = 7041760851916874910L;

	public MacroEmoteButton(Dimension size, Point location, ButtonType type) {
		super(size, location, type);
		addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        if (SwingUtilities.isRightMouseButton(e)) {
		            System.out.println("rechts");
		            //Open the emote selecotr, and updatet the button.
		        }
		    }
		});
	}
	
}
