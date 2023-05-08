package de.minetrain.minechat.gui.obj;

public enum TabButtonType {
	TAB_MAIN("Tabs.first"),
	TAB_SECOND("Tabs.second"),
	TAB_THIRD("Tabs.thrid");

	private String configPath;
	public String getConfigPath(){return configPath;}
	
	
	private TabButtonType(String configPath) {
		this.configPath = configPath;
	}

	public int getOffset(TabButtonType compair, int currentLocation){
		return (this.equals(compair)) ? -500 : TitleBar.tabButtonHight;
	}
}
