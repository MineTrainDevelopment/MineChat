package de.minetrain.minechat.gui.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.config.enums.ReplyType;
import de.minetrain.minechat.features.messagehighlight.HighlightString;
import de.minetrain.minechat.gui.emotes.Emote;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.emotes.FlippedImageIcon;
import de.minetrain.minechat.gui.emotes.MirroredImageIcon;
import de.minetrain.minechat.gui.frames.ChatWindow;
import de.minetrain.minechat.gui.obj.ChatStatusPanel;
import de.minetrain.minechat.gui.obj.ChatWindowMessageComponent;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.obj.buttons.MineButton;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.main.MessageComponentContent;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.HTMLColors;
import de.minetrain.minechat.utils.MineStringBuilder;

//https://stackoverflow.com/questions/29962886/cloning-jpanel-into-another-jframe
public class MessageComponent extends JPanel {
	private static final Locale locale = new Locale(System.getProperty("user.language"), System.getProperty("user.country"));
	private static final Logger logger = LoggerFactory.getLogger(ChatWindowMessageComponent.class);
	private static final long serialVersionUID = -3116239050269500823L;
	private static final HashMap<String, Document> documentCache = new HashMap<String, Document>();//Channel_id+message, document
	
	private static final Map<String, String> emoteReplacements = new HashMap<>(); //emoteName, enryped replacement.
	private static MineStringBuilder stringBuilder = new MineStringBuilder();
	private static final Dimension buttonSize = new Dimension(28, 28);
	public static final Random random = new Random();
	private static final Document CLEAR_DOCUMENT = new JTextPane().getDocument();
	
	private boolean highlighted = false;
	private HighlightString highlightString = null;
	private TitledBorder titledBorder;
	private Map<String, String> webEmotes = new HashMap<String, String>();//name, immage path
	private MessageComponentContent messageContent;
	private JTextPane messageLabel;
	
	private int splitChunkSize = 45;
	
	public MessageComponent(MessageComponentContent content) {
		super(new BorderLayout());
		JPanel messagePanel = this;
        setBackground(ColorManager.GUI_BACKGROUND);
        webEmotes.putAll(content.getEmoteSet());
        
        TwitchMessage twitchMessage = content.twitchMessage();
        ChatWindow chatWindow = content.chatWindow();
        messageContent = content;
        
		titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ColorManager.GUI_BACKGROUND_LIGHT, 2, true), "");
		titledBorder.setTitleJustification(TitledBorder.LEFT);
//		titledBorder.setTitleColor(content.getUserColor());
		titledBorder.setTitleFont(new Font(null, Font.BOLD, 20));
		setBorder(titledBorder);
		
		TwitchMessage.collectBadges(content.getChannelId(), content.getBadges()).forEach(badge -> stringBuilder.appendIcon(badge.toString(), true));
    	titledBorder.setTitle(stringBuilder
    			.setPrefix(stringBuilder.getRawContent())
    			.setSuffix(":", HTMLColors.WHITE)
    			.clearContent()
    			.appendString(content.getUserName(), content.getUserColor())
    			.toString());
		
    	JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setBackground(ColorManager.GUI_BACKGROUND);
		buttonPanel.setMinimumSize(new Dimension(26, 26));
		add(buttonPanel, BorderLayout.WEST);
        
		MineButton markReadButton = new MineButton(buttonSize, null, ButtonType.NON);//.setInvisible(!MainFrame.debug);
		markReadButton.setPreferredSize(buttonSize);
		markReadButton.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		markReadButton.setBorderPainted(false);
		markReadButton.setIcon(Main.TEXTURE_MANAGER.getMarkReadButton());
		markReadButton.setToolTipText("Mark this message as read.");
		markReadButton.addActionListener(event -> {
			chatWindow.chatPanel.remove(messagePanel);
			content.chatWindow().chatPanel.revalidate();
			content.chatWindow().chatPanel.repaint();
		});

		MineButton replyButton = new MineButton(buttonSize, null, ButtonType.NON);//.setInvisible(!MainFrame.debug);
		replyButton.setPreferredSize(buttonSize);
		replyButton.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		replyButton.setBorderPainted(false);
		replyButton.setToolTipText("Replay to this message.");
		replyButton.setIcon((twitchMessage != null && content.twitchMessage().isReply()) ? Main.TEXTURE_MANAGER.getReplyChainButton() : Main.TEXTURE_MANAGER.getReplyButton());
		replyButton.addMouseListener(replyButtonMouseAdapter(replyButton));
		replyButton.setVisible(false);

		buttonPanel.add(markReadButton, BorderLayout.EAST);
		
		if(twitchMessage != null && !twitchMessage.isNull()){
			add(replyButton, BorderLayout.EAST);
			replyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					chatWindow.setMessageToReply(twitchMessage);
					chatWindow.chatStatusPanel.getinputArea().requestFocus();
					
					if(twitchMessage.getReplyType() == ReplyType.USER_NAME){
						ChatStatusPanel chatStatusPanel = chatWindow.chatStatusPanel;
						chatStatusPanel.overrideCurrentInputCache(false);
						chatStatusPanel.overrideUserInput("@"+twitchMessage.getUserName()+" "+chatStatusPanel.getCurrentUserInput());
					}
					
					if(twitchMessage.getReplyType() == ReplyType.MESSAGE){
						twitchMessage.setReply(twitchMessage.getMessageId(), twitchMessage.getUserName());
					}
				}
			});
		}
		
        //Right panel with message label
		JPanel messageContentPanel = new JPanel(new BorderLayout());
        messageContentPanel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        add(messageContentPanel, BorderLayout.CENTER);

        messageLabel = new JTextPane();
        messageLabel.setEditable(false);
		messageLabel.setFont(Settings.MESSAGE_FONT);
        messageLabel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		messageLabel.addMouseListener(replyButtonMouseAdapter(replyButton));
        messageContentPanel.add(messageLabel, BorderLayout.CENTER);
        
        String documentCacheKey = content.getChannelId()+content.message();
        
        if(documentCache.containsKey(documentCacheKey)){
        	messageLabel.setDocument(documentCache.get(documentCacheKey));
//    		System.err.println("reuse - "+emoteReplacements.size());
        }else{
        	formatText(content.message(), messageLabel.getDocument(), ColorManager.FONT, content.getTimeStamp());
//    		System.err.println("new content");
        }

//        System.out.println(ClassLayout.parseInstance(documentCacheKey).toPrintable());
//        System.out.println(ClassLayout.parseInstance(messageLabel.getDocument()).toPrintable());
        
        
        if(twitchMessage != null){
        	if(twitchMessage.isHighlighted() && Settings.displayTwitchHighlighted.isActive()){
        		messageLabel.setBackground(Settings.displayTwitchHighlighted.getColor());
            }
        	
	    	if(chatWindow.greetingsManager.contains(content.getUserName())){
		    	Settings.highlightStrings.values().stream().filter(highlight -> highlight.isAktiv()).forEach(highlight -> {
		    		Pattern pattern = Pattern.compile("\\b" + highlight.getWord() + "\\b", Pattern.CASE_INSENSITIVE);
		            Matcher matcher = pattern.matcher(content.message());
		            
		            if (matcher.find() && !highlighted) {
		    			titledBorder.setBorder(BorderFactory.createLineBorder(Color.decode(highlight.getBorderColorCode()), 2));
		    			highlightString = highlight;
		    			highlighted = true;
		            }
		    	});
	    	}else{
	    		chatWindow.greetingsManager.add(content.getUserName());
	    		if(Settings.highlightUserFirstMessages.isActive()){
					titledBorder.setBorder(BorderFactory.createLineBorder(Settings.highlightUserFirstMessages.getColor(), 2));
					highlighted = false;
	    		}
	    	}
	    	
	    	if(!chatWindow.greetingsManager.isMentioned(content.getUserName())){
		    	JButton waveButton = new MineButton(buttonSize, null, ButtonType.NON);//.setInvisible(!MainFrame.debug);
				waveButton.setPreferredSize(buttonSize);
				waveButton.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
				waveButton.setBorderPainted(false);
				waveButton.setIcon(Main.TEXTURE_MANAGER.getWaveButton());
				waveButton.setToolTipText("Wellcome this user!");
				waveButton.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent e){
						chatWindow.chatStatusPanel.overrideCurrentInputCache(false);
						chatWindow.chatStatusPanel.getinputArea().requestFocus();
						
						String greeting = chatWindow.parentTab.getGreetingTexts().get(random.nextInt(chatWindow.parentTab.getGreetingTexts().size()));
						chatWindow.chatStatusPanel.overrideUserInput(greeting.replace("{USER}", Settings.GREETING_TYPE == ReplyType.USER_NAME ? "@"+twitchMessage.getUserName() : "").trim().replaceAll(" +", " "));
						twitchMessage.setReplyType(Settings.GREETING_TYPE);
						
						if(twitchMessage.getReplyType() == ReplyType.THREAD){
							twitchMessage.setReply(twitchMessage.getReplyId(), twitchMessage.getUserName());
						}
						
						if(twitchMessage.getReplyType() == ReplyType.MESSAGE){
							twitchMessage.setReply(twitchMessage.getMessageId(), twitchMessage.getUserName());
						}
						
						chatWindow.setMessageToReply(twitchMessage);
					}
				});
				
				waveButton.addMouseListener(new MouseAdapter() {
				    @Override
				    public void mouseClicked(MouseEvent event) {
				        if (SwingUtilities.isRightMouseButton(event)) {
				        	chatWindow.greetingsManager.setMentioned(content.getUserName());
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
	    }
    	
    	
    	setPreferredSize(new Dimension(485, getPreferredSize().height));
    	webEmotes.clear(); //NOTE This is to improve ram usage.
    	messageContent = null;
    	stringBuilder.clear();
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
	
	private final void formatText(String input, Document document, Color fontColor, LocalDateTime timeStamp) {
    	String newInput="";
    	if(fontColor == ColorManager.FONT){
    		input = "["+timeStamp.format(DateTimeFormatter.ofPattern(Settings.messageTimeFormat, locale))+"] "+input;
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

//			HashMap<String, TwitchEmote> emotesByName = TwitchEmote.getEmotesByName();
			
			Settings.highlightStrings.values().stream().filter(highlight -> highlight.isAktiv()).forEach(highlight -> {
	    		Pattern pattern = Pattern.compile("\\b" + highlight.getWord() + "\\b", Pattern.CASE_INSENSITIVE);
	            Matcher matcher = pattern.matcher(word);
	            if (matcher.find()) {
	    			StyleConstants.setForeground(attributeSet, Color.decode(highlight.getWordColorCode()));
	    		}
	    	});
			
			String emotePath = webEmotes.get(word);
			if (emotePath != null) {
				ImageIcon emote = null;
				
				try {
					emote = !emotePath.startsWith("URL%") ? new ImageIcon(emotePath) : new ImageIcon(ImageIO.read(new URL(emotePath.substring(4))));
					
					switch (emoteStyle) {
					case 1: StyleConstants.setIcon(attributeSet, new MirroredImageIcon(emote.getImage())); break;
					case 2: StyleConstants.setIcon(attributeSet, new FlippedImageIcon(emote.getImage())); break;
					default: StyleConstants.setIcon(attributeSet, emote); break;}
					
					isEmote = true;
				} catch (IOException ex) {
					logger.warn("Failed to load an emote!\nPath: "+emotePath, ex);
				}
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
		
		documentCache.put(messageContent.getChannelId()+messageContent.message(), document);
    }
    
    
    //Replace emotes with 3 Chars for filtering.
	private final List<String> splitString(String input) {
    	input = input.replace("\\n", "�");
        List<String> chunks = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        input = (webEmotes == null) ? input : encryptEmotes(input);

        int wordBoundary = -1; // Index of the last space character within the chunk limit
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c != '�') {
                builder.append(c);
	            if (c == ' ') {
	                wordBoundary = builder.length() - 1;
	            }
	
	            if (builder.length() == splitChunkSize) {
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
    
    private final String encryptEmotes(String input) {
    	String[] split = input.split(" ");
    	emoteReplacements.clear();
    	
    	for(int i=0; i<split.length; i++){
    		String word = split[i];
			emoteReplacements.computeIfAbsent(word, key -> {
    			Emote emoteByName = EmoteManager.getChannelEmoteByName(messageContent.getChannelId(), word);
    			if(emoteByName != null) {
    				webEmotes.put(emoteByName.getName(), emoteByName.getFilePath());
    			}else{
    				emoteByName = EmoteManager.getEmoteByName(word);
    				if(emoteByName != null){
    					webEmotes.put(emoteByName.getName(), emoteByName.getFilePath());
    				}
    			}
        		
    			if(this.webEmotes.containsKey(word)){
    	            return generateReplacement(word);
    			}
    			return null;
    		});
		}
    	
    	for (Entry<String, String> entry : emoteReplacements.entrySet()) {
    		input = input.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
    	}
        return input.trim();
    }
    
    private static final String decryptEmotes(String input) {
        for (Entry<String, String> entry : emoteReplacements.entrySet()) {
            input = input.replaceAll("\\b" + entry.getValue() + "\\b", entry.getKey());
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
        if(emoteReplacements.containsValue(builder.toString())){
        	return generateReplacement(input);
        }
        
        return builder.toString();
    }
	
	
	public void clear(){
		messageLabel.setDocument(CLEAR_DOCUMENT);
	}

	public boolean isHighlighted() {
		return highlighted;
	}
	
	public HighlightString getHighlightString(){
		return highlightString;
	}
}
