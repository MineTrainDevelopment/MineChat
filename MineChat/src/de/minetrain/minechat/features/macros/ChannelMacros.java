package de.minetrain.minechat.features.macros;

import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.minetrain.minechat.data.DatabaseManager;
import de.minetrain.minechat.data.objectdata.MacroData;

public class ChannelMacros {
	private static final Logger logger = LoggerFactory.getLogger(ChannelMacros.class);
	HashMap<Integer, MacroObject> macrosByButtonId = new HashMap<Integer, MacroObject>();
	private final String channelId;
	
	//ButtonIds:
	//00, 01, 02, 03...
	//10, 11, 12, 13...
	//20, 21, 22, 23...
	
	public ChannelMacros(String channelId) {
		this.channelId = channelId;
		reloadMacros(channelId);
	}

	public ChannelMacros reloadMacros(String configID) {
		macrosByButtonId.clear();
		macrosByButtonId.putAll(DatabaseManager.getMacro()
			.getChannelDataById(configID)
			.stream().map(MacroObject::new)
			.collect(Collectors.toMap(MacroObject::getButtonId, Function.identity(), (existing, neu) -> {
				
				//Delete the older macro should a duplicate acour.
				if(existing.getDatabaseId() < neu.getDatabaseId()){
					deleteMacro(existing, true);
					return neu;
				}
				
				deleteMacro(neu, true);
				return existing;
			})));
		
		return this;
	}
	
	/**
	 * Adding a macro and store it in database.
	 * @param macro
	 * @return
	 */
	public ChannelMacros createMacro(MacroObject macro){
		if(macrosByButtonId.containsKey(macro.getButtonId())){
			deleteMacro(macro, false);
		}
		
		macrosByButtonId.put(macro.getButtonId(), macro);
		DatabaseManager.getMacro().insert(new MacroData(channelId, macro), true);
		return this;
	}
	
	/**
	 * Deletes from database and cache.
	 * @param macro
	 * @param commit Whether the database connection gets commit automatically.
	 * @return
	 */
	public ChannelMacros deleteMacro(MacroObject macro, boolean commit){
		DatabaseManager.getMacro().deleteMacro(channelId, macro, commit);
		macrosByButtonId.remove(macro.getButtonId());
		return this;
	}
	
	/**
	 * NOTE: May be null, if no macro is set for this button.
	 * @param buttonId
	 * @return
	 */
	public MacroObject getMacro(int buttonId){
		return macrosByButtonId.get(buttonId);
	}
	
	public void updateMacro(MacroObject macro, String emoteId, String title, String[] output){
		macro = new MacroObject(macro.getMacroType(), emoteId, macro.getButtonId(), title, channelId, output);
		createMacro(macro);
	}
}
