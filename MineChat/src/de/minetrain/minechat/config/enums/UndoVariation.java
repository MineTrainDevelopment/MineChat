package de.minetrain.minechat.config.enums;

import org.slf4j.LoggerFactory;

public enum UndoVariation {
	WORD, 
	LETTER;
	
	public static UndoVariation get(String name) {
		try {
			return UndoVariation.valueOf(name);
		} catch (IllegalArgumentException ex) {
			LoggerFactory.getLogger(UndoVariation.class).warn("Invalid UndoVariation -> "+name+""
					+ "\n	You may need to check the settigns file."
					+ "\n	Valid types are: [WORD, LETTER]");
			return WORD;
		}
	}
}
