package de.minetrain.minechat.gui.obj;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.config.obj.ChannelMacros;
import de.minetrain.minechat.config.obj.ChannelMacros.MacroRow;
import de.minetrain.minechat.gui.frames.ChatWindow;
import de.minetrain.minechat.gui.frames.EditChannelFrame;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.obj.chat.userinput.textarea.SuggestionObj;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.ChannelStatistics;

public class ChannelTab {
	public static final Map<String, String> channelDisplayNameList = new HashMap<String, String>();
	public static final Map<String, ChannelTab> byId = new HashMap<String, ChannelTab>();//channelId -> channelTab
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
	private static List<ActionListener> editWindowActions = new ArrayList<ActionListener>();
	private ChannelMacros macros;
	private ChatWindow chatWindow;
	private ChannelStatistics statistics = new ChannelStatistics(this);
	
	private JLabel tabLabel;
//	private ChannelMacros macros;
	
	
	public ChannelTab(MainFrame mainFrame, JButton button, TabButtonType tab, JLabel nameLabel) {
		YamlManager config = Main.CONFIG;
		configID = ""+config.getLong(tab.getConfigPath(), 0);
		
		this.mainFrame = mainFrame;
		this.chatWindow = new ChatWindow(this);
		chatWindow.setLocation(8, 186);
		chatWindow.setVisible(false);
		this.mainFrame.getContentPane().add(chatWindow);
		
		this.texture = Main.TEXTURE_MANAGER.getMainFrame_Blank();
		this.tabType = tab;
		this.thisObject = this;
		this.tabButton = button;
		
		if(tab.isFirstRow()){
			System.err.println("Add mouseListner - " + tabButton);
			tabButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						System.out.println("Right klick");
						switch (tab) {
						case TAB_MAIN, TAB_MAIN_ROW_2:
							ChannelTab mainTab = Main.MAIN_FRAME.getTitleBar().getMainTab();
							if(mainTab.isOccupied()){mainTab.openEditFrame();}
							break;
							
						case TAB_SECOND, TAB_SECOND_ROW_2:
							ChannelTab secondTab = Main.MAIN_FRAME.getTitleBar().getSecondTab();
							if(secondTab.isOccupied()){secondTab.openEditFrame();}
							break;
							
						case TAB_THIRD, TAB_THIRD_ROW_2:
							ChannelTab thirdTab = Main.MAIN_FRAME.getTitleBar().getThirdTab();
							if(thirdTab.isOccupied()){thirdTab.openEditFrame();}
							break;
	
						default:
							break;
						}
					}
				}
			});
		}
		
		if(!isOccupied()){
			displayName = "";
			moderator = false;
			greetingTexts = null;
			
			ActionListener actionListener = new ActionListener(){public void actionPerformed(ActionEvent e){openEditFrame();}};
			editWindowActions.add(actionListener);
			this.tabButton.addActionListener(actionListener);
			macros = new ChannelMacros(null);
		}else{
			loadData(configID);
		}
	
		tabLabel = nameLabel;
		tabLabel.setText(getTabName());
		tabLabel.setVisible(true);
		tabLabel.setForeground(Color.WHITE);
	}

	private String getTabName() {
//		String profilePic = TextureManager.profilePicPath.replace("{ID}", configID).replace("{SIZE}", "18");
//		return new IconStringBuilder().appendIcon(profilePic, true).appendString(getDisplayName()).toString();
		return getDisplayName();
	}

	public void reload(String configID) {
		loadData(configID);
		tabLabel.setText(getTabName());
	}
	
	
	private void loadData(String configID) {
		this.configID = configID;
		
		editWindowActions.forEach(editWindowAction -> {
			if(Arrays.asList(this.tabButton.getActionListeners()).contains(editWindowAction)){
				this.tabButton.removeActionListener(editWindowAction);
			}
		});
		
		
		if(configID.equals("0")) {
			displayName = "";
			moderator = false;
			greetingTexts = null;
			
			ActionListener actionListener = new ActionListener(){public void actionPerformed(ActionEvent e){openEditFrame();}};
			editWindowActions.add(actionListener);
			this.tabButton.addActionListener(actionListener);
		}else{
			YamlManager config = Main.CONFIG;
			String configPath = "Channel_"+configID+".";
			channelName = config.getString(configPath+"Name");
			displayName = config.getString(configPath+"DisplayName");
			moderator = (config.getString(configPath+"ChannelRole").equalsIgnoreCase("moderator") ? true : false);
			greetingTexts = config.getStringList(configPath+"GreetingText");
			chatWindow.chatStatusPanel.getinputArea().addToDictionary(new SuggestionObj("@"+getChannelName(), null), 0);
			this.texture = Main.TEXTURE_MANAGER.getByTabButton(tabType);
			TwitchManager.joinChannel(channelName);
			
			channelDisplayNameList.put(channelName, displayName);
			byId.put(configID, this);
		}
		
		
		loadMacros(configID);
	}

	public void loadMacros(String configID) {
		if(macros == null){
			macros = new ChannelMacros(configID);
			return;
		}
		
		macros.reloadMacros(configID);
	}
	
	public void openEditFrame() {
		new EditChannelFrame(Main.MAIN_FRAME, thisObject);
	}
	
	public ChannelTab offsetButton(TabButtonType offset){
		Point location = tabButton.getLocation();
		location.setLocation(location.getX(), offset.getOffset(tabType, location.y));
		tabButton.setLocation(location);
//		chatWindow.setVisible((tabType.equals(offset)) ? true : false);
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

	public boolean isModerator() {
		return moderator;
	}

	public ChatWindow getChatWindow() {
		return chatWindow;
	}

	public ChannelStatistics getStatistics() {
		return statistics;
	}
	
	public MainFrame getMainFrame(){
		return mainFrame;
	}
	
	public static ChannelTab getById(String channelId){
		return byId.get(channelId);
	}
	
}
