package de.minetrain.minechat.config.obj;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.twitch4j.pubsub.domain.Leaderboard.Identifier;

import de.minetrain.minechat.config.YamlManager;
import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.objectdata.MacroData;
import de.minetrain.minechat.gui.obj.buttons.ButtonType;
import de.minetrain.minechat.main.Main;

public class ChannelMacros {
	private static final Logger logger = LoggerFactory.getLogger(ChannelMacros.class);
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
		List<MacroData> macroDatas = DatabaseManager.getMacro().getChannelDataById(configID);
		
//		macroDatas.stream().filter(macro -> macro.getButton_row().equalsIgnoreCase(MacroRow.ROW_0.name())).collect(Collectors.toMap(MacroData::getButton_type, Function.identity()));

		createMaps(MacroRow.ROW_0, macroDatas.stream()
				.filter(macro -> macro.getButton_row().equalsIgnoreCase(MacroRow.ROW_0.name()))
				.collect(Collectors.toMap(MacroData::getButton_type, Function.identity(), (existingValue, newValue) -> {
		                logger.warn("Duplicate key found for macro button " + existingValue + ". Skipping.");
		                return existingValue;})));// Keeping the existing value in case of duplicates

		createMaps(MacroRow.ROW_1, macroDatas.stream()
				.filter(macro -> macro.getButton_row().equalsIgnoreCase(MacroRow.ROW_0.name()))
				.collect(Collectors.toMap(MacroData::getButton_type, Function.identity(), (existingValue, newValue) -> {
		                logger.warn("Duplicate key found for macro button " + existingValue + ". Skipping.");
		                return existingValue;})));// Keeping the existing value in case of duplicates

		createMaps(MacroRow.ROW_2, macroDatas.stream()
				.filter(macro -> macro.getButton_row().equalsIgnoreCase(MacroRow.ROW_0.name()))
				.collect(Collectors.toMap(MacroData::getButton_type, Function.identity(), (existingValue, newValue) -> {
		                logger.warn("Duplicate key found for macro button " + existingValue + ". Skipping.");
		                return existingValue;})));// Keeping the existing value in case of duplicates
		
		
//		createMaps(MacroRow.ROW_0, configID);
//		createMaps(MacroRow.ROW_1, configID);
//		createMaps(MacroRow.ROW_2, configID);
		
		DatabaseManager.commit();
	}
	
	private void createMaps(MacroRow row, Map<ButtonType, MacroData> macroData) {
		HashMap<ButtonType, MacroObject> macroRow = getMacroRow(row);

		macroRow.put(ButtonType.MACRO_1, getMacroObject(macroData, ButtonType.MACRO_1));
		macroRow.put(ButtonType.MACRO_2, getMacroObject(macroData, ButtonType.MACRO_2));
		macroRow.put(ButtonType.MACRO_3, getMacroObject(macroData, ButtonType.MACRO_3));
		macroRow.put(ButtonType.MACRO_4, getMacroObject(macroData, ButtonType.MACRO_4));
		macroRow.put(ButtonType.MACRO_5, getMacroObject(macroData, ButtonType.MACRO_5));
		macroRow.put(ButtonType.MACRO_6, getMacroObject(macroData, ButtonType.MACRO_6));
		
		macroRow.put(ButtonType.EMOTE_1, getDefaultMacroObject(ButtonType.EMOTE_1));
		macroRow.put(ButtonType.EMOTE_2, getDefaultMacroObject(ButtonType.EMOTE_2));
		macroRow.put(ButtonType.EMOTE_3, getDefaultMacroObject(ButtonType.EMOTE_3));
		macroRow.put(ButtonType.EMOTE_4, getDefaultMacroObject(ButtonType.EMOTE_4));
		macroRow.put(ButtonType.EMOTE_5, getDefaultMacroObject(ButtonType.EMOTE_5));
		macroRow.put(ButtonType.EMOTE_6, getDefaultMacroObject(ButtonType.EMOTE_6));
		macroRow.put(ButtonType.EMOTE_7, getDefaultMacroObject(ButtonType.EMOTE_7));
	}

	private MacroObject getMacroObject(Map<ButtonType, MacroData> macroData, ButtonType buttonType) {
		return macroData.containsKey(buttonType) ? getMacroObject(macroData.get(buttonType)) : getDefaultMacroObject(buttonType);
	}
//	
//	private void createMaps(MacroRow row, String configID) {
//		HashMap<ButtonType, MacroObject> macroRow = getMacroRow(row);
//		if(configID == null || configID.isEmpty() || configID.equals("0")){
//			macroRow.put(ButtonType.MACRO_1, getDefaultMacroObject(ButtonType.MACRO_1));
//			macroRow.put(ButtonType.MACRO_2, getDefaultMacroObject(ButtonType.MACRO_2));
//			macroRow.put(ButtonType.MACRO_3, getDefaultMacroObject(ButtonType.MACRO_3));
//			macroRow.put(ButtonType.MACRO_4, getDefaultMacroObject(ButtonType.MACRO_4));
//			macroRow.put(ButtonType.MACRO_5, getDefaultMacroObject(ButtonType.MACRO_5));
//			macroRow.put(ButtonType.MACRO_6, getDefaultMacroObject(ButtonType.MACRO_6));
//			
//			macroRow.put(ButtonType.EMOTE_1, getDefaultMacroObject(ButtonType.EMOTE_1));
//			macroRow.put(ButtonType.EMOTE_2, getDefaultMacroObject(ButtonType.EMOTE_2));
//			macroRow.put(ButtonType.EMOTE_3, getDefaultMacroObject(ButtonType.EMOTE_3));
//			macroRow.put(ButtonType.EMOTE_4, getDefaultMacroObject(ButtonType.EMOTE_4));
//			macroRow.put(ButtonType.EMOTE_5, getDefaultMacroObject(ButtonType.EMOTE_5));
//			macroRow.put(ButtonType.EMOTE_6, getDefaultMacroObject(ButtonType.EMOTE_6));
//			macroRow.put(ButtonType.EMOTE_7, getDefaultMacroObject(ButtonType.EMOTE_7));
//		}else{
//			YamlManager config = Main.CONFIG;
//			String path = "Channel_"+configID+".Macros"+row.name().toLowerCase().substring(3)+".";
//
//			String[] macro0 = config.getString(path+"M0").split("%-%");
//			String[] macro1 = config.getString(path+"M1").split("%-%");
//			String[] macro2 = config.getString(path+"M2").split("%-%");
//			String[] macro3 = config.getString(path+"M3").split("%-%");
//			String[] macro4 = config.getString(path+"M4").split("%-%");
//			String[] macro5 = config.getString(path+"M5").split("%-%");
//			String[] macro6 = config.getString(path+"M6").split("%-%");
//			String[] macro7 = config.getString(path+"M7").split("%-%");
//			String[] macro8 = config.getString(path+"M8").split("%-%");
//			String[] macro9 = config.getString(path+"M9").split("%-%");
//			String[] macro10 = config.getString(path+"M10").split("%-%");
//			String[] macro11 = config.getString(path+"M11").split("%-%");
//			String[] macro12 = config.getString(path+"M12").split("%-%");
//			
//			macroRow.put(ButtonType.MACRO_1, new MacroObject(ButtonType.MACRO_1, macro0[0].contains("%&%") ? macro0[0].split("%&%")[1]:"null", macro0[0].split("%&%")[0], macro0[1]));
//			macroRow.put(ButtonType.MACRO_2, new MacroObject(ButtonType.MACRO_2, macro1[0].contains("%&%") ? macro1[0].split("%&%")[1]:"null", macro1[0].split("%&%")[0], macro1[1]));
//			macroRow.put(ButtonType.MACRO_3, new MacroObject(ButtonType.MACRO_3, macro2[0].contains("%&%") ? macro2[0].split("%&%")[1]:"null", macro2[0].split("%&%")[0], macro2[1]));
//			macroRow.put(ButtonType.MACRO_4, new MacroObject(ButtonType.MACRO_4, macro3[0].contains("%&%") ? macro3[0].split("%&%")[1]:"null", macro3[0].split("%&%")[0], macro3[1]));
//			macroRow.put(ButtonType.MACRO_5, new MacroObject(ButtonType.MACRO_5, macro4[0].contains("%&%") ? macro4[0].split("%&%")[1]:"null", macro4[0].split("%&%")[0], macro4[1]));
//			macroRow.put(ButtonType.MACRO_6, new MacroObject(ButtonType.MACRO_6, macro5[0].contains("%&%") ? macro5[0].split("%&%")[1]:"null", macro5[0].split("%&%")[0], macro5[1]));
//			
//			macroRow.put(ButtonType.EMOTE_1, new MacroObject(ButtonType.EMOTE_1, macro6[0], ">null<", macro6[1]));
//			macroRow.put(ButtonType.EMOTE_2, new MacroObject(ButtonType.EMOTE_2, macro7[0], ">null<", macro7[1]));
//			macroRow.put(ButtonType.EMOTE_3, new MacroObject(ButtonType.EMOTE_3, macro8[0], ">null<", macro8[1]));
//			macroRow.put(ButtonType.EMOTE_4, new MacroObject(ButtonType.EMOTE_4, macro9[0], ">null<", macro9[1]));
//			macroRow.put(ButtonType.EMOTE_5, new MacroObject(ButtonType.EMOTE_5, macro10[0],">null<",  macro10[1]));
//			macroRow.put(ButtonType.EMOTE_6, new MacroObject(ButtonType.EMOTE_6, macro11[0], ">null<", macro11[1]));
//			macroRow.put(ButtonType.EMOTE_7, new MacroObject(ButtonType.EMOTE_7, macro12[0], ">null<", macro12[1]));
//		}
//	}

	private MacroObject getDefaultMacroObject(ButtonType buttonType) {
		return new MacroObject(buttonType, "null", "null", ">null<".split(""));
	}
	
	public MacroObject getMacroObject(MacroData data){
		return new MacroObject(data);
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
