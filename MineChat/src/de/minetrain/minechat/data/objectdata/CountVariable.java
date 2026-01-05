package de.minetrain.minechat.data.objectdata;

import java.util.Objects;

public class CountVariable {

	private final String name;
	private final long value;

	public static Builder builder() {
		return new Builder();
	}

	public CountVariable(String name, long value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public long getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
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
		CountVariable other = (CountVariable) obj;
		return Objects.equals(name, other.name);
	}

	public Builder buildCopy() {
		return builder()
			.withName(this.name)
			.withValue(this.value);
	}

	public static class Builder {
		private String name;
		private long value;

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withValue(long value) {
			this.value = value;
			return this;
		}

		public CountVariable build() {
			return new CountVariable(name, value);
		}
	}
}
