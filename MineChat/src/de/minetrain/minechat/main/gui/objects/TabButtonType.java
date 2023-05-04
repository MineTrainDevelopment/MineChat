package de.minetrain.minechat.main.gui.objects;

public enum TabButtonType {
	TAB_MAIN,
	TAB_SECOND,
	TAB_THIRD;
	
	private TabButtonType() {
		// TODO Auto-generated constructor stub
	}
	
	public int getOffset(TabButtonType compair, int currentLocation){
		return (this.equals(compair)) ? -500 : TitleBar.tabButtonHight;
	}
}
