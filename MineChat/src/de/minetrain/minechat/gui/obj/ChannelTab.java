package de.minetrain.minechat.gui.obj;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.minetrain.minechat.config.ConfigManager;
import de.minetrain.minechat.config.obj.ChannelMacros;
import de.minetrain.minechat.gui.frames.EditChannelFrame;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Main;

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
	
	private JLabel tabLabel;
//	private ChannelMacros macros;
	
	
	public ChannelTab(JButton button, TabButtonType tab) {
		ConfigManager config = Main.CONFIG;
		configID = ""+config.getLong(tab.getConfigPath(), 0);
		this.texture = Main.TEXTURE_MANAGER.getByTabButton(tab);
		this.tabType = tab;
		this.thisObject = this;
		this.tabButton = button;
		
		if(!isOccupied()){
			displayName = "";
			moderator = false;
			greetingTexts = null;
			spamTriggerAmound = 4l;
			spamDeprecateAfter = 5l;
			editWindowAction = new ActionListener(){public void actionPerformed(ActionEvent e){new EditChannelFrame(Main.MAIN_FRAME, thisObject);}};
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
		loadMacros(configID);
	}

	public void loadMacros(String configID) {
		macros = new ChannelMacros(configID);
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
	
	

	
}
