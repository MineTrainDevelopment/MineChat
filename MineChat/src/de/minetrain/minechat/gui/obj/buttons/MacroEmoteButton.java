package de.minetrain.minechat.gui.obj.buttons;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.frames.EmoteSelector;
import de.minetrain.minechat.gui.frames.InputFrame;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.obj.TitleBar;
import de.minetrain.minechat.main.Main;

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
		addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        if (SwingUtilities.isRightMouseButton(e)) {
		            System.out.println("rechts");
		            EmoteSelector emoteSelector = new EmoteSelector(Main.MAIN_FRAME, true);
		            
		            new Thread(() -> {
		            	Emote selectedEmote = emoteSelector.getSelectedEmote();
		            	
						while(!emoteSelector.isDisposed() && selectedEmote == null){
			            	try{Thread.sleep(250);}catch(InterruptedException ex){ }
			            }
		            	
		            	if(selectedEmote != null){
//		            		selectedEmote = selectedEmote.getFilePath().replace("_BG.png", selectedEmote.getFileFormat());
		            		
							String selectedEmoteName = selectedEmote.getName();
		            		InputFrame inputFrame;
	            			inputFrame = new InputFrame(Main.MAIN_FRAME, "Selected Emote:", selectedEmoteName, "Change output:", selectedEmoteName);
		            		
		            		while(!inputFrame.isDispose() && inputFrame.getOutputInput() == null){
				            	try{Thread.sleep(250);}catch(InterruptedException ex){ }
		            		}
		            		
		            		if(inputFrame.getOutputInput() != null){
		            			String output = (inputFrame.getOutputInput().length()>0) ? inputFrame.getOutputInput() : selectedEmoteName;
		            			String macroRow = TitleBar.currentTab.getMacros().getCurrentMacroRow().name().toLowerCase().substring(3);
		            			Main.CONFIG.setString("Channel_"+TitleBar.currentTab.getConfigID()+".Macros"+macroRow+"."+type.getConfigIndex(), (selectedEmote.getFilePath().replace("_BG", ""))+"%-%"+output, true);
		            			TitleBar.currentTab.loadMacros(TitleBar.currentTab.getConfigID());
		            			Main.MAIN_FRAME.getTitleBar().changeTab(TitleBar.currentTab.getTabType(), TitleBar.currentTab);
		            		}
		            	}
		            }).start();
		        }
		    }
		});
	}
	
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
	
}
