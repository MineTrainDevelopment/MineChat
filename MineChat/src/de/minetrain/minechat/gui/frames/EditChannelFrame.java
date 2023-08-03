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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Main;
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
        panel.add(createDropdownPanel("User Typ:", new String[]{"Viewer", "Moderator"}, "type"));
        panel.add(seperator1);
        panel.add(createDropdownPanel("Clone macros:", new String[]{" "}, "clone"));
        panel.add(createCheckBox("Install channel emotes:", "emote"));
        panel.add(createCheckBox("Install channel badges:", "badge"));
        panel.add(createCheckBox("Are you subscriber on this channel:", "subscriber"));
        
        loginNameField.setText(tab.getChannelName());
        
        YamlManager config = Main.CONFIG;
		config.entrySet().forEach(entry -> {
        	if(entry.getKey().startsWith("Channel_")){
        		channelsFromConfig.put(config.getString(entry.getKey()+".Name"), entry.getKey());
        	}
        });
		
		//Add all channels to the clone selector.
		for(Entry<String, String> entry : channelsFromConfig.entrySet()){
        	cloneMacrosComboBox.addItem(config.getString(entry.getValue()+".DisplayName")+" - ("+entry.getKey()+")");
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
					}
        			
					//Set the default values, depending if there is alrady data for the inpit login name.
        			if(channelsFromConfig.containsKey(currentInput)){
        				String configIndex = channelsFromConfig.get(currentInput)+".";
        				displayNameField.setText(config.getString(configIndex+"DisplayName"));
        				userTypeComboBox.setSelectedIndex(config.getString(configIndex+"ChannelRole").equalsIgnoreCase("moderator") ? 1 : 0);
        				badgesCheckBox.setSelected(false);
        				emotesCheckBox.setSelected(false);
        				subscriberCheckBox.setSelected(EmoteManager.getYaml().getBoolean(configIndex+"sub"));
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

        // Erstellen des Haupt-Panels und Hinzufügen der Elemente
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
				
				YamlManager config = Main.CONFIG;
				String path = "Channel_"+twitchUser.getUserId()+".";
				config.setNumber(editedTab.getTabType().getConfigPath(), Long.parseLong(twitchUser.getUserId()));
				
				String nameSavedInConfigName = config.getString(path+"Name");
				config.setString(path + "Name", twitchUser.getLoginName());
				config.setString(path + "DisplayName", (displayNameField.getText().isEmpty() ? loginNameField.getText() : displayNameField.getText()));
				config.setString(path + "ChannelRole", userTypeComboBox.getSelectedItem().toString());
				config.setBoolean(path + "MessageLog", true);

				if(cloneMacrosComboBox.getSelectedIndex()>0){
					String selectedName = cloneMacrosComboBox.getSelectedItem().toString();
					String clonePath = channelsFromConfig.get(selectedName.contains("(") ? selectedName.split("\\(")[1].replace(")", "") : selectedName)+".";
					
					for(int i=0; i<=12; i++){
						config.setString(path + "Macros_0.M"+i, config.getString(clonePath + "Macros_0.M"+i));
					}

					for(int i=0; i<=12; i++){
						config.setString(path + "Macros_1.M"+i, config.getString(clonePath + "Macros_1.M"+i));
					}

					for(int i=0; i<=12; i++){
						config.setString(path + "Macros_2.M"+i, config.getString(clonePath + "Macros_2.M"+i));
					}
					
					config.setStringList(path + "GreetingText", config.getStringList(clonePath+"GreetingText"), false);
					config.setStringList(path + "GoodbyText", config.getStringList(clonePath+"GoodbyText"), false);
				}else if(nameSavedInConfigName.equalsIgnoreCase(">null<")){
					for(int i=0; i<=12; i++){
						config.setString(path + "Macros_0.M"+i, "null%-%>null<");
					}

					for(int i=0; i<=12; i++){
						config.setString(path + "Macros_1.M"+i, "null%-%>null<");
					}

					for(int i=0; i<=12; i++){
						config.setString(path + "Macros_2.M"+i, "null%-%>null<");
					}
	
					List<String> greetingsList = new ArrayList<String>();
					greetingsList.add("Hello {USER} HeyGuys");
					greetingsList.add("Welcome {USER} HeyGuys");
					config.setStringList(path + "GreetingText", greetingsList, false);
					
					List<String> goodbysList = new ArrayList<String>();
					goodbysList.add("By {USER}!");
					goodbysList.add("Have a good one! {USER} <3");
					config.setStringList(path + "GoodbyText", goodbysList, false);
				}

				config.saveConfigToFile();
				TextureManager.downloadProfileImage(twitchUser.getProfileImageUrl(), twitchUser.getUserId());
				
				new Thread(() -> {
					String yamlPath = "Channel_"+twitchUser.getUserId()+".";
					YamlManager yaml = EmoteManager.getYaml();
					yaml.setString(yamlPath+"Id", twitchUser.getUserId());
					yaml.setString(yamlPath+"Name", twitchUser.getDisplayName());
					yaml.setBoolean(yamlPath+"sub", subscriberCheckBox.isSelected());
					
					if(emotesCheckBox.isSelected()){
						TextureManager.downloadChannelEmotes(twitchUser.getUserId(), yaml);
						TextureManager.downloadBttvEmotes(twitchUser.getUserId(), yaml);
//						new EmoteDownlodFrame(Main.MAIN_FRAME, twitchUser.getLoginName());
					}
					
					yaml.saveConfigToFile();
					EmoteManager.load();
					
					if(badgesCheckBox.isSelected()){
						TextureManager.downloadChannelBadges(twitchUser.getUserId());
					}
				}).start();
				
				editedTab.getTabButton().removeActionListener(editedTab.getEditWindowAction());
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