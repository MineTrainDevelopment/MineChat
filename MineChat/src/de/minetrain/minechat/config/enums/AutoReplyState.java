package de.minetrain.minechat.config.enums;

import org.slf4j.LoggerFactory;

public enum AutoReplyState {
	ALL,
	CURRENT_TAB;
	
	public static AutoReplyState get(String name) {
		try {
			return AutoReplyState.valueOf(name);
		} catch (IllegalArgumentException ex) {
			LoggerFactory.getLogger(AutoReplyState.class).warn("Invalid AutoReplyState -> "+name+""
					+ "\n	You may need to check the settigns file."
					+ "\n	Valid types are: [ALL, CURRENT_TAB]");
			return ALL;
		}
	}
	
	/**
	 * Temp method as long the UX is just a Toggle check box.
	 * @return true if the reply state equals {@link AutoReplyState#ALL}.
	 */
	public boolean isAktiv(){
		return this.equals(AutoReplyState.ALL);
	}
	
	public AutoReplyState toggle(){
		return this.equals(AutoReplyState.ALL) ? CURRENT_TAB : ALL;
	}
}
