package de.minetrain.minechat.gui.obj;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import de.minetrain.minechat.config.obj.ChannelMacros;
import de.minetrain.minechat.config.obj.ChannelMacros.MacroRow;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.objectdata.ChannelData;
import de.minetrain.minechat.gui.frames.ChatWindow;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.frames.settings.channel.ChannelSettingsFrame;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.obj.chat.userinput.textarea.SuggestionObj;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.ChannelStatistics;

public class ChannelTab {
	public static final Map<String, String> channelDisplayNameList = new HashMap<String, String>();
	private static final Map<String, ChannelTab> byId = new HashMap<String, ChannelTab>();//channelId -> channelTab
	private static final ChannelSettingsFrame channelSettingsFrame = new ChannelSettingsFrame();
	private TabButtonType tabType;
	private String channelId;
	private ImageIcon texture;
	private JButton tabButton;
	private String displayName;
	private String channelName;
	private boolean moderator;
	private boolean liveState = false; //NOTE: This may not me synct with the current twitch state.
	private MainFrame mainFrame;
	private List<String> greetingTexts;
	private List<String> goodByTexts;
	private List<String> returnTexts;
	private static List<ActionListener> editWindowActions = new ArrayList<ActionListener>();
	private ChannelMacros macros;
	private ChatWindow chatWindow;
	private ChannelStatistics statistics = new ChannelStatistics(this);
	
	private JLabel tabLabel;
//	private ChannelMacros macros;
	
	
	public ChannelTab(MainFrame mainFrame, JButton button, TabButtonType tab, JLabel nameLabel) {
		channelId = DatabaseManager.getChannelTabIndexDatabase().getChannelId(tab);
		
		this.mainFrame = mainFrame;
		this.chatWindow = new ChatWindow(this);
		chatWindow.setLocation(8, 186);
		chatWindow.setVisible(false);
		this.mainFrame.getContentPane().add(chatWindow);
		
		this.texture = Main.TEXTURE_MANAGER.getMainFrame_Blank();
		this.tabType = tab;
		this.tabButton = button;
		
		if(tab.isFirstRow()){
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
			macros = new ChannelMacros("0");
		}else{
			loadData(channelId);
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

	public void reload(String channelId) {
		loadData(channelId);
		tabLabel.setText(getTabName());
		tabLabel.setForeground(liveState ? Color.RED : ColorManager.FONT);
	}
	
	
	private void loadData(String channelId) {
		if(!this.channelId.equals(channelId)){
			statistics.save(true);
			chatWindow.clear();
			this.channelId = channelId;
		}
		
		editWindowActions.forEach(editWindowAction -> {
			if(Arrays.asList(this.tabButton.getActionListeners()).contains(editWindowAction)){
				this.tabButton.removeActionListener(editWindowAction);
			}
		});
		
		if(channelId.equals("0")) {
			if(channelName != null || channelId.equals("")){
				TwitchManager.leaveChannel(channelName);
			}
			
			channelId = "0";
			displayName = null;
			channelName = null;
			moderator = false;
			liveState = false;
			greetingTexts = null;
			goodByTexts = null;
			returnTexts = null;
			
			ActionListener actionListener = new ActionListener(){public void actionPerformed(ActionEvent e){openEditFrame();}};
			editWindowActions.add(actionListener);
			this.tabButton.addActionListener(actionListener);
		}else{
			ChannelData channelById = DatabaseManager.getChannel().getChannelById(channelId);
			
			if(channelById != null){
				channelName = channelById.getLoginName();
				displayName = channelById.getDisplayName();
				moderator = channelById.getChatRole().equalsIgnoreCase("moderator") || channelById.getChatRole().equalsIgnoreCase("vip");
				greetingTexts = Arrays.asList(channelById.getGreetingText().split("\n"));
				goodByTexts = Arrays.asList(channelById.getGoodbyText().split("\n"));
				returnTexts = Arrays.asList(channelById.getReturnText().split("\n"));
				chatWindow.chatStatusPanel.getinputArea().addToDictionary(new SuggestionObj("@"+getChannelName(), null), 0);// TODO Only if he is not alrady in there.
				this.texture = Main.TEXTURE_MANAGER.getByTabButton(tabType);
				TwitchManager.joinChannel(channelName);
				
				channelDisplayNameList.put(channelName, displayName);
				byId.put(channelId, this);
			}else{
				loadData("0");
			}
			
		}
		
		
		loadMacros(channelId);
	}

	public void loadMacros(String channelId) {
		if(macros == null){
			macros = new ChannelMacros(channelId);
			return;
		}
		
		macros.reloadMacros(channelId);
	}
	
	public ChannelSettingsFrame openEditFrame() {
		return channelSettingsFrame.setData(this);
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
		mainFrame.emoteButton0.setIcon(getMacros().getEmote_1(row).getEmote().getImageIcon());
		mainFrame.emoteButton1.setIcon(getMacros().getEmote_2(row).getEmote().getImageIcon());
		mainFrame.emoteButton2.setIcon(getMacros().getEmote_3(row).getEmote().getImageIcon());
		mainFrame.emoteButton3.setIcon(getMacros().getEmote_4(row).getEmote().getImageIcon());
		mainFrame.emoteButton4.setIcon(getMacros().getEmote_5(row).getEmote().getImageIcon());
		mainFrame.emoteButton5.setIcon(getMacros().getEmote_6(row).getEmote().getImageIcon());
		mainFrame.emoteButton6.setIcon(getMacros().getEmote_7(row).getEmote().getImageIcon());
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
		return !channelId.equals("0");
	}


	public TabButtonType getTabType() {
		return tabType;
	}


	public String getConfigID() {
		return channelId;
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

	public List<String> getGoodbyTexts() {
		return goodByTexts;
	}

	public List<String> getReturnTexts() {
		return returnTexts;
	}


	public JLabel getTabLabel() {
		return tabLabel;
	}

	public Path getProfileImagePath() {
		return Path.of(TextureManager.texturePath+"Icons/"+channelId+"/profile_75.png");
	}

	public Path getProfileImagePath80() {
		return Path.of(TextureManager.texturePath+"Icons/"+channelId+"/profile_80.png");
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
	
	/**
	 * Channel_id, ChannelTab
	 * @return
	 */
	public static Map<String, ChannelTab> getAll(){
		return byId;
	}
	
	public void setLiveState(boolean state){
		this.liveState = state;
		if(tabLabel.getText().equals(getDisplayName())){
			reload(channelId);
		}
	}
	
}
