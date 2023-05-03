package de.minetrain.minechat.main.gui.buttons;

import de.minetrain.minechat.main.gui.frames.TitleBar;

public enum TabButtonOffset {
	TAB_1,
	TAB_2,
	TAB_3;

	private TabButtonOffset() {
		// TODO Auto-generated constructor stub
	}
	
	private static TabButtonOffset movedButton;
	
	public int getOffset(TabButtonOffset compair, int currentLocation){
		return (this.equals(compair)) ? -500 : TitleBar.tabButtonHight;
	}
}
