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
import de.minetrain.minechat.gui.frames.EditChannelFrame;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Main;

public class ChannelTab {
	private ChannelTab thisObject;
	private TabButtonType tabType;
	private Long configID;
	private ImageIcon texture;
	private JButton tabButton;
	private String displayName;
	private String channelName;
	private List<String> greetingTexts;
	private Long spamTriggerAmound; //Messages
	private Long spamDeprecateAfter; //Seconds
	public ActionListener editWindowAction;
	
	private JLabel tabLabel;
//	private ChannelMacros macros;
	
	
	public ChannelTab(JButton button, TabButtonType tab) {
		ConfigManager config = Main.CONFIG;
		configID = config.getLong(tab.getConfigPath());
		this.texture = Main.TEXTURE_MANAGER.getByTabButton(tab);
		this.tabType = tab;
		this.thisObject = this;
		this.tabButton = button;
		
		if(!isOccupied()){
			displayName = "";
			greetingTexts = null;
			spamTriggerAmound = 4l;
			spamDeprecateAfter = 5l;
			editWindowAction = new ActionListener(){public void actionPerformed(ActionEvent e){new EditChannelFrame(Main.mainFrame, thisObject);}};
			this.tabButton.addActionListener(editWindowAction);
		}else{
			String configPath = "Channel_"+configID+".";
			channelName = config.getString(configPath+"Name");
			displayName = config.getString(configPath+"DisplayName");
			greetingTexts = config.getStringList(configPath+"GreetingText");
			spamTriggerAmound = config.getLong(configPath+"SpamButton.TriggerAmoundMessages", 4);
			spamDeprecateAfter = config.getLong(configPath+"SpamButton.DeprecateAfterSeconds", 5);
		}
	
		tabLabel = new JLabel(getDisplayName(), SwingConstants.CENTER);
		tabLabel.setVisible(true);
		tabLabel.setForeground(Color.WHITE);
	}

	public void reload(JButton button, TabButtonType tab, Long configID) {
		this.configID = configID;
		ConfigManager config = Main.CONFIG;
		String configPath = "Channel_"+configID+".";
		channelName = config.getString(configPath+"Name");
		displayName = config.getString(configPath+"DisplayName");
		greetingTexts = config.getStringList(configPath+"GreetingText");
		spamTriggerAmound = config.getLong(configPath+"SpamButton.TriggerAmoundMessages", 4);
		spamDeprecateAfter = config.getLong(configPath+"SpamButton.DeprecateAfterSeconds", 5);
		tabLabel.setText(displayName);
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
		return configID != 0;
	}


	public ChannelTab getThisObject() {
		return thisObject;
	}


	public TabButtonType getTabType() {
		return tabType;
	}


	public Long getConfigID() {
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
	
	

	
}
