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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.ConfigManager;
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
	private JTextField twitchChannelNameField, channelDisplayNameField, triggerAmountField, deprecateAfterField;
    private JComboBox<String> userTypeComboBox;
    private int mouseX, mouseY;
    private ChannelTab editedTab;
    private static final int fontSize = 13;

    public EditChannelFrame(MainFrame mainFrame, ChannelTab tab) {
    	super(mainFrame, "Twitch Channel Einstellungen", true);
    	this.editedTab = tab;
        setSize(400, 180);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setBackground(ColorManager.BACKGROUND);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createLineBorder(ColorManager.BORDER, 2));
        panel.setBackground(ColorManager.BACKGROUND);
        panel.setLayout(new GridLayout(6, 2));
        addMouseListener(MoiseListner());
        addMouseMotionListener(mouseMotionListner());

        // Erstellen der Texteingabefelder
        twitchChannelNameField = new JTextField(10);
        twitchChannelNameField.setBorder(null);

        channelDisplayNameField = new JTextField(10);
        channelDisplayNameField.setBorder(null);

        triggerAmountField = new JTextField(10);
        triggerAmountField.setBorder(null);

        deprecateAfterField = new JTextField(10);
        deprecateAfterField.setBorder(null);

        // Erstellen der Labels für die Texteingabefelder
        JLabel twitchChannelNameLabel = new JLabel(" Twitch Channel Name:");
        JLabel channelDisplayNameLabel = new JLabel(" Channel Anzeige Name:");
        JLabel userTypeComboNameLabel = new JLabel(" User Typ: ");
        JLabel SpamButtonLabel1 = new JLabel("--------------------------------------Spam");
        JLabel SpamButtonLabel2 = new JLabel("Button--------------------------------------");
        JLabel triggerAmountLabel = new JLabel(" Trigger Amount: (Messages)");
        JLabel deprecateAfterLabel = new JLabel(" Deprecate After: (Seconds)");

        twitchChannelNameLabel.setForeground(Color.WHITE);
        channelDisplayNameLabel.setForeground(Color.WHITE);
        userTypeComboNameLabel.setForeground(Color.WHITE);
        SpamButtonLabel1.setForeground(Color.WHITE);
        SpamButtonLabel2.setForeground(Color.WHITE);
        triggerAmountLabel.setForeground(Color.WHITE);
        deprecateAfterLabel.setForeground(Color.WHITE);

        twitchChannelNameField = new JTextField();
        twitchChannelNameField.setBackground(ColorManager.BACKGROUND_LIGHT);
		twitchChannelNameField.setFont(new Font(null, Font.BOLD, fontSize));
        twitchChannelNameField.setForeground(Color.WHITE);
        twitchChannelNameField.setText(tab.getChannelName());
        panel.add(twitchChannelNameLabel);
        panel.add(twitchChannelNameField);

        channelDisplayNameField = new JTextField();
        channelDisplayNameField.setBackground(ColorManager.BACKGROUND_LIGHT);
        channelDisplayNameField.setFont(new Font(null, Font.BOLD, fontSize));
        channelDisplayNameField.setForeground(Color.WHITE);
        channelDisplayNameField.setText(tab.getDisplayName());
        panel.add(channelDisplayNameLabel);
        panel.add(channelDisplayNameField);

        userTypeComboBox = new JComboBox<>(new String[]{"Viewer", "Moderator"});
        userTypeComboBox.setBackground(ColorManager.BACKGROUND_LIGHT);
        userTypeComboBox.setFont(new Font(null, Font.BOLD, fontSize));
        userTypeComboBox.setForeground(Color.WHITE);
//        userTypeComboBox.setSelectedIndex((tab.isModerator()) ? 1 : 0);
        userTypeComboBox.setSelectedIndex(0);
        panel.add(userTypeComboNameLabel);
        panel.add(userTypeComboBox);

        triggerAmountField = new JTextField();
        triggerAmountField.setBackground(ColorManager.BACKGROUND_LIGHT);
        triggerAmountField.setFont(new Font(null, Font.BOLD, fontSize));
        triggerAmountField.setForeground(Color.WHITE);
        triggerAmountField.setText(""+tab.getSpamTriggerAmound());
        panel.add(SpamButtonLabel1);
        panel.add(SpamButtonLabel2);
        panel.add(triggerAmountLabel);
        panel.add(triggerAmountField);
        
        deprecateAfterField = new JTextField();
        deprecateAfterField.setBackground(ColorManager.BACKGROUND_LIGHT);
        deprecateAfterField.setFont(new Font(null, Font.BOLD, fontSize));
        deprecateAfterField.setForeground(Color.WHITE);
        deprecateAfterField.setText(""+tab.getSpamDeprecateAfter());
        panel.add(deprecateAfterLabel);
        panel.add(deprecateAfterField);

        // Erstellen der Buttons
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setBackground(ColorManager.BACKGROUND);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setBorder(null);
        confirmButton.addActionListener(closeWindow(false));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(ColorManager.BACKGROUND);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(null);
        cancelButton.addActionListener(closeWindow(true));

        // Hinzufügen der Buttons am unteren Rand des JFrame
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 9, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(7, 2, 2, 2));
        buttonPanel.setBackground(ColorManager.BORDER);
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        panel.add(buttonPanel);
        
        // Setzen der Breite der Buttons auf die Breite der Textfelder
        int buttonWidth = (twitchChannelNameField.getPreferredSize().width + channelDisplayNameField.getPreferredSize().width) / 2;
        cancelButton.setPreferredSize(new Dimension(buttonWidth, cancelButton.getPreferredSize().height));
        confirmButton.setPreferredSize(new Dimension(buttonWidth, confirmButton.getPreferredSize().height));

        // Erstellen des Haupt-Panels und Hinzufügen der Elemente
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(ColorManager.BORDER, 7));
        mainPanel.setBackground(ColorManager.BACKGROUND);
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
    
    private ActionListener closeWindow(Boolean isCanselt){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isCanselt){dispose(); return;}
				
				TwitchUserObj twitchUser = TwitchManager.getTwitchUser(TwitchApiCallType.LOGIN, twitchChannelNameField.getText().replace("!", "").replace("?", ""));
				if(twitchUser.isDummy()){
					twitchChannelNameField.setText("Invalid channel!");
					new Thread(()->{
						for (int i = 0; i < 15; i++) {
							twitchChannelNameField.setForeground(Color.RED);
							try{Thread.sleep(200);}catch(Exception ex){ }
							twitchChannelNameField.setForeground(Color.YELLOW);
							try{Thread.sleep(200);}catch(Exception ex){ }
						}
						twitchChannelNameField.setForeground(Color.WHITE);
					}).start();
					return;
				}
				
				ConfigManager config = Main.CONFIG;
				String path = "Channel_"+twitchUser.getUserId()+".";
				config.setNumber(editedTab.getTabType().getConfigPath(), Long.parseLong(twitchUser.getUserId()));
				
				String nameSavedInConfigName = config.getString(path+"Name");
				config.setString(path + "Name", twitchUser.getLoginName());
				config.setString(path + "DisplayName", (channelDisplayNameField.getText().isEmpty() ? twitchChannelNameField.getText() : channelDisplayNameField.getText()));
				config.setString(path + "ChannelRole", userTypeComboBox.getSelectedItem().toString());

				if(nameSavedInConfigName.equalsIgnoreCase(">null<")){
					for(int i=0; i<=12; i++){
						config.setString(path + "Macros.M"+i, "null%-%>null<");
					}
	
					List<String> greetingsList = new ArrayList<String>();
					greetingsList.add("Hello {USER} HeyGuys");
					greetingsList.add("Welcome {USER} HeyGuys");
					config.setStringList(path + "GreetingText", greetingsList, false);
					
					List<String> goodbysList = new ArrayList<String>();
					goodbysList.add("By {USER}!");
					goodbysList.add("Have a good one! {USER} <3");
					config.setStringList(path + "GoodbyText", goodbysList, false);
					
					TextureManager.downloadProfileImage(twitchUser.getProfileImageUrl(), Long.parseLong(twitchUser.getUserId()));
					new EmoteDownlodFrame(Main.MAIN_FRAME, twitchUser.getLoginName());
				}
				
				
				
				long parseSpamTrigger = 4;
				long parseDeprecateAfter = 5;
				
				try {
					parseSpamTrigger = Long.parseLong(triggerAmountField.getText());
					parseDeprecateAfter = Long.parseLong(deprecateAfterField.getText());
				} catch (NumberFormatException ex) {
					logger.warn("Can�t format user input into long", ex);
				}
				
				config.setNumber(path + "SpamButton.TriggerAmoundMessages", parseSpamTrigger);
				config.setNumber(path + "SpamButton.DeprecateAfterSeconds", parseDeprecateAfter);
				config.saveConfigToFile();
				
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
}