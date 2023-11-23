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
				.filter(macro -> macro.getButton_row().equalsIgnoreCase(MacroRow.ROW_1.name()))
				.collect(Collectors.toMap(MacroData::getButton_type, Function.identity(), (existingValue, newValue) -> {
		                logger.warn("Duplicate key found for macro button " + existingValue + ". Skipping.");
		                return existingValue;})));// Keeping the existing value in case of duplicates

		createMaps(MacroRow.ROW_2, macroDatas.stream()
				.filter(macro -> macro.getButton_row().equalsIgnoreCase(MacroRow.ROW_2.name()))
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
		
		macroRow.put(ButtonType.EMOTE_1, getMacroObject(macroData, ButtonType.EMOTE_1));
		macroRow.put(ButtonType.EMOTE_2, getMacroObject(macroData, ButtonType.EMOTE_2));
		macroRow.put(ButtonType.EMOTE_3, getMacroObject(macroData, ButtonType.EMOTE_3));
		macroRow.put(ButtonType.EMOTE_4, getMacroObject(macroData, ButtonType.EMOTE_4));
		macroRow.put(ButtonType.EMOTE_5, getMacroObject(macroData, ButtonType.EMOTE_5));
		macroRow.put(ButtonType.EMOTE_6, getMacroObject(macroData, ButtonType.EMOTE_6));
		macroRow.put(ButtonType.EMOTE_7, getMacroObject(macroData, ButtonType.EMOTE_7));
	}

	private MacroObject getMacroObject(Map<ButtonType, MacroData> macroData, ButtonType buttonType) {
		return macroData.containsKey(buttonType) ? getMacroObject(macroData.get(buttonType)) : getDefaultMacroObject(buttonType);
	}

	private MacroObject getDefaultMacroObject(ButtonType buttonType) {
		return new MacroObject(buttonType, "null", "null", ">null<".split("\n"));
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
