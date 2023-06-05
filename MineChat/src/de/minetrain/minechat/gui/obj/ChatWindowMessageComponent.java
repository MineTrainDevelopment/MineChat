package de.minetrain.minechat.gui.obj;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.obj.TwitchEmote;
import de.minetrain.minechat.gui.frames.ChatWindow;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.obj.buttons.MineButton;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.FlippedImageIcon;
import de.minetrain.minechat.gui.utils.MirroredImageIcon;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.utils.IconStringBuilder;
import de.minetrain.minechat.utils.Settings;
import de.minetrain.minechat.utils.TwitchMessage;

public class ChatWindowMessageComponent extends JPanel{
	private static final Logger logger = LoggerFactory.getLogger(ChatWindowMessageComponent.class);
	private static final long serialVersionUID = -3116239050269500823L;
	
	private static final Font MESSAGE_FONT = new Font("Arial Unicode MS", Font.BOLD, 17);
	private static final Map<String, String> emoteReplacements = new HashMap<>();
	private static final Dimension buttonSize = new Dimension(28, 28);
	public static final Random random = new Random();
	
	private boolean highlighted = false;
	private JPanel buttonPanel;
	private TitledBorder titledBorder;
	private JTextPane messageLabel;
	private JPanel messageContentPanel;
	private MineButton markReadButton, replyButton;
	private final String userName;
	
	public ChatWindowMessageComponent(String topic, String message, Color borderColor, MineButton actionButton, ChatWindow chatWindow) {
		super(new BorderLayout());
		this.userName = topic;
		this.highlighted = true;
		JPanel messagePanel = this;
		setMinimumSize(new Dimension(400, 25));
		setBackground(ColorManager.GUI_BACKGROUND);
		
		messageLabel = new JTextPane();
		messageLabel.setEditable(false);
		messageLabel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		messageLabel.setFont(MESSAGE_FONT);
		formatText(message, messageLabel.getStyledDocument(), new Color(30, 30, 30));
		
		messageContentPanel = new JPanel(new BorderLayout());
		messageContentPanel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		messageContentPanel.add(messageLabel, BorderLayout.CENTER);
		add(messageContentPanel, BorderLayout.CENTER);
		  
		titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(borderColor, 2, true), "  "+topic+"  ");
		titledBorder.setTitleJustification(TitledBorder.CENTER);
		titledBorder.setTitleColor(borderColor); //Color.CYAN
		titledBorder.setTitleFont(new Font(null, Font.BOLD, 20));
		setBorder(titledBorder);
		
		markReadButton = new MineButton(buttonSize, null, ButtonType.NON);//.setInvisible(!MainFrame.debug);
		markReadButton.setPreferredSize(buttonSize);
		markReadButton.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		markReadButton.setBorderPainted(false);
		markReadButton.setIcon(Main.TEXTURE_MANAGER.getMarkReadButton());
		markReadButton.setToolTipText("Mark this message as read.");
		markReadButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e){chatWindow.chatPanel.remove(messagePanel); chatWindow.chatPanel.revalidate(); chatWindow.chatPanel.repaint();}
		});
		
		if(actionButton != null){
			add(actionButton, BorderLayout.EAST);
		}
		
		add(markReadButton, BorderLayout.WEST);
    	setPreferredSize(new Dimension(485, getPreferredSize().height));
	}
	
	public ChatWindowMessageComponent(String message, String userName, Color userColor, TwitchMessage twitchMessage, ChatWindow chatWindow) {
		super(new BorderLayout());
		this.userName = userName;
		JPanel messagePanel = this;
        setBackground(ColorManager.GUI_BACKGROUND);
        
		titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ColorManager.GUI_BACKGROUND_LIGHT, 2, true), userName+":");
		titledBorder.setTitleJustification(TitledBorder.LEFT);
		titledBorder.setTitleColor(userColor);
		titledBorder.setTitleFont(new Font(null, Font.BOLD, 20));
		setBorder(titledBorder);
		
		
		if (chatWindow.badges.containsKey(userName.toLowerCase())) {
			IconStringBuilder stringBuilder = new IconStringBuilder().setSuffix(userName+":");
			chatWindow.badges.get(userName.toLowerCase()).forEach(badge -> {
				stringBuilder.appendIcon(badge, true);
			});
			
        	titledBorder.setTitle(stringBuilder.toString());
        }
		
		buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setBackground(ColorManager.GUI_BACKGROUND);
		buttonPanel.setMinimumSize(new Dimension(26, 26));
		add(buttonPanel, BorderLayout.WEST);
        
		markReadButton = new MineButton(buttonSize, null, ButtonType.NON);//.setInvisible(!MainFrame.debug);
		markReadButton.setPreferredSize(buttonSize);
		markReadButton.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		markReadButton.setBorderPainted(false);
		markReadButton.setIcon(Main.TEXTURE_MANAGER.getMarkReadButton());
		markReadButton.setToolTipText("Mark this message as read.");
		markReadButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e){chatWindow.chatPanel.remove(messagePanel); chatWindow.chatPanel.revalidate(); chatWindow.chatPanel.repaint();}
		});

		replyButton = new MineButton(buttonSize, null, ButtonType.NON);//.setInvisible(!MainFrame.debug);
		replyButton.setPreferredSize(buttonSize);
		replyButton.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		replyButton.setBorderPainted(false);
		replyButton.setToolTipText("Replay to this message.");
		replyButton.setIcon((twitchMessage != null && twitchMessage.isReply()) ? Main.TEXTURE_MANAGER.getReplyChainButton() : Main.TEXTURE_MANAGER.getReplyButton());
		replyButton.addMouseListener(replyButtonMouseAdapter(replyButton));
		replyButton.setVisible(false);

		buttonPanel.add(markReadButton, BorderLayout.EAST);
		
		if(twitchMessage != null && !twitchMessage.isNull()){
			add(replyButton, BorderLayout.EAST);
			replyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					chatWindow.setMessageToReply(twitchMessage);
				}
			});
		}
		
        //Right panel with message label
		messageContentPanel = new JPanel(new BorderLayout());
        messageContentPanel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        add(messageContentPanel, BorderLayout.CENTER);

		messageLabel = new JTextPane();
        messageLabel.setEditable(false);
		messageLabel.setFont(MESSAGE_FONT);
        messageLabel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		messageLabel.addMouseListener(replyButtonMouseAdapter(replyButton));
		formatText(message, messageLabel.getStyledDocument(), Color.WHITE);
        messageContentPanel.add(messageLabel, BorderLayout.CENTER);
        
    	if(chatWindow.greetingsManager.contains(userName)){
	    	Settings.highlightStrings.forEach(string -> {
	    		Pattern pattern = Pattern.compile("\\b" + string + "\\b", Pattern.CASE_INSENSITIVE);
	            Matcher matcher = pattern.matcher(message);
	            
	            if (matcher.find()) {
	    			titledBorder.setBorder(BorderFactory.createLineBorder(ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT, 2));
	    			highlighted = true;
	            }
	    	});
    	}else{
    		chatWindow.greetingsManager.add(userName);
			titledBorder.setBorder(BorderFactory.createLineBorder(ColorManager.CHAT_MESSAGE_GREETING_HIGHLIGHT, 2));
			highlighted = true;
    	}
    	
    	if(!chatWindow.greetingsManager.isMentioned(userName)){
	    	JButton waveButton = new MineButton(buttonSize, null, ButtonType.NON);//.setInvisible(!MainFrame.debug);
			waveButton.setPreferredSize(buttonSize);
			waveButton.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
			waveButton.setBorderPainted(false);
			waveButton.setIcon(Main.TEXTURE_MANAGER.getWaveButton());
			waveButton.setToolTipText("Wellcome this user!");
			waveButton.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e){
					chatWindow.chatStatusPanel.overrideCurrentInputCache();
					
					String greeting = chatWindow.parentTab.getGreetingTexts().get(random.nextInt(chatWindow.parentTab.getGreetingTexts().size()));
					chatWindow.chatStatusPanel.overrideUserInput(greeting.replace("{USER}", "").trim().replaceAll(" +", " "));
					chatWindow.setMessageToReply(twitchMessage);
				}
			});
			
			waveButton.addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent event) {
			        if (SwingUtilities.isRightMouseButton(event)) {
			        	chatWindow.greetingsManager.setMentioned(userName);
			        	buttonPanel.remove(waveButton);
						buttonPanel.setMinimumSize(new Dimension(26, 26));
						chatWindow.chatPanel.revalidate();
			        	chatWindow.chatPanel.repaint();
			        }
			    }
			});
			
			buttonPanel.add(waveButton, BorderLayout.WEST);
			buttonPanel.setMinimumSize(new Dimension(52, 26));
    	}
    	
    	
    	replyButtonMouseAdapter(replyButton);
    	setPreferredSize(new Dimension(485, getPreferredSize().height));
	}
	
	private static final MouseAdapter replyButtonMouseAdapter(JButton button) {
		return new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Wenn die Maus ins Panel eintritt, zeige den Knopf an
            	button.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Wenn die Maus das Panel verlässt, blende den Knopf aus
            	button.setVisible(false);
            }
        };
	}
	
	private static final void formatText(String input, StyledDocument document, Color fontColor) {
    	String newInput="";
    	if(fontColor == Color.WHITE){
    		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin")); //Set the default time zone.
    		input = "["+LocalTime.now().format(DateTimeFormatter.ofPattern(Settings.timeFormat, Locale.GERMAN))+"] "+input;
    	}
        
        for(String string : splitString(input)){newInput += string.trim()+" \n ";}
        input = (newInput.contains("\n") ? newInput.substring(0, newInput.lastIndexOf("\n")).trim() : newInput.trim());
        
        String previousWord = "";
		for (String word : input.split(" ")) {
			SimpleAttributeSet attributeSet = new SimpleAttributeSet();
			StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
			StyleConstants.setForeground(attributeSet, fontColor);
			
			boolean isEmote = false;
			int emoteStyle = (previousWord.equals("h!") ? 1 : (previousWord.equals("v!") ? 2 : 0 ));
			previousWord = word;

			HashMap<String, TwitchEmote> emotesByName = TwitchEmote.getEmotesByName();
			
			Settings.highlightStrings.forEach(string -> {
	    		Pattern pattern = Pattern.compile("\\b" + string + "\\b", Pattern.CASE_INSENSITIVE);
	            Matcher matcher = pattern.matcher(word);
	            if (matcher.find()) {
	    			StyleConstants.setForeground(attributeSet, new Color(255, 40, 40));
	    		}
	    	});
			
			if (emotesByName.get(word) != null) {
				ImageIcon emote = emotesByName.get(word).getImageIcon();
				
				System.out.println("EmoteText --> "+word);
				
				switch (emoteStyle) {
				case 1: StyleConstants.setIcon(attributeSet, new MirroredImageIcon(emote.getImage())); break;
				case 2: StyleConstants.setIcon(attributeSet, new FlippedImageIcon(emote.getImage())); break;
				default: StyleConstants.setIcon(attributeSet, emote); break;}
				
				isEmote = true;
			}

			try {
				if(!previousWord.equals("h!") && !previousWord.equals("v!")){
					document.insertString(document.getLength(), word + " ", attributeSet);
				}

				if (isEmote) {
					document.insertString(document.getLength(), " ", null);
				}
			} catch (BadLocationException ex) {
				logger.error("Can´t modify styledDocument! ", ex);
			}
		}
    }
    
    
    //TODO Replace emotes with 3 Chars for filtering.
	private static final List<String> splitString(String input) {
    	input = input.replace("\\n", "�");
        int chunkSize = 45;
        List<String> chunks = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        input = encryptEmotes(input);

        int wordBoundary = -1; // Index of the last space character within the chunk limit
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c != '�') {
                builder.append(c);
	            if (c == ' ') {
	                wordBoundary = builder.length() - 1;
	            }
	
	            if (builder.length() == chunkSize) {
	                if (wordBoundary != -1) {
	                    chunks.add(builder.substring(0, wordBoundary).trim());
	                    builder.delete(0, wordBoundary + 1);
	                    wordBoundary = -1;
	                } else {
	                    chunks.add(builder.toString().trim());
	                    builder.setLength(0);
	                }
	            }
            } else {
                if (builder.length() > 0) {
                    chunks.add(builder.toString().trim());
                    builder.setLength(0);
                }
            }
        }

        // Add the remaining characters as the last chunk
        if (builder.length() > 0) {
            chunks.add(builder.toString().trim());
        }
        
        for (int i=0; i<chunks.size(); i++) {
			chunks.set(i, decryptEmotes(chunks.get(i)));
		}
        
        return chunks;
    }
    
    private static final String encryptEmotes(String input) {
    	HashMap<String, TwitchEmote> emotesByName = TwitchEmote.getEmotesByName();
    	String[] split = input.split(" ");
    	String output = "";
    	emoteReplacements.clear();
    	
    	for (int i=0; i<split.length; i++) {
			if(emotesByName.containsKey(split[i])){
	            String replacement = generateReplacement(split[i]);
	            emoteReplacements.put(replacement, split[i]);
	            split[i] = split[i].replaceAll("\\b" + split[i] + "\\b", replacement);
			}
			output += split[i]+" ";
		}
    	
        return output.trim();
    }

    private static final String decryptEmotes(String input) {
        for (Entry<String, String> entry : emoteReplacements.entrySet()) {
            input = input.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
        }
        return input;
    }


    private static final String generateReplacement(String input) {
        String alphabet = "abcdefghijklmnopqrstuvwABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            int index = (int) (Math.random() * alphabet.length());
            builder.append(alphabet.charAt(index));
        }
        
        //Check if the replacement is alrady taken.
        if(emoteReplacements.containsKey(builder.toString())){
        	return generateReplacement(input);
        }
        
        return builder.toString();
    }
	
	
	public JPanel getMinimised(){
		if(!isHighlighted()){
			
			if(messageLabel.getMouseListeners().length>0){
				messageLabel.removeMouseListener(messageLabel.getMouseListeners()[0]);
			}
			
			messageLabel.setText("["+userName+"] - "+messageLabel.getText());
			
			buttonPanel = null;
			titledBorder = null;
			markReadButton = null;
			replyButton = null;
			return messageContentPanel;
		}
		return this;
	}

	public boolean isHighlighted() {
		return highlighted;
	}
}
