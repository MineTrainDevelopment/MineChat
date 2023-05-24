package de.minetrain.minechat.gui.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.util.Optional;
import java.util.Random;
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
import de.minetrain.minechat.gui.obj.ChatWindowMessageComponent;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.obj.buttons.MineButton;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.gui.utils.TextureManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.MessageManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.GreetingsManager;
import de.minetrain.minechat.utils.CallCounter;
import de.minetrain.minechat.utils.Settings;

public class ChatWindow extends JLabel {
	private static final long serialVersionUID = -8392586696866883591L;
	private static final Logger logger = LoggerFactory.getLogger(ChatWindow.class);
	private Dimension preferredScrollBarSize = new JScrollBar().getPreferredSize();
	public static final Font MESSAGE_FONT = new Font("SansSerif", Font.BOLD, 17);
	private static Map<String, String> emoteReplacements = new HashMap<>();
    private static final int MAX_MESSAGE_SICE = 500;
//	public  List<String> chatterNames = new ArrayList<String>();
	public final GreetingsManager greetingsManager;
	public final HashMap<String, List<String>> badges = new HashMap<String, List<String>>();
	public AbstractChannelMessageEvent messageEvent = null;
	private CallCounter messagesPerMin = new CallCounter();
	private MineButton sendButton;
	public MineButton cancelReplyButton;
	public String currentlyWritingString = "";
	private Integer messagesPerDay = 0;
    public JPanel chatPanel;
    public JTextField inputField;
    private JScrollPane scrollPane;
    private JLabel inputInfo;
    public final ChannelTab parentTab;
    
//    public JPanel simpleMessagePanel = new JPanel();
    
    public ChatWindow(ChannelTab parentTab) {
    	this.parentTab = parentTab;
    	greetingsManager = new GreetingsManager(parentTab);
    	greetingsManager.setMentioned(TwitchManager.ownerChannelName);
		
        setSize(485, 504);
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
        
//        simpleMessagePanel.setLayout(new BoxLayout(simpleMessagePanel, BoxLayout.Y_AXIS));
//        chatPanel.add(simpleMessagePanel);

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

//	private List<ChatWindowMessageComponent> list = new ArrayList<ChatWindowMessageComponent>();
	public void displayMessage(String message, String userName, Color userColor, AbstractChannelMessageEvent event) {
		ChatWindowMessageComponent messagePanel = new ChatWindowMessageComponent(message, userName, userColor, event, this);
//		int minimisedPanelHight = 0;
		
        chatPanel.add(messagePanel);
//		list.add(messagePanel);
		
//		if(list.size()==MAX_MESSAGE_OBJ){
//			JPanel minimised = list.remove(0).getMinimised();
//			minimisedPanelHight = minimised.getHeight();
//			simpleMessagePanel.add(minimised);
//			chatPanel.remove((chatPanel.getComponentCount()+1) - MAX_MESSAGE_SICE);
//		}
        
        if(chatPanel.getComponentCount() >= MAX_MESSAGE_SICE) {
        	ChatWindowMessageComponent component = (ChatWindowMessageComponent) chatPanel.getComponent(chatPanel.getComponentCount() - (MAX_MESSAGE_SICE-1));
        	if(!component.isHighlighted()){
        		chatPanel.remove(component);
        	}
        }
    	
        messagesPerDay++;
        messagesPerMin.recordCallTime();
        chatPanel.revalidate();
        chatPanel.repaint();
        setNessageInfoText();
        
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
    	int maxValue = verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount();
    	int currentValue = verticalScrollBar.getValue();
//    	int newMinimisedPanelHight = minimisedPanelHight;

        // Update the simpleMessagePanel height here

        scrollPane.revalidate();
        scrollPane.repaint();
    	
    	SwingUtilities.invokeLater(() -> {
//            verticalScrollBar.setValue(currentValue-newMinimisedPanelHight);
            
	    	if (currentValue >= maxValue-200) {
	    			Rectangle bounds = messagePanel.getBounds();
//	    	        bounds.setLocation(bounds.width, bounds.height + simpleMessagePanel.getHeight());
//	    	        System.out.println(messagePanel.getHeight()+" -- "+bounds.getHeight() +" -- "+(simpleMessagePanel.getHeight()));
	    			scrollPane.getViewport().scrollRectToVisible(bounds);
	    			scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
	    	}else{
				scrollPane.getVerticalScrollBar().setPreferredSize(preferredScrollBarSize);
	    	}
    	});
    }
    
    public void displaySystemInfo(String topic, String message, Color borderColor, MineButton button){
		ChatWindowMessageComponent messagePanel = new ChatWindowMessageComponent(topic, message, borderColor, button, this);
		
        chatPanel.add(messagePanel);
        chatPanel.revalidate();
        chatPanel.repaint();
        
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
    	int maxValue = verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount();
    	int currentValue = verticalScrollBar.getValue();

    	if (currentValue >= maxValue-200) {
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
