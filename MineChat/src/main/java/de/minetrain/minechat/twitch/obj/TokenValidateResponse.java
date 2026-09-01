package de.minetrain.minechat.twitch.obj;

import java.util.List;

public class TokenValidateResponse {
	private String clientId;
	private String login;
	private List<String> scopes;
	private String userId;
	private long expiresIn;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public List<String> getScopes() {
		return scopes;
	}

	public void setScopes(List<String> scopes) {
		this.scopes = scopes;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}
}
