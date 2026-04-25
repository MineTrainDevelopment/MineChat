package de.minetrain.minechat.data.objectdata;

import java.util.Objects;

import org.slf4j.helpers.MessageFormatter;

public class BadgeId {

	private final String setId;
	private final String badgeId;

	public BadgeId(String setId, String badgeId) {
		this.setId = setId;
		this.badgeId = badgeId;
	}

	public String getSetId() {
		return setId;
	}

	public String getBadgeId() {
		return badgeId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(badgeId, setId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BadgeId other = (BadgeId) obj;
		return Objects.equals(badgeId, other.badgeId) && Objects.equals(setId, other.setId);
	}

	@Override
	public String toString() {
		return MessageFormatter.basicArrayFormat("BadgeId [setId={}, badgeId={}]", new Object[] { setId, badgeId });
	}
}
