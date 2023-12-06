package de.minetrain.minechat.gui.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.config.Settings;
import de.minetrain.minechat.config.enums.ReplyType;
import de.minetrain.minechat.data.databases.OwnerCacheDatabase.OwnerCacheData;
import de.minetrain.minechat.gui.obj.ChannelTab;
import de.minetrain.minechat.gui.obj.ChatStatusPanel;
import de.minetrain.minechat.gui.obj.ChatWindowMessageComponent;
import de.minetrain.minechat.gui.obj.buttons.MineButton;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.twitch.MessageManager;
import de.minetrain.minechat.twitch.TwitchManager;
import de.minetrain.minechat.twitch.obj.GreetingsManager;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.CallCounter;

public class ChatWindow extends JLabel {
	private static final long serialVersionUID = -8392586696866883591L;
	private static final Logger logger = LoggerFactory.getLogger(ChatWindow.class);
	private Dimension preferredScrollBarSize = new JScrollBar().getPreferredSize();
	public static final Font MESSAGE_FONT = new Font("SansSerif", Font.BOLD, 17);
    private static final int MAX_MESSAGE_SICE = 500;
//	public  List<String> chatterNames = new ArrayList<String>();
	public final GreetingsManager greetingsManager;
//	public final HashMap<String, List<String>> badges = new HashMap<String, List<String>>();
	public CallCounter messagesPerMin = new CallCounter(60);
	public Integer messagesPerDay = 0;
	public TwitchMessage replyMessage = null;
    public JPanel chatPanel;
    private JScrollPane scrollPane;
    public final ChannelTab parentTab;
    public ChatStatusPanel chatStatusPanel;
    public final String channelId;
    
    public ChatWindow(ChannelTab parentTab) {
    	this.parentTab = parentTab;
    	this.channelId = parentTab.getConfigID();
    	greetingsManager = new GreetingsManager(parentTab);
    	greetingsManager.setMentioned(TwitchManager.ownerChannelName);
		
        setSize(485, 504);
        setLayout(new BorderLayout());
        setBackground(ColorManager.GUI_BACKGROUND);
		
        // Chat Panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(chatPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel placeholder = new JPanel();
		placeholder.setBackground(ColorManager.GUI_BACKGROUND);
		placeholder.setBorder(BorderFactory.createEmptyBorder(205, 200, 205, 200));
//		placeholder.setMinimumSize(new Dimension(480, 500));
		chatPanel.add(placeholder);
		chatPanel.revalidate();
        chatPanel.repaint();
        
        chatStatusPanel = new ChatStatusPanel(this);
        add(chatStatusPanel, BorderLayout.SOUTH);
        
        scrollPane.setBorder(null);
//		scrollPane.setBorder(BorderFactory.createLineBorder(ColorManager.BACKGROUND_LIGHT, 2));
		scrollPane.setBackground(ColorManager.GUI_BACKGROUND);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBackground(ColorManager.GUI_BORDER);
        scrollPane.getVerticalScrollBar().setForeground(ColorManager.GUI_BUTTON_BACKGROUND);
        scrollPane.getVerticalScrollBar().setBorder(null);
        
        chatStatusPanel.getSendButton().addActionListener(new ActionListener() {
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
            	
            	sendMessage();
            }
        });
    }
    
    public void sendMessage() {
		String message = chatStatusPanel.getCurrentUserInput();
    	System.out.println("Message --> ["+message+"]");
    	if(!message.isEmpty()){
    		MessageManager.sendMessage(message);
    		chatStatusPanel.setLockedState(false);
    		chatStatusPanel.loadCurrentlyCachedInput();
    		setMessageToReply(null);
    	}
	}
    
    public void displayMessage(TwitchMessage message) {
    	displayMessage(message.getMessage(), message.getUserName(), Color.decode(message.getUserColorCode()), message);
    }
    
	public void displayMessage(String message, String userName, Color userColor) {
    	displayMessage(message, userName, userColor, null, null);
    }

	public void displayMessage(String message, String userName, Color userColor, TwitchMessage twitchMessage) {
		displayMessage(message, userName, userColor, twitchMessage, null);
	}

//	private List<ChatWindowMessageComponent> list = new ArrayList<ChatWindowMessageComponent>();
	public void displayMessage(String message, String userName, Color userColor, TwitchMessage twitchMessage, OwnerCacheData badgeData) {
		ChatWindowMessageComponent messagePanel = new ChatWindowMessageComponent(message, userName, userColor, twitchMessage, this, badgeData);
//		int minimisedPanelHight = 0;
		
        chatPanel.add(messagePanel);
        if(messagePanel.getHighlightString() != null){
        	messagePanel.getHighlightString().playSound();
        }
//		list.add(messagePanel);
		
		if(chatPanel.getComponentCount() > Settings.MAX_MESSAGE_DISPLAYING){
			if(chatPanel.getComponent(0) instanceof ChatWindowMessageComponent){
				ChatWindowMessageComponent component = (ChatWindowMessageComponent) chatPanel.getComponent(0);
				if(!component.isHighlighted()){
					chatPanel.remove(0);
				}
			}
		}
		
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
        chatStatusPanel.setDefault(false);
        
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
    
    public void setMessageToReply(TwitchMessage twitchMessage) {
    	if(twitchMessage != null && twitchMessage.getReplyType() != ReplyType.USER_NAME){
    		this.replyMessage = twitchMessage;
    	}else{
    		this.replyMessage = null;
    	}

    	//Fixed that the Greeting button, overrides the reply type, for the particular message.
    	if(twitchMessage != null){
    		twitchMessage.setReplyType(Settings.REPLY_TYPE);
    	}
    	
		chatStatusPanel.setReply(twitchMessage);
	}


	public JScrollPane getScrollPane() {
		return scrollPane;
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		this.revalidate();
		this.repaint();
	}

}
