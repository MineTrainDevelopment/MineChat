package de.minetrain.minechat.gui.obj.buttons;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import de.minetrain.minechat.config.obj.MacroObject;
import de.minetrain.minechat.gui.frames.MacroEditFrame;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.obj.TitleBar;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.MessageManager;

/**
 * A button used for Emote macros.
 * When right-clicked, it opens an emote selector window to choose an emote, and then prompts the user to enter a macro output.
 * Once the output is entered, it saves the macro with the selected emote and output to the configuration file
 * for the current channel and reloads the macros for the current tab.
 * <br>Extends the {@link MineButton} class.
 * 
 * @author MineTrain/Justin
 * @since 14.05.2023
 * @version 1.0
 * @see MineButton
 */

public class MacroEmoteButton extends MineButton{
	private static final long serialVersionUID = 7041760851916874910L;
	public JLabel textureLabel;
	private record ToolTipCacheKey(String channelId, ButtonType type){};
	private static final HashMap<ToolTipCacheKey, JToolTip> toolTipCache = new HashMap<ToolTipCacheKey, JToolTip>();
	private final MacroEditFrame macroEditFrame;

	/**
	 * A button used for Emote macros. 
	 * When right-clicked, it opens an emote selector window to choose an emote, and then prompts the user to enter a macro output.
	 * Once the output is entered, it saves the macro with the selected emote and output to the configuration file
	 * for the current channel and reloads the macros for the current tab.
	 * 
	 * @param size     the dimensions of the button.
	 * @param location the location of the button on the frame.
	 * @param type     the type of macro button this is.
	 */
	public MacroEmoteButton(Dimension size, Point location, ButtonType type, MainFrame mainFrame) {
		super(size, location, type);
		setHolding(true);
        setToolTipText(type.name());
        macroEditFrame = new MacroEditFrame(mainFrame, "Configure this emote macro.");
		textureLabel = new JLabel();
		mainFrame.add(textureLabel);
		textureLabel.setLayout(new BorderLayout());
		textureLabel.setSize(getSize());
		textureLabel.setLocation(getLocation());
		textureLabel.setIcon(Main.TEXTURE_MANAGER.getMacroEmoteKey());
		textureLabel.add(this);
		addMouseListener(new MouseAdapter() {
			
			@Override
		    public void mouseEntered(MouseEvent evt) {
		    	textureLabel.setIcon(Main.TEXTURE_MANAGER.getMacroEmoteKeyHover());
		    }

		    @Override
		    public void mouseExited(MouseEvent evt) {
		        if (textureLabel.getIcon().equals(Main.TEXTURE_MANAGER.getMacroEmoteKeyHover())) {
		        	textureLabel.setIcon(Main.TEXTURE_MANAGER.getMacroEmoteKey());
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
					macroEditFrame.setData(getMacroObject(), true);
		        }
		    }
		});
	}
	
	private MacroObject getMacroObject() {
		return TitleBar.currentTab.getMacros().getMacro(getType(), TitleBar.currentTab.getMacros().getCurrentMacroRow());
	}
	
	
	public void buttonPressed(){
		if (textureLabel.getIcon().equals(Main.TEXTURE_MANAGER.getMacroEmoteKeyPressed())) {
            return;
        }
    	
    	Timer timer = new Timer(0, e -> {
        	textureLabel.setIcon(Main.TEXTURE_MANAGER.getMacroEmoteKey());
        });

    	timer.setRepeats(false);
    	timer.setInitialDelay(320);
    	timer.start();
        textureLabel.setIcon(Main.TEXTURE_MANAGER.getMacroEmoteKeyPressed());
        
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
	
	
	/**
	 * Overrides the setInvisible method in {@link MineButton}.
	 * Sets the button's focusability, opacity, content area filled, and border painting based on the given state.
	 * @param state true to make the button invisible, false to make it visible.
	 * @return this {@link MacroEmoteButton} object.
	 */
	@Override
	public MacroEmoteButton setInvisible(boolean state) {
		super.setInvisible(state);
		return this;
	}
	
	@Override
	public JToolTip createToolTip() {
		return toolTipCache.computeIfAbsent(
				new ToolTipCacheKey(TitleBar.currentTab.getConfigID(), getType()),
				key -> new MacroButtonToolTip(super.createToolTip(), getMacroObject()));
	}
	
}
