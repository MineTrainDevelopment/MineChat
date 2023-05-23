package de.minetrain.minechat.gui.obj.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import de.minetrain.minechat.config.obj.MacroObject;
import de.minetrain.minechat.gui.obj.TitleBar;
import de.minetrain.minechat.twitch.MessageManager;

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
		addActionListener(new ActionListener() {
			
			/**
			 * If the button type is a macro type, it retrieves the associated macro's output from the current tab's macros and sends it as a message using the MessageManager.
			 * 
			 * @param e the ActionEvent object.
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				if(type == null || type == ButtonType.NON){
					return;
				}
				
				if(type == ButtonType.GREET){
					TitleBar.currentTab.getChatWindow().greetingsManager.sendGreetingToAll();
				}
				
				if((type.name().toLowerCase().startsWith("macro") || type.name().toLowerCase().startsWith("emote"))){
					MacroObject macro = TitleBar.currentTab.getMacros().getMacro(type);
					if(!macro.getMacroOutput().equals(">null<")){
						System.out.println("send message");
						MessageManager.sendMessage(macro.getMacroOutput());
					}
				}
				
				switch (type) {
				case STOP_QUEUE:
					MessageManager.getMessageHandler().clearQueue();
					break;
					
				default:
					break;
				}
				
			}
		});
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
		while(getPreferredSize().getWidth() > getWidth()) {
		    font = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
		    setFont(font);
		}
	}
}
