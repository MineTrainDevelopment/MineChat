package de.minetrain.minechat.gui;

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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AddChannelFrame extends JFrame {
	private static final long serialVersionUID = 8773100712568642831L;
	private JTextField twitchChannelNameField, channelDisplayNameField, triggerAmountField, deprecateAfterField;
    private JComboBox<String> userTypeComboBox;
    private int mouseX, mouseY;
    private static final int fontSize = 13;

    public AddChannelFrame(MainFrame mainFrame) {
    	super("Twitch Channel Einstellungen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 180);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setBackground(new Color(40, 40, 40));
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createLineBorder(new Color(14, 14, 14), 2));
        panel.setBackground(new Color(40, 40, 40));
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
        twitchChannelNameField.setBackground(new Color(90, 90, 90));
		twitchChannelNameField.setFont(new Font(null, Font.BOLD, fontSize));
        twitchChannelNameField.setForeground(Color.WHITE);
        panel.add(twitchChannelNameLabel);
        panel.add(twitchChannelNameField);

        channelDisplayNameField = new JTextField();
        channelDisplayNameField.setBackground(new Color(90, 90, 90));
        channelDisplayNameField.setFont(new Font(null, Font.BOLD, fontSize));
        channelDisplayNameField.setForeground(Color.WHITE);
        panel.add(channelDisplayNameLabel);
        panel.add(channelDisplayNameField);

        userTypeComboBox = new JComboBox<>(new String[]{"Viewer", "Moderator"});
        userTypeComboBox.setBackground(new Color(90, 90, 90));
        userTypeComboBox.setFont(new Font(null, Font.BOLD, fontSize));
        userTypeComboBox.setForeground(Color.WHITE);
        userTypeComboBox.setSelectedIndex(0);
        panel.add(userTypeComboNameLabel);
        panel.add(userTypeComboBox);

        triggerAmountField = new JTextField();
        triggerAmountField.setBackground(new Color(90, 90, 90));
        triggerAmountField.setFont(new Font(null, Font.BOLD, fontSize));
        triggerAmountField.setForeground(Color.WHITE);
        panel.add(SpamButtonLabel1);
        panel.add(SpamButtonLabel2);
        panel.add(triggerAmountLabel);
        panel.add(triggerAmountField);
        
        deprecateAfterField = new JTextField();
        deprecateAfterField.setBackground(new Color(90, 90, 90));
        deprecateAfterField.setFont(new Font(null, Font.BOLD, fontSize));
        deprecateAfterField.setForeground(Color.WHITE);
        panel.add(deprecateAfterLabel);
        panel.add(deprecateAfterField);

        // Erstellen der Buttons
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setBackground(new Color(30, 30, 30));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setBorder(null);
        confirmButton.addActionListener(closeWindow());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(30, 30, 30));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(null);
        cancelButton.addActionListener(closeWindow());

        // Hinzufügen der Buttons am unteren Rand des JFrame
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 9, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(7, 2, 2, 2));
        buttonPanel.setBackground(new Color(14, 14, 14));
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        panel.add(buttonPanel);
        
        // Setzen der Breite der Buttons auf die Breite der Textfelder
        int buttonWidth = (twitchChannelNameField.getPreferredSize().width + channelDisplayNameField.getPreferredSize().width) / 2;
        cancelButton.setPreferredSize(new Dimension(buttonWidth, cancelButton.getPreferredSize().height));
        confirmButton.setPreferredSize(new Dimension(buttonWidth, confirmButton.getPreferredSize().height));

        // Erstellen des Haupt-Panels und Hinzufügen der Elemente
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(14, 14, 14), 7));
        mainPanel.setBackground(new Color(40, 40, 40));
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
    
    private ActionListener closeWindow(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("test");
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