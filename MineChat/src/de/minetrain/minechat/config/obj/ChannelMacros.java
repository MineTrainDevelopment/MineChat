package de.minetrain.minechat.config.obj;

import java.util.HashMap;

import de.minetrain.minechat.config.ConfigManager;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.main.Main;

public class ChannelMacros {
	HashMap<ButtonType, MacroObject> row_0 = new HashMap<ButtonType, MacroObject>();
	HashMap<ButtonType, MacroObject> row_1 = new HashMap<ButtonType, MacroObject>();
	HashMap<ButtonType, MacroObject> row_2 = new HashMap<ButtonType, MacroObject>();
	public enum MacroRow {ROW_0, ROW_1, ROW_2}
	private MacroRow currentMacroRow;
	
	public ChannelMacros(String configID) {
		setCurrentMacroRow(MacroRow.ROW_0);
		reloadMacros(configID);
	}

	public void reloadMacros(String configID) {
		createMaps(MacroRow.ROW_0, configID);
		createMaps(MacroRow.ROW_1, configID);
		createMaps(MacroRow.ROW_2, configID);
	}
	
	private void createMaps(MacroRow row, String configID) {
		HashMap<ButtonType, MacroObject> macroRow = getMacroRow(row);
		if(configID == null || configID.isEmpty()){
			macroRow.put(ButtonType.MACRO_1, new MacroObject(ButtonType.MACRO_1, new TwitchEmote("null"), "null", ">null<"));
			macroRow.put(ButtonType.MACRO_2, new MacroObject(ButtonType.MACRO_2, new TwitchEmote("null"), "null", ">null<"));
			macroRow.put(ButtonType.MACRO_3, new MacroObject(ButtonType.MACRO_3, new TwitchEmote("null"), "null", ">null<"));
			macroRow.put(ButtonType.MACRO_4, new MacroObject(ButtonType.MACRO_4, new TwitchEmote("null"), "null", ">null<"));
			macroRow.put(ButtonType.MACRO_5, new MacroObject(ButtonType.MACRO_5, new TwitchEmote("null"), "null", ">null<"));
			macroRow.put(ButtonType.MACRO_6, new MacroObject(ButtonType.MACRO_6, new TwitchEmote("null"), "null", ">null<"));
			
			macroRow.put(ButtonType.EMOTE_1, new MacroObject(ButtonType.EMOTE_1, new TwitchEmote("null"), ">null<", ">null<"));
			macroRow.put(ButtonType.EMOTE_2, new MacroObject(ButtonType.EMOTE_2, new TwitchEmote("null"), ">null<", ">null<"));
			macroRow.put(ButtonType.EMOTE_3, new MacroObject(ButtonType.EMOTE_3, new TwitchEmote("null"), ">null<", ">null<"));
			macroRow.put(ButtonType.EMOTE_4, new MacroObject(ButtonType.EMOTE_4, new TwitchEmote("null"), ">null<", ">null<"));
			macroRow.put(ButtonType.EMOTE_5, new MacroObject(ButtonType.EMOTE_5, new TwitchEmote("null"), ">null<", ">null<"));
			macroRow.put(ButtonType.EMOTE_6, new MacroObject(ButtonType.EMOTE_6, new TwitchEmote("null"), ">null<", ">null<"));
			macroRow.put(ButtonType.EMOTE_7, new MacroObject(ButtonType.EMOTE_7, new TwitchEmote("null"), ">null<", ">null<"));
		}else{
			ConfigManager config = Main.CONFIG;
			String path = "Channel_"+configID+".Macros"+row.name().toLowerCase().substring(3)+".";

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
			
			macroRow.put(ButtonType.MACRO_1, new MacroObject(ButtonType.MACRO_1, new TwitchEmote((macro0[0].contains("%&%")) ? macro0[0].split("%&%")[1]:"null"), macro0[0].split("%&%")[0], macro0[1]));
			macroRow.put(ButtonType.MACRO_2, new MacroObject(ButtonType.MACRO_2, new TwitchEmote((macro1[0].contains("%&%")) ? macro1[0].split("%&%")[1]:"null"), macro1[0].split("%&%")[0], macro1[1]));
			macroRow.put(ButtonType.MACRO_3, new MacroObject(ButtonType.MACRO_3, new TwitchEmote((macro2[0].contains("%&%")) ? macro2[0].split("%&%")[1]:"null"), macro2[0].split("%&%")[0], macro2[1]));
			macroRow.put(ButtonType.MACRO_4, new MacroObject(ButtonType.MACRO_4, new TwitchEmote((macro3[0].contains("%&%")) ? macro3[0].split("%&%")[1]:"null"), macro3[0].split("%&%")[0], macro3[1]));
			macroRow.put(ButtonType.MACRO_5, new MacroObject(ButtonType.MACRO_5, new TwitchEmote((macro4[0].contains("%&%")) ? macro4[0].split("%&%")[1]:"null"), macro4[0].split("%&%")[0], macro4[1]));
			macroRow.put(ButtonType.MACRO_6, new MacroObject(ButtonType.MACRO_6, new TwitchEmote((macro5[0].contains("%&%")) ? macro5[0].split("%&%")[1]:"null"), macro5[0].split("%&%")[0], macro5[1]));
			
			macroRow.put(ButtonType.EMOTE_1, new MacroObject(ButtonType.EMOTE_1, new TwitchEmote(macro6[0]), ">null<", macro6[1]));
			macroRow.put(ButtonType.EMOTE_2, new MacroObject(ButtonType.EMOTE_2, new TwitchEmote(macro7[0]), ">null<", macro7[1]));
			macroRow.put(ButtonType.EMOTE_3, new MacroObject(ButtonType.EMOTE_3, new TwitchEmote(macro8[0]), ">null<", macro8[1]));
			macroRow.put(ButtonType.EMOTE_4, new MacroObject(ButtonType.EMOTE_4, new TwitchEmote(macro9[0]), ">null<", macro9[1]));
			macroRow.put(ButtonType.EMOTE_5, new MacroObject(ButtonType.EMOTE_5, new TwitchEmote(macro10[0]),">null<",  macro10[1]));
			macroRow.put(ButtonType.EMOTE_6, new MacroObject(ButtonType.EMOTE_6, new TwitchEmote(macro11[0]), ">null<", macro11[1]));
			macroRow.put(ButtonType.EMOTE_7, new MacroObject(ButtonType.EMOTE_7, new TwitchEmote(macro12[0]), ">null<", macro12[1]));
		}
	}
	
	private HashMap<ButtonType, MacroObject> getMacroRow(MacroRow row){
		switch(row){
			case ROW_1: return row_1;
			case ROW_2: return row_2;
			default: return row_0;
		}
	}
	
	public MacroObject getMacro(ButtonType button, MacroRow row) {
		return getMacroRow(row).get(button);
	}

	public MacroObject getMacro_1(MacroRow row) {
		return getMacro(ButtonType.MACRO_1, row);
	}

	public MacroObject getMacro_2(MacroRow row) {
		return getMacro(ButtonType.MACRO_2, row);
	}

	public MacroObject getMacro_3(MacroRow row) {
		return getMacro(ButtonType.MACRO_3, row);
	}

	public MacroObject getMacro_4(MacroRow row) {
		return getMacro(ButtonType.MACRO_4, row);
	}

	public MacroObject getMacro_5(MacroRow row) {
		return getMacro(ButtonType.MACRO_5, row);
	}

	public MacroObject getMacro_6(MacroRow row) {
		return getMacro(ButtonType.MACRO_6, row);
	}

	public MacroObject getEmote_1(MacroRow row) {
		return getMacro(ButtonType.EMOTE_1, row);
	}

	public MacroObject getEmote_2(MacroRow row) {
		return getMacro(ButtonType.EMOTE_2, row);
	}

	public MacroObject getEmote_3(MacroRow row) {
		return getMacro(ButtonType.EMOTE_3, row);
	}

	public MacroObject getEmote_4(MacroRow row) {
		return getMacro(ButtonType.EMOTE_4, row);
	}

	public MacroObject getEmote_5(MacroRow row) {
		return getMacro(ButtonType.EMOTE_5, row);
	}

	public MacroObject getEmote_6(MacroRow row) {
		return getMacro(ButtonType.EMOTE_6, row);
	}

	public MacroObject getEmote_7(MacroRow row) {
		return getMacro(ButtonType.EMOTE_7, row);
	}

	public MacroRow getCurrentMacroRow() {
		return currentMacroRow;
	}

	public void setCurrentMacroRow(MacroRow currentMacroRow) {
		this.currentMacroRow = currentMacroRow;
	}
	
}
