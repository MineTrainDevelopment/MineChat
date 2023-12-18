package de.minetrain.minechat.gui.frames.settings.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.regexp.Pattern;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.utils.HTMLColors;
import de.minetrain.minechat.utils.MineStringBuilder;
import de.minetrain.minechat.utils.audio.AudioManager;
import de.minetrain.minechat.utils.audio.AudioPath;
import de.minetrain.minechat.utils.audio.AudioVolume;
import javafx.scene.media.AudioClip;

public class CreateWordHighlightFrame extends MineDialog {
	private static final long serialVersionUID = -4774556639413934907L;
	private static final Logger logger = LoggerFactory.getLogger(CreateWordHighlightFrame.class);
	private Color borderColor = ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT;
	private Color wordColor = ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT;
	private JDialog thisDialog = this;
	private JTextField inputField;
	private TitledBorder titledBorder;
	private JLabel textLabel;
	private JComboBox<AudioPath> soundSelector;
	private JComboBox<AudioVolume> volumeSelector;
	private AudioClip lastAudioClip = null;
	/**Sound path, {@link AudioPath}*/
	private HashMap<String, AudioPath> pathCache = new HashMap<String, AudioPath>();
	private static final MineStringBuilder stringBuilder = new MineStringBuilder();
	
	private String uuid = UUID.randomUUID().toString();

	public CreateWordHighlightFrame(JFrame parentFrame) {
		super(parentFrame, "Add a word Highlight", new Dimension(400, 145));
		
		inputField = new JTextField();
        inputField.setText("{WORD}");
        inputField.setHorizontalAlignment(SwingConstants.CENTER);
        inputField.setFont(Settings.MESSAGE_FONT);
        inputField.setForeground(ColorManager.FONT);
        inputField.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		
		titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(borderColor, 2, true), "System:");
		titledBorder.setTitleJustification(TitledBorder.LEFT);
		titledBorder.setTitleColor(Color.GREEN);
		titledBorder.setTitleFont(new Font(null, Font.BOLD, 20));
		
        textLabel = new JLabel(getPreviewText().toString());
        textLabel.setForeground(ColorManager.FONT);
        textLabel.setFont(Settings.MESSAGE_FONT);
        
        inputField.getDocument().addDocumentListener(new DocumentListener() {
        	public void changedUpdate(DocumentEvent event){ }
			public void removeUpdate(DocumentEvent event){textLabel.setText(getPreviewText().toString());}
			public void insertUpdate(DocumentEvent event){textLabel.setText(getPreviewText().toString());}
		});
        
        JLabel dummyButton = new JLabel();
        dummyButton.setPreferredSize(new Dimension(30, 28));
        dummyButton.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        dummyButton.setIcon(Main.TEXTURE_MANAGER.getMarkReadButton());
        dummyButton.setToolTipText("Mark this message as read.");
        
        JPanel  messageContentPanel = new JPanel(new BorderLayout());
        messageContentPanel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        messageContentPanel.add(dummyButton, BorderLayout.WEST);
		messageContentPanel.add(textLabel, BorderLayout.CENTER);
		
		JPanel messagePreview = new JPanel(new BorderLayout());
		messagePreview.setBorder(titledBorder);
		messagePreview.setBackground(ColorManager.GUI_BACKGROUND);
		messagePreview.add(messageContentPanel, BorderLayout.CENTER);
		
		JButton borderColorButton = new JButton("Border");
		borderColorButton.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		borderColorButton.setForeground(ColorManager.GUI_BORDER);
		borderColorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(thisDialog, "Change the border color!", borderColor, true);
				borderColor = newColor == null ? borderColor : newColor; 
    			titledBorder.setBorder(BorderFactory.createLineBorder(borderColor, 2));
    			messagePreview.revalidate();
    			messagePreview.repaint();
			}
		});
		
		JButton wordColorButton = new JButton("Word");
		wordColorButton.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		wordColorButton.setForeground(ColorManager.GUI_BORDER);
		wordColorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(thisDialog, "Change the word color!", wordColor, true);
				wordColor = newColor == null ? wordColor : newColor; 
				textLabel.setText(getPreviewText().toString());
			}
		});

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 2, 2));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        buttonPanel.setBackground(ColorManager.GUI_BACKGROUND);
        buttonPanel.add(borderColorButton);
        buttonPanel.add(wordColorButton);
        
		JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.add(inputField, BorderLayout.CENTER);
		inputPanel.add(buttonPanel, BorderLayout.EAST);
		inputPanel.setBackground(ColorManager.GUI_BACKGROUND);
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBackground(ColorManager.GUI_BACKGROUND);
		contentPanel.add(inputPanel, BorderLayout.SOUTH);
		
		
		inputPanel.add(getSoundSelector(), BorderLayout.NORTH);
		contentPanel.add(messagePreview, BorderLayout.NORTH);
		
		addContent(contentPanel, BorderLayout.CENTER);
		setConfirmButtonAction(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				logger.debug("Try to save a new HighlightString\n  " + (inputField != null ? inputField.getText() : "null"));
				String newWord = saveNewWord();
				if(newWord == null){
					dispose();
					return;
				}

				stringBuilder.clear();
				stringBuilder.setPrefix("["+getCurentTime()+"] ");
				stringBuilder.appendString(newWord, HTMLColors.RED);
				textLabel.setText(stringBuilder.toString());
			}
		});

		setInfoButtonAction(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					java.awt.Desktop.getDesktop().browse(new URI("https://www.javainuse.com/rexgenerator"));
				} catch (IOException | URISyntaxException ex) {
					logger.error("Can´t open a web URL -> https://www.javainuse.com/rexgenerator", ex);
				}
			}
		});
		
//		setVisible(true); DONT USE, bcs of the loadPreset method.
	}
	
	public CreateWordHighlightFrame loadPreset(HighlightString preset){
		uuid = preset.getUuid();
		inputField.setText(preset.getWord());
		borderColor = Color.decode(preset.getBorderColorCode());
		wordColor = Color.decode(preset.getWordColorCode());

		titledBorder.setBorder(BorderFactory.createLineBorder(borderColor, 2));
		textLabel.setText(getPreviewText().toString());

		if(preset.isPlaySound()){
			soundSelector.setSelectedItem(pathCache.get(preset.getSoundPath()));
		}
		volumeSelector.setSelectedItem(preset.getSoundVolume());
		return this;
	}

	private MineStringBuilder getPreviewText() {
		stringBuilder.clear();
		stringBuilder.setPrefix("["+getCurentTime()+"] ");
		stringBuilder.appendString("I recently heard about the meaning of the word ");
		stringBuilder.appendString(inputField.getText(), wordColor);
		stringBuilder.appendString(". It's very interesting!");
		return stringBuilder;
	}

	private String getCurentTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(Settings.messageTimeFormat,
				new Locale(System.getProperty("user.language"), System.getProperty("user.country"))));
	}
	
	/**
	 * Save
	 * @return
	 */
	public String saveNewWord() {
		String word = inputField.getText();
		if(Pattern.compile("[{}.]").matcher(word).find()){
			return "\"{\", \"}\" and \".\" are invalid chars!";
		}
		
		AudioPath audioPath = (AudioPath) soundSelector.getSelectedItem();
		
		DatabaseManager.getMessageHighlight().insert(
				uuid,
				word,
				String.format("#%06x", wordColor.getRGB() & 0x00FFFFFF),
				String.format("#%06x", borderColor.getRGB() & 0x00FFFFFF),
				!audioPath.isDummy() ? audioPath.getFilePathAsString() : null,
				(AudioVolume) volumeSelector.getSelectedItem(),
				true);
		
		DatabaseManager.commit();
		Settings.reloadHighlights();
		return null;
	}
	
	private JPanel getSoundSelector(){
		JPanel soundPanel = new JPanel(new BorderLayout());
		
		volumeSelector = new JComboBox<AudioVolume>();
		volumeSelector.setToolTipText("Select a sound.");
		for(AudioVolume volume : AudioVolume.getAll()){volumeSelector.addItem(volume);}
		volumeSelector.setBounds(10, 10, 184, 35);
		volumeSelector.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		volumeSelector.setForeground(ColorManager.FONT);
		volumeSelector.addActionListener(e -> playTestAudio());
		
		soundSelector = new JComboBox<AudioPath>();
		soundSelector.setToolTipText("Select a sound.");
		soundSelector.addItem(new AudioPath());
		AudioManager.scrapeAudioFiles().forEach(path -> {
			AudioPath audioPath = new AudioPath(path);
			pathCache.put(path.getParent().toString()+"\\"+path.getFileName(), audioPath);
			soundSelector.addItem(audioPath);
		});
		soundSelector.setBounds(10, 10, 184, 35);
		((JLabel)soundSelector.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		soundSelector.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		soundSelector.setForeground(ColorManager.FONT);
		soundSelector.addActionListener(e -> playTestAudio());

		soundPanel.add(soundSelector, BorderLayout.CENTER);
		soundPanel.add(volumeSelector, BorderLayout.EAST);
		return soundPanel;
	}
	
	private void playTestAudio() {
		AudioPath audioPath = (AudioPath) soundSelector.getSelectedItem();
		if(!audioPath.isDummy()){
			AudioManager.stopAudioClip(lastAudioClip);
			AudioVolume audioVolume = (AudioVolume) volumeSelector.getSelectedItem();
			lastAudioClip = Main.getAudioManager().playAudioClip(audioPath.getUri(), audioVolume);
		}
	}
	
	@Override
	public void dispose() {
		AudioManager.stopAudioClip(lastAudioClip);
		super.dispose();
	}
	
}
