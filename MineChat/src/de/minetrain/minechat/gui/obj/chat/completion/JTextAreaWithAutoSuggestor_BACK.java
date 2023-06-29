//package de.minetrain.minechat.gui.obj.chat.completion;
//
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.GridLayout;
//import java.awt.Window;
//import java.awt.event.ActionEvent;
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
//import java.util.ArrayList;
//
//import javax.swing.AbstractAction;
//import javax.swing.JComponent;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JTextArea;
//import javax.swing.JWindow;
//import javax.swing.KeyStroke;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//
//public class AutoSuggestor extends JTextArea {
//	private final Window container;
//	private JPanel suggestionsPanel;
//	private JWindow autoSuggestionPopUpWindow;
//	private String typedWord;
//	private final ArrayList<String> dictionary = new ArrayList<>();
//	private int currentIndexOfSpace, tW, tH;
//	private final String prefix;
//	
//	private DocumentListener documentListener = new DocumentListener() {
//		@Override
//		public void insertUpdate(DocumentEvent de) {
//			checkForAndShowSuggestions();
//		}
//
//		@Override
//		public void removeUpdate(DocumentEvent de) {
//			checkForAndShowSuggestions();
//		}
//
//		@Override
//		public void changedUpdate(DocumentEvent de) {
//			checkForAndShowSuggestions();
//		}
//	};
//	
//	private final Color suggestionsTextColor;
//	private final Color suggestionFocusedColor;
//
//	public AutoSuggestor(Window mainWindow, ArrayList<String> words, String prefix, Color popUpBackground,
//			Color textColor, Color suggestionFocusedColor, float opacity) {
//		this.suggestionsTextColor = textColor;
//		this.container = mainWindow;
//		this.suggestionFocusedColor = suggestionFocusedColor;
//		getDocument().addDocumentListener(documentListener);
//		this.prefix = prefix;
//
//		setDictionary(words);
//
//		typedWord = "";
//		currentIndexOfSpace = 0;
//		tW = 0;
//		tH = 0;
//
//		autoSuggestionPopUpWindow = new JWindow(mainWindow);
//		autoSuggestionPopUpWindow.setOpacity(opacity);
//
//		suggestionsPanel = new JPanel();
//		suggestionsPanel.setLayout(new GridLayout(0, 1));
//		suggestionsPanel.setBackground(popUpBackground);
//
//		addKeyBindingToRequestFocusInPopUpWindow();
//	}
//
//	private void addKeyBindingToRequestFocusInPopUpWindow() {
//		setFocusTraversalKeysEnabled(false);
//		addKeyListener(new KeyAdapter() {
//	      @Override
//			public void keyPressed(KeyEvent event) {
//				if (isFocusOwner() && getAutoSuggestionPopUpWindow().isVisible()) {
//					switch (event.getKeyCode()) {
//					case KeyEvent.VK_DOWN:
//						if (autoSuggestionPopUpWindow.isVisible()) {
//							for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
//								if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
//									((SuggestionLabel) suggestionsPanel.getComponent(i)).setFocused(true);
//									autoSuggestionPopUpWindow.toFront();
//									autoSuggestionPopUpWindow.requestFocusInWindow();
//									suggestionsPanel.requestFocusInWindow();
//									suggestionsPanel.getComponent(i).requestFocusInWindow();
//									break;
//								}
//							}
//						}
//						break;
//
//					case KeyEvent.VK_ENTER, KeyEvent.VK_TAB:
//						if (suggestionsPanel.getComponents().length > 0 && autoSuggestionPopUpWindow.isVisible()) {
//							SuggestionLabel component = (SuggestionLabel) suggestionsPanel.getComponent(0);
//							component.select();
//						}
//						break;
//
//					default:
//						break;
//					}
//				}
//				
//				if(event.getKeyCode() == KeyEvent.VK_TAB){
//					event.consume();
//				}
//			}
//		});
//		
//		suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
//		suggestionsPanel.getActionMap().put("Down released", new AbstractAction() {
//			private static final long serialVersionUID = -2362595924406171787L;
//			int lastFocusableIndex = 0;
//
//			@Override
//			public void actionPerformed(ActionEvent ae) {// allows scrolling of labels in pop window (I know very hacky for now :))
//
//				ArrayList<SuggestionLabel> sls = getAddedSuggestionLabels();
//				int max = sls.size();
//
//				if (max > 1) {// more than 1 suggestion
//					for (int i = 0; i < max; i++) {
//						SuggestionLabel sl = sls.get(i);
//						if (sl.isFocused()) {
//							if (lastFocusableIndex == max - 1) {
//								lastFocusableIndex = 0;
//								sl.setFocused(false);
//								autoSuggestionPopUpWindow.setVisible(false);
//								setFocusToTextField();
//								checkForAndShowSuggestions();// fire method as if document listener change occured and fired it
//
//							} else {
//								sl.setFocused(false);
//								lastFocusableIndex = i;
//							}
//						} else if (lastFocusableIndex <= i) {
//							if (i < max) {
//								sl.setFocused(true);
//								autoSuggestionPopUpWindow.toFront();
//								autoSuggestionPopUpWindow.requestFocusInWindow();
//								suggestionsPanel.requestFocusInWindow();
//								suggestionsPanel.getComponent(i).requestFocusInWindow();
//								lastFocusableIndex = i;
//								break;
//							}
//						}
//					}
//				} else {// only a single suggestion was given
//					autoSuggestionPopUpWindow.setVisible(false);
//					setFocusToTextField();
//					checkForAndShowSuggestions();// fire method as if document listener change occured and fired it
//				}
//			}
//		});
//	}
//
//	private void setFocusToTextField() {
//		container.toFront();
//		container.requestFocusInWindow();
//		requestFocusInWindow();
//	}
//
//	public ArrayList<SuggestionLabel> getAddedSuggestionLabels() {
//		ArrayList<SuggestionLabel> sls = new ArrayList<>();
//		for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
//			if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
//				SuggestionLabel sl = (SuggestionLabel) suggestionsPanel.getComponent(i);
//				sls.add(sl);
//			}
//		}
//		return sls;
//	}
//
//	private void checkForAndShowSuggestions() {
//		typedWord = getCurrentlyTypedWord();
//
//		suggestionsPanel.removeAll();// remove previos words/jlabels that were added
//
//		// used to calcualte size of JWindow as new Jlabels are added
//		tW = 0;
//		tH = 0;
//
//		boolean added = wordTyped(typedWord);
//
//		if (!added) {
//			if (autoSuggestionPopUpWindow.isVisible()) {
//				autoSuggestionPopUpWindow.setVisible(false);
//			}
//		} else {
//			showPopUpWindow();
//			setFocusToTextField();
//		}
//	}
//
//	protected void addWordToSuggestions(String word) {
//		SuggestionLabel suggestionLabel = new SuggestionLabel(word, suggestionFocusedColor, suggestionsTextColor, this);
//
//		calculatePopUpWindowSize(suggestionLabel);
//
//		suggestionsPanel.add(suggestionLabel);
//	}
//
//	public String getCurrentlyTypedWord() {// get newest word after last white spaceif any or the first word if no white spaces
//		String text = getText();
//		String wordBeingTyped = "";
//		if (text.contains(" ")) {
//			int tmp = text.lastIndexOf(" ");
//			if (tmp >= currentIndexOfSpace) {
//				currentIndexOfSpace = tmp;
//				wordBeingTyped = text.substring(text.lastIndexOf(" "));
//			}
//		} else {
//			wordBeingTyped = text;
//		}
//		return wordBeingTyped.trim();
//	}
//
//	private void calculatePopUpWindowSize(JLabel label) {
//		// so we can size the JWindow correctly
//		if (tW < label.getPreferredSize().width) {
//			tW = label.getPreferredSize().width;
//		}
//		tH += label.getPreferredSize().height;
//	}
//
//	private void showPopUpWindow() {
//		autoSuggestionPopUpWindow.getContentPane().add(suggestionsPanel);
//		autoSuggestionPopUpWindow.setMinimumSize(new Dimension(getWidth(), 30));
//		autoSuggestionPopUpWindow.setSize(tW, tH);
//		autoSuggestionPopUpWindow.setVisible(true);
//
//		int windowX = 0;
//		int windowY = 0;
//
//		windowX = container.getX() + getX() + 5;
//		if (suggestionsPanel.getHeight() > autoSuggestionPopUpWindow.getMinimumSize().height) {
//			windowY = container.getY() + getY() + getHeight()
//					+ autoSuggestionPopUpWindow.getMinimumSize().height;
//		} else {
//			windowY = container.getY() + getY() + getHeight()
//					+ autoSuggestionPopUpWindow.getHeight();
//		}
//
//		autoSuggestionPopUpWindow.setLocation(windowX, windowY);
//		autoSuggestionPopUpWindow.setMinimumSize(new Dimension(getWidth(), 30));
//		autoSuggestionPopUpWindow.revalidate();
//		autoSuggestionPopUpWindow.repaint();
//
//	}
//
//	public void setDictionary(ArrayList<String> words) {
//		dictionary.clear();
//		if (words == null) {
//			return;// so we can call constructor with null value for dictionary without exception
//					// thrown
//		}
//		for (String word : words) {
//			dictionary.add(word);
//		}
//	}
//
//	public JWindow getAutoSuggestionPopUpWindow() {
//		return autoSuggestionPopUpWindow;
//	}
//
//	public Window getContainer() {
//		return container;
//	}
//
//	public void addToDictionary(String word) {
//		dictionary.add(word);
//	}
//
//	public boolean wordTyped(String typedWord) {
//	    if (typedWord.isEmpty() || !typedWord.startsWith(prefix)) {
//	        return false;
//	    }
//	    System.out.println("Typed word: " + typedWord);
//
//	    boolean suggestionAdded = false;
//
//	    for (String word : dictionary) {
//	    	System.out.println(word+" - "+typedWord);
//	        if (word.toLowerCase().contains(typedWord.replace(prefix, "").toLowerCase())) {
//	            addWordToSuggestions(word);
//	            suggestionAdded = true;
//	        }
//	    }
//	    return suggestionAdded;
//	}
//
//}