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
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import de.minetrain.minechat.config.ConfigManager;
import de.minetrain.minechat.config.obj.ChannelMacros;
import de.minetrain.minechat.gui.frames.ChatWindow;
import de.minetrain.minechat.gui.frames.EditChannelFrame;
import de.minetrain.minechat.gui.frames.MainFrame;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.TwitchManager;

public class ChannelTab {
	private ChannelTab thisObject;
	private TabButtonType tabType;
	private String configID;
	private ImageIcon texture;
	private JButton tabButton;
	private String displayName;
	private String channelName;
	private boolean moderator;
	private List<String> greetingTexts;
	private Long spamTriggerAmound; //Messages
	private Long spamDeprecateAfter; //Seconds
	private ActionListener editWindowAction;
	private ChannelMacros macros;
	private ChatWindow chatWindow = new ChatWindow(this);
	
	private JLabel tabLabel;
//	private ChannelMacros macros;
	
	
	public ChannelTab(MainFrame mainFrame, JButton button, TabButtonType tab) {
		ConfigManager config = Main.CONFIG;
		configID = ""+config.getLong(tab.getConfigPath(), 0);
		chatWindow.setLocation(8, 186);
		chatWindow.setVisible(false);
		mainFrame.getContentPane().add(chatWindow);
		
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
			spamTriggerAmound = 4l;
			spamDeprecateAfter = 5l;
			editWindowAction = new ActionListener(){public void actionPerformed(ActionEvent e){openEditFrame();}};
			this.tabButton.addActionListener(getEditWindowAction());
			macros = new ChannelMacros(true);
		}else{
			loadData(configID);
		}
	
		tabLabel = new JLabel(getDisplayName(), SwingConstants.CENTER);
		tabLabel.setVisible(true);
		tabLabel.setForeground(Color.WHITE);
	}

	public void reload(String configID) {
		loadData(configID);
		tabLabel.setText(displayName);
	}
	
	
	private void loadData(String configID) {
		this.configID = configID;
		ConfigManager config = Main.CONFIG;
		String configPath = "Channel_"+configID+".";
		channelName = config.getString(configPath+"Name");
		displayName = config.getString(configPath+"DisplayName");
		moderator = (config.getString(configPath+"ChannelRole").equalsIgnoreCase("moderator") ? true : false);
		greetingTexts = config.getStringList(configPath+"GreetingText");
		spamTriggerAmound = config.getLong(configPath+"SpamButton.TriggerAmoundMessages", 4);
		spamDeprecateAfter = config.getLong(configPath+"SpamButton.DeprecateAfterSeconds", 5);
		TwitchManager.joinChannel(channelName);
		loadMacros(configID);
	}

	public void loadMacros(String configID) {
		macros = new ChannelMacros(configID);
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

//	public ChannelMacros getMacros() {
//		return macros;
//	}
	
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


	public Long getSpamTriggerAmound() {
		return spamTriggerAmound;
	}


	public Long getSpamDeprecateAfter() {
		return spamDeprecateAfter;
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
	
	

	
}
