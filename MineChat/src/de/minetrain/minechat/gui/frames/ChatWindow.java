package de.minetrain.minechat.gui.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.twitch4j.chat.events.AbstractChannelMessageEvent;

import de.minetrain.minechat.config.obj.TwitchEmote;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.obj.buttons.MineButton;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.MessageManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.utils.Settings;

public class ChatWindow extends JLabel {
	private static final Logger logger = LoggerFactory.getLogger(ChatWindow.class);
	private static final long serialVersionUID = -8392586696866883591L;
	private static Map<String, String> emoteReplacements = new HashMap<>();
	public AbstractChannelMessageEvent messageEvent = null;
    private JPanel chatPanel;
    private JTextField inputField;
    private JScrollPane scrollPane;
    private JButton sendButton;
    private JLabel inputInfo;

    public ChatWindow() {
        setSize(482, 504);
        setLayout(new BorderLayout());
        setBackground(ColorManager.BACKGROUND);

//        inputInfo = new JLabel("Reply: @Kuchenhopper: Guten Morgen eguyLurk sinticaHuhu sinticaLove");
        inputInfo = new JLabel("Messages: 12645 | PerMin: 115");
        inputInfo.setForeground(Color.WHITE);
        inputInfo.setHorizontalAlignment(CENTER);
        inputInfo.setFont(new Font(null, Font.BOLD, 20));
//        add(stats, BorderLayout.NORTH);
        
        // Chat Panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(chatPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Input Field and Send Button
        inputField = new JTextField();
        sendButton = new JButton("Send");
        JPanel inputPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(new JButton("Emotes"), BorderLayout.WEST);
        buttonPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(inputInfo, BorderLayout.NORTH);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        inputPanel.setBackground(ColorManager.BACKGROUND);
		inputPanel.setBorder(BorderFactory.createLineBorder(ColorManager.BORDER, 4, true));
        add(inputPanel, BorderLayout.SOUTH);
        
        scrollPane.setBorder(null);
//		scrollPane.setBorder(BorderFactory.createLineBorder(ColorManager.BACKGROUND_LIGHT, 2));
		scrollPane.setBackground(ColorManager.BACKGROUND);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBackground(ColorManager.BORDER);
        scrollPane.getVerticalScrollBar().setForeground(ColorManager.BUTTON_BACKGROUND);
        scrollPane.getVerticalScrollBar().setBorder(null);
        
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String message = inputField.getText();
            	if(message.isEmpty()){return;}
                inputField.setText("");
                
                
                if(messageEvent != null){
                	displayMessage("@"+messageEvent.getUser().getName()+" "+message, "", Color.WHITE);
                	setMessageToReply(null);
                }else{
                	displayMessage(message, "", Color.WHITE);
                }
                
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
        messagePanel.setBackground(ColorManager.BACKGROUND);
        
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ColorManager.BACKGROUND_LIGHT, 2, true), userName+":");
		titledBorder.setTitleJustification(TitledBorder.LEFT);
		titledBorder.setTitleColor(userColor);
		titledBorder.setTitleFont(new Font(null, Font.BOLD, 20));
		messagePanel.setBorder(titledBorder);
        
		Dimension buttonSize = new Dimension(28, 28);
		JButton button1 = new MineButton(buttonSize, null, ButtonType.NON);//.setInvisible(!MainFrame.debug);
		button1.setPreferredSize(buttonSize);
		button1.setBackground(ColorManager.BACKGROUND_LIGHT);
		button1.setBorderPainted(false);
		button1.setIcon(Main.TEXTURE_MANAGER.getMarkReadButton());
		button1.setToolTipText("Mark this message as read.");
		button1.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e){chatPanel.remove(messagePanel); chatPanel.revalidate(); chatPanel.repaint();}
		});

		JButton button2 = new MineButton(buttonSize, null, ButtonType.NON);//.setInvisible(!MainFrame.debug);
		button2.setPreferredSize(buttonSize);
		button2.setBackground(ColorManager.BACKGROUND_LIGHT);
		button2.setBorderPainted(false);
		button2.setToolTipText("Replay to this message.");
		button2.setIcon(Main.TEXTURE_MANAGER.getReplyButton());
		button2.addMouseListener(replyButtonMouseAdapter(button2));
		button2.setVisible(false);

		messagePanel.add(button1, BorderLayout.WEST);
		if(event != null){
			messagePanel.add(button2, BorderLayout.EAST);
			button2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("reply");
					setMessageToReply(event);
					//When the reply button was pressed, the message input should recolor itself and the send button should go into reply mode.
				}
			});
		}
		
        // Right panel with message label
        JPanel messageContentPanel = new JPanel(new BorderLayout());
        messagePanel.add(messageContentPanel, BorderLayout.CENTER);

        JTextPane messageLabel = new JTextPane();
        messageLabel.setEditable(false);
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        messageLabel.setBackground(ColorManager.BACKGROUND_LIGHT);
		messageLabel.addMouseListener(replyButtonMouseAdapter(button2));
        setEmoteText(message, messageLabel.getStyledDocument());
        System.out.println("---- "+messageLabel.getText()+" ----");
        messageContentPanel.add(messageLabel, BorderLayout.CENTER);
        
        chatPanel.add(messagePanel);
        chatPanel.revalidate();
        chatPanel.repaint();
        
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
    	int maxValue = verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount();
    	int currentValue = verticalScrollBar.getValue();
    	
    	if (currentValue == maxValue) {
    		System.out.println("scroll");
    		SwingUtilities.invokeLater(() -> {
    			Rectangle bounds = messagePanel.getBounds();
    			scrollPane.getViewport().scrollRectToVisible(bounds);
    		});
    	}
    	
    	Settings.highlightStrings.forEach(string -> {
    		Pattern pattern = Pattern.compile("\\b" + string + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(message);
            
            if (matcher.find()) {
    			System.out.println("change color");
    			titledBorder.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            }
    	});
    	
    	replyButtonMouseAdapter(button2);
    }
    
    public void setMessageToReply(AbstractChannelMessageEvent messageEvent) {
		this.messageEvent = messageEvent;
		inputInfo.setText((messageEvent == null) ? "Messages: 12645 | PerMin: 115" : "Reply: "+messageEvent.getUser().getName()+": "+messageEvent.getMessage());
		
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
                // Wenn die Maus das Panel verl‰sst, blende den Knopf aus
            	button.setVisible(false);
            }
        };
	}
    

    
    private void setEmoteText(String input, StyledDocument document) {
    	TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin")); //Set the default time zone.
    	String newInput = "["+LocalTime.now().format(DateTimeFormatter.ofPattern(Settings.timeFormat, Locale.GERMAN))+"] ";
        
        for(String string : splitString(input)){newInput += string.trim()+" \n ";}
        input = (newInput.contains("\n") ? newInput.substring(0, newInput.lastIndexOf("\n")).trim() : newInput.trim());

		for (String word : input.split(" ")) {
			SimpleAttributeSet attributeSet = new SimpleAttributeSet();
			StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
			StyleConstants.setForeground(attributeSet, Color.WHITE);
			boolean isEmote = false;

			HashMap<String, TwitchEmote> emotesByName = TwitchEmote.getEmotesByName();
			
			Settings.highlightStrings.forEach(string -> {
    			System.out.println(string +" - "+word);
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
				logger.error("Can¥t modify styledDocument! ", ex);
			}
		}
    }
    
    
    //TODO Replace emotes with 3 Chars for filtering.
    public static List<String> splitString(String input) {
        int chunkSize = 40;
        List<String> chunks = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        input = encryptEmotes(input);

        int wordBoundary = -1; // Index of the last space character within the chunk limit

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            builder.append(c);

            if (c == ' ') {
                wordBoundary = builder.length() - 1;
            }

            if (builder.length() == chunkSize) {
                if (wordBoundary != -1) {
                    chunks.add(builder.substring(0, wordBoundary));
                    builder.delete(0, wordBoundary + 1);
                    wordBoundary = -1;
                } else {
                    chunks.add(builder.toString());
                    builder.setLength(0);
                }
            }
        }

        // Add the remaining characters as the last chunk
        if (builder.length() > 0) {
            chunks.add(builder.toString());
        }
        
        for (int i=0; i<chunks.size(); i++) {
			chunks.set(i, decryptEmotes(chunks.get(i)));
		}
        
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
        	System.out.println(entry.getKey()+" - "+entry.getValue());
            input = input.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
        }
        return input;
    }


    private static String generateReplacement(String input) {
        // Generiere eine zuf‰llige Zeichenfolge mit drei Groﬂbuchstaben
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
    

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

}
