package de.minetrain.minechat.util;

import java.util.HashMap;

import de.minetrain.minechat.gui.objects.ButtonType;

public class ChannelMacros {
	HashMap<String, String> macros = new HashMap<String, String>();
	
	public ChannelMacros() {
		macros.put("macro_1", "blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS blobHYPERS ");
		macros.put("macro_2", "Pferdchen ist Fein!");
		macros.put("macro_3", "SehrFein SehrFein!");
		macros.put("macro_4", "macro_4");
		macros.put("macro_5", "macro_5");
		macros.put("macro_6", "macro_6");
	}
	
	public String getMacro(ButtonType button) {
		return macros.get(button.name().toLowerCase());
	}

	public String getMacro_1() {
		return macros.get("macro_1");
	}

	public String getMacro_2() {
		return macros.get("macro_2");
	}

	public String getMacro_3() {
		return macros.get("macro_3");
	}

	public String getMacro_4() {
		return macros.get("macro_4");
	}

	public String getMacro_5() {
		return macros.get("macro_5");
	}

	public String getMacro_6() {
		return macros.get("macro_6");
	}
	
	
	
}
