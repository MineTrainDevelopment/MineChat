package de.minetrain.minechat.gui.emotes;

public enum EmoteType {
	SUB(true, true),
	SUB_2(true, true),
	SUB_3(true, true),
	BIT(true, true),
	FOLLOW(false, false),
	BTTV(false, false),
	DEFAULT(false, true),
	NON(false, false);

	private boolean subOnly;

	public boolean isSubOnly() {
		return subOnly;
	}

	public boolean isBitOnly() {
		return this.equals(BIT);
	}

	private boolean global;

	public boolean isGlobal() {
		return global;
	}

	private EmoteType(boolean subOnly, boolean global) {
		this.subOnly = subOnly;
		this.global = global;
	}

	public static EmoteType get(String input, String tier) {
		return switch (input) {
			case "subscriptions" -> switch (tier) {
				case "1000" -> SUB;
				case "2000" -> SUB_2;
				case "3000" -> SUB_3;
				default -> SUB;
			};
			case "follower" -> FOLLOW;
			case "bitstier" -> BIT;
			case "bttv" -> BTTV;
			case "non" -> NON;
			default -> DEFAULT;
		};
	}
}