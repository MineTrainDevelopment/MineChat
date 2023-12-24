package de.minetrain.minechat.gui.frames.settings.channel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.objectdata.ChannelData;
import de.minetrain.minechat.gui.emotes.ChannelEmotes;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.emotes.RoundetImageIcon;
import de.minetrain.minechat.gui.frames.parant.RoundedJPanel;
import de.minetrain.minechat.gui.frames.settings.channel.ChannelSettingsChannel.ChannelSettingsChannelData;
import de.minetrain.minechat.gui.frames.settings.channel.ChannelSettingsGeneral.ChannelSettingsGeneralData;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.obj.RoundedLineBorder;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;
import de.minetrain.minechat.utils.audio.AudioVolume;

public class ChannelSettingsFrame extends JFrame{
	private static final Logger logger = LoggerFactory.getLogger(ChannelSettingsFrame.class);
	private static final long serialVersionUID = -7891455101744081125L;
	private int mouseX, mouseY;
	private List<JPanel> subscriptionChips = new ArrayList<JPanel>();
	private List<JPanel> tabChips = new ArrayList<JPanel>();
	JPanel subscriptionTier0, subscriptionTier1, subscriptionTier2, subscriptionTier3;
	
	public Color backgroundColorLight = ColorManager.GUI_BACKGROUND_LIGHT_DEFAULT;
	public Color backgroundColorDark = ColorManager.GUI_BUTTON_BACKGROUND_DEFAULT;
	public Color backgroundColor = ColorManager.GUI_BACKGROUND_DEFAULT;
	public Color borderColor = Color.BLACK;
	public Color fontColor = Color.WHITE;
	public Color acentColor = Color.GREEN;
	public Color chipBackground = Color.decode("#8a8a8a");

	private ChannelSettingsChannel settingsTabChannel;
	private ChannelSettingsGeneral settingsTabGeneral;
	private JPanel contentPanel, nameStrut;
	private JLabel profilePicPanel, channelName;
	private JComboBox<String> channelSelector;
	
	private ChannelTab channelTab;
	private String channelId;
	private static HashMap<String, String> channelNameToIdCache = new HashMap<String, String>();//Channel_name, channel_id

	public ChannelSettingsFrame() {
		super("Channel settings <Channel_name>");
		
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(borderColor);
        setUndecorated(true);
        setLocationRelativeTo(null);// This needs to be parent frame.
//        setBackground(Color.PINK);
        setBackground(backgroundColorLight);
        setSize(600, 500);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));
		addMouseListener(MoiseListner());
		addMouseMotionListener(mouseMotionListner());
		setAlwaysOnTop(true);
		
		
		//Frame
		JPanel borderPanel = new JPanel();
        borderPanel.setBackground(borderColor);
        borderPanel.setLayout(null);
        getContentPane().add(borderPanel);
        
		contentPanel = new RoundedJPanel(backgroundColor, borderColor, getWidth()-10, getHeight()-10);
		contentPanel.setBackground(backgroundColor);
		contentPanel.setBounds(5, 5, contentPanel.getWidth(), contentPanel.getHeight());
		contentPanel.setLayout(null);
		borderPanel.add(contentPanel);
		
		JPanel frameStrut = new JPanel();
		frameStrut.setBackground(borderColor);
		frameStrut.setBounds(0, 100, 590, 4);
		contentPanel.add(frameStrut);

		
		
		
		//Frame buttons.
		JButton cancelButton = new JButton("");
		cancelButton.setIcon(Main.TEXTURE_MANAGER.getCancelButton());
		cancelButton.setBackground(contentPanel.getBackground());
		cancelButton.setMinimumSize(new Dimension(30, 30));
		cancelButton.setBorder(null);
		cancelButton.setToolTipText("Cancel");
		cancelButton.addActionListener(e -> setVisible(false));
		
		JButton confirmButton = new JButton("");
		confirmButton.setIcon(Main.TEXTURE_MANAGER.getConfirmButton());
		confirmButton.setBackground(contentPanel.getBackground());
		confirmButton.setMinimumSize(new Dimension(30, 30));
		confirmButton.setBorder(null);
		confirmButton.setToolTipText("Confirm");
		confirmButton.addActionListener(e -> collectData());

		JPanel buttonPanal = new JPanel(new BorderLayout());
		buttonPanal.setBorder(new EmptyBorder(2, 5, 2, 5));
		buttonPanal.setBackground(contentPanel.getBackground());
		buttonPanal.setBounds(523, 5, 65, 30);
		buttonPanal.add(confirmButton, BorderLayout.WEST);
		buttonPanal.add(cancelButton, BorderLayout.EAST);
		contentPanel.add(buttonPanal);
		
		
		
		channelSelector = new JComboBox<>();
		channelSelector.setBounds(380, 60, 200, 30);
		channelSelector.setForeground(Color.WHITE);
		channelSelector.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		channelSelector.getEditor().getEditorComponent().setBackground(channelSelector.getBackground());
		channelSelector.getEditor().getEditorComponent().setForeground(channelSelector.getForeground());
		channelSelector.setFont(new Font(null, Font.BOLD, 12));
		channelSelector.addItem("");
		channelSelector.setEditable(true);
		contentPanel.add(channelSelector);
		
        try {
        	ResultSet resultSet = DatabaseManager.getChannel().getAll();
			while(resultSet.next()){
				channelSelector.addItem(resultSet.getString("login_name"));
				channelNameToIdCache.put(resultSet.getString("login_name"), resultSet.getString("channel_id"));
			}
			DatabaseManager.commit();
		} catch (SQLException ex) {
			logger.error("Can´t load all channel data properly.",ex);
		}
        
        //channelSelector listner to update UI for selectet users.
        ((JTextComponent) channelSelector.getEditor().getEditorComponent()).getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
            	if(!isVisible()){return;}
            	channelSelector.requestFocusInWindow();
            	channelSelector.getEditor().getEditorComponent().requestFocusInWindow();
            	timer.setRepeats(false);
            	channelId = null;
            	timer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            	if(!isVisible()){return;}
            	channelSelector.requestFocusInWindow();
            	channelSelector.getEditor().getEditorComponent().requestFocusInWindow();
            	channelId = null;
            	timer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) { }

            Timer timer = new Timer(250, t -> update());
            Timer requestTwitchData = new Timer(3000, t -> getTwitchUserData());
            
            private void update(){
            	String currentInput = channelSelector.getSelectedItem().toString().strip().trim();
            	
                //format the login input, should it be a link.
                if(currentInput.contains("https://www.twitch.tv/")){
                	channelSelector.setSelectedItem(currentInput.replace("https://www.twitch.tv/", "").split("\\?")[0]);
                    currentInput = (String) channelSelector.getSelectedItem();
                }
                
                //Set the default values, depending if there is already data for the input login name.
                if(channelNameToIdCache.containsKey(currentInput.toLowerCase())){
                    ChannelData channelById = DatabaseManager.getChannel().getChannelById(channelNameToIdCache.get(currentInput.toLowerCase()));
                    if(channelById != null){
                    	requestTwitchData.stop();
                        setData(channelById);
                    }
                }else{
                	profilePicPanel.setIcon(Main.TEXTURE_MANAGER.getProfilePicLoading());
                	setChannelName("Loading...");
                	requestTwitchData.setRepeats(false);
                	requestTwitchData.restart();
                }
            }
            
            public void getTwitchUserData(){
            	String currentInput = channelSelector.getSelectedItem().toString().strip().trim();
            	if(currentInput.isEmpty()){
                	setChannelName("No channel selected!");
                	resetSubscriptionTier();
                	return;
            	}
            	
            	TwitchUserObj twitchUser = TwitchManager.getTwitchUser(TwitchApiCallType.LOGIN, currentInput);
            	if(!twitchUser.isDummy() && !twitchUser.getProfileImageUrl().isBlank()){
            		setData(twitchUser);
            		return;
            	}
            	
            	setChannelName("Invalid channel name");
            	resetSubscriptionTier();
            }
        });
        
		
		//Channel profile information
		profilePicPanel = new JLabel(Main.TEXTURE_MANAGER.getProfilePicLoading());
		profilePicPanel.setBounds(10, 10, 80, 80);
		contentPanel.add(profilePicPanel);
		
		channelName = new JLabel("No channel selected!");
		channelName.setBounds(95, 10, 350, 33);
		channelName.setForeground(fontColor);
		channelName.setFont(new Font("Inter", Font.BOLD, 30));
		contentPanel.add(channelName);
		
		nameStrut = new JPanel();
		nameStrut.setBackground(fontColor);
		int textWidth = channelName.getFontMetrics(channelName.getFont()).stringWidth(channelName.getText());
		nameStrut.setBounds(95, 46, textWidth <= 300 ? textWidth : 300, 2);
		contentPanel.add(nameStrut);
		
		
		
		
		//Subscription Clips.
		JPanel subscriptionContainer = new RoundedJPanel(backgroundColorDark, borderColor, 223, 38, 20);
		subscriptionContainer.setBounds(95, 52, subscriptionContainer.getWidth(), subscriptionContainer.getHeight());
		subscriptionContainer.setBorder(new RoundedLineBorder(borderColor, 2, 10));
		subscriptionContainer.setLayout(null);
		contentPanel.add(subscriptionContainer);
		
		subscriptionTier0 = createSubscriptionChipButton("OFF", 50, 30, chipBackground);
		subscriptionTier0.setBounds(4, 4, subscriptionTier0.getWidth(), subscriptionTier0.getHeight());
		subscriptionContainer.add(subscriptionTier0);
		
		subscriptionTier1 = createSubscriptionChipButton("Tier 1", 50, 30, chipBackground);
		subscriptionTier1.setBounds(59, 4, subscriptionTier1.getWidth(), subscriptionTier1.getHeight());
		subscriptionContainer.add(subscriptionTier1);
		
		subscriptionTier2 = createSubscriptionChipButton("Tier 2", 50, 30, chipBackground);
		subscriptionTier2.setBounds(114, 4, subscriptionTier2.getWidth(), subscriptionTier2.getHeight());
		subscriptionContainer.add(subscriptionTier2);
		
		subscriptionTier3 = createSubscriptionChipButton("Tier 3", 50, 30, chipBackground);
		subscriptionTier3.setBounds(169, 4, subscriptionTier3.getWidth(), subscriptionTier3.getHeight());
		subscriptionContainer.add(subscriptionTier3);
		
		
		//
		JPanel tabContainer = new RoundedJPanel(backgroundColorLight, backgroundColorLight, 534, 54, 30);
		tabContainer.setBounds(28, 114, tabContainer.getWidth(), tabContainer.getHeight());
		tabContainer.setLayout(null);
		contentPanel.add(tabContainer);
		
		JPanel tabGeneral = createTabChipButton("Gerneral", 160, 40, chipBackground);
		tabGeneral.setBounds(7, 7, tabGeneral.getWidth(), tabGeneral.getHeight());
		tabGeneral.setBackground(backgroundColorDark);
		tabGeneral.setForeground(backgroundColorDark);
		tabGeneral.getComponent(0).setForeground(acentColor);
		tabContainer.add(tabGeneral);
		
		JPanel tabMacros = createTabChipButton("Macros", 160, 40, chipBackground);
		tabMacros.setBounds(187, 7, tabMacros.getWidth(), tabMacros.getHeight());
		tabContainer.add(tabMacros);
		
		JPanel tabChannel = createTabChipButton("Channel", 160, 40, chipBackground);
		tabChannel.setBounds(367, 7, tabChannel.getWidth(), tabChannel.getHeight());
		tabContainer.add(tabChannel);
		
		
		
		//
		settingsTabChannel = new ChannelSettingsChannel(this, "Channel");
//		settingsTabChannel.setData(
//				"Pferdchen",
//				"VIP",
//				"default\\message0.mp3",
//				AudioVolume.VOLUME_30);
		
		settingsTabGeneral = new ChannelSettingsGeneral(this, "General");
		
		contentPanel.add(settingsTabChannel);
		contentPanel.add(settingsTabGeneral);
	}
	
	

	private JPanel createSubscriptionChipButton(String title, int width, int hight, Color background) {
		JPanel chip = new RoundedJPanel(background, borderColor, width, hight);
		chip.setLayout(null);
		
		JLabel chipLabel = new JLabel(title, SwingConstants.CENTER);
		chipLabel.setBounds(chip.getBounds());
		chipLabel.setForeground(borderColor);
		chipLabel.setFont(new Font("Inter", Font.BOLD, 15));
		chip.add(chipLabel);
		
		chip.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseExited(MouseEvent e) {
				if(chip.getBackground() != acentColor) {
					chip.setBackground(background);
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if(chip.getBackground() != acentColor) {
					chip.setBackground(Color.LIGHT_GRAY);
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				System.err.println("Cilckt chip - "+title);
				subscriptionChips.forEach(chips -> chips.setBackground(background));
				chip.setBackground(acentColor);
			}
		});
		
		subscriptionChips.add(chip);
		return chip;
	}

	private JPanel createTabChipButton(String title, int width, int hight, Color background) {
		JPanel chip = new RoundedJPanel(background, background, width, hight, 30);
//		chip.setBorder(new RoundedLineBorder(borderColor, 3, 24));
		chip.setLayout(null);
		
		JLabel chipLabel = new JLabel(title, SwingConstants.CENTER);
		chipLabel.setBounds(0, -1, chip.getBounds().width, chip.getBounds().height);
		chipLabel.setForeground(borderColor);
		chipLabel.setFont(new Font("Inter", Font.BOLD, 30));
		chip.add(chipLabel);
		
		chip.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseExited(MouseEvent e) {
				if(chip.getBackground() != backgroundColorDark) {
					chip.setBackground(background);
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if(chip.getBackground() != backgroundColorDark) {
					chip.setBackground(Color.LIGHT_GRAY);
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				System.err.println("Cilckt chip - "+title);
				tabChips.forEach(chips -> {
					chips.setBackground(background);
					chips.setForeground(background);
					chips.getComponent(0).setForeground(backgroundColor);
				});
				chipLabel.setForeground(acentColor);
				chip.setBackground(backgroundColorDark);
				chip.setForeground(backgroundColorDark);
				
				switch (title) {
				case "Channel":
					settingsTabChannel.setVisible(true);
					settingsTabGeneral.setVisible(false);
					break;
					
				case "Macros":
					settingsTabChannel.setVisible(false);
					settingsTabGeneral.setVisible(false);
					break;

				default:
					settingsTabChannel.setVisible(false);
					settingsTabGeneral.setVisible(true);
					break;
				}
			}
		});
		
		tabChips.add(chip);
		return chip;
	}
	
	
	public void collectData(){
		if(channelId == null || channelId.equals("0")){
			DatabaseManager.getChannelTabIndexDatabase().insert(channelTab.getTabType(), "0");
			channelTab.reload("0");
			setVisible(false);
			return;
		}
		
		ChannelSettingsChannelData dataChannel = settingsTabChannel.getData();
		ChannelSettingsGeneralData dataGeneral = settingsTabGeneral.getData();
		
		String subTier = subscriptionTier1.getBackground().equals(acentColor) ? "tier1"
				: subscriptionTier2.getBackground().equals(acentColor) ? "tier2"
				: subscriptionTier3.getBackground().equals(acentColor) ? "tier3"
				: "tier0";
		

		DatabaseManager.getChannelTabIndexDatabase().insert(channelTab.getTabType(), channelId);
		DatabaseManager.getChannel().insert(
				channelId,
				channelSelector.getSelectedItem().toString(),
				dataChannel.displayName().isBlank() ? channelSelector.getSelectedItem().toString() : dataChannel.displayName(),
				dataChannel.role(),
				null, //disable chat log
				dataGeneral.greetingText(),
				dataGeneral.goodbyText(),
				dataGeneral.returnText(),
				dataChannel.audioPath().isDummy() ? null : dataChannel.audioPath().getFilePathAsString(),
				dataChannel.volume());
		
		if(EmoteManager.getChannelEmotes(channelId) == null) {
			ArrayList<String> list = new ArrayList<String>();
			DatabaseManager.getEmote().insertChannel(channelId, subTier, list, list, list, list, list);
			TextureManager.downloadChannelEmotes(channelId);
			TextureManager.downloadBttvEmotes(channelId);
			TextureManager.downloadChannelBadges(channelId);
			DatabaseManager.getEmote().getAllChannels();
		}else{
			EmoteManager.getChannelEmotes(channelId).setSubTier(subTier);
		}
		
//		DatabaseManager.commit();
		EmoteManager.load();
		
		channelTab.reload(channelId);
		Main.MAIN_FRAME.getTitleBar().changeTab(channelTab);
		setVisible(false);
	}
	
	public ChannelSettingsFrame setData(ChannelTab tab){
		channelTab = tab;
		resetSubscriptionTier();

		if(!tab.isOccupied()){
			System.err.println("??????");
			this.channelId = "0";
			subscriptionTier0.setBackground(acentColor);
			try{channelSelector.setSelectedItem("");}catch(Exception e){ }
			settingsTabGeneral.setData(new ChannelSettingsGeneralData(
					"Hello {USER} HeyGuys\nWelcome {USER} HeyGuys",
					"By {USER}!\nHave a good one! {USER} <3",
					"Welcome back {USER} <3\nWB {USER} HeyGuys"),
					"0");
			
			settingsTabChannel.setData("", "Viewer", null, AudioVolume.VOLUME_70);
			setVisible(true);
			return this;
		}
		
		ChannelEmotes channelEmotes = EmoteManager.getChannelEmotes(tab.getConfigID());
		if(channelEmotes != null){
			switch (channelEmotes.getSubLevel()) {
			case "tier1": subscriptionTier1.setBackground(acentColor); break;
			case "tier2": subscriptionTier2.setBackground(acentColor); break;
			case "tier3": subscriptionTier3.setBackground(acentColor); break;
			default: subscriptionTier0.setBackground(acentColor); break;
			}
		}

		this.channelId = tab.getConfigID();
		profilePicPanel.setIcon(new RoundetImageIcon(tab.getProfileImagePath80(), 30));
		try{channelSelector.setSelectedItem(tab.getChannelName());}catch(Exception e){ }
		setChannelName(tab.getDisplayName());
		
		settingsTabGeneral.setData(new ChannelSettingsGeneralData(
			String.join("\n", tab.getGreetingTexts()),
			String.join("\n", tab.getGoodbyTexts()),
			String.join("\n", tab.getReturnTexts())),
			tab.getConfigID());

        ChannelData channelData = DatabaseManager.getChannel().getChannelById(tab.getConfigID());
		settingsTabChannel.setData(
				tab.getDisplayName(),
				channelData.getChatRole(),
				channelData.getAudioPath(),
				channelData.getAudioVolume()
				);
		
		setVisible(true);
		return this;
	}
	

	public ChannelSettingsFrame setData(TwitchUserObj twitchUser){
		resetSubscriptionTier();
		
		if(channelId == null || channelId.equals("0")){
			try{channelSelector.setSelectedItem(twitchUser.getLoginName());}catch(Exception e){ }
		}
		
		this.channelId = twitchUser.getUserId();
		subscriptionTier0.setBackground(acentColor);

		TextureManager.downloadProfileImage(twitchUser.getProfileImageUrl(), twitchUser.getUserId());
		profilePicPanel.setIcon(new RoundetImageIcon(Path.of(TextureManager.texturePath+"Icons/"+twitchUser.getUserId()+"/profile_80.png"), 30));
		setChannelName(twitchUser.getDisplayName());
		
		settingsTabGeneral.setData(new ChannelSettingsGeneralData(
				"Hello {USER} HeyGuys\nWelcome {USER} HeyGuys",
				"By {USER}!\nHave a good one! {USER} <3",
				"Welcome back {USER} <3\nWB {USER} HeyGuys"),
				"0");

		settingsTabChannel.setData(twitchUser.getDisplayName(), "Viewer", null, AudioVolume.VOLUME_70);
		return this;
	}
	

	private void setData(ChannelData data){
		resetSubscriptionTier();
		this.channelId = data.getChannelId();
		
		ChannelEmotes channelEmotes = EmoteManager.getChannelEmotes(data.getChannelId());
		if(channelEmotes != null){
			switch (channelEmotes.getSubLevel()) {
			case "tier1": subscriptionTier1.setBackground(acentColor); break;
			case "tier2": subscriptionTier2.setBackground(acentColor); break;
			case "tier3": subscriptionTier3.setBackground(acentColor); break;
			default: subscriptionTier0.setBackground(acentColor); break;
			}
		}

		profilePicPanel.setIcon(new RoundetImageIcon(Path.of(TextureManager.texturePath+"Icons/"+data.getChannelId()+"/profile_80.png"), 30));
		try{channelSelector.setSelectedItem(data.getLoginName());}catch(Exception e){ }
		setChannelName(data.getDisplayName());
		
		settingsTabGeneral.setData(new ChannelSettingsGeneralData(
			String.join("\n", data.getGreetingText()),
			String.join("\n", data.getGoodbyText()),
			String.join("\n", data.getReturnText())),
			data.getChannelId());

        ChannelData channelData = DatabaseManager.getChannel().getChannelById(data.getChannelId());
		settingsTabChannel.setData(
				data.getDisplayName(),
				channelData.getChatRole(),
				channelData.getAudioPath(),
				channelData.getAudioVolume());
	}



	
	public void toggleProfilePicVisible(){
		if(profilePicPanel.getIcon().equals(Main.TEXTURE_MANAGER.getProfilePicLoading()) && channelTab.isOccupied()){
			profilePicPanel.setIcon(new RoundetImageIcon(channelTab.getProfileImagePath80(), 30));
			return;
		}

		profilePicPanel.setIcon(Main.TEXTURE_MANAGER.getProfilePicLoading());
	}
	
	public void setChannelName(String name){
		channelName.setText(name.strip().trim());
		int textWidth = channelName.getFontMetrics(channelName.getFont()).stringWidth(channelName.getText());
		nameStrut.setBounds(95, 46, textWidth <= 300 ? textWidth : 300, 2);
	}
	
	private void resetSubscriptionTier() {
		subscriptionTier0.setBackground(chipBackground);
		subscriptionTier1.setBackground(chipBackground);
		subscriptionTier2.setBackground(chipBackground);
		subscriptionTier3.setBackground(chipBackground);
	}
	
	@Override
	public void dispose() {
		settingsTabChannel.stopTestSound();
		super.dispose();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		//This is nesesery for the content panels to look as indent.
		settingsTabChannel.setVisible(false);
	}
	
	@Override
	public void setVisible(boolean b) {
		setLocationRelativeTo(Main.MAIN_FRAME);
		super.setVisible(b);
		
		if(!b){
			settingsTabChannel.stopTestSound();
		}
	}
	
	
	private MouseAdapter MoiseListner() {
		return new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        };
	}

	private MouseAdapter mouseMotionListner() {
		return new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int newX = e.getXOnScreen() - mouseX;
                int newY = e.getYOnScreen() - mouseY;

                setLocation(newX, newY);
            }
        };
	}

}
