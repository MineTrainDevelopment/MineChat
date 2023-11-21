package de.minetrain.minechat.gui.frames.settings.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.stream.LongStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.regexp.Pattern;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.features.autoreply.AutoReply;
import de.minetrain.minechat.features.autoreply.AutoReplyManager;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.TwitchUserObj;
import de.minetrain.minechat.twitch.obj.TwitchUserObj.TwitchApiCallType;

public class CreateAutoReplyFrame extends MineDialog{
	private static final long serialVersionUID = 3602274005010690308L;
	private static final Logger logger = LoggerFactory.getLogger(CreateWordHighlightFrame.class);
	private String uuid = UUID.randomUUID().toString();
	private JComboBox<Long> messagePerMinSelector, fireDelaySelector;
	private JComboBox<String> channelSelector;
	private JCheckBox chatReply;
	private JTextField triggerWord;
	private JTextArea outputText;
	private AutoReply preset;
	
	
	public CreateAutoReplyFrame(JFrame parentFrame) {
		super(parentFrame, "Create a AutoReply", new Dimension(400, 215));
		// TODO Auto-generated constructor stub
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBackground(getBackground());
		getContentPanel().add(panel, BorderLayout.CENTER);
//		addContent(panel, BorderLayout.CENTER);
		
		messagePerMinSelector = new JComboBox<Long>();
		messagePerMinSelector.setToolTipText("Select how often your trigger word needs to be sent before it is replied.");
		messagePerMinSelector.setEditable(true);
		messagePerMinSelector.setBounds(333, 10, 58, 22);
		panel.add(messagePerMinSelector);
		
		fireDelaySelector = new JComboBox<Long>();
		fireDelaySelector.setToolTipText("Select how long you want to pause after replying.");
		fireDelaySelector.setEditable(true);
		fireDelaySelector.setBounds(333, 33, 58, 22);
		panel.add(fireDelaySelector);
		
		LongStream.range(1, 101).forEach(i -> {
			messagePerMinSelector.addItem(i);
			fireDelaySelector.addItem(i);
		});
		
		JTextField txtMessagesPerMin = new JTextField();
		txtMessagesPerMin.setEditable(false);
		txtMessagesPerMin.setText("Messages per min");
		txtMessagesPerMin.setBounds(221, 11, 113, 20);
		panel.add(txtMessagesPerMin);
		txtMessagesPerMin.setColumns(10);
		
		JTextField txtFireDelay = new JTextField();
		txtFireDelay.setEditable(false);
		txtFireDelay.setText("Fire delay (sec)");
		txtFireDelay.setColumns(10);
		txtFireDelay.setBounds(221, 32, 113, 20);
		panel.add(txtFireDelay);
		
		outputText = new JTextArea();
		outputText.setText("Output text\r\nLine split (ENTER key) == Random output text list");
		outputText.setBounds(10, 94, 381, 107);
		outputText.setLineWrap(true);
		panel.add(outputText);
		outputText.setColumns(10);
		
		chatReply = new JCheckBox("Message as chat reply");
		chatReply.setToolTipText("If the message should be replying to the Last message that Triggert the event, or not");
		chatReply.setBounds(221, 64, 170, 23);
		panel.add(chatReply);
		
		channelSelector = new JComboBox<String>();
		channelSelector.setToolTipText("Select a chat.");
//		channelSelector.setModel(new DefaultComboBoxModel(new String[] {"dani_backt", "drop_username", "sintica", "shaakdj"}));
		ChannelTab.channelDisplayNameList.entrySet().forEach(entry -> channelSelector.addItem(entry.getValue()+" - ("+entry.getKey()+")"));
		channelSelector.addItem("");
		channelSelector.setBounds(10, 10, 184, 35);
		panel.add(channelSelector);
		
		triggerWord = new JTextField();
		triggerWord.setToolTipText("Add a Trigger word or regex");
		triggerWord.setHorizontalAlignment(SwingConstants.CENTER);
		triggerWord.setText("Trigger word/regex");
		triggerWord.setBounds(10, 56, 184, 27);
		triggerWord.setColumns(10);
		panel.add(triggerWord);
		
		setConfirmButtonAction(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Long messagesPerMin = 0l;
				Long fireDelay = 0l;
				
				try {
					messagesPerMin = Long.valueOf(String.valueOf(messagePerMinSelector.getSelectedItem()));
					fireDelay = Long.valueOf(String.valueOf(fireDelaySelector.getSelectedItem()));
				} catch (Exception ex) {
					logger.debug("Can´t create a new AutoReply. Invalid long values for messagePerMin or fireDelay! -> "+ex.getMessage());
//					setError("Invalid number input");
					setError(ex.getMessage());
					return; //Display some sort of error message.
				}
				
				if(Pattern.compile("[{}.]").matcher(triggerWord.getText()).find()){
					setError("Ivalid trgger text!");
					return; //Display some sort of error message.      "\"{\", \"}\" and \".\" are invalid chars!";
				}
				
				if(Pattern.compile("[{}.]").matcher(outputText.getText()).find()){
					setError("Ivalid output text!");
					return; //Display some sort of error message.      "\"{\", \"}\" and \".\" are invalid chars!";
				}
				
				String selectedChannel = String.valueOf(channelSelector.getSelectedItem());
				String channelName = selectedChannel.substring(selectedChannel.indexOf("(")+1, selectedChannel.lastIndexOf(")"));
				boolean isAktiv = true;
				
				if(!TwitchManager.twitch.getChat().getChannels().stream().anyMatch(string -> string.equals(channelName))){
					isAktiv = false;
				}
				
				TwitchUserObj twitchUser = TwitchManager.getTwitchUser(TwitchApiCallType.LOGIN, channelName);
				if(twitchUser.isDummy()){
					setError("Channel dosen´t exist.");
					return;
				}
				
				if(preset == null && AutoReplyManager.getAutoReplyTrigger(twitchUser.getUserId()).contains(triggerWord.getText())){
					setError("Trigger is already in use.");
					return; //Trigger already in use.
				}
				
				if(preset != null){
					AutoReplyManager.getAutoReplys().get(preset.getChannelId()).remove(preset.getTrigger());
				}
				
				
				String[] output = outputText.getText().strip().trim().split("\\r?\\n");
				DatabaseManager.getAutoReply().insert(uuid.toString(), twitchUser.getUserId(), isAktiv, chatReply.isSelected(), messagesPerMin, fireDelay, triggerWord.getText(), output);
				DatabaseManager.commit();
				dispose();
			}
			
		});
		
		setInfoButtonAction(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					java.awt.Desktop.getDesktop().browse(new URI("https://www.javainuse.com/rexgenerator"));
				} catch (IOException | URISyntaxException ex) {
					logger.error("CanÂ´t open a web URL -> https://www.javainuse.com/rexgenerator", ex);
				}
			}
		});
		
//		setVisible(true); DONT USE, bcs of the loadPreset method.
	}
	
	public CreateAutoReplyFrame loadPreset(AutoReply preset){
		uuid = preset.getUuid();
		chatReply.setSelected(preset.isChatReply());
		messagePerMinSelector.setSelectedItem(preset.getMessagesPerMin());
		fireDelaySelector.setSelectedItem(preset.getFireDelay());
		triggerWord.setText(preset.getTrigger());
		outputText.setText(String.join(System.lineSeparator(), preset.getOutputs()));
		this.preset = preset;
		
		try {
			channelSelector.setSelectedItem(ChannelTab.channelDisplayNameList.get(preset.getChannelName())+" - ("+preset.getChannelName()+")");
		} catch (Exception ex) {
			// TODO: handle exception
		}
		
		return this;
	}
	
	private void setError(String message){
		setTitle(message);
		setTitleColor(Color.ORANGE);
	}
}
