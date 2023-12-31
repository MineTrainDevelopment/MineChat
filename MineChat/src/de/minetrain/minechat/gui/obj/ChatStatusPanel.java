package de.minetrain.minechat.gui.obj;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.github.twitch4j.client.websocket.domain.WebsocketConnectionState;

import de.minetrain.minechat.gui.frames.ChatWindow;
import de.minetrain.minechat.gui.frames.EmoteSelector;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.gui.obj.buttons.MineButton;
import de.minetrain.minechat.gui.obj.chat.userinput.textarea.MineTextArea;
import de.minetrain.minechat.gui.utils.ColorManager;
import de.minetrain.minechat.main.Main;
import de.minetrain.minechat.twitch.obj.TwitchMessage;
import de.minetrain.minechat.utils.HTMLColors;
import de.minetrain.minechat.utils.MessageHistory;
import de.minetrain.minechat.utils.MineStringBuilder;

public class ChatStatusPanel extends JPanel{
	private static final long serialVersionUID = -1247943509194239246L;
	public static final Font MESSAGE_FONT = new Font("SansSerif", Font.BOLD, 17);
	private static final String lineSeparator =  System.getProperty("line.separator");
	private MineStringBuilder stringBuilder = new MineStringBuilder();
	private MessageHistory messageHistory = new MessageHistory();
	private String currentlyCachedInput = "";
	private boolean lockedState = false;
	private boolean forceLockedState = false;
	private final ChatWindow chat;
	
    private JLabel inputInfo;
	private MineButton sendButton;
	private MineButton cancelReplyButton;
	private MineTextArea inputArea;
	
	public ChatStatusPanel(ChatWindow chat) {
		super(new BorderLayout());
		this.chat = chat;
		

		JPanel infoHoldingLabel = new JPanel(new BorderLayout());
		infoHoldingLabel.setBackground(ColorManager.GUI_BACKGROUND);
        infoHoldingLabel.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 3));
		
		inputInfo = new JLabel(getMineChatStatusText().toString());
        inputInfo.setBackground(ColorManager.GUI_BACKGROUND);
        inputInfo.setForeground(Color.WHITE);
        inputInfo.setLayout(new BorderLayout());
        inputInfo.setHorizontalAlignment(SwingConstants.CENTER);
        inputInfo.setFont(new Font(null, Font.BOLD, 20));
        
        cancelReplyButton = createNewButton(Main.TEXTURE_MANAGER.getCancelButton(), "Cancel reply", new Dimension(26, 26), ColorManager.GUI_BACKGROUND);
        cancelReplyButton.setVisible(false);
        cancelReplyButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e){
//				if(!currentlyCachedInput.isEmpty()){
					loadCurrentlyCachedInput();
//				}
				
				setLockedState(false);
				chat.setMessageToReply(null);
			}
		});
//		inputInfo.add(cancelReplyButton, BorderLayout.WEST);
		infoHoldingLabel.add(cancelReplyButton, BorderLayout.WEST);
		infoHoldingLabel.add(inputInfo, BorderLayout.CENTER);

//		inputArea = new AutoSuggestor(chat.parentTab.getMainFrame(), null, Color.WHITE, Color.BLUE, Color.RED, 0.75f, "@", ":");
		inputArea = new MineTextArea(chat, null, ColorManager.GUI_BACKGROUND_LIGHT, Color.WHITE, Color.RED, 1f, "@", ":", "{");
        inputArea.setFont(MESSAGE_FONT);
        inputArea.setForeground(ColorManager.FONT);
        inputArea.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 1));
        
        inputArea.addKeyListener(new KeyAdapter() {
			@Override
            public void keyPressed(KeyEvent event) {
				if(inputArea.isFocusOwner()){
					if (!(event.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK && event.getKeyCode() == KeyEvent.VK_ENTER) && !inputArea.getAutoSuggestionPopUpWindow().isVisible()) {
						switch(event.getKeyCode()){
						case KeyEvent.VK_UP:
							overrideUserInput(messageHistory.getNextItem(getCurrentUserInput()));
							inputArea.setForeground(messageHistory.isNewText() ? ColorManager.FONT : Color.CYAN);
							break;
							
						case KeyEvent.VK_DOWN:
							overrideUserInput(messageHistory.getPreviousItem(getCurrentUserInput()));
							inputArea.setForeground(messageHistory.isNewText() ? ColorManager.FONT : Color.CYAN);
							break;
							
						case KeyEvent.VK_ENTER:
							inputArea.setForeground(ColorManager.FONT);
							chat.sendMessage();
							event.consume();
							break;
							
						case KeyEvent.VK_TAB:
							event.consume();
							break;
						
						default:
							messageHistory.resetIndex();
							break;
						}
						
						if(event.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && event.getKeyCode() == KeyEvent.VK_Z){
							inputArea.getUndoManager().undo();
						}
//						
						if(event.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && event.getKeyCode() == KeyEvent.VK_Y){
							inputArea.getUndoManager().redo();
						}
					}
				}
			}
        });
        
        sendButton = createNewButton(Main.TEXTURE_MANAGER.getEnterButton(), "Send message", new Dimension(62, 28), ColorManager.GUI_BACKGROUND_LIGHT);
        MineButton emoteButton = createNewButton(Main.TEXTURE_MANAGER.getEmoteButton(), "Send message", new Dimension(28, 28), ColorManager.GUI_BACKGROUND_LIGHT);
        emoteButton.addActionListener(e -> {
			EmoteSelector emoteSelector = new EmoteSelector(Main.MAIN_FRAME, true);
			emoteSelector.addSelectetEmoteToText(inputArea);
		});
		
		JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(ColorManager.GUI_BACKGROUND_LIGHT);
		buttonPanel.add(emoteButton, BorderLayout.WEST);
        buttonPanel.add(getSendButton(), BorderLayout.EAST);
        buttonPanel.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 1));
        
        add(infoHoldingLabel, BorderLayout.NORTH);
        add(inputArea, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
        setBackground(ColorManager.GUI_BACKGROUND);
		setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 4, true));
		
		
		
		
		
		
	}

	private final String getDefaultText(){
		return "Messages: "+chat.messagesPerDay+" | PerMin: "+chat.messagesPerMin.getCallCount();
	}
	
	private static final MineButton createNewButton(ImageIcon icon, String toolTip, Dimension dimension, Color backgroundColor){
    	MineButton button = new MineButton(dimension, null, ButtonType.NON);
    	button.setIcon(icon);
    	button.setColors(Color.WHITE, backgroundColor);
    	button.setPreferredSize(button.getSize());
    	button.setBorderPainted(false);
    	button.setToolTipText(toolTip);
    	return button;
    }
    
    public static final MineStringBuilder getMineChatStatusText() {
		MineStringBuilder stringBuilder = new MineStringBuilder();
		stringBuilder.appendString("Welcome to ", HTMLColors.WHITE);
		stringBuilder.appendString("MineChat ", HTMLColors.AQUA);
		stringBuilder.appendString("<3", HTMLColors.RED);
		stringBuilder.appendSpace();
		stringBuilder.appendString("  -  Version: "+Main.VERSION, HTMLColors.GRAY);
		return stringBuilder;
	}


    public ChatStatusPanel setReply(TwitchMessage twitchMessage){
//    	System.out.println("reply -> "+(event != null ? event.getMessage() : "null"));
    	if(forceLockedState){return this;}
    	
    	if(twitchMessage == null){
    		setDefault(true);
    		return this;
    	}
    	
    	String message = twitchMessage.getMessage();
    	cancelReplyButton.setVisible(true);
    	stringBuilder.appendSpace();
    	
    	if(twitchMessage.isParentReply()){
        	stringBuilder.appendString("Reply to thrad from: ", HTMLColors.AQUA);
        	stringBuilder.appendString(twitchMessage.getParentReplyUser(), twitchMessage.getParentReplyColor());
        	stringBuilder.appendString(":", HTMLColors.WHITE);
        	stringBuilder.appendLineSplit();
        	message = message.substring(twitchMessage.getParentReplyUser().length()+1);
    	}else{
        	stringBuilder.appendString("Reply: ", HTMLColors.AQUA);
    	}
    	
    	stringBuilder.appendString(twitchMessage.getUserName(), twitchMessage.getUserColorCode());
    	stringBuilder.appendString(" --> ", HTMLColors.WHITE);
    	stringBuilder.appendString(message);
    	
        inputInfo.setHorizontalAlignment(SwingConstants.LEFT);
        setText(stringBuilder.toString(), true, (twitchMessage.isParentReply()) ? 265 : 200);
		stringBuilder.clear();
    	return this;
	}

	public ChatStatusPanel setDefault(boolean force) {
		if((force && !forceLockedState) || !isLocked()){
			setLockedState(false);
			setText(getDefaultText(), false);
			cancelReplyButton.setVisible(false);
			inputInfo.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return this;
	}
    
    private ChatStatusPanel setText(String message, boolean locked){
    	return setText(message, locked, 200);
	}
    
    private ChatStatusPanel setText(String message, boolean locked, int limit){
//    	if(!isLocked()){
    		setLockedState(locked);
    		this.inputInfo.setText((message.length()>limit) ? message.substring(0, (limit-1))+"..." : message);
//    	}
    	return this;
	}
    
    public void setDownloadStatus(String type, String name, boolean locked){
		inputInfo.setHorizontalAlignment(SwingConstants.LEFT);
		stringBuilder.appendString("Downloading", HTMLColors.CYAN);
		stringBuilder.appendString(" a ", HTMLColors.WHITE);
		stringBuilder.appendString(type+":", HTMLColors.AQUA);
		stringBuilder.appendLineSplit();
		stringBuilder.appendString(name, HTMLColors.WHITE);
    	setText(stringBuilder.toString(), locked);
    	stringBuilder.clear();
    }
    
	public void setConectionStatus(WebsocketConnectionState stat){
    	switch (stat) {
		case LOST:
			stringBuilder.appendString("Lost connection to Twitch-IRC!", HTMLColors.RED);
			forceLockedState = true;
			break;
			
		case CONNECTING:
			stringBuilder.appendString("Connecting to Twitch-IRC...", HTMLColors.YELLOW);
			forceLockedState = true;
			break;
			
		case CONNECTED:
//			chatStatusPanel.appendString("Connection to Twitch-IRC established!", HTMLColors.GREEN);
			stringBuilder = ChatStatusPanel.getMineChatStatusText();
			forceLockedState = false;
			break;
			
		case DISCONNECTING:
			stringBuilder.appendString("Disconnecting from Twitch-IRC...", HTMLColors.YELLOW);
			forceLockedState = true;
			break;
			
		case DISCONNECTED:
			stringBuilder.appendString("Disconnected from Twitch successfully!", HTMLColors.TEAL);
			forceLockedState = true;
			break;
			
		case RECONNECTING:
			stringBuilder.appendString("Reconnecting to Twitch-IRC...", HTMLColors.YELLOW);
			forceLockedState = true;
			break;

		default:
			stringBuilder.appendString("Unknown connection state...", HTMLColors.YELLOW);
			forceLockedState = false;
			break;
		}
		
		this.inputInfo.setText(stringBuilder.toString());
		stringBuilder.clear();
	}
	
	public boolean isLocked() {
		return (forceLockedState) ? true : lockedState;
	}

	public void setLockedState(boolean lockedState) {
		this.lockedState = lockedState;
	}
	
	public MineButton getSendButton() {
		return sendButton;
	}

	public void overrideUserInput(String text) {
		inputArea.setText(text);
	}
	
	public String getCurrentUserInput() {
		return inputArea.getText();
	}
	
	public String loadCurrentlyCachedInput() {
		inputArea.setText(currentlyCachedInput);
		currentlyCachedInput = "";
		return getCurrentUserInput();
	}

	/**
	 * @param force
	 */
	public void overrideCurrentInputCache(boolean force) {
		if(force || currentlyCachedInput.isEmpty()){
			this.currentlyCachedInput = getCurrentUserInput();
		}
	}

	public static String getLineseparator() {
		return lineSeparator;
	}

	public MessageHistory getMessageHistory() {
		return messageHistory;
	}
	
	public MineTextArea getinputArea(){
		return inputArea;
	}
	
	public void requestFocus(){
		inputArea.requestFocus();
	}

}
