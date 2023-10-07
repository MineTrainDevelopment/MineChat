package de.minetrain.minechat.gui.obj;

import de.minetrain.minechat.main.Main;

public enum TabButtonType {
	TAB_MAIN("Tabs.first"),
	TAB_SECOND("Tabs.second"),
	TAB_THIRD("Tabs.thrid"),
	

	TAB_MAIN_ROW_2("Tabs.row2.first"),
	TAB_SECOND_ROW_2("Tabs.row2.second"),
	TAB_THIRD_ROW_2("Tabs.row2.thrid");

	private String configPath;
	public String getConfigPath(){
		return configPath;
	}
	
	
	private TabButtonType(String configPath) {
		this.configPath = configPath;
	}

	public int getOffset(TabButtonType compair, int currentLocation){
		if(Main.MAIN_FRAME != null && !Main.MAIN_FRAME.getTitleBar().getMainTab().isOccupied() && (this.equals(TAB_MAIN) || this.equals(TAB_MAIN_ROW_2))){
			return TitleBar.tabButtonHight;
		}
		
		return (this.equals(compair)) ? -500 : TitleBar.tabButtonHight;
	}
	
	public boolean isFirstRow(){
		switch (this) {
		case TAB_MAIN, TAB_SECOND, TAB_THIRD:
			return true;

		default:
			return false;
		}
	}
}
