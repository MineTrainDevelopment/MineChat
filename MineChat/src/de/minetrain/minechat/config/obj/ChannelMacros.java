package de.minetrain.minechat.config.obj;

import java.util.HashMap;

import de.minetrain.minechat.gui.objects.ButtonType;
import de.minetrain.minechat.util.MacroObject;

public class ChannelMacros {
	HashMap<ButtonType, MacroObject> macros = new HashMap<ButtonType, MacroObject>();
	
	public ChannelMacros() {
		macros.put(ButtonType.MACRO_1, new MacroObject(ButtonType.MACRO_1, null, "peepoCheer h! peepoCheer"));
		macros.put(ButtonType.MACRO_2, new MacroObject(ButtonType.MACRO_2, null, "VIBE"));
		macros.put(ButtonType.MACRO_3, new MacroObject(ButtonType.MACRO_3, null, "Woah sinticaBooonk"));
		macros.put(ButtonType.MACRO_4, new MacroObject(ButtonType.MACRO_4, null, "blobDance rooBobble blobDance rooBobble blobDance rooBobble blobDance rooBobble blobDance rooBobble blobDance rooBobble blobDance rooBobble blobDance rooBobble blobDance rooBobble blobDance rooBobble blobDance rooBobble "));
		macros.put(ButtonType.MACRO_5, new MacroObject(ButtonType.MACRO_5, null, "SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty SinticaEskalation SinticaParty"));
		macros.put(ButtonType.MACRO_6, new MacroObject(ButtonType.MACRO_6, null, "SinticaEskalation sinticaBooonk SinticaEskalation sinticaBooonk SinticaEskalation sinticaBooonk SinticaEskalation sinticaBooonk SinticaEskalation sinticaBooonk SinticaEskalation sinticaBooonk SinticaEskalation sinticaBooonk SinticaEskalation sinticaBooonk SinticaEskalation sinticaBooonk SinticaEskalation sinticaBooonk SinticaEskalation sinticaBooonk "));
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
