package de.minetrain.minechat.gui.obj.buttons;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import de.minetrain.minechat.config.obj.MacroObject;
import de.minetrain.minechat.gui.frames.EmoteSelector;
import de.minetrain.minechat.gui.frames.MacroEditFrame;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.obj.TitleBar;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.MessageManager;

/**
 * A button used for message macros. 
 * <br>Provides functionality to set the button text, macro output, and associated emote.
 * <br>Extends the {@link MineButton} class.
 * 
 * @author MineTrain/Justin
 * @since 14.05.23
 * @version 1.0
 */

public class MacroButton extends MineButton{
	private static final long serialVersionUID = 1144702595268749652L;
	private JLabel icon = new JLabel();
	public JLabel textureLabel;
	private record ToolTipCacheKey(String channelId, ButtonType type){};
	private static final HashMap<ToolTipCacheKey, JToolTip> toolTipCache = new HashMap<ToolTipCacheKey, JToolTip>();
	private final MacroEditFrame macroEditFrame;

	/**
	 * A button used for message macros. 
	 * <br>Provides functionality to set the button text, macro output, and associated emote.
	 * <br>Extends the {@link MineButton} class.
	 * 
	 * @param size 		the {@link Dimension} of the button.
	 * @param location 	the location of the button on the frame. ({@link Point})
	 * @param type 		the {@link ButtonType} of button this is.
	 * @param mainFrame the {@link MainFrame} of the application.
	 */
	public MacroButton(Dimension size, Point location, ButtonType type, MainFrame mainFrame) {
		super(size, location, type);
		setHorizontalAlignment(JTextField.RIGHT);
		setForeground(Color.WHITE);
		setHolding(true);
        setToolTipText(type.name());
		
        macroEditFrame = new MacroEditFrame(mainFrame, "Configure this emote macro.");
		textureLabel = new JLabel();
		mainFrame.add(textureLabel);
		textureLabel.setLayout(new BorderLayout());
		textureLabel.setSize(getSize());
		textureLabel.setLocation(getLocation());
		textureLabel.setIcon(Main.TEXTURE_MANAGER.getMacroKey());
		textureLabel.add(icon);
		textureLabel.add(this);
		
		addMouseListener(new MouseAdapter() {
			@Override
		    public void mouseEntered(MouseEvent evt) {
		    	textureLabel.setIcon(Main.TEXTURE_MANAGER.getMacroKeyHover());
		    }

		    @Override
		    public void mouseExited(MouseEvent evt) {
		        if (textureLabel.getIcon().equals(Main.TEXTURE_MANAGER.getMacroKeyHover())) {
		        	textureLabel.setIcon(Main.TEXTURE_MANAGER.getMacroKey());
		        }
		    }
		    
		    @Override
            public void mousePressed(MouseEvent event) {
		    	buttonHoldTimer.start(); // Start the timer when the mouse button is pressed
            }

            @Override
            public void mouseReleased(MouseEvent event) {
            	buttonHoldTimer.stop(); // Stop the timer when the mouse button is released
            }
		    
			@Override
		    public void mouseClicked(MouseEvent event) {
		        if (SwingUtilities.isLeftMouseButton(event)) {
		        	buttonPressed();
		        }
		        
		        if (SwingUtilities.isRightMouseButton(event)) {
		        	macroEditFrame.setData(getMacroObject(), false);
		        }
		    }

		});
	}
	
	public void buttonPressed(){
		if (textureLabel.getIcon().equals(Main.TEXTURE_MANAGER.getMacroKeyPressed())) {
            return;
        }
    	
    	Timer timer = new Timer(0, e -> {
        	textureLabel.setIcon(Main.TEXTURE_MANAGER.getMacroKey());
        });

    	timer.setRepeats(false);
    	timer.setInitialDelay(330);
    	timer.start();
        textureLabel.setIcon(Main.TEXTURE_MANAGER.getMacroKeyPressed());
        
		MacroObject macro = TitleBar.currentTab.getMacros().getMacro(getType(), TitleBar.currentTab.getMacros().getCurrentMacroRow());
		if(!macro.getRawOutput().contains(">null<")){
			System.out.println("send message");
			MessageManager.sendMessage(macro.getOutput());
		}
	}
	
	Timer buttonHoldTimer = new Timer(holdingMillisecond, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        	if(isHolding()){
        		buttonPressed();
        	}
        }
    });
	
	private MacroObject getMacroObject() {
		return TitleBar.currentTab.getMacros().getMacro(getType(), TitleBar.currentTab.getMacros().getCurrentMacroRow());
	}
	
	
	/**
	 * Sets the button's focusability, opacity, content area filled, and border painting based on the given state.
	 * 
	 * @param state true to make the button invisible, false to make it visible.
	 * @return this {@link MacroButton} object.
	 */
	@Override
	public MacroButton setInvisible(boolean state) {
		super.setInvisible(state);
		return this;
	}
	
	/**
	 * Sets the icon for the button and adjusts its position and size.
	 * 
	 * @param iconImage the icon to set for the button.
	 */
	@Override
	public void setIcon(Icon iconImage) {
		icon.setIcon(iconImage);
		icon.setLocation(getX()+5, getY()-2);
		icon.setSize(EmoteSelector.BUTTON_SIZE, EmoteSelector.BUTTON_SIZE);
	}
	
	/**
	 * super.setIcon
	 */
	public void setBackgroundIcon(Icon iconImage) {
		super.setIcon(iconImage);
	}
	
	/**
	 * Sets the data for the {@link MacroButton} based on the given {@link MacroObject}.
	 * 
	 * @param macro the {@link MacroObject} containing the data for the button.
	 */
	public void setData(MacroObject macro) {
		setIcon(macro.getEmote().getImageIcon());
		setText((macro.getTitle().equalsIgnoreCase("null")) ? "" : macro.getTitle());
		setToolTipText(macro.getRawOutput());
	}
	
	@Override
	public void setText(String text) {
		super.setText(text.length()>8 ? text.substring(0, 6)+".." : text);
	}
	
	@Override
	public JToolTip createToolTip() {
		return toolTipCache.computeIfAbsent(
				new ToolTipCacheKey(TitleBar.currentTab.getConfigID(), getType()),
				key -> new MacroButtonToolTip(super.createToolTip(), getMacroObject()));
	}
	
}
