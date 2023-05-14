package de.minetrain.minechat.gui.obj.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import de.minetrain.minechat.config.obj.MacroObject;
import de.minetrain.minechat.gui.frames.AskToAddEmoteFrame;
import de.minetrain.minechat.gui.frames.EmoteSelector;
import de.minetrain.minechat.gui.frames.InputFrame;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.obj.TitleBar;
import de.minetrain.minechat.main.Main;

public class MacroButton extends MineButton{
	private static final long serialVersionUID = 1144702595268749652L;
	private JLabel icon = new JLabel();

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
					MacroObject macro = TitleBar.currentTab.getMacros().getMacro(type);
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
	            				String selectedEmote = emoteSelector.getSelectedEmote();
	            				
	    						while(!emoteSelector.isDisposed() && selectedEmote == null){
	    			            	try{Thread.sleep(250);}catch(InterruptedException ex){ }
	    			            }
	    		            	
	    		            	if(selectedEmote != null){
	    		            		emotePath = "%&%"+selectedEmote.replace("_BG", "");
	    		            	}
	            			}

	            			String input = (inputFrame.getNameInput().length()>0) ? inputFrame.getNameInput() : "Unknown";
	            			String output = (inputFrame.getOutputInput().length()>0) ? inputFrame.getOutputInput() : "Unknown macro... - "+type.name();
	            			Main.CONFIG.setString("Channel_"+TitleBar.currentTab.getConfigID()+".Macros."+type.getConfigIndex(), input+emotePath+"%-%"+output, true);
	            			TitleBar.currentTab.loadMacros(TitleBar.currentTab.getConfigID());
	            			Main.MAIN_FRAME.getTitleBar().changeTab(TitleBar.currentTab.getTabType(), TitleBar.currentTab);
	            		}
						
						
					}).start();
		        }
		    }
		});
	}
	
	@Override
	public MacroButton setInvisible(boolean state) {
		setFocusable(!state);
		setOpaque(!state);
		setContentAreaFilled(!state);
		setBorderPainted(!state);
		return this;
	}
	
	@Override
	public void setIcon(Icon iconImage) {
		icon.setIcon(iconImage);
		icon.setLocation(getX()+4, getY()-4);
		icon.setSize(EmoteSelector.BUTTON_SIZE, EmoteSelector.BUTTON_SIZE);
	}
	
	public void setData(MacroObject macro) {
		setIcon(macro.getTwitchEmote().getImageIcon());
		setText((macro.getButtonName().equalsIgnoreCase("null")) ? "" : macro.getButtonName());

		Font font = new Font(null, Font.BOLD, 10);
		while(getPreferredSize().getWidth() > getWidth()) {
		    font = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
		    setFont(font);
		}
	}
	
}
