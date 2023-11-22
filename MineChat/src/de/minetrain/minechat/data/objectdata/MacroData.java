package de.minetrain.minechat.data.objectdata;

import java.awt.Button;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.minetrain.minechat.gui.obj.buttons.ButtonType;

public class MacroData {
	private final long id;
	private final String channelId;
	private final ButtonType button_type;
	private final String button_id;
	private final String button_row;
	private final String title;
	private final String emoteId;
	private final String[] output;
	
	public MacroData(ResultSet resultSet) throws SQLException {
		this.id = resultSet.getLong("id");
		this.channelId = resultSet.getString("channel_id");
		this.button_type = ButtonType.get(resultSet.getString("button_type"));
		this.button_id = resultSet.getString("button_id");
		this.button_row = resultSet.getString("button_row");
		this.title = resultSet.getString("title");
		this.emoteId = resultSet.getString("emote_id");
		this.output = resultSet.getString("output").split("\n");
	}
	
	public long getId() {
		return id;
	}
	
	
	public ButtonType getButton_type() {
		return button_type;
	}

	public String getChannelId() {
		return channelId;
	}
	
	public String getButton_id() {
		return button_id;
	}
	
	public String getButton_row() {
		return button_row;
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
