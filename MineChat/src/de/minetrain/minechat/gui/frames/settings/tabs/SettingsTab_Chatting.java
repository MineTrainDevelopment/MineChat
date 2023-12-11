package de.minetrain.minechat.gui.frames.settings.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.config.enums.ReplyType;
import de.minetrain.minechat.config.enums.UndoVariation;
import de.minetrain.minechat.features.messagehighlight.HighlightDefault;
import de.minetrain.minechat.gui.frames.settings.SettingsTab;
import de.minetrain.minechat.gui.obj.panels.tabel.MineTabel;
import de.minetrain.minechat.gui.obj.panels.tabel.TabelObj;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.TextureManager;

public class SettingsTab_Chatting extends SettingsTab{
	private static final long serialVersionUID = -2903086341948789185L;
	private JPanel highlightFormatConentPanel;
	
	public SettingsTab_Chatting(JFrame parentFrame) {
		super(parentFrame, "Chatting");
		
		TitledBorder generalBorder = new TitledBorder(new EmptyBorder(0, 0, 0, 0), "General settings:", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, null, new Color(0, 0, 0));
		generalBorder.setTitleFont(Settings.MESSAGE_FONT);
		generalBorder.setTitleColor(ColorManager.FONT);
		
		JPanel generalFormatPanel = new JPanel(new BorderLayout());
		generalFormatPanel.setBackground(new Color(128, 128, 128));
		generalFormatPanel.setBorder(generalBorder);
		generalFormatPanel.setBounds(30, 15, 408, 190);
		generalFormatPanel.setBackground(getBackground());
		add(generalFormatPanel);
		
		MineTabel generalTabel = new MineTabel();
		generalFormatPanel.add(generalTabel, BorderLayout.CENTER);
		
		
		String[] replyType = String.valueOf(ReplyType.MESSAGE.name()+" "+ReplyType.THREAD.name()+" "+ReplyType.USER_NAME.name()).split(" ");

		JComboBox<String> replyDropDown = new JComboBox<String>(replyType);
		replyDropDown.setSelectedItem(Settings.REPLY_TYPE.name());
		replyDropDown.setForeground(ColorManager.FONT);
		replyDropDown.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		replyDropDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Settings.REPLY_TYPE = ReplyType.get(String.valueOf(replyDropDown.getSelectedItem()));
				Settings.settings.setString("Chatting.ReplyType", String.valueOf(replyDropDown.getSelectedItem()), true);
			}
		});
		
		JComboBox<String> greetDropDown = new JComboBox<String>(replyType);
		greetDropDown.setSelectedItem(Settings.GREETING_TYPE.name());
		greetDropDown.setForeground(ColorManager.FONT);
		greetDropDown.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		greetDropDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Settings.GREETING_TYPE = ReplyType.get(String.valueOf(greetDropDown.getSelectedItem()));
				Settings.settings.setString("Chatting.GreetingType", String.valueOf(greetDropDown.getSelectedItem()), true);
			}
		});
		
		JComboBox<String> undoDropDown = new JComboBox<String>(String.valueOf(UndoVariation.LETTER.name()+" "+UndoVariation.WORD).split(" "));
		undoDropDown.setSelectedItem(Settings.UNDO_VARIATION.name());
		undoDropDown.setForeground(ColorManager.FONT);
		undoDropDown.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		undoDropDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Settings.UNDO_VARIATION = UndoVariation.get(String.valueOf(undoDropDown.getSelectedItem()));
				Settings.settings.setString("Chatting.UndoMode", String.valueOf(undoDropDown.getSelectedItem()), true);
			}
		});
		
		JCheckBox holdToSendBox = new JCheckBox();
		holdToSendBox.setBorder(new LineBorder(getBackground(), 4, false));
		holdToSendBox.setSelected(Settings.holdToSendMessages);
		holdToSendBox.setBackground(holdToSendBox.isSelected() ? Color.GREEN : Color.RED);
//		holdToSendBox.setPreferredSize(new Dimension(36, 28));
		holdToSendBox.setFont(Settings.MESSAGE_FONT);
		holdToSendBox.setToolTipText("Press and hold to send multiple messages.");
		holdToSendBox.setHorizontalAlignment(SwingConstants.CENTER);
		holdToSendBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Settings.setHoltToSendMessages(holdToSendBox.isSelected());
				holdToSendBox.setBackground(holdToSendBox.isSelected() ? Color.GREEN : Color.RED);
			}
		});
		
		JCheckBox nonChannelEmotesBox = new JCheckBox();
		nonChannelEmotesBox.setBorder(new LineBorder(getBackground(), 4, false));
		nonChannelEmotesBox.setSelected(Settings.emoteBlendinOnDisplaying);
		nonChannelEmotesBox.setBackground(nonChannelEmotesBox.isSelected() ? Color.GREEN : Color.RED);
		nonChannelEmotesBox.setFont(Settings.MESSAGE_FONT);
		nonChannelEmotesBox.setToolTipText("Activate to blend non channel-specific emotes in with all others. Enables display of emotes like \"modCheck\" even if not present in the current channel.");
		nonChannelEmotesBox.setHorizontalAlignment(SwingConstants.CENTER);
		nonChannelEmotesBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Settings.setEmoteBlendinOnDisplaying(nonChannelEmotesBox.isSelected());
				nonChannelEmotesBox.setBackground(nonChannelEmotesBox.isSelected() ? Color.GREEN : Color.RED);
			}
		});
		

		generalTabel.add(new TabelObj("Reply type:", generalTabel).overrideOptionPanel(replyDropDown));
		generalTabel.add(new TabelObj("Greeting type:", generalTabel).overrideOptionPanel(greetDropDown));
		generalTabel.add(new TabelObj("Undo Variation:", generalTabel).overrideOptionPanel(undoDropDown));
		generalTabel.add(new TabelObj("Undo cache size.", generalTabel).overrideOptionPanel(new JTextArea("[TODO] - 100")));
		generalTabel.add(new TabelObj("Hold to send multiple messages:", generalTabel).overrideOptionPanel(holdToSendBox));
		generalTabel.add(new TabelObj("Display non channel specific emotes:", generalTabel).overrideOptionPanel(nonChannelEmotesBox));
		generalTabel.setVerticalScrollPosition(0);
		
		
		
		
		TitledBorder highlightBorder = new TitledBorder(new EmptyBorder(0, 0, 0, 0), "Event infos:", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, null, new Color(0, 0, 0));
		highlightBorder.setTitleFont(Settings.MESSAGE_FONT);
		highlightBorder.setTitleColor(ColorManager.FONT);
		
		JPanel highlightFormatPanel = new JPanel();
		highlightFormatPanel.setBackground(new Color(128, 128, 128));
		highlightFormatPanel.setBorder(highlightBorder);
		highlightFormatPanel.setBounds(30, 205, 408, 379);
		highlightFormatPanel.setBackground(getBackground());
		highlightFormatPanel.setLayout(null);
		add(highlightFormatPanel);
		
		highlightFormatConentPanel = new JPanel();
		highlightFormatConentPanel.setBackground(new Color(98, 98, 98));
		highlightFormatConentPanel.setBorder(null);
		highlightFormatConentPanel.setBounds(10, 25, 385, 358);
		highlightFormatConentPanel.setLayout(null);
		highlightFormatPanel.add(highlightFormatConentPanel);
		
		

		addHighlightLabel("First Messages.", 2, Settings.highlightUserFirstMessages);
		addHighlightLabel("User Rewards.", 41, Settings.displayUserRewards);
		addHighlightLabel("Announcements.", 80, Settings.displayModActions);
		addHighlightLabel("Subscriptions.", 119, Settings.displaySubs);
		addHighlightLabel("Gifted subs.", 158, Settings.displayGiftedSubs);
		addHighlightLabel("Individual GiftSubs", 197, Settings.displayIndividualGiftedSubs);
		addHighlightLabel("Follows.", 236, Settings.displayFollows);
		addHighlightLabel("Bits Cheerd.", 275, Settings.displayBitsCheerd);
		addHighlightLabel("Twitch Highlighted.", 314, Settings.displayTwitchHighlighted);

		
		JButton emoteButton = new JButton("Download public emotes.");
		emoteButton.setBounds(430, 300, 170, 30);
		emoteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TextureManager.getDefaultEmotes();
			}
		});
		add(emoteButton);
		
		JButton badgesButton = new JButton("Download public badges.");
		badgesButton.setBounds(430, 340, 170, 30);
		badgesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TextureManager.getDefaultBadges();
			}
		});
		add(badgesButton);
	}
	
	
	private void addHighlightLabel(String title, int y, HighlightDefault highlight){
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(Settings.MESSAGE_FONT);
		titleLabel.setForeground(ColorManager.FONT);
		titleLabel.setBounds(2, y, 200, 32);
		highlightFormatConentPanel.add(titleLabel);
		
		JPanel colorLabel = new JPanel();
		colorLabel.setBackground(highlight.getColor());
		colorLabel.setBounds(207, y, 32, 32);
		highlightFormatConentPanel.add(colorLabel);
		
		JButton changeButton = new JButton("Change");
		changeButton.setBounds(242, y, 95, 32);
		changeButton.setFont(Settings.MESSAGE_FONT);
		changeButton.setForeground(ColorManager.FONT);
		changeButton.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		changeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Color newColor = JColorChooser.showDialog(getParentFrame(), "Change the "+title, highlight.getColor(), true);
				if(newColor != null) {
					highlight.setColorCode(ColorManager.encode(newColor));
					colorLabel.setBackground(newColor);
					highlight.save();
				}
			}
		});
		highlightFormatConentPanel.add(changeButton);
		
		JCheckBox checkBox = new JCheckBox();
		checkBox.setBounds(339, y, 45, 32);
		checkBox.setFont(Settings.MESSAGE_FONT);
		checkBox.setForeground(ColorManager.FONT);
		checkBox.setSelected(highlight.isActive());
		checkBox.setBackground(ColorManager.GUI_BUTTON_BACKGROUND);
		checkBox.setHorizontalAlignment(SwingConstants.CENTER);
		checkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				highlight.setActive(checkBox.isSelected());
				highlight.save();
			}
		});
		highlightFormatConentPanel.add(checkBox);
	}
}
