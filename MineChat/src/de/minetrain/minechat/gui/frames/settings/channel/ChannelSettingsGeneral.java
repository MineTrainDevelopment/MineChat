package de.minetrain.minechat.gui.frames.settings.channel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.gui.frames.parant.RoundedJPanel;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;

public class ChannelSettingsGeneral extends RoundedJPanel{
	private static final Logger logger = LoggerFactory.getLogger(ChannelSettingsGeneral.class);
	private static final long serialVersionUID = 719785712710357457L;
	private Color componentBackground = Color.decode("#8a8a8a");
	private Color listTabChip = Color.decode("#2e2e2e");
	private Color listTabChipHover = Color.decode("#5c5c5c");
	private List<JPanel> listTabChips = new ArrayList<JPanel>();
	private JTextArea listInputField;
	private String channelId;
	private String currentListTabName = "Greeting";
	private String greetingText = "<greeting>";
	private String goodbyText = "<goodby>";
	private String returnText = "<return>";
	
	public record ChannelSettingsGeneralData(String greetingText, String goodbyText, String returnText){};

	public ChannelSettingsGeneral(ChannelSettingsFrame parent, String name) {
		super(parent.getBackground(), parent.getBackground(), 534, 281, 30);
		this.setName(name);
		setBounds(28, 181, getWidth(), getHeight());
		setLayout(null);

		add(createButton("Reinstall Emotes", 20, 20));
		add(createButton("Reinstall Badges", 20, 60));
		add(createButton("Reinstall Profile Pic", 20, 100));
		

		
		RoundedJPanel listContainerBottom = new RoundedJPanel(componentBackground, componentBackground, 302, 207, 25);
		listContainerBottom.setBounds(212, 54, listContainerBottom.getWidth(), listContainerBottom.getHeight());
		listContainerBottom.setLayout(null);
		add(listContainerBottom);

		RoundedJPanel listContainerTop = new RoundedJPanel(componentBackground, componentBackground, 302, 100, 75);
		listContainerTop.setBounds(212, 20, listContainerTop.getWidth(), listContainerTop.getHeight());
		listContainerTop.setLayout(null);
		add(listContainerTop);
		
		RoundedJPanel listTextContainer = new RoundedJPanel(parent.backgroundColorDark, parent.backgroundColorDark, 272, 187, 25);
		listTextContainer.setBounds(15, 7, listTextContainer.getWidth(), listTextContainer.getHeight());
		listTextContainer.setLayout(null);
		listContainerBottom.add(listTextContainer);

		listContainerTop.add(createListTabChipButton("Greeting", 30, 10));
		listContainerTop.add(createListTabChipButton("Goodby", 114, 10));
		listContainerTop.add(createListTabChipButton("Return", 198, 10));
		
		listInputField = new JTextArea(greetingText);
		listInputField.setForeground(parent.fontColor);
		listInputField.setBackground(parent.backgroundColorDark);
		listInputField.setFont(new Font("Inter", Font.PLAIN, 12));
//		listInputField.setLineWrap(true);
		listInputField.setColumns(10);
		listInputField.setCaretColor(listInputField.getForeground());
		listInputField.setBorder(BorderFactory.createEmptyBorder());
		listInputField.setVisible(true);
		
		JScrollPane listTextScrollPane = new JScrollPane(listInputField);
		listTextScrollPane.setBounds(5, 5, 250, 175);
		listTextScrollPane.setBackground(listInputField.getBackground());
		listTextScrollPane.setBounds(15, 7, listTextScrollPane.getWidth(), listTextScrollPane.getHeight());
		listTextScrollPane.setBorder(null);
		listTextScrollPane.setBackground(ColorManager.GUI_BACKGROUND);
		listTextScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		listTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		listTextScrollPane.getVerticalScrollBar().setBackground(listTextScrollPane.getBackground());
		listTextScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, listTextContainer.getHeight()));
		listTextScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		listTextScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listTextScrollPane.getHorizontalScrollBar().setBackground(listTextScrollPane.getBackground());
		listTextScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(listTextContainer.getHeight(), 12));
		listTextContainer.add(listTextScrollPane);
	}
	
	public RoundedJPanel createButton(String text, int pointX, int poinY) {
		RoundedJPanel emoteButton = new RoundedJPanel(ColorManager.GUI_BUTTON_BACKGROUND, ColorManager.GUI_BUTTON_BACKGROUND, 150, 30, 25);
		emoteButton.setBounds(pointX, poinY, emoteButton.getWidth(), emoteButton.getHeight());
		
		JLabel emoteText = new JLabel(text, SwingConstants.CENTER);
		emoteText.setBounds(emoteButton.getBounds());
		emoteText.setForeground(ColorManager.FONT);
		emoteText.setFont(new Font("Inter", Font.BOLD, 15));
		emoteButton.add(emoteText);
		
		emoteButton.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e) {
				emoteText.setForeground(ColorManager.GUI_ACCENT_DEFAULT);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				emoteText.setForeground(ColorManager.FONT);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(channelId.equals("0")){
					return;
				}
				
				emoteButton.setBackground(componentBackground);
				switch (text.substring(10)) {
				
				case "Emotes":
					System.err.println("TODO - Emotes");
					SwingUtilities.invokeLater(() -> {
						TextureManager.downloadChannelEmotes(channelId);
						TextureManager.downloadBttvEmotes(channelId);
					});
					break;
					
				case "Badges":
					System.err.println("TODO - Badges");
					SwingUtilities.invokeLater(() -> TextureManager.downloadChannelBadges(channelId));
					break;
					
				case "Profile Pic":
					TwitchUserObj twitchUser = TwitchManager.getTwitchUser(TwitchApiCallType.ID, channelId);
					if(!twitchUser.isDummy()){
						ChannelSettingsFrame parent = (ChannelSettingsFrame) getParent().getParent().getParent().getParent().getParent().getParent();
						parent.toggleProfilePicVisible();
						TextureManager.downloadProfileImage(twitchUser.getProfileImageUrl(), twitchUser.getUserId());
						Timer timer = new Timer(1000, t -> parent.toggleProfilePicVisible());
						timer.setRepeats(false);
						timer.start();
					}
					break;
					
				default:
					logger.warn("Unexpected button action.", new IllegalArgumentException("Unexpected value: " + text));
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				emoteButton.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
			}
			
		});
		return emoteButton;
	}
	
	private JPanel createListTabChipButton(String title, int pointX, int pointY) {
		JPanel chip = new RoundedJPanel(listTabChip, listTabChip, 74, 24, 20);
		chip.setBounds(pointX, pointY, chip.getWidth(), chip.getHeight());
		chip.setLayout(null);
		
		JLabel chipLabel = new JLabel(title, SwingConstants.CENTER);
		chipLabel.setBounds(0, -1, chip.getBounds().width, chip.getBounds().height);
		chipLabel.setForeground(ColorManager.FONT);
		chipLabel.setFont(new Font("Inter", Font.BOLD, 15));
		chip.add(chipLabel);
		
		chip.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseExited(MouseEvent e) {
				if(chip.getBackground() != ColorManager.GUI_BUTTON_BACKGROUND_DEFAULT) {
					chip.setBackground(listTabChip);
					chip.setForeground(listTabChip);
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if(chip.getBackground() != ColorManager.GUI_BUTTON_BACKGROUND_DEFAULT) {
					chip.setBackground(listTabChipHover);
					chip.setForeground(listTabChipHover);
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				System.err.println("Cilckt chip - "+title);
				listTabChips.forEach(chips -> {
					chips.setBackground(listTabChip);
					chips.setForeground(listTabChip);
					chips.getComponent(0).setForeground(ColorManager.FONT);
				});
				
				chipLabel.setForeground(ColorManager.GUI_ACCENT_DEFAULT);
				chip.setBackground(ColorManager.GUI_BUTTON_BACKGROUND_DEFAULT);
				chip.setForeground(ColorManager.GUI_BUTTON_BACKGROUND_DEFAULT);

				switch (currentListTabName) {
					case "Greeting": greetingText = listInputField.getText(); break;
					case "Goodby": goodbyText = listInputField.getText(); break;
					case "Return": returnText = listInputField.getText(); break;
					default: logger.warn("Unexpected button action.", new IllegalArgumentException("Unexpected value: " + currentListTabName));
				}
				
				currentListTabName = title;
				switch (title) {
					case "Greeting": listInputField.setText(greetingText); break;
					case "Goodby": listInputField.setText(goodbyText); break;
					case "Return": listInputField.setText(returnText); break;
					default: logger.warn("Unexpected button action.", new IllegalArgumentException("Unexpected value: " + currentListTabName));
				}
				
			}
		});
		
		listTabChips.add(chip);
		return chip;
	}
	
	
	
	public void setData(ChannelSettingsGeneralData data, String channelId){
		greetingText = data.greetingText;
		goodbyText = data.goodbyText;
		returnText = data.returnText;
		
		listInputField.setText(greetingText);
		this.channelId = channelId;
	}
	
	public ChannelSettingsGeneralData getData(){
		switch (currentListTabName) {
			case "Greeting": greetingText = listInputField.getText(); break;
			case "Goodby": goodbyText = listInputField.getText(); break;
			case "Return": returnText = listInputField.getText(); break;
			default: logger.warn("Unexpected button action.", new IllegalArgumentException("Unexpected value: " + currentListTabName));
		}
		return new ChannelSettingsGeneralData(
				Arrays.stream(greetingText.split("\n")).map(line -> line.contains("{USER}") ? line : "{USER} "+line).collect(Collectors.joining("\n")),
				Arrays.stream(goodbyText.split("\n")).map(line -> line.contains("{USER}") ? line : "{USER} "+line).collect(Collectors.joining("\n")),
				Arrays.stream(returnText.split("\n")).map(line -> line.contains("{USER}") ? line : "{USER} "+line).collect(Collectors.joining("\n")));
	}

}
