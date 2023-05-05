package de.minetrain.minechat.util;

import java.util.HashMap;

import de.minetrain.minechat.gui.objects.ButtonType;

public class ChannelMacros {
	HashMap<ButtonType, MacroObject> macros = new HashMap<ButtonType, MacroObject>();
	
	public ChannelMacros() {
		macros.put(ButtonType.MACRO_1, new MacroObject(ButtonType.MACRO_1, "Eskalation", "SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty"));
		macros.put(ButtonType.MACRO_2, new MacroObject(ButtonType.MACRO_2, "Pferdchen!", "Pferdchen ist Fein!"));
		macros.put(ButtonType.MACRO_3, new MacroObject(ButtonType.MACRO_3, "SehrFein", "SehrFein!"));
		macros.put(ButtonType.MACRO_4, new MacroObject(ButtonType.MACRO_4, null, null));
		macros.put(ButtonType.MACRO_5, new MacroObject(ButtonType.MACRO_5, null, null));
		macros.put(ButtonType.MACRO_6, new MacroObject(ButtonType.MACRO_6, null, null));
	}
	
	public MacroObject getMacro(ButtonType button) {
		return macros.get(button);
	}

	public MacroObject getMacro_1() {
		return macros.get(ButtonType.MACRO_1);
	}

	public MacroObject getMacro_2() {
		return macros.get(ButtonType.MACRO_2);
	}

	public MacroObject getMacro_3() {
		return macros.get(ButtonType.MACRO_3);
	}

	public MacroObject getMacro_4() {
		return macros.get(ButtonType.MACRO_4);
	}

	public MacroObject getMacro_5() {
		return macros.get(ButtonType.MACRO_5);
	}

	public MacroObject getMacro_6() {
		return macros.get(ButtonType.MACRO_6);
	}
	
	
	
}
