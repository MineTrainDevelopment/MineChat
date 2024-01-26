package de.minetrain.minechat.data.objectdata;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.minetrain.minechat.features.macros.MacroObject;
import de.minetrain.minechat.features.macros.MacroType;

public class MacroData {
	private final Long id;
	private String channelId;
	private final MacroType macro_type;
	private final int button_id;
	private final String title;
	private final String emoteId;
	private final String[] output;
	
	public MacroData(ResultSet resultSet) throws SQLException {
		this.id = resultSet.getLong("id");
		this.channelId = resultSet.getString("channel_id");
		this.macro_type = MacroType.get(resultSet.getString("macro_type"));
		this.button_id = resultSet.getInt("button_id");
		this.title = resultSet.getString("title");
		this.emoteId = resultSet.getString("emote_id");
		this.output = resultSet.getString("output").split("\n");
	}
	
	public MacroData(MacroData data){
		this.id = null;
		this.channelId = data.getChannelId();
		this.macro_type = data.getMacro_type();
		this.button_id = data.getButton_id();
		this.title = data.getTitle();
		this.emoteId = data.getEmoteId();
		this.output = data.getOutput();
	}
	
	public MacroData(String channelId, MacroObject data){
		this.id = data.getDatabaseId();
		this.channelId = channelId;
		this.macro_type = data.getMacroType();
		this.button_id = data.getButtonId();
		this.title = data.getTitle();
		this.emoteId = data.getEmoteId();
		this.output = data.getAllOutputs();
	}
	
	public MacroData fromChannelId(String channel_id){
		return new MacroData(this).setChannelId(channel_id);
	}
	
	public MacroData setChannelId(String channel_id) {
		this.channelId = channel_id;
		return this;
	}
	
	public Long getId() {
		return id;
	}
	
	public MacroType getMacro_type() {
		return macro_type;
	}

	public String getChannelId() {
		return channelId;
	}
	
	public int getButton_id() {
		return button_id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getEmoteId() {
		return emoteId;
	}
	
	public String[] getOutput() {
		return output;
	}
	
	
	
}
