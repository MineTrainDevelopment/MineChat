package de.minetrain.minechat.gui.obj.chat.userinput.textarea;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;

import de.minetrain.minechat.utils.Settings;

/**
 * NOTE: I stoll this code from <a href="https://stackoverflow.com/questions/14186955/create-a-autocompleting-textbox-in-java-with-a-dropdown-list">StackOverflow</a> 
 */
public class SuggestionLabel extends JLabel {
	private static final long serialVersionUID = 2707205614443138010L;
	private boolean focused = false;
    private final JWindow autoSuggestionsPopUpWindow;
    private final JTextArea textArea;
    private final AutoSuggestor autoSuggestor;
    private Color suggestionsTextColor, suggestionBorderColor;

    public SuggestionLabel(SuggestionObj word, final Color borderColor, Color suggestionsTextColor, AutoSuggestor autoSuggestor) {
    	super(word.getDisplayText());
    	setIcon(word.getIcon());
        setFont(Settings.MESSAGE_FONT);

        this.suggestionsTextColor = suggestionsTextColor;
        this.autoSuggestor = autoSuggestor;
        this.textArea = autoSuggestor;
        this.suggestionBorderColor = borderColor;
        this.autoSuggestionsPopUpWindow = autoSuggestor.getAutoSuggestionPopUpWindow();

        initComponent();
    }

    private void initComponent() {
        setFocusable(true);
        setForeground(suggestionsTextColor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);
				select();
            }
        });

        

		setFocusTraversalKeysEnabled(false);
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, true), "Enter released");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "Enter released");
        getActionMap().put("Enter released", new AbstractAction() {
			private static final long serialVersionUID = -1556400970184988934L;

			@Override
            public void actionPerformed(ActionEvent ae) {
				select();
            }
        });
        
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "Escape released");
        getActionMap().put("Escape released", new AbstractAction() {
			private static final long serialVersionUID = 7404235621986705040L;

			@Override
            public void actionPerformed(ActionEvent ae) {
                autoSuggestionsPopUpWindow.setVisible(false);
            }
        });
        
    }

    public void setFocused(boolean focused) {
        if (focused) {
            setBorder(new LineBorder(suggestionBorderColor));
        } else {
            setBorder(null);
        }
        repaint();
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    private void replaceWithSuggestedText() {
        String suggestedWord = getText().split(" ")[0];
        suggestedWord = suggestedWord.startsWith("@@") ? suggestedWord.substring(1) : suggestedWord;
        
        String text = textArea.getText();
        String typedWord = autoSuggestor.getCurrentlyTypedWord();
        String t = text.substring(0, text.lastIndexOf(typedWord));
        String tmp = t + text.substring(text.lastIndexOf(typedWord)).replace(typedWord, suggestedWord);
        textArea.setText(tmp + " ");
    }
    
    public void select(){
    	replaceWithSuggestedText();
        autoSuggestionsPopUpWindow.setVisible(false);
    }
}