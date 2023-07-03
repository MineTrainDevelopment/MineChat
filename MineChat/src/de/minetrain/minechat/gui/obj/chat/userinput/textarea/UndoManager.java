package de.minetrain.minechat.gui.obj.chat.userinput.textarea;

import java.util.ArrayList;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.minetrain.minechat.utils.Settings;

public class UndoManager extends ArrayList<String>{
	private static final long serialVersionUID = 3614006111036329804L;
	public enum UndoVariation {WORD, LETTER}
	private final JTextArea textArea;
	private final UndoVariation variation;
	
	private int index = 1;
	private boolean isUserInput = true;
	private boolean inputCheck = false;
	private boolean lastWordSaved = false;
	private String previousWord = "";
	
	//Mode for per word and per leter changes.
	
	/**
	 * 
	 * @param textArea
	 * @param variation NOTE: This is optional and should be used to override the user settings.
	 */
	public UndoManager(JTextArea textArea, UndoVariation... variations) {
		this.textArea = textArea;
		this.variation = variations.length > 0 ? variations[0] : null;
		super.add("");
		
		this.textArea.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent event) { }

	        @Override
	        public void removeUpdate(DocumentEvent event) {
	        	if(!add(null)){
	        		index--;
	        	}
	        }

	        @Override
	        public void insertUpdate(DocumentEvent event) {
	        	if(!add(null)){
	        		index--;
	        	}
	        	
	        	if(isUserInput){
	        		lastWordSaved = false;
	        	}
	        }

	        
	    });
	}
	
	public void undo(){
		if(!isEmpty() && index > 0){
			isUserInput = false;
			inputCheck = true;
			textArea.setText(get(index-1));
			index--;
		}
	}
	
	public void redo(){
		if(!isEmpty() && index >= 0 && size() > index && !(index+1 >= size())){
			index++;
			isUserInput = false;
			inputCheck = true;
			textArea.setText(get(index));
		}
	}
	
	public UndoVariation getVariation() {
		return variation != null ? variation : Settings.UNDO_VARIATION;
	}
	
	/**
	 * NOTE: This method ALWAYS increases the index count. You may need to decreases it when it returns false.
	 */
	@Override
	public boolean add(String item) {
		if(!isUserInput){
			index++;
			isUserInput = inputCheck ? false : true;
			inputCheck = false;
			return false;
		}
		
		//Clear the inputs that are no longer needet.
		if(index != size()){
    		subList(index, size()).clear();
    		index = size();
    	}
		
		if(size() >= Settings.MAX_UNDO_LOG_SIZE){
			index--;
			remove(0);
		}
		
		if(item != null){
			index++;
			return item.isEmpty() ? false : super.add(item);
		}
		
		item = textArea.getText();
		
		if(getVariation().equals(UndoVariation.LETTER)){
			index++;
			return item.isEmpty() ? false : super.add(item);
		}

		if(getVariation().equals(UndoVariation.WORD)){
			String lastWord = item.trim().replaceAll("^.*\\s", "");

			if(item.endsWith(" ") && (isEmpty() ? true : !get(size()-1).equals(item))){
				index++;
				previousWord = lastWord;
				return item.isEmpty() ? false : super.add(item);
			}else if(!previousWord.isEmpty() && lastWord.equals(previousWord.substring(0, previousWord.length()-1)) && !lastWordSaved){
				index++;
				lastWordSaved = true;
				return item.isEmpty() ? false : super.add(item.substring(0, item.lastIndexOf(lastWord))+previousWord+" ");
			}
			
			previousWord = lastWord;
		}
		
		index++;
		return false;
	}
}
