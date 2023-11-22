package de.minetrain.minechat.gui.obj.buttons;

public enum ButtonType {
	NON(null),
	
	MACRO_1("M0"),
	MACRO_2("M1"),
	MACRO_3("M2"),
	MACRO_4("M3"),
	MACRO_5("M4"),
	MACRO_6("M5"),
	

	EMOTE_1("M6"),
	EMOTE_2("M7"),
	EMOTE_3("M8"),
	EMOTE_4("M9"),
	EMOTE_5("M10"),
	EMOTE_6("M11"),
	EMOTE_7("M12"),
	

	TAB_1(null),
	TAB_2(null),
	TAB_3(null),
	
	MINIMIZE(null),
	SETTINGS(null),
	CLOSE(null),

	CHANGE_ROW_LEFT(null),
	CHANGE_ROW_RIGHT(null),
	
	STATUS(null),
	STOP_QUEUE(null),
	TWITCH_PROFILE(null);
	
	
	private String configIndex;
	public String getConfigIndex(){return configIndex;}
	
	
	private ButtonType(String configIndex) {
		this.configIndex = configIndex;
	}
	
	public static ButtonType get(String input){
		if(ButtonType.valueOf(input) != null){
			return ButtonType.valueOf(input);
		}
		
		return ButtonType.NON;
	}
	
	
}
