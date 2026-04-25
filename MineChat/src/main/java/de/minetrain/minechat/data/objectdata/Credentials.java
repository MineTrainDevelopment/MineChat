package de.minetrain.minechat.data.objectdata;

import de.minetrain.minechat.data.eclipsestore.EclipseStoreKeeper;

public class Credentials {

	private String oAuth2Token;

	public void setOAuth2Token(String oAuth2Token) {
		this.oAuth2Token = oAuth2Token;
		EclipseStoreKeeper.storeManager().store(this);
	}

	public String getOAuth2Token() {
		return oAuth2Token;
	}
}
