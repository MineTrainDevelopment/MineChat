package de.minetrain.minechat.gui.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.objectdata.ChannelData;
import de.minetrain.minechat.gui.emotes.ChannelEmotes;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;

public class EditChannelFrame extends JDialog {
	private static final Logger logger = LoggerFactory.getLogger(EditChannelFrame.class);
	private static final long serialVersionUID = 8773100712568642831L;
	private Map<String, String> channelsFromConfig = new HashMap<String, String>();
	private JTextField loginNameField, displayNameField;
    private JComboBox<String> userTypeComboBox, cloneMacrosComboBox;
    private JCheckBox emotesCheckBox, badgesCheckBox, subscriberCheckBox;
    private Thread loginChangeListner;
    private int mouseX, mouseY;
    private ChannelTab editedTab;
    private static final int fontSize = 13;
    
    public EditChannelFrame(MainFrame mainFrame, ChannelTab tab) {
    	super(mainFrame, "Twitch Channel Einstellungen", true);
    	this.editedTab = tab;
        setSize(400, 250);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setBackground(ColorManager.GUI_BACKGROUND);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 2));
        panel.setBackground(ColorManager.GUI_BACKGROUND);
//        panel.setLayout(new GridLayout(7, 2));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        addMouseListener(MoiseListner());
        addMouseMotionListener(mouseMotionListner());


        JLabel seperator0 = new JLabel(" ");
        seperator0.setForeground(Color.WHITE);
        seperator0.setHorizontalTextPosition(SwingConstants.CENTER);
        
        JLabel seperator1 = new JLabel(" ");
        seperator1.setForeground(Color.WHITE);
        seperator1.setHorizontalTextPosition(SwingConstants.CENTER);

        panel.add(createInputPanel("Channel login / URL:", "login"));
        panel.add(seperator0);
        panel.add(createInputPanel("Display name:", "display"));
        panel.add(createDropdownPanel("User Typ:", new String[]{"Viewer", "Moderator", "VIP"}, "type"));
        panel.add(seperator1);
        panel.add(createDropdownPanel("Clone macros:", new String[]{" "}, "clone"));
        panel.add(createCheckBox("Install channel emotes:", "emote"));
        panel.add(createCheckBox("Install channel badges:", "badge"));
        panel.add(createCheckBox("Are you subscriber on this channel:", "subscriber"));
        
        loginNameField.setText(tab.getChannelName());
        
        ResultSet resultSet = DatabaseManager.getChannel().getAll();
        try {
			while(resultSet.next()){
				channelsFromConfig.put(resultSet.getString("login_name"), "Channel_"+resultSet.getString("channel_id"));
				//Add all channels to the clone selector.
				cloneMacrosComboBox.addItem(resultSet.getString("display_name")+" - ("+resultSet.getString("login_name")+")");
			}
			DatabaseManager.commit();
		} catch (SQLException ex) {
			logger.error("Can´t load all channel data properly.",ex);
		}
		
		//Check if the current login input is alrady existing.
        loginChangeListner = new Thread(() -> {
        	String previousInput = "";
        	while(true){
        		try{Thread.sleep(250);}catch(InterruptedException e){ }
				
        		String currentInput = loginNameField.getText().toLowerCase();
				if(!currentInput.equals(previousInput)){
					previousInput = currentInput;
					
					//format the login input, should it be a link.
					if(currentInput.contains("https://www.twitch.tv/")){
						loginNameField.setText(loginNameField.getText().replace("https://www.twitch.tv/", "").split("\\?")[0]);
						currentInput = loginNameField.getText().toLowerCase();
					}
        			
					//Set the default values, depending if there is alrady data for the inpit login name.
        			if(channelsFromConfig.containsKey(currentInput)){
        				ChannelData channelById = DatabaseManager.getChannel().getChannelById(channelsFromConfig.get(currentInput).replace("Channel_", ""));
        				if(channelById != null){
        					displayNameField.setText(channelById.getDisplayName());
        					userTypeComboBox.setSelectedIndex(channelById.getChatRole().equalsIgnoreCase("moderator") ? 1 : channelById.getChatRole().equalsIgnoreCase("vip") ? 2 : 0);
        					badgesCheckBox.setSelected(false);
        					emotesCheckBox.setSelected(false);
        					
        					ChannelEmotes channelEmotes = EmoteManager.getChannelEmotes(channelsFromConfig.get(currentInput).replace("Channel_", ""));
        					subscriberCheckBox.setSelected(channelEmotes != null ? channelEmotes.isSub() : false);
        				}
        			}else{
        				displayNameField.setText(loginNameField.getText()); //Sync display name with login name.
        				userTypeComboBox.setSelectedIndex(0);
        				badgesCheckBox.setSelected(true);
        				emotesCheckBox.setSelected(true);
        				subscriberCheckBox.setSelected(false);
        			}
        		}
        	}
        });
        
        loginChangeListner.start();
        
        
        // Erstellen der Buttons
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setBackground(ColorManager.GUI_BACKGROUND);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setBorder(null);
        confirmButton.addActionListener(closeWindow(false));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(ColorManager.GUI_BACKGROUND);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(null);
        cancelButton.addActionListener(closeWindow(true));

        // Hinzufügen der Buttons am unteren Rand des JFrame
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 9, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(7, 2, 2, 2));
        buttonPanel.setBackground(ColorManager.GUI_BORDER);
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        panel.add(buttonPanel);
        
        // Setzen der Breite der Buttons auf die Breite der Textfelder
        int buttonWidth = (loginNameField.getPreferredSize().width + displayNameField.getPreferredSize().width) / 2;
        cancelButton.setPreferredSize(new Dimension(buttonWidth, cancelButton.getPreferredSize().height));
        confirmButton.setPreferredSize(new Dimension(buttonWidth, confirmButton.getPreferredSize().height));

        // Erstellen des Haupt-Panels und HinzufÃ¼gen der Elemente
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 7));
        mainPanel.setBackground(ColorManager.GUI_BACKGROUND);
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Setzen der Position des Frames relativ zum Haupt-Frame
        Point location = mainFrame.getLocation();
        location.setLocation(location.x+50, location.y+200);
        setLocation(location);

        // Hinzufügen des Haupt-Panels zum Frame und Anzeigen des Frames
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }
    
    private JPanel createCheckBox(String title, String type) {
        JPanel panel = new JPanel(new BorderLayout());
        JCheckBox checkBox = new JCheckBox();
        panel.addMouseListener(new MouseAdapter(){@Override public void mousePressed(MouseEvent e){checkBox.setSelected(!checkBox.isSelected());}});
        
        JLabel text = new JLabel(" "+title, JLabel.LEFT);
        text.setForeground(ColorManager.FONT);
		panel.add(text, BorderLayout.WEST);
        
		checkBox.setSelected(true);
		checkBox.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        panel.add(checkBox, BorderLayout.EAST);
        
        panel.setBackground(ColorManager.GUI_BACKGROUND);
        
        
        switch (type) {
			case "emote": emotesCheckBox = checkBox; break;
			case "badge": badgesCheckBox = checkBox; break;
			case "subscriber": subscriberCheckBox = checkBox; break;
			default: break;
		}
        return panel;
    }
    
    private JPanel createInputPanel(String title, String type){
        JPanel panel = new JPanel(new GridLayout(1, 2));
        JLabel textField = new JLabel(" "+title);
        JTextField inputField = new JTextField();
        
        inputField.setFont(new Font(null, Font.BOLD, fontSize));
        inputField.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        inputField.setForeground(Color.WHITE);

        textField.setForeground(ColorManager.FONT);
        panel.setBackground(ColorManager.GUI_BACKGROUND);
        
        panel.add(textField);
        panel.add(inputField);
        
        if(type.equals("login")){loginNameField = inputField;}else{displayNameField = inputField;}
		return panel;
    }
    
    private JPanel createDropdownPanel(String title, String[] items, String type){
        JPanel panel = new JPanel(new GridLayout(1, 2));
        JLabel textField = new JLabel(" "+title);
        JComboBox<String> inputField = new JComboBox<>(items);
        
        inputField.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        inputField.setFont(new Font(null, Font.BOLD, fontSize));
        inputField.setForeground(Color.WHITE);

        textField.setForeground(ColorManager.FONT);
        panel.setBackground(ColorManager.GUI_BACKGROUND);
        
        panel.add(textField);
        panel.add(inputField);
        
        if(type.equals("type")){userTypeComboBox = inputField;}else{cloneMacrosComboBox = inputField;}
		return panel;
    }
    
    private ActionListener closeWindow(Boolean isCanselt){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isCanselt){dispose(); return;}
				
				TwitchUserObj twitchUser = (loginNameField.getText().isEmpty() ? new TwitchUserObj(null, null, true) : TwitchManager.getTwitchUser(TwitchApiCallType.LOGIN, loginNameField.getText().replace("!", "").replace("?", "")));
				if(twitchUser.isDummy()){
					loginNameField.setText("Invalid channel!");
					new Thread(()->{
						for (int i = 0; i < 10; i++) {
							loginNameField.setForeground(Color.RED);
							try{Thread.sleep(200);}catch(Exception ex){ }
							loginNameField.setForeground(Color.YELLOW);
							try{Thread.sleep(200);}catch(Exception ex){ }
						}
						loginNameField.setForeground(Color.WHITE);
					}).start();
					return;
				}
				
				ChannelData channelData = DatabaseManager.getChannel().getChannelById(twitchUser.getUserId());

				String greetingList = "Hello {USER} HeyGuys\nWelcome {USER} HeyGuys";
				String goodbyList = "By {USER}!\nHave a good one! {USER} <3";
				String returnList = "Welcome back {USER} <3\n wb {USER} HeyGuys";
				
				if(channelData != null){
					greetingList = channelData.getGreetingText();
					goodbyList = channelData.getGoodbyText();
					returnList = channelData.getReturnText();
				}
				
				if(displayNameField.getText().isEmpty() || displayNameField.getText().equals(twitchUser.getLoginName())){
					displayNameField.setText(twitchUser.getDisplayName());
				}
				

				DatabaseManager.getChannelTabIndexDatabase().insert(editedTab.getTabType(), twitchUser.getUserId());
				DatabaseManager.getChannel().insert(
						twitchUser.getUserId(),
						twitchUser.getLoginName(),
						displayNameField.getText(),
						userTypeComboBox.getSelectedItem().toString(),
						null, //disable chat log
						greetingList,
						goodbyList,
						returnList);
				
				DatabaseManager.commit();
				TextureManager.downloadProfileImage(twitchUser.getProfileImageUrl(), twitchUser.getUserId());
				
				new Thread(() -> {
					if(emotesCheckBox.isSelected()){
						TextureManager.downloadChannelEmotes(twitchUser.getUserId());
						TextureManager.downloadBttvEmotes(twitchUser.getUserId());
						EmoteManager.getChannelEmotes(twitchUser.getUserId()).setSubState(subscriberCheckBox.isSelected());
//						new EmoteDownlodFrame(Main.MAIN_FRAME, twitchUser.getLoginName());
					}else if(EmoteManager.getChannelEmotes(twitchUser.getUserId()) == null) {
							DatabaseManager.getEmote().insertNewChannel(twitchUser.getUserId(), subscriberCheckBox.isSelected());
							DatabaseManager.commit();
							DatabaseManager.getEmote().getAllChannels();
					}else{
						EmoteManager.getChannelEmotes(twitchUser.getUserId()).setSubState(subscriberCheckBox.isSelected());
					}
					
					EmoteManager.load();
					
					if(badgesCheckBox.isSelected()){
						TextureManager.downloadChannelBadges(twitchUser.getUserId());
					}
				}).start();
				
				editedTab.reload(twitchUser.getUserId());
				dispose();
			}
		};
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
	
	
	@SuppressWarnings("deprecation")
	@Override
	public void dispose() {
		super.dispose();
		loginChangeListner.stop();
	}
}