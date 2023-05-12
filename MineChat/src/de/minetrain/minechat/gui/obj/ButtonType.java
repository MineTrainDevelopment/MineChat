package de.minetrain.minechat.gui.obj;

public enum ButtonType {
	NON(null),
	
	MACRO_1("M1"),
	MACRO_2("M2"),
	MACRO_3("M3"),
	MACRO_4("M4"),
	MACRO_5("M5"),
	MACRO_6("M6"),
	

	EMOTE_1("M7"),
	EMOTE_2("M8"),
	EMOTE_3("M9"),
	EMOTE_4("M10"),
	EMOTE_5("M11"),
	EMOTE_6("M12"),
	EMOTE_7("M13"),
	

	TAB_1(null),
	TAB_2(null),
	TAB_3(null),
	
	MINIMIZE(null),
	SETTINGS(null),
	CLOSE(null),
	
	SPAM(null),
	GREET(null),
	STOP_QUEUE(null),
	TWITCH_PROFILE(null);
	
	
	private String configIndex;
	public String getConfigIndex(){return configIndex;}
	
	
	private ButtonType(String configIndex) {
		this.configIndex = configIndex;
	}
	
	
}
