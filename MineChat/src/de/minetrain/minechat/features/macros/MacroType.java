package de.minetrain.minechat.features.macros;

public enum MacroType {
	TEXT,
	EMOTE;
	
	public static MacroType get(String input){
		if(MacroType.valueOf(input) != null){
			return MacroType.valueOf(input);
		}
		
		return MacroType.EMOTE;
	}
}
