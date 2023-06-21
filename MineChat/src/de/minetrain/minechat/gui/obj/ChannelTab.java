package de.minetrain.minechat.gui.obj;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import de.minetrain.minechat.config.ConfigManager;
import de.minetrain.minechat.config.obj.ChannelMacros;
import de.minetrain.minechat.config.obj.ChannelMacros.MacroRow;
import de.minetrain.minechat.gui.frames.ChatWindow;
import de.minetrain.minechat.gui.frames.EditChannelFrame;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.ChannelStatistics;
import de.minetrain.minechat.utils.IconStringBuilder;

public class ChannelTab {
	private ChannelTab thisObject;
	private TabButtonType tabType;
	private String configID;
	private ImageIcon texture;
	private JButton tabButton;
	private String displayName;
	private String channelName;
	private boolean moderator;
	private MainFrame mainFrame;
	private List<String> greetingTexts;
	private ActionListener editWindowAction;
	private ChannelMacros macros;
	private ChatWindow chatWindow = new ChatWindow(this);
	private ChannelStatistics statistics = new ChannelStatistics();
	
	private JLabel tabLabel;
//	private ChannelMacros macros;
	
	
	public ChannelTab(MainFrame mainFrame, JButton button, TabButtonType tab) {
		ConfigManager config = Main.CONFIG;
		configID = ""+config.getLong(tab.getConfigPath(), 0);
		chatWindow.setLocation(8, 186);
		chatWindow.setVisible(false);
		mainFrame.getContentPane().add(chatWindow);
		this.mainFrame = mainFrame;
		
		this.texture = Main.TEXTURE_MANAGER.getByTabButton(tab);
		this.tabType = tab;
		this.thisObject = this;
		this.tabButton = button;
		
		tabButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					System.out.println("Right klick");
					switch (tab) {
					case TAB_MAIN:
						ChannelTab mainTab = Main.MAIN_FRAME.getTitleBar().getMainTab();
						if(mainTab.isOccupied()){mainTab.openEditFrame();}
						break;
						
					case TAB_SECOND:
						ChannelTab secondTab = Main.MAIN_FRAME.getTitleBar().getSecondTab();
						if(secondTab.isOccupied()){secondTab.openEditFrame();}
						break;
						
					case TAB_THIRD:
						ChannelTab thirdTab = Main.MAIN_FRAME.getTitleBar().getThirdTab();
						if(thirdTab.isOccupied()){thirdTab.openEditFrame();}
						break;

					default:
						break;
					}
				}
			}
		});
		
		if(!isOccupied()){
			displayName = "";
			moderator = false;
			greetingTexts = null;
			editWindowAction = new ActionListener(){public void actionPerformed(ActionEvent e){openEditFrame();}};
			this.tabButton.addActionListener(getEditWindowAction());
			macros = new ChannelMacros(null);
		}else{
			loadData(configID);
		}
	
		tabLabel = new JLabel(getTabName(), SwingConstants.CENTER);
		tabLabel.setVisible(true);
		tabLabel.setForeground(Color.WHITE);
	}

	private String getTabName() {
		String profilePic = TextureManager.profilePicPath.replace("{ID}", configID).replace("{SIZE}", "25");
		return new IconStringBuilder().appendIcon(profilePic, true).appendString(getDisplayName()).toString();
	}

	public void reload(String configID) {
		loadData(configID);
		tabLabel.setText(getTabName());
	}
	
	
	private void loadData(String configID) {
		this.configID = configID;
		ConfigManager config = Main.CONFIG;
		String configPath = "Channel_"+configID+".";
		channelName = config.getString(configPath+"Name");
		displayName = config.getString(configPath+"DisplayName");
		moderator = (config.getString(configPath+"ChannelRole").equalsIgnoreCase("moderator") ? true : false);
		greetingTexts = config.getStringList(configPath+"GreetingText");
		TwitchManager.joinChannel(channelName);
		loadMacros(configID);
	}

	public void loadMacros(String configID) {
		if(macros == null){
			macros = new ChannelMacros(configID);
			return;
		}
		
		macros.reloadMacros(configID);
	}
	
	public void openEditFrame(){
		new EditChannelFrame(Main.MAIN_FRAME, thisObject);
	}
	
	public JLabel getTabLabelWithData(Point point, Dimension size) {
		tabLabel.setLocation(point);
		tabLabel.setSize(size);
		return tabLabel;
	}
	
	public ChannelTab offsetButton(TabButtonType offset){
		Point location = tabButton.getLocation();
		location.setLocation(location.getX(), offset.getOffset(tabType, location.y));
		tabButton.setLocation(location);
		chatWindow.setVisible((tabType.equals(offset)) ? true : false);
		return this;
	}
	
	public void loadMacroRow(MacroRow row){
		if(row == null){row = getMacros().getCurrentMacroRow();}
		getMacros().setCurrentMacroRow(row);
		mainFrame.emoteButton0.setIcon(new ImageIcon(getMacros().getEmote_1(row).getEmotePath()));
		mainFrame.emoteButton1.setIcon(new ImageIcon(getMacros().getEmote_2(row).getEmotePath()));
		mainFrame.emoteButton2.setIcon(new ImageIcon(getMacros().getEmote_3(row).getEmotePath()));
		mainFrame.emoteButton3.setIcon(new ImageIcon(getMacros().getEmote_4(row).getEmotePath()));
		mainFrame.emoteButton4.setIcon(new ImageIcon(getMacros().getEmote_5(row).getEmotePath()));
		mainFrame.emoteButton5.setIcon(new ImageIcon(getMacros().getEmote_6(row).getEmotePath()));
		mainFrame.emoteButton6.setIcon(new ImageIcon(getMacros().getEmote_7(row).getEmotePath()));
//		mainFrame.emoteButton1.setIcon(getMacros().getEmote_2(row).getTwitchEmote().getImageIcon());
//		mainFrame.emoteButton2.setIcon(getMacros().getEmote_3(row).getTwitchEmote().getImageIcon());
//		mainFrame.emoteButton3.setIcon(getMacros().getEmote_4(row).getTwitchEmote().getImageIcon());
//		mainFrame.emoteButton4.setIcon(getMacros().getEmote_5(row).getTwitchEmote().getImageIcon());
//		mainFrame.emoteButton5.setIcon(getMacros().getEmote_6(row).getTwitchEmote().getImageIcon());
//		mainFrame.emoteButton6.setIcon(getMacros().getEmote_7(row).getTwitchEmote().getImageIcon());

		mainFrame.macroButton0.setData(getMacros().getMacro(ButtonType.MACRO_1, row));
		mainFrame.macroButton1.setData(getMacros().getMacro(ButtonType.MACRO_2, row));
		mainFrame.macroButton2.setData(getMacros().getMacro(ButtonType.MACRO_3, row));
		mainFrame.macroButton3.setData(getMacros().getMacro(ButtonType.MACRO_4, row));
		mainFrame.macroButton4.setData(getMacros().getMacro(ButtonType.MACRO_5, row));
		mainFrame.macroButton5.setData(getMacros().getMacro(ButtonType.MACRO_6, row));
		
		mainFrame.leftRowButton.setIcon((row == MacroRow.ROW_0) ? null : Main.TEXTURE_MANAGER.getRowArrowLeft());
		mainFrame.rightRowButton.setIcon((row == MacroRow.ROW_2) ? null : Main.TEXTURE_MANAGER.getRowArrowRight());
	}

	public boolean isOccupied(){
		return !configID.equals("0");
	}


	public ChannelTab getThisObject() {
		return thisObject;
	}


	public TabButtonType getTabType() {
		return tabType;
	}


	public String getConfigID() {
		return configID;
	}


	public ImageIcon getTexture() {
		return texture;
	}


	public JButton getTabButton() {
		return tabButton;
	}


	public String getDisplayName() {
		return displayName;
	}


	public List<String> getGreetingTexts() {
		return greetingTexts;
	}


	public JLabel getTabLabel() {
		return tabLabel;
	}

	public String getProfileImagePath() {
		return TextureManager.texturePath+"Icons/"+configID+"/profile_75.png";
	}

	public String getChannelName() {
		return channelName;
	}

	public ChannelMacros getMacros() {
		return macros;
	}

	public ActionListener getEditWindowAction() {
		return editWindowAction;
	}

	public boolean isModerator() {
		return moderator;
	}

	public ChatWindow getChatWindow() {
		return chatWindow;
	}

	public ChannelStatistics getStatistics() {
		return statistics;
	}
	
}
