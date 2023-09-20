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
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
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

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.gui.frames.parant.MineDialog;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.utils.HTMLColors;
import de.minetrain.minechat.utils.MineStringBuilder;
import de.minetrain.minechat.utils.obj.HighlightString;

public class AddWordHighlightFrame extends MineDialog {
	private static final long serialVersionUID = -4774556639413934907L;
	private static final Logger logger = LoggerFactory.getLogger(AddWordHighlightFrame.class);
	private Color borderColor = ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT;
	private Color wordColor = ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT;
	private JDialog thisDialog = this;
	private JTextField inputField;
	TitledBorder titledBorder;
	private static final MineStringBuilder stringBuilder = new MineStringBuilder();

	public AddWordHighlightFrame(JFrame parentFrame) {
		super(parentFrame, "Add a word Highlight", new Dimension(400, 115));
		
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
		
        JLabel textLabel = new JLabel(getPreviewText().toString());
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
		contentPanel.add(messagePreview, BorderLayout.NORTH);
		
		addContent(contentPanel, BorderLayout.CENTER);
		setConfirmButtonAction(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				logger.debug("Try to save a new HighlightString\n  " + (inputField != null ? inputField.getText() : "null"));
				String newWord = HighlightString.saveNewWord(inputField.getText(), wordColor, borderColor);
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
		
		setVisible(true);
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
	
}
