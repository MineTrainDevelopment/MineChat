package de.minetrain.minechat.gui.obj.chat.userinput.textarea;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.minetrain.minechat.gui.emotes.ChannelEmotes;
import de.minetrain.minechat.gui.emotes.EmoteManager;
import de.minetrain.minechat.gui.frames.ChatWindow;
import de.minetrain.minechat.gui.utils.ColorManager;

/**
 * NOTE: I stoll this code from <a href="https://stackoverflow.com/questions/14186955/create-a-autocompleting-textbox-in-java-with-a-dropdown-list">StackOverflow</a> 
 */
public class MineTextArea extends JTextArea {
	private static final long serialVersionUID = 8558626585350253751L;
	private final JFrame container;
	private final ChatWindow chat;
	private JPanel suggestionsPanel;
	private JWindow popUpWindow;
	private String typedWord;
	private static final ArrayList<SuggestionObj> staticDictionary = new ArrayList<>();
	private static final ArrayList<SuggestionObj> staticChannelDictionary = new ArrayList<>();
	private final ArrayList<SuggestionObj> dictionary = new ArrayList<>();
	private int tW, tH;
	private final List<String> prefixs;
	private final Color suggestionsTextColor;
	private final Color suggestionFocusedColor;
	private final UndoManager undoManager;
	
	private DocumentListener documentListener = new DocumentListener() {
		@Override
		public void insertUpdate(DocumentEvent event) {
			checkForAndShowSuggestions();
		}

		@Override
		public void removeUpdate(DocumentEvent event) {
			checkForAndShowSuggestions();
		}

		@Override
		public void changedUpdate(DocumentEvent event) {
			checkForAndShowSuggestions();
		}
	};

	public MineTextArea(ChatWindow chat, ArrayList<SuggestionObj> words, Color popUpBackground, Color textColor, Color suggestionFocusedColor, float opacity, String... prefixs) {
		this.container = chat.parentTab.getMainFrame();
		this.chat = chat;
		this.suggestionsTextColor = textColor;
		this.suggestionFocusedColor = suggestionFocusedColor;
		getDocument().addDocumentListener(documentListener);
		this.prefixs = Arrays.asList(prefixs);
		this.undoManager = new UndoManager(this);
		
		setDictionary(words);

		typedWord = "";
		tW = 0;
		tH = 0;

		popUpWindow = new JWindow(container);
		popUpWindow.setOpacity(opacity);

		suggestionsPanel = new JPanel();
		suggestionsPanel.setLayout(new GridLayout(0, 1));
		suggestionsPanel.setBackground(popUpBackground);
		suggestionsPanel.setBorder(BorderFactory.createLineBorder(ColorManager.GUI_BORDER, 5));

		addKeyBindingToRequestFocusInPopUpWindow();
	}

	private void addKeyBindingToRequestFocusInPopUpWindow() {
		setFocusTraversalKeysEnabled(false);
		addKeyListener(new KeyAdapter() {
	      @Override
			public void keyPressed(KeyEvent event) {
				if (isFocusOwner() && getAutoSuggestionPopUpWindow().isVisible()) {
					switch (event.getKeyCode()) {
					case KeyEvent.VK_DOWN:
						if (popUpWindow.isVisible()) {
							for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
								if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
									((SuggestionLabel) suggestionsPanel.getComponent(i)).setFocused(true);
									popUpWindow.toFront();
									popUpWindow.requestFocusInWindow();
									suggestionsPanel.requestFocusInWindow();
									suggestionsPanel.getComponent(i).requestFocusInWindow();
									break;
								}
							}
						}
						break;

					case KeyEvent.VK_ENTER, KeyEvent.VK_TAB:
						if (suggestionsPanel.getComponents().length > 0 && popUpWindow.isVisible()) {
							SuggestionLabel component = (SuggestionLabel) suggestionsPanel.getComponent(0);
							component.select();
						}
						break;
						
					default:
						break;
					}
				}
				
				if(event.getKeyCode() == KeyEvent.VK_TAB){
					event.consume();
				}
			}
		});
		
		suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
		suggestionsPanel.getActionMap().put("Down released", new AbstractAction() {
			private static final long serialVersionUID = -2362595924406171787L;
			int lastFocusableIndex = 0;

			@Override
			public void actionPerformed(ActionEvent ae) {// allows scrolling of labels in pop window (I know very hacky for now :))

				ArrayList<SuggestionLabel> sls = getAddedSuggestionLabels();
				int max = sls.size();

				if (max > 1) {// more than 1 suggestion
					for (int i = 0; i < max; i++) {
						SuggestionLabel sl = sls.get(i);
						if (sl.isFocused()) {
							if (lastFocusableIndex == max - 1) {
								lastFocusableIndex = 0;
								sl.setFocused(false);
								popUpWindow.setVisible(false);
								setFocusToTextField();
								checkForAndShowSuggestions();// fire method as if document listener change occured and fired it

							} else {
								sl.setFocused(false);
								lastFocusableIndex = i;
							}
						} else if (lastFocusableIndex <= i) {
							if (i < max) {
								sl.setFocused(true);
								popUpWindow.toFront();
								popUpWindow.requestFocusInWindow();
								suggestionsPanel.requestFocusInWindow();
								suggestionsPanel.getComponent(i).requestFocusInWindow();
								lastFocusableIndex = i;
								break;
							}
						}
					}
				} else {// only a single suggestion was given
					popUpWindow.setVisible(false);
					setFocusToTextField();
					checkForAndShowSuggestions();// fire method as if document listener change occured and fired it
				}
			}
		});
	}

	private void setFocusToTextField() {
		container.toFront();
		container.requestFocusInWindow();
		requestFocusInWindow();
	}

	public ArrayList<SuggestionLabel> getAddedSuggestionLabels() {
		ArrayList<SuggestionLabel> sls = new ArrayList<>();
		for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
			if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
				SuggestionLabel suggestionLabel = (SuggestionLabel) suggestionsPanel.getComponent(i);
				sls.add(suggestionLabel);
			}
		}
		return sls;
	}

	private void checkForAndShowSuggestions() {
		typedWord = getCurrentlyTypedWord();

		suggestionsPanel.removeAll();// remove previos words/jlabels that were added

		// used to calcualte size of JWindow as new Jlabels are added
		tW = 0;
		tH = 0;

		boolean added = wordTyped(typedWord);

		if (!added) {
			popUpWindow.setVisible(false);
		} else {
			showPopUpWindow();
			setFocusToTextField();
		}
	}

	protected void addWordToSuggestions(SuggestionObj word) {
		if(suggestionsPanel.getComponents().length < 20){
			SuggestionLabel suggestionLabel = new SuggestionLabel(word, suggestionFocusedColor, suggestionsTextColor, this);
			calculatePopUpWindowSize(suggestionLabel);
			suggestionsPanel.add(suggestionLabel);
		}
	}

	public String getCurrentlyTypedWord() {
		String text = getText();
		return text.substring(text.contains(" ") ? text.lastIndexOf(" ") : 0).trim();
	}

	private void calculatePopUpWindowSize(JLabel label) {
		// so we can size the JWindow correctly
		if (tW < label.getPreferredSize().width) {
			tW = label.getPreferredSize().width;
		}
		tH += label.getPreferredSize().height;
	}

	private void showPopUpWindow() {
		popUpWindow.getContentPane().add(suggestionsPanel);
		popUpWindow.setMinimumSize(new Dimension(getWidth(), 30));
		popUpWindow.setSize(tW+10, tH+10); //Add 10 Pixels for border
		popUpWindow.setVisible(true);

		int windowX = 0;
		int windowY = 0;

		windowX = container.getX() + getX();
		if (suggestionsPanel.getHeight() > popUpWindow.getMinimumSize().height) {
			windowY = container.getY() + getY() + getHeight() + popUpWindow.getMinimumSize().height;
		} else {
			windowY = container.getY() + getY() + getHeight() + popUpWindow.getHeight();
		}

		popUpWindow.setLocation(windowX+50, (windowY+520)-popUpWindow.getHeight());
		popUpWindow.setMinimumSize(new Dimension(getWidth(), 30));
		popUpWindow.revalidate();
		popUpWindow.repaint();

	}

	public void setDictionary(ArrayList<SuggestionObj> words) {
		dictionary.clear();
		if(words != null){
			words.forEach(word -> dictionary.add(word));
		}
	}

	public JWindow getAutoSuggestionPopUpWindow() {
		return popUpWindow;
	}
	
	public ArrayList<SuggestionObj> getDictionary() {
		ArrayList<SuggestionObj> tempList = new ArrayList<>();
		tempList.addAll(staticDictionary);
		tempList.addAll(staticChannelDictionary);
		tempList.addAll(dictionary);
		tempList.addAll(getUnMentionedUsers());
		
		return tempList;
	}
	
	public List<SuggestionObj> getUnMentionedUsers() {
		List<String> mentionedUsers = chat.greetingsManager.getMentionedUsers();
		List<String> chatUsers = new ArrayList<String>();

		chat.parentTab.getStatistics().getSendedMessages().entrySet().forEach(entry -> {
			if(!chatUsers.contains(entry.getKey())){
				chatUsers.add(entry.getKey());
			}
		});
		
		List<String> tempList = new ArrayList<String>(chatUsers);
		tempList.removeAll(mentionedUsers);
		
		List<SuggestionObj> outputList = new ArrayList<>();
		tempList.forEach(userName -> outputList.add(new SuggestionObj("@@"+userName, null)));
		return outputList;
	}
	

	public void addToDictionary(SuggestionObj word) {
		dictionary.add(word);
	}
	
	public void addToDictionary(SuggestionObj word, int index) {
		dictionary.add(index, word);
	}
	
	public static void addToStaticDictionary(SuggestionObj word) {
		staticDictionary.add(word);
	}
	
	public static void clearStaticDictionary() {
		staticDictionary.clear();
	}
	
	public static void setStaticChannelDictionary(String channelId) {
		staticChannelDictionary.clear();
		ChannelEmotes channel = EmoteManager.getChannelEmotes(channelId);
		if(channel != null){
			channel.values().forEach(emote -> {
				if(channel.hasPermission(emote) && !emote.isGlobal()){
					staticChannelDictionary.add(new SuggestionObj(emote));
				}
			});
		}
	}


	
	public boolean wordTyped(String typedWord) {
	    if (typedWord.isEmpty()) {
	        return false;
	    }
	    
	    String currentPrefix = "";
	    for(String prefix : prefixs){
	    	if(typedWord.startsWith(prefix)){
	    		typedWord = typedWord.substring(1);
	    		currentPrefix = prefix;
	    	}
	    }
	    

	    if (currentPrefix.isEmpty()) {
	        return false;
	    }

	    boolean suggestionAdded = false;
	    for (SuggestionObj word : getDictionary()) {
	        String wordText = word.getText();
			if (wordText.toLowerCase().contains(typedWord.toLowerCase()) && wordText.startsWith(currentPrefix)) {
				if(typedWord.startsWith("@")){
					if(wordText.startsWith("@@")){
						addWordToSuggestions(word);
					}
				}else{
					if(!wordText.startsWith("@@")){
						addWordToSuggestions(word.setDisplayText(currentPrefix.equals(":") ? wordText.substring(1) : wordText));
					}
				}
				
	            suggestionAdded = true;
	        }
	    }
	    return suggestionAdded;
	}

	public UndoManager getUndoManager() {
		return undoManager;
	}
}