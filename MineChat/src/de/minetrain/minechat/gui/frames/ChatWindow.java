package de.minetrain.minechat.gui.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
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
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.twitch4j.chat.events.AbstractChannelMessageEvent;

import de.minetrain.minechat.config.obj.TwitchEmote;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.obj.buttons.MineButton;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.MessageManager;
import de.minetrain.minechat.utils.CallCounter;
import de.minetrain.minechat.utils.Settings;

public class ChatWindow extends JLabel {
	private static final long serialVersionUID = -8392586696866883591L;
	private static final Logger logger = LoggerFactory.getLogger(ChatWindow.class);
	private static final Font MESSAGE_FONT = new Font("SansSerif", Font.BOLD, 17);
	private static Map<String, String> emoteReplacements = new HashMap<>();
	public List<String> chatterNames = new ArrayList<String>();
	public AbstractChannelMessageEvent messageEvent = null;
	private CallCounter messagesPerMin = new CallCounter();
	private MineButton sendButton, cancelReplyButton;
	private String currentlyWritingString = "";
	private Integer messagesPerDay = 0;
    private JPanel chatPanel;
    private JTextField inputField;
    private JScrollPane scrollPane;
    private JLabel inputInfo;
    private final ChannelTab parrentTab;
    
    public ChatWindow(ChannelTab parrentTab) {
    	this.parrentTab = parrentTab;
        setSize(482, 504);
        setLayout(new BorderLayout());
        setBackground(ColorManager.GUI_BACKGROUND);

//        inputInfo = new JLabel("Reply: @Kuchenhopper: Guten Morgen eguyLurk sinticaHuhu sinticaLove");
        inputInfo = new JLabel(getMessageInfoText());
        inputInfo.setBackground(ColorManager.GUI_BACKGROUND);
        inputInfo.setForeground(Color.WHITE);
        inputInfo.setLayout(new BorderLayout());
        inputInfo.setHorizontalAlignment(CENTER);
        inputInfo.setFont(new Font(null, Font.BOLD, 20));
//        inputInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
//        inputInfo.add(Box.createHorizontalStrut(2), BorderLayout.SOUTH);
        
        cancelReplyButton = createNewButton(Main.TEXTURE_MANAGER.getCancelButton(), "Cancel reply", new Dimension(26, 26), ColorManager.GUI_BACKGROUND);
        cancelReplyButton.setVisible(false);
        cancelReplyButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e){
				if(!currentlyWritingString.isEmpty()){
					inputField.setText(currentlyWritingString);
				}
				setMessageToReply(null);
			}
		});
		inputInfo.add(cancelReplyButton, BorderLayout.WEST);
        
        // Chat Panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(chatPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Input Field and Send Button
        inputField = new JTextField();
        inputField.setFont(MESSAGE_FONT);
        inputField.setForeground(Color.WHITE);
        inputField.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        
        sendButton = createNewButton(Main.TEXTURE_MANAGER.getEnterButton(), "Send message", new Dimension(62, 28), ColorManager.GUI_BACKGROUND_LIGHT);
        MineButton emoteButton = createNewButton(Main.TEXTURE_MANAGER.getEmoteButton(), "Send message", new Dimension(28, 28), ColorManager.GUI_BACKGROUND_LIGHT);
        emoteButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e){
				EmoteSelector emoteSelector = new EmoteSelector(Main.MAIN_FRAME, true);
				emoteSelector.addSelectetEmoteToText(inputField);
			}
		});
        
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		buttonPanel.add(emoteButton, BorderLayout.WEST);
        buttonPanel.add(sendButton, BorderLayout.EAST);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputInfo, BorderLayout.NORTH);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        inputPanel.setBackground(ColorManager.GUI_BACKGROUND);
		inputPanel.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 4, true));
        add(inputPanel, BorderLayout.SOUTH);
        
        scrollPane.setBorder(null);
//		scrollPane.setBorder(BorderFactory.createLineBorder(ColorManager.BACKGROUND_LIGHT, 2));
		scrollPane.setBackground(ColorManager.GUI_BACKGROUND);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBackground(ColorManager.GUI_BORDER);
        scrollPane.getVerticalScrollBar().setForeground(ColorManager.GUI_BUTTON_BACKGROUND);
        scrollPane.getVerticalScrollBar().setBorder(null);
        
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//            	displaySystemInfo("unimportant", "This is a test", ColorManager.CHAT_UNIMPORTANT);
//            	displaySystemInfo("moderationColor", "This is a test", ColorManager.CHAT_MODERATION);
//            	displaySystemInfo("spendingSmall", "This is a test", ColorManager.CHAT_SPENDING_SMALL);
//            	displaySystemInfo("spendingBig", "This is a test", ColorManager.CHAT_SPENDING_BIG);
//            	displaySystemInfo("announcement", "This is a test", ColorManager.CHAT_ANNOUNCEMENT);
//            	displaySystemInfo("userReward", "This is a test", ColorManager.CHAT_USER_REWARD);
//            	displaySystemInfo("userping", "This is a test", ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT);
//            	displaySystemInfo("greeding", "This is a test", ColorManager.CHAT_MESSAGE_GREETING_HIGHLIGHT);
            	String message = inputField.getText();
            	if(message.isEmpty()){return;}
                inputField.setText(currentlyWritingString);
                currentlyWritingString = "";
                
                MessageManager.sendMessage(message);
            }
        });
    }
    
    public void displayMessage(String message, String userName, Color userColor) {
    	displayMessage(message, userName, userColor, null);
    }

    public void displayMessage(String message, String userName, Color userColor, AbstractChannelMessageEvent event) {
    	JPanel messagePanel = new JPanel(new BorderLayout());
//        messagePanel.setPreferredSize(new Dimension(400, 25)); // Set the height of each message panel to 25 pixels
        messagePanel.setMinimumSize(new Dimension(400, 25));
        messagePanel.setBackground(ColorManager.GUI_BACKGROUND);
        
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ColorManager.GUI_BACKGROUND_LIGHT, 2, true), userName+":");
		titledBorder.setTitleJustification(TitledBorder.LEFT);
		titledBorder.setTitleColor(userColor);
		titledBorder.setTitleFont(new Font(null, Font.BOLD, 20));
		messagePanel.setBorder(titledBorder);
		
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setBackground(ColorManager.GUI_BACKGROUND);
		buttonPanel.setMinimumSize(new Dimension(26, 26));
		messagePanel.add(buttonPanel, BorderLayout.WEST);
        
		Dimension buttonSize = new Dimension(28, 28);
		JButton button1 = new MineButton(buttonSize, null, ButtonType.NON);//.setInvisible(!MainFrame.debug);
		button1.setPreferredSize(buttonSize);
		button1.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		button1.setBorderPainted(false);
		button1.setIcon(Main.TEXTURE_MANAGER.getMarkReadButton());
		button1.setToolTipText("Mark this message as read.");
		button1.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e){chatPanel.remove(messagePanel); chatPanel.revalidate(); chatPanel.repaint();}
		});

		JButton button2 = new MineButton(buttonSize, null, ButtonType.NON);//.setInvisible(!MainFrame.debug);
		button2.setPreferredSize(buttonSize);
		button2.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		button2.setBorderPainted(false);
		button2.setToolTipText("Replay to this message.");
		button2.setIcon(Main.TEXTURE_MANAGER.getReplyButton());
		button2.addMouseListener(replyButtonMouseAdapter(button2));
		button2.setVisible(false);

		buttonPanel.add(button1, BorderLayout.EAST);
		
		if(event != null){
			messagePanel.add(button2, BorderLayout.EAST);
			button2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("reply");
					setMessageToReply(event);
				}
			});
		}
		
        // Right panel with message label
        JPanel messageContentPanel = new JPanel(new BorderLayout());
        messagePanel.add(messageContentPanel, BorderLayout.CENTER);

        JTextPane messageLabel = new JTextPane();
        messageLabel.setEditable(false);
		messageLabel.setFont(MESSAGE_FONT);
        messageLabel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		messageLabel.addMouseListener(replyButtonMouseAdapter(button2));
		formatText(message, messageLabel.getStyledDocument(), Color.WHITE);
        messageContentPanel.add(messageLabel, BorderLayout.CENTER);
        
        messagesPerDay++;
        messagesPerMin.recordCallTime();
        chatPanel.add(messagePanel);
        chatPanel.revalidate();
        chatPanel.repaint();
        setNessageInfoText();
        
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
    	int maxValue = verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount();
    	int currentValue = verticalScrollBar.getValue();
    	
    	if (currentValue == maxValue) {
    		SwingUtilities.invokeLater(() -> {
    			Rectangle bounds = messagePanel.getBounds();
    			scrollPane.getViewport().scrollRectToVisible(bounds);
    		});
    	}
    	
    	if(chatterNames.contains(userName.toLowerCase())){
	    	Settings.highlightStrings.forEach(string -> {
	    		Pattern pattern = Pattern.compile("\\b" + string + "\\b", Pattern.CASE_INSENSITIVE);
	            Matcher matcher = pattern.matcher(message);
	            
	            if (matcher.find()) {
	    			titledBorder.setBorder(BorderFactory.createLineBorder(ColorManager.CHAT_MESSAGE_KEY_HIGHLIGHT, 2));
	            }
	    	});
    	}else{
    		chatterNames.add(userName.toLowerCase());
			titledBorder.setBorder(BorderFactory.createLineBorder(ColorManager.CHAT_MESSAGE_GREETING_HIGHLIGHT, 2));
    	}
    	
    	if(!chatterNames.contains(userName.toLowerCase()+"%-&-%")){
	    	JButton waveButton = new MineButton(buttonSize, null, ButtonType.NON);//.setInvisible(!MainFrame.debug);
			waveButton.setPreferredSize(buttonSize);
			waveButton.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
			waveButton.setBorderPainted(false);
			waveButton.setIcon(Main.TEXTURE_MANAGER.getWaveButton());
			waveButton.setToolTipText("Wellcome this user!");
			waveButton.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e){
					currentlyWritingString = inputField.getText();
					inputField.setText(parrentTab.getGreetingTexts().get(0));
					setMessageToReply(event);
				}
			});
			
			buttonPanel.add(waveButton, BorderLayout.WEST);
			buttonPanel.setMinimumSize(new Dimension(52, 26));
    	}
    	
    	
    	replyButtonMouseAdapter(button2);
    }
    
    public void displaySystemInfo(String topic, String message, Color borderColor){
    	JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setMinimumSize(new Dimension(400, 25));
        messagePanel.setBackground(ColorManager.GUI_BACKGROUND);

        JTextPane textLabel = new JTextPane();
        textLabel.setEditable(false);
    	textLabel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
    	textLabel.setFont(MESSAGE_FONT);
    	formatText(message, textLabel.getStyledDocument(), new Color(30, 30, 30));
    	
		JPanel messageContentPanel = new JPanel(new BorderLayout());
	    messageContentPanel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
	    messageContentPanel.add(textLabel, BorderLayout.CENTER);
	    messagePanel.add(messageContentPanel, BorderLayout.CENTER);
        
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(borderColor, 2, true), "  "+topic+"  ");
		titledBorder.setTitleJustification(TitledBorder.CENTER);
		titledBorder.setTitleColor(borderColor); //Color.CYAN
		titledBorder.setTitleFont(new Font(null, Font.BOLD, 20));
		messagePanel.setBorder(titledBorder);
		
		JButton button1 = new MineButton(new Dimension(28, 28), null, ButtonType.NON);//.setInvisible(!MainFrame.debug);
		button1.setPreferredSize(button1.getSize());
		button1.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		button1.setBorderPainted(false);
		button1.setIcon(Main.TEXTURE_MANAGER.getMarkReadButton());
		button1.setToolTipText("Mark this message as read.");
		button1.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e){chatPanel.remove(messagePanel); chatPanel.revalidate(); chatPanel.repaint();}
		});

		messagePanel.add(button1, BorderLayout.WEST);
        chatPanel.add(messagePanel);
        chatPanel.revalidate();
        chatPanel.repaint();
        
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
    	int maxValue = verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount();
    	int currentValue = verticalScrollBar.getValue();
    	
    	if (currentValue == maxValue) {
    		SwingUtilities.invokeLater(() -> {
    			Rectangle bounds = messagePanel.getBounds();
    			scrollPane.getViewport().scrollRectToVisible(bounds);
    		});
    	}
    }
    
    public String getMessageInfoText(){
    	return "Messages: "+messagesPerDay+" | PerMin: "+messagesPerMin.getCallCount();
    }
    
    public void setMessageToReply(AbstractChannelMessageEvent messageEvent) {
		this.messageEvent = messageEvent;
		setNessageInfoText();
	}


	private void setNessageInfoText() {
		inputInfo.setText((messageEvent == null) ? getMessageInfoText() : "     Reply: "+messageEvent.getUser().getName()+": "+messageEvent.getMessage());
		cancelReplyButton.setVisible((messageEvent == null) ? false : true);
	}


	private MouseAdapter replyButtonMouseAdapter(JButton button) {
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
    

    
    private void formatText(String input, StyledDocument document, Color fontColor) {
    	String newInput="";
    	if(fontColor == Color.WHITE){
    		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin")); //Set the default time zone.
    		newInput += "["+LocalTime.now().format(DateTimeFormatter.ofPattern(Settings.timeFormat, Locale.GERMAN))+"] ";
    	}
        
        for(String string : splitString(input)){newInput += string.trim()+" \n ";}
        input = (newInput.contains("\n") ? newInput.substring(0, newInput.lastIndexOf("\n")).trim() : newInput.trim());

		for (String word : input.split(" ")) {
			SimpleAttributeSet attributeSet = new SimpleAttributeSet();
			StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
			StyleConstants.setForeground(attributeSet, fontColor);
			boolean isEmote = false;

			HashMap<String, TwitchEmote> emotesByName = TwitchEmote.getEmotesByName();
			
			Settings.highlightStrings.forEach(string -> {
	    		Pattern pattern = Pattern.compile("\\b" + string + "\\b", Pattern.CASE_INSENSITIVE);
	            Matcher matcher = pattern.matcher(word);
	            if (matcher.find()) {
	    			StyleConstants.setForeground(attributeSet, new Color(255, 40, 40));
	    		}
	    	});
			
			if (emotesByName.get(word) != null) {
				StyleConstants.setIcon(attributeSet, emotesByName.get(word).getImageIcon());
				isEmote = true;
			}

			try {
				document.insertString(document.getLength(), word + " ", attributeSet);

				if (isEmote) {
					document.insertString(document.getLength(), " ", null);
				}
			} catch (BadLocationException ex) {
				logger.error("Can´t modify styledDocument! ", ex);
			}
		}
    }
    
    
    //TODO Replace emotes with 3 Chars for filtering.
    public static List<String> splitString(String input) {
    	input = input.replace("\\n", "�");
        int chunkSize = 45;
        List<String> chunks = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        input = encryptEmotes(input);

        int wordBoundary = -1; // Index of the last space character within the chunk limit
        System.out.println(input);
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
                	System.out.println("Split!");
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
        
        chunks.forEach(s -> System.out.println(s));
        return chunks;
    }
    
    public static String encryptEmotes(String input) {
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

    public static String decryptEmotes(String input) {
        for (Entry<String, String> entry : emoteReplacements.entrySet()) {
            input = input.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
        }
        return input;
    }


    private static String generateReplacement(String input) {
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
    
    private MineButton createNewButton(ImageIcon icon, String toolTip, Dimension dimension, Color backgroundColor){
    	MineButton button = new MineButton(dimension, null, ButtonType.NON);
    	button.setIcon(icon);
    	button.setColors(Color.WHITE, backgroundColor);
    	button.setPreferredSize(button.getSize());
    	button.setBorderPainted(false);
    	button.setToolTipText(toolTip);
    	return button;
    }
    

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

}
