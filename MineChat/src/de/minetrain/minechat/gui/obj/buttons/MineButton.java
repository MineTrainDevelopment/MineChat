package de.minetrain.minechat.gui.obj.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.Timer;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.config.obj.ChannelMacros.MacroRow;
import de.minetrain.minechat.gui.obj.TitleBar;
import de.minetrain.minechat.twitch.MessageManager;
import de.minetrain.minechat.twitch.TwitchManager;

/**
 * A custom button used in the application.
 * <br>Extends the {@link JButton} class.
 * 
 * <p>This button is designed to handle specific actions for different types of buttons, such as {@link MacroButton}.
 * <br>It provides methods to set colors, visibility, and handles the actionPerformed event for buttons.
 * 
 * <p>NOTE: This class assumes the existence of a MessageManager class and a TitleBar class with a static currentTab field.
 * 
 * @author MineTrain/Justin
 * @since 14.05.2023
 * @version 1.0
 */

public class MineButton extends JButton{
	private static final long serialVersionUID = -8580714214977222615L;
	private boolean holding = false;
	private ButtonType type;
	public int holdingMillisecond = 500;

	/**
	 * A custom button used in the application.
	 * <br>Extends the {@link JButton} class.
	 * 
	 * <p>This button is designed to handle specific actions for different types of buttons, such as {@link MacroButton}.
	 * <br>It provides methods to set colors, visibility, and handles the actionPerformed event for buttons.
	 * 
	 * <p>NOTE: This class assumes the existence of a MessageManager class and a TitleBar class with a static currentTab field.
	 * 
	 * @param size the dimensions of the button.
	 * @param location the location of the button on the frame.
	 * @param type the type of button.
	 */

	public MineButton(Dimension size, Point location, ButtonType type) {
		setSize((size != null) ? size : new Dimension(0, 0));
		setLocation((location != null) ? location : new Point(0, 0));
		this.type = type;
		
		addActionListener(new ActionListener() {
			
			/**
			 * If the button type is a macro type, it retrieves the associated macro's output from the current tab's macros and sends it as a message using the MessageManager.
			 * @param e the ActionEvent object.
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				onButtonPressed(type);
				
			}
		});
		
        Timer timer = new Timer(holdingMillisecond, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	if(isHolding()){
            		onButtonPressed(type);
            	}
            }
        });		
		addMouseListener(new MouseAdapter() {
		    @Override
            public void mousePressed(MouseEvent event) {
                timer.start(); // Start the timer when the mouse button is pressed
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                timer.stop(); // Stop the timer when the mouse button is released
            }
		});
	}
	
	private void onButtonPressed(ButtonType type) {
		if(type == null || type == ButtonType.NON){
			return;
		}
		
		if(type == ButtonType.STATUS){
			TwitchManager.twitch.getChat().reconnect();
		}
		
		MacroRow currentRow = TitleBar.currentTab.getMacros().getCurrentMacroRow();
		if(type == ButtonType.CHANGE_ROW_LEFT){
			TitleBar.currentTab.loadMacroRow((currentRow == MacroRow.ROW_2) ? MacroRow.ROW_1 : MacroRow.ROW_0);
		}
		
		if(type == ButtonType.CHANGE_ROW_RIGHT){
			TitleBar.currentTab.loadMacroRow((currentRow == MacroRow.ROW_0) ? MacroRow.ROW_1 : MacroRow.ROW_2);
		}
		
//		if(type.name().toLowerCase().startsWith("emote")){
//			MacroObject macro = TitleBar.currentTab.getMacros().getMacro(type, TitleBar.currentTab.getMacros().getCurrentMacroRow());
//			if(!macro.getRawOutput().contains(">null<")){
//				System.out.println("send message");
//				MessageManager.sendMessage(macro.getOutput());
//			}
//		}

		
		switch (type) {
		case STOP_QUEUE:
			MessageManager.getDefaultMessageHandler().clearQueue();
			MessageManager.getModeratorMessageHandler().clearQueue();
			break;
			
		default:
			break;
		}
	}
	
	/**
	 * Sets the foreground and background colors of the button.
	 * 
	 * @param foreGround the foreground color to set.
	 * @param backGround the background color to set.
	 * @return this {@link MineButton} object.
	 */
	public MineButton setColors(Color foreGround, Color backGround){
		setForeground(foreGround);
		setBackground(backGround);
		return this;
	}
	
	/**
	 * Sets the button's focusability, opacity, content area filled, and border painting based on the given state.
	 * 
	 * @param state true to make the button invisible, false to make it visible.
	 * @return this {@link MineButton} object.
	 */
	public MineButton setInvisible(boolean state){
		setFocusable(!state);
		setOpaque(!state);
		setContentAreaFilled(!state);
		setBorderPainted(!state);
		return this;
	}
	
	/**
     * Sets the button's text.
     * <br>Sets the icon, button text, and adjusts the font size if needed to fit the button's width.
     * 
     * @param text the string used to set the text
     * @see #getText
     */
	@Override
	public void setText(String text) {
		super.setText(text);
		
		Font font = new Font(null, Font.BOLD, 15);
        FontMetrics metrics = getFontMetrics(font);
        
        while (metrics.stringWidth(text) > getWidth() - 50) {
            font = font.deriveFont(font.getSize2D() - 1f);
            metrics = getFontMetrics(font);
        }
        
        setFont(font);
	}

	public ButtonType getType(){
		return type;
	}
	
	public boolean isHolding() {
		return Settings.holdToSendMessages ? holding : false;
	}

	public void setHolding(boolean holding) {
		this.holding = holding;
	}

	public int getHoldingMillisecond() {
		return holdingMillisecond;
	}

	public void setHoldingMillisecond(int holdingMillisecond) {
		this.holdingMillisecond = holdingMillisecond;
	}
	
	
}
