package de.minetrain.minechat.gui.obj;

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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.config.enums.ReplyType;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.emotes.FlippedImageIcon;
import de.minetrain.minechat.gui.emotes.MirroredImageIcon;
import de.minetrain.minechat.gui.frames.ChatWindow;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.obj.buttons.MineButton;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.MineStringBuilder;

public class ChatWindowMessageComponent extends JPanel{
	private static final Locale locale = new Locale(System.getProperty("user.language"), System.getProperty("user.country"));
	private static final Logger logger = LoggerFactory.getLogger(ChatWindowMessageComponent.class);
	private static final long serialVersionUID = -3116239050269500823L;
	
	private static final Map<String, String> emoteReplacements = new HashMap<>();
	private static final Dimension buttonSize = new Dimension(28, 28);
	public static final Random random = new Random();
	
	private boolean highlighted = false;
	private JPanel buttonPanel;
	private TitledBorder titledBorder;
	private JTextPane messageLabel;
	private JPanel messageContentPanel;
	private MineButton markReadButton, replyButton;
	private Map<String, String> emotes;
	private long epochTime = 0;
	private final String userName;
	
	public ChatWindowMessageComponent(String topic, String message, Color borderColor, MineButton actionButton, ChatWindow chatWindow, Map<String, String> emotes) {
		super(new BorderLayout());
		this.emotes = emotes;
		this.userName = topic;
		this.highlighted = true;
		JPanel messagePanel = this;
		setMinimumSize(new Dimension(400, 25));
		setBackground(ColorManager.GUI_BACKGROUND);
		
		messageLabel = new JTextPane();
		messageLabel.setEditable(false);
		messageLabel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		messageLabel.setFont(Settings.MESSAGE_FONT);
		formatText(message, messageLabel.getStyledDocument(), new Color(30, 30, 30), epochTime);
		
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
			@Override 
			public void actionPerformed(ActionEvent e){
				chatWindow.chatPanel.remove(messagePanel); 
				chatWindow.chatPanel.revalidate(); 
				chatWindow.chatPanel.repaint();
			}
		});
		
		if(actionButton != null){
			add(actionButton, BorderLayout.EAST);
		}
		
		add(markReadButton, BorderLayout.WEST);
    	setPreferredSize(new Dimension(485, getPreferredSize().height));
	}
	
	public ChatWindowMessageComponent(String message, String userName, Color userColor, TwitchMessage twitchMessage, ChatWindow chatWindow) {
		super(new BorderLayout());
		this.emotes = twitchMessage == null ? EmoteManager.getAllEmotesByName() : twitchMessage.getEmotes();
		this.epochTime = twitchMessage == null ? epochTime : twitchMessage.getEpochTime();
		this.userName = userName;
		JPanel messagePanel = this;
        setBackground(ColorManager.GUI_BACKGROUND);
        
		titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ColorManager.GUI_BACKGROUND_LIGHT, 2, true), userName+":");
		titledBorder.setTitleJustification(TitledBorder.LEFT);
		titledBorder.setTitleColor(userColor);
		titledBorder.setTitleFont(new Font(null, Font.BOLD, 20));
		setBorder(titledBorder);
		
		
		if(twitchMessage != null && !twitchMessage.getBadges().isEmpty()){
			MineStringBuilder stringBuilder = new MineStringBuilder().setSuffix(userName+":");
			twitchMessage.getBadges().forEach(badge -> stringBuilder.appendIcon(badge, true));
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
		messageContentPanel = new JPanel(new BorderLayout());
        messageContentPanel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        add(messageContentPanel, BorderLayout.CENTER);

		messageLabel = new JTextPane();
        messageLabel.setEditable(false);
		messageLabel.setFont(Settings.MESSAGE_FONT);
        messageLabel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		messageLabel.addMouseListener(replyButtonMouseAdapter(replyButton));
		formatText(message, messageLabel.getStyledDocument(), ColorManager.FONT, epochTime);
        messageContentPanel.add(messageLabel, BorderLayout.CENTER);
        
        
        if(twitchMessage != null){
        	if(twitchMessage.isHighlighted() && Settings.displayTwitchHighlighted.isActive()){
        		messageLabel.setBackground(Settings.displayTwitchHighlighted.getColor());
            }
        	
	    	if(chatWindow.greetingsManager.contains(userName)){
		    	Settings.highlightStrings.forEach(highlight -> {
		    		Pattern pattern = Pattern.compile("\\b" + highlight.getWord() + "\\b", Pattern.CASE_INSENSITIVE);
		            Matcher matcher = pattern.matcher(message);
		            
		            if (matcher.find() && !highlighted) {
		    			titledBorder.setBorder(BorderFactory.createLineBorder(Color.decode(highlight.getBorderColorCode()), 2));
		    			highlighted = true;
		            }
		    	});
	    	}else{
	    		chatWindow.greetingsManager.add(userName);
	    		if(Settings.highlightUserFirstMessages.isActive()){
					titledBorder.setBorder(BorderFactory.createLineBorder(Settings.highlightUserFirstMessages.getColor(), 2));
					highlighted = true;
	    		}
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
						chatWindow.chatStatusPanel.overrideCurrentInputCache(false);
						chatWindow.chatStatusPanel.getinputArea().requestFocus();
						
						String greeting = chatWindow.parentTab.getGreetingTexts().get(random.nextInt(chatWindow.parentTab.getGreetingTexts().size()));
						chatWindow.chatStatusPanel.overrideUserInput(greeting.replace("{USER}", Settings.GREETING_TYPE == ReplyType.USER_NAME ? "@"+twitchMessage.getUserName() : "").trim().replaceAll(" +", " "));
						twitchMessage.setReplyType(Settings.GREETING_TYPE);
						
						if(twitchMessage.getReplyType() != ReplyType.THREAD){
							twitchMessage.setReply(twitchMessage.getMessageId(), twitchMessage.getUserName());
						}
						
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
	    }
    	
    	
    	setPreferredSize(new Dimension(485, getPreferredSize().height));
    	emotes.clear(); //NOTE This is to improve ram usage.
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
	
	private final void formatText(String input, StyledDocument document, Color fontColor, long epochTime) {
    	String newInput="";
    	if(fontColor == ColorManager.FONT){
			LocalDateTime dateTime = epochTime > 0
					? LocalDateTime.ofEpochSecond(epochTime/ 1000, 0, ZoneId.systemDefault().getRules().getOffset(Instant.now()))
					: LocalDateTime.now();
    		input = "["+dateTime.format(DateTimeFormatter.ofPattern(Settings.messageTimeFormat, locale))+"] "+input;
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
			Map<String, String> emotesByName = this.emotes;
			
			Settings.highlightStrings.forEach(highlight -> {
	    		Pattern pattern = Pattern.compile("\\b" + highlight.getWord() + "\\b", Pattern.CASE_INSENSITIVE);
	            Matcher matcher = pattern.matcher(word);
	            if (matcher.find()) {
	    			StyleConstants.setForeground(attributeSet, Color.decode(highlight.getWordColorCode()));
	    		}
	    	});
			
			String emotePath = emotesByName.get(word);
			if (emotesByName != null && emotePath != null) {
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
    }
    
    
    //TODO Replace emotes with 3 Chars for filtering.
	private final List<String> splitString(String input) {
    	input = input.replace("\\n", "�");
        int chunkSize = 45;
        List<String> chunks = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        input = (emotes == null) ? input : encryptEmotes(input);

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
    
    private final String encryptEmotes(String input) {
    	String[] split = input.split(" ");
    	String output = "";
    	emoteReplacements.clear();
    	
    	for (int i=0; i<split.length; i++) {
			if(this.emotes.containsKey(split[i])){
	            String replacement = generateReplacement(split[i]);
	            emoteReplacements.put(replacement, split[i]);
	            split[i] = split[i].replaceAll("\\b" + Pattern.quote(split[i]) + "\\b", replacement);
			}
			output += split[i]+" ";
		}
    	
        return output.trim();
    }
    
//    java.lang.reflect.InvocationTargetException
//	at jdk.internal.reflect.GeneratedMethodAccessor12.invoke(Unknown Source)
//	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
//	at java.base/java.lang.reflect.Method.invoke(Method.java:564)
//	at com.github.philippheuer.events4j.simple.SimpleEventHandler.lambda$handleAnnotationHandlers$5(SimpleEventHandler.java:130)
//	at java.base/java.util.concurrent.CopyOnWriteArrayList.forEach(CopyOnWriteArrayList.java:807)
//	at com.github.philippheuer.events4j.simple.SimpleEventHandler.lambda$handleAnnotationHandlers$6(SimpleEventHandler.java:127)
//	at java.base/java.util.concurrent.ConcurrentHashMap.forEach(ConcurrentHashMap.java:1603)
//	at com.github.philippheuer.events4j.simple.SimpleEventHandler.handleAnnotationHandlers(SimpleEventHandler.java:126)
//	at com.github.philippheuer.events4j.simple.SimpleEventHandler.publish(SimpleEventHandler.java:100)
//	at com.github.philippheuer.events4j.core.EventManager.lambda$publish$0(EventManager.java:157)
//	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
//	at com.github.philippheuer.events4j.core.EventManager.publish(EventManager.java:157)
//	at com.github.twitch4j.chat.events.IRCEventHandler.onChannelMessage(IRCEventHandler.java:124)
//	at com.github.philippheuer.events4j.simple.SimpleEventHandler.lambda$handleConsumerHandlers$3(SimpleEventHandler.java:112)
//	at java.base/java.util.concurrent.CopyOnWriteArrayList.forEach(CopyOnWriteArrayList.java:807)
//	at com.github.philippheuer.events4j.simple.SimpleEventHandler.lambda$handleConsumerHandlers$4(SimpleEventHandler.java:112)
//	at java.base/java.lang.Iterable.forEach(Iterable.java:75)
//	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1087)
//	at com.github.philippheuer.events4j.simple.SimpleEventHandler.handleConsumerHandlers(SimpleEventHandler.java:109)
//	at com.github.philippheuer.events4j.simple.SimpleEventHandler.publish(SimpleEventHandler.java:99)
//	at com.github.philippheuer.events4j.core.EventManager.lambda$publish$0(EventManager.java:157)
//	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
//	at com.github.philippheuer.events4j.core.EventManager.publish(EventManager.java:157)
//	at com.github.twitch4j.chat.TwitchChat.lambda$onTextMessage$16(TwitchChat.java:572)
//	at java.base/java.util.Arrays$ArrayList.forEach(Arrays.java:4203)
//	at com.github.twitch4j.chat.TwitchChat.onTextMessage(TwitchChat.java:544)
//	at com.github.twitch4j.client.websocket.WebsocketConnection$1.onTextMessage(WebsocketConnection.java:123)
//	at com.neovisionaries.ws.client.ListenerManager.callOnTextMessage(ListenerManager.java:353)
//	at com.neovisionaries.ws.client.ReadingThread.callOnTextMessage(ReadingThread.java:266)
//	at com.neovisionaries.ws.client.ReadingThread.callOnTextMessage(ReadingThread.java:244)
//	at com.neovisionaries.ws.client.ReadingThread.handleTextFrame(ReadingThread.java:969)
//	at com.neovisionaries.ws.client.ReadingThread.handleFrame(ReadingThread.java:752)
//	at com.neovisionaries.ws.client.ReadingThread.main(ReadingThread.java:108)
//	at com.neovisionaries.ws.client.ReadingThread.runMain(ReadingThread.java:64)
//	at com.neovisionaries.ws.client.WebSocketThread.run(WebSocketThread.java:45)
//Caused by: java.util.regex.PatternSyntaxException: Unmatched closing ')' near index 2
//\b:)\b
//  ^
//	at java.base/java.util.regex.Pattern.error(Pattern.java:2028)
//	at java.base/java.util.regex.Pattern.compile(Pattern.java:1787)
//	at java.base/java.util.regex.Pattern.<init>(Pattern.java:1430)
//	at java.base/java.util.regex.Pattern.compile(Pattern.java:1069)
//	at java.base/java.lang.String.replaceAll(String.java:2142)
//	at de.minetrain.minechat.gui.obj.ChatWindowMessageComponent.encryptEmotes(ChatWindowMessageComponent.java:395)
//	at de.minetrain.minechat.gui.obj.ChatWindowMessageComponent.splitString(ChatWindowMessageComponent.java:344)
//	at de.minetrain.minechat.gui.obj.ChatWindowMessageComponent.formatText(ChatWindowMessageComponent.java:281)
//	at de.minetrain.minechat.gui.obj.ChatWindowMessageComponent.<init>(ChatWindowMessageComponent.java:193)
//	at de.minetrain.minechat.gui.frames.ChatWindow.displayMessage(ChatWindow.java:127)
//	at de.minetrain.minechat.gui.frames.ChatWindow.displayMessage(ChatWindow.java:118)
//	at de.minetrain.minechat.twitch.TwitchListner.onAbstractChannelMessage(TwitchListner.java:111)
//	... 35 more

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
