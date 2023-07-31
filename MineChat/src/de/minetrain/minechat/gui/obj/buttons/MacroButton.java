package de.minetrain.minechat.gui.obj.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import de.minetrain.minechat.config.obj.MacroObject;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.frames.AskToAddEmoteFrame;
import de.minetrain.minechat.gui.frames.EmoteSelector;
import de.minetrain.minechat.gui.frames.InputFrame;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.obj.TitleBar;
import de.minetrain.minechat.main.Main;

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
		mainFrame.add(icon);
		setHorizontalAlignment(JTextField.RIGHT);
		setForeground(Color.WHITE);
		
		addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        if (SwingUtilities.isRightMouseButton(e)) {
		            System.out.println("rechts");
		            
		            InputFrame inputFrame;
					MacroObject macro = TitleBar.currentTab.getMacros().getMacro(type, TitleBar.currentTab.getMacros().getCurrentMacroRow());
					if(!macro.getButtonName().equalsIgnoreCase("null")){
			            inputFrame = new InputFrame(mainFrame, "Button text:", macro.getButtonName(), "Macro output:", macro.getMacroOutput());
		            }else{
		            	inputFrame = new InputFrame(mainFrame, "Button text:", "", "Macro output:", "");
		            }
					
					new Thread(() -> {
						while(!inputFrame.isDispose() && inputFrame.getOutputInput() == null){
			            	try{Thread.sleep(250);}catch(InterruptedException ex){ }
	            		}
	            		
	            		if(inputFrame.getOutputInput() != null){
	            			AskToAddEmoteFrame askToAddEmoteFrame = new AskToAddEmoteFrame(mainFrame);
	            			while(!askToAddEmoteFrame.isDispose()){
				            	try{Thread.sleep(250);}catch(InterruptedException ex){ }
		            		}
	            			
	            			String emotePath = "";
	            			if(askToAddEmoteFrame.isAddEmote()){
	            				EmoteSelector emoteSelector = new EmoteSelector(mainFrame, true);
	            				Emote selectedEmote = emoteSelector.getSelectedEmote();
	            				
	    						while(!emoteSelector.isDisposed() && selectedEmote == null){
	    			            	try{Thread.sleep(250);}catch(InterruptedException ex){ }
	    			            }
	    		            	
	    		            	if(selectedEmote != null){
	    		            		emotePath = "%&%"+selectedEmote.getFilePath().replace("_BG.png", selectedEmote.getFileFormat());
	    		            	}
	            			}

	            			String input = (inputFrame.getNameInput().length()>0) ? inputFrame.getNameInput() : "Unknown";
	            			String output = (inputFrame.getOutputInput().length()>0) ? inputFrame.getOutputInput() : "Unknown macro... - "+type.name();
	            			String macroRow = TitleBar.currentTab.getMacros().getCurrentMacroRow().name().toLowerCase().substring(3);
							Main.CONFIG.setString("Channel_"+TitleBar.currentTab.getConfigID()+".Macros"+macroRow+"."+type.getConfigIndex(), input+emotePath+"%-%"+output, true);
	            			TitleBar.currentTab.loadMacros(TitleBar.currentTab.getConfigID());
	            			Main.MAIN_FRAME.getTitleBar().changeTab(TitleBar.currentTab.getTabType(), TitleBar.currentTab);
	            		}
						
						
					}).start();
		        }
		    }
		});
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
		icon.setLocation(getX()+4, getY()-4);
		icon.setSize(EmoteSelector.BUTTON_SIZE, EmoteSelector.BUTTON_SIZE);
	}
	
	/**
	 * Sets the data for the {@link MacroButton} based on the given {@link MacroObject}.
	 * 
	 * @param macro the {@link MacroObject} containing the data for the button.
	 */
	public void setData(MacroObject macro) {
		setIcon(new ImageIcon(macro.getEmotePath()));
		setText((macro.getButtonName().equalsIgnoreCase("null")) ? "" : macro.getButtonName());
	}
	
	@Override
	public void setText(String text) {
		super.setText(text.length()>8 ? text.substring(0, 6)+".." : text);
	}
	
}
