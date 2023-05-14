package de.minetrain.minechat.config.obj;

import java.util.HashMap;

import de.minetrain.minechat.config.ConfigManager;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.main.Main;

public class ChannelMacros {
	HashMap<ButtonType, MacroObject> macros = new HashMap<ButtonType, MacroObject>();
	
	public ChannelMacros(String configID) {
		ConfigManager config = Main.CONFIG;
		String path = "Channel_"+configID+".Macros.";

		String[] macro0 = config.getString(path+"M0").split("%-%");
		String[] macro1 = config.getString(path+"M1").split("%-%");
		String[] macro2 = config.getString(path+"M2").split("%-%");
		String[] macro3 = config.getString(path+"M3").split("%-%");
		String[] macro4 = config.getString(path+"M4").split("%-%");
		String[] macro5 = config.getString(path+"M5").split("%-%");
		String[] macro6 = config.getString(path+"M6").split("%-%");
		String[] macro7 = config.getString(path+"M7").split("%-%");
		String[] macro8 = config.getString(path+"M8").split("%-%");
		String[] macro9 = config.getString(path+"M9").split("%-%");
		String[] macro10 = config.getString(path+"M10").split("%-%");
		String[] macro11 = config.getString(path+"M11").split("%-%");
		String[] macro12 = config.getString(path+"M12").split("%-%");
		
		macros.put(ButtonType.MACRO_1, new MacroObject(ButtonType.MACRO_1, new TwitchEmote((macro0[0].contains("%&%")) ? macro0[0].split("%&%")[1]:"null"), macro0[0].split("%&%")[0], macro0[1]));
		macros.put(ButtonType.MACRO_2, new MacroObject(ButtonType.MACRO_2, new TwitchEmote((macro1[0].contains("%&%")) ? macro1[0].split("%&%")[1]:"null"), macro1[0].split("%&%")[0], macro1[1]));
		macros.put(ButtonType.MACRO_3, new MacroObject(ButtonType.MACRO_3, new TwitchEmote((macro2[0].contains("%&%")) ? macro2[0].split("%&%")[1]:"null"), macro2[0].split("%&%")[0], macro2[1]));
		macros.put(ButtonType.MACRO_4, new MacroObject(ButtonType.MACRO_4, new TwitchEmote((macro3[0].contains("%&%")) ? macro3[0].split("%&%")[1]:"null"), macro3[0].split("%&%")[0], macro3[1]));
		macros.put(ButtonType.MACRO_5, new MacroObject(ButtonType.MACRO_5, new TwitchEmote((macro4[0].contains("%&%")) ? macro4[0].split("%&%")[1]:"null"), macro4[0].split("%&%")[0], macro4[1]));
		macros.put(ButtonType.MACRO_6, new MacroObject(ButtonType.MACRO_6, new TwitchEmote((macro5[0].contains("%&%")) ? macro5[0].split("%&%")[1]:"null"), macro5[0].split("%&%")[0], macro5[1]));
		
		macros.put(ButtonType.EMOTE_1, new MacroObject(ButtonType.EMOTE_1, new TwitchEmote(macro6[0]), ">null<", macro6[1]));
		macros.put(ButtonType.EMOTE_2, new MacroObject(ButtonType.EMOTE_2, new TwitchEmote(macro7[0]), ">null<", macro7[1]));
		macros.put(ButtonType.EMOTE_3, new MacroObject(ButtonType.EMOTE_3, new TwitchEmote(macro8[0]), ">null<", macro8[1]));
		macros.put(ButtonType.EMOTE_4, new MacroObject(ButtonType.EMOTE_4, new TwitchEmote(macro9[0]), ">null<", macro9[1]));
		macros.put(ButtonType.EMOTE_5, new MacroObject(ButtonType.EMOTE_5, new TwitchEmote(macro10[0]),">null<",  macro10[1]));
		macros.put(ButtonType.EMOTE_6, new MacroObject(ButtonType.EMOTE_6, new TwitchEmote(macro11[0]), ">null<", macro11[1]));
		macros.put(ButtonType.EMOTE_7, new MacroObject(ButtonType.EMOTE_7, new TwitchEmote(macro12[0]), ">null<", macro12[1]));
		
		System.out.println(getEMOTE_7().getTwitchEmote().getImageIcon() != null);
	}
	
	public ChannelMacros(boolean dummy) {
		macros.put(ButtonType.MACRO_1, new MacroObject(ButtonType.MACRO_1, new TwitchEmote("null"), "null", ">null<"));
		macros.put(ButtonType.MACRO_2, new MacroObject(ButtonType.MACRO_2, new TwitchEmote("null"), "null", ">null<"));
		macros.put(ButtonType.MACRO_3, new MacroObject(ButtonType.MACRO_3, new TwitchEmote("null"), "null", ">null<"));
		macros.put(ButtonType.MACRO_4, new MacroObject(ButtonType.MACRO_4, new TwitchEmote("null"), "null", ">null<"));
		macros.put(ButtonType.MACRO_5, new MacroObject(ButtonType.MACRO_5, new TwitchEmote("null"), "null", ">null<"));
		macros.put(ButtonType.MACRO_6, new MacroObject(ButtonType.MACRO_6, new TwitchEmote("null"), "null", ">null<"));
		
		macros.put(ButtonType.EMOTE_1, new MacroObject(ButtonType.EMOTE_1, new TwitchEmote("null"), ">null<", ">null<"));
		macros.put(ButtonType.EMOTE_2, new MacroObject(ButtonType.EMOTE_2, new TwitchEmote("null"), ">null<", ">null<"));
		macros.put(ButtonType.EMOTE_3, new MacroObject(ButtonType.EMOTE_3, new TwitchEmote("null"), ">null<", ">null<"));
		macros.put(ButtonType.EMOTE_4, new MacroObject(ButtonType.EMOTE_4, new TwitchEmote("null"), ">null<", ">null<"));
		macros.put(ButtonType.EMOTE_5, new MacroObject(ButtonType.EMOTE_5, new TwitchEmote("null"), ">null<", ">null<"));
		macros.put(ButtonType.EMOTE_6, new MacroObject(ButtonType.EMOTE_6, new TwitchEmote("null"), ">null<", ">null<"));
		macros.put(ButtonType.EMOTE_7, new MacroObject(ButtonType.EMOTE_7, new TwitchEmote("null"), ">null<", ">null<"));
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

	public MacroObject getEMOTE_1() {
		return macros.get(ButtonType.EMOTE_1);
	}

	public MacroObject getEMOTE_2() {
		return macros.get(ButtonType.EMOTE_2);
	}

	public MacroObject getEMOTE_3() {
		return macros.get(ButtonType.EMOTE_3);
	}

	public MacroObject getEMOTE_4() {
		return macros.get(ButtonType.EMOTE_4);
	}

	public MacroObject getEMOTE_5() {
		return macros.get(ButtonType.EMOTE_5);
	}

	public MacroObject getEMOTE_6() {
		return macros.get(ButtonType.EMOTE_6);
	}

	public MacroObject getEMOTE_7() {
		return macros.get(ButtonType.EMOTE_7);
	}
	
}
